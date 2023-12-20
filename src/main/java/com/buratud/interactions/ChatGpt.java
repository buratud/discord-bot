package com.buratud.interactions;

import com.buratud.Service;
import com.buratud.data.openai.ChatGptChannelInfo;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatGpt implements Handler {
    private static final Logger logger = LogManager.getLogger(ChatGpt.class);
    private static ChatGpt instance;
    private final com.buratud.services.ChatGpt chatGpt;
    private static final HashMap<String, String> fileExtMap;

    static {
        fileExtMap = createFileExtensionMap();
    }

    private ChatGpt() throws IOException {
        Service service = Service.getInstance();
        chatGpt = service.chatgpt;
    }

    public static synchronized ChatGpt getInstance() throws IOException {
        // Create the instance if it doesn't exist yet
        if (instance == null) {
            instance = new ChatGpt();
        }
        return instance;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (chatGpt != null) {
            try {
                if (event.getAuthor().isBot()) {
                    return;
                }
                Message message = event.getMessage();
                String rawMessage = message.getContentRaw();
                String channelId = event.getChannel().getId();
                String userId = event.getAuthor().getId();
                ChatGptChannelInfo info = chatGpt.getInfo(channelId, userId);
                if (message.getMentions().isMentioned(event.getJDA().getSelfUser()) || info != null && info.isActivated()) {
                    if (message.getMentions().isMentioned(event.getJDA().getSelfUser())) {
                        int pos = rawMessage.indexOf(String.format("<@%s>", event.getJDA().getSelfUser().getId()));
                        int lastPos = rawMessage.indexOf('>', pos);
                        rawMessage = rawMessage.substring(lastPos + 1).trim();
                    }
                    rawMessage = replaceFileContent(rawMessage, message.getAttachments());
                    String flagged = chatGpt.moderationCheck(rawMessage);
                    if (flagged != null) {
                        message.reply(flagged).complete();
                        return;
                    }
                    String res = chatGpt.sendStreamEnabled(channelId, userId, rawMessage);
                    List<String> responses = splitResponse(res);
                    for (String response : responses) {
                        if (response.length() > 2000) {
                            message.replyFiles(convertToDiscordFile(response)).complete();
                        } else {
                            message.reply(response).complete();
                        }
                    }
                }
            } catch (Exception e) {
                event.getMessage().reply("Something went wrong, try again later.").complete();
                logger.error(e);
                for (StackTraceElement element : e.getStackTrace()) {
                    logger.error(element.toString());
                }
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String[] params = event.getModalId().split("_");
        String systemMessage = event.getValues().get(0).getAsString();
        chatGpt.setSystemMessage(params[3], params[4], systemMessage);
        event.reply("System message set and will be applied to next session.").complete();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        try {
            if (chatGpt != null) {
                String subName = event.getSubcommandName();
                switch (subName) {
                    case "reset" -> resetChatHistory(event);
                    case "model" -> switchModel(event);
                    case "activation" -> activate(event);
                    case "system" -> systemMessage(event);
                    case "oneshot" -> oneshot(event);
                    default -> event.reply("Subcommand doesn't exist.").complete();
                }
            } else {
                event.reply("Module is unavailable").complete();
            }
        } catch (Exception e) {
            if (event.isAcknowledged()) {
                event.getHook().sendMessage("Something went wrong, try again later.").complete();
            } else {
                event.reply("Something went wrong, try again later.").setEphemeral(true).complete();
            }
            logger.error(e);
            for (StackTraceElement element : e.getStackTrace()) {
                logger.error(element.toString());
            }
        }
    }

    private void systemMessage(SlashCommandInteractionEvent event) {
        final String id = String.format("chatgpt_system_%s_%s_%s", event.getGuild().getId(), event.getChannel().getId(), event.getMember().getId());
        String currentSystemMessage = chatGpt.getSystemMessage(event.getChannel().getId(), event.getMember().getId());
        TextInput systemMessageInput = TextInput.create("system_message", "System Message", TextInputStyle.PARAGRAPH)
                .setValue(currentSystemMessage).setRequired(false).build();
        Modal modal = Modal.create(id, "System Message").addActionRow(systemMessageInput).build();
        event.replyModal(modal).complete();
    }

    private void activate(SlashCommandInteractionEvent event) {
        String channelId = event.getMessageChannel().getId();
        String userId = event.getMember().getId();
        Boolean activation = event.getOption("activate").getAsBoolean();
        chatGpt.SetActivation(channelId, userId, activation);
        if (activation) {
            event.reply("Activated.").complete();
        } else {
            event.reply("Deactivated.").complete();
        }
    }

    private void oneshot(SlashCommandInteractionEvent event) {
        String channelId = event.getMessageChannel().getId();
        String userId = event.getMember().getId();
        Boolean activation = event.getOption("activate").getAsBoolean();
        chatGpt.SetOneShot(channelId, userId, activation);
        if (activation) {
            event.reply("Activated. The response will not be saved from now on.").complete();
        } else {
            event.reply("Deactivated. The response will now continue to save.").complete();
        }
    }

    private void switchModel(SlashCommandInteractionEvent event) {
        String channelId = event.getMessageChannel().getId();
        String userId = event.getMember().getId();
        String model = event.getOption("model").getAsString();
        chatGpt.SwitchModel(channelId, userId, model);
        event.reply(String.format("Switched to %s model.", model)).complete();
    }

    private void resetChatHistory(SlashCommandInteractionEvent event) {
        String channelId = event.getMessageChannel().getId();
        String userId = event.getMember().getId();
        chatGpt.reset(channelId, userId);
        String systemMessage = chatGpt.getSystemMessage(channelId, userId);
        if (systemMessage == null) {
            event.reply("Chat history reset.").complete();
        } else {
            event.reply(String.format("Chat history reset.\nCurrent system message: %s", systemMessage)).complete();
        }
    }

    public static CompletableFuture<String> convertInputStreamToString(CompletableFuture<InputStream> inputStreamFuture) {
        return inputStreamFuture.thenApplyAsync(inputStream -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append(System.lineSeparator());
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                throw new RuntimeException("Error reading InputStream", e);
            }
        });
    }

    public static List<String> splitResponse(String response) {
        final int MAX_LENGTH = 2000;
        List<String> responses = new ArrayList<>();
        int strlen = response.length();
        Pattern pattern = Pattern.compile("(\\d+\\.|-)\\s");
        Matcher matcher = pattern.matcher(response);
        int start = 0, end = 0;
        int startLine = 0, endLine = -2;
        // Special means a list or a code
        int startSpecial = -1, endSpecial = -1;
        boolean isCode = false, isList = false;
        while (endLine != strlen) {
            while (startLine < strlen && response.charAt(startLine) == '\n') {
                startLine++;
            }
            endLine = response.indexOf('\n', endLine + 2);
            if (endLine == -1) {
                endLine = strlen;
            }
            // This line is a top-level list
            if (matcher.find(startLine) && matcher.start() == startLine) {
                // This line starts a list
                if (isList) {
                    // If this is true earlier, it means this line is a new top-level list
                    if (endSpecial - start <= MAX_LENGTH) {
                        end = endSpecial;
                    } else {
                        responses.add(response.substring(start, end));
                        start = startSpecial;
                        end = endSpecial;
                    }
                }
                startSpecial = startLine;
                isList = true;
            } else if (response.startsWith("```", startLine)) {
                // This line is either a start or end of code
                if (isCode) {
                    // Indicates that this is the end of the code
                    if (endLine - start > MAX_LENGTH) {
                        responses.add(response.substring(start, end));
                        start = startSpecial;
                    }
                    end = endLine;
                    startSpecial = -1;
                } else {
                    startSpecial = startLine;
                }
                isCode = !isCode;
            } else if (!isCode && response.charAt(startLine) != ' ') {
                // This is regular line of text
                isList = false;
                // There might be some special text
                if (startSpecial != -1) {
                    if (endSpecial - start <= MAX_LENGTH) {
                        end = endSpecial;
                    } else {
                        responses.add(response.substring(start, end));
                        start = startSpecial;
                        end = endSpecial;
                    }
                    startSpecial = -1;
                }
                if (endLine - start > MAX_LENGTH) {
                    responses.add(response.substring(start, end));
                    start = startLine;
                }
                end = endLine;
            }
            if (isList || isCode) {
                endSpecial = endLine;
            }
            startLine = endLine;
        }
        // Add the remaining text
        if (endLine - start > MAX_LENGTH) {
            responses.add(response.substring(start, end));
            start = end;
        }
        while (start < strlen && response.charAt(start) == '\n') {
            start++;
        }
        responses.add(response.substring(start, endLine));
        return responses;
    }

    private static String replaceFileContent(String message, List<Message.Attachment> attachments) throws ExecutionException, InterruptedException {
        for (Message.Attachment attachment : attachments) {
            message = message.replace("[[" + attachment.getFileName() + "]]", convertInputStreamToString(attachment.getProxy().download()).get());
        }
        return message;
    }

    private static HashMap<String, String> createFileExtensionMap() {
        HashMap<String, String> fileExtension = new HashMap<>();
        fileExtension.put("python", ".py");
        fileExtension.put("javascript", ".js");
        fileExtension.put("typescript", ".ts");
        fileExtension.put("java", ".java");
        fileExtension.put("c++", ".cpp");
        fileExtension.put("cpp", ".cpp");
        fileExtension.put("c#", ".cs");
        fileExtension.put("html", ".html");
        fileExtension.put("xhtml", ".html");
        fileExtension.put("css", ".css");
        fileExtension.put("ruby", ".rb");
        fileExtension.put("go", ".go");
        fileExtension.put("php", ".php");
        fileExtension.put("swift", ".swift");
        fileExtension.put("kotlin", ".kt");
        fileExtension.put("rust", ".rs");
        fileExtension.put("scala", ".scala");
        fileExtension.put("r", ".r");
        fileExtension.put("powershell", ".ps1");
        fileExtension.put("bash", ".sh");
        fileExtension.put("markdown", ".md");
        fileExtension.put("sql", ".sql");
        fileExtension.put("perl", ".pl");
        fileExtension.put("lua", ".lua");
        fileExtension.put("haskell", ".hs");
        fileExtension.put("dart", ".dart");
        fileExtension.put("xml", ".xml");
        fileExtension.put("json", ".json");
        fileExtension.put("batch", ".bat");
        fileExtension.put("clojure", ".clj");
        return fileExtension;
    }

    private static String detectFileExtension(String first_line) {
        String language = first_line.substring(3);
        return fileExtMap.getOrDefault(language, ".txt");
    }

    private static FileUpload convertToDiscordFile(String response) {
        int firstNewLine = response.indexOf('\n');
        int lastNewLine = response.lastIndexOf('\n');
        String firstLine = response.substring(0, firstNewLine);
        String extension = detectFileExtension(firstLine);
        return FileUpload.fromData(response.substring(firstNewLine + 1, lastNewLine).getBytes(StandardCharsets.UTF_8), "code" + extension);
    }
}
