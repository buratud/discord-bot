package com.buratud.interactions;

import com.buratud.Service;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public final class ChatGpt implements Handler {
    private static final Logger logger = LogManager.getLogger(ChatGpt.class);
    private static ChatGpt instance;
    private final com.buratud.services.ChatGpt chatGpt;
    private final HashMap<String, String> fileExtMap;

    private ChatGpt() throws IOException {
        Service service = Service.getInstance();
        fileExtMap = createFileExtensionMap();
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
                if (message.getMentions().isMentioned(event.getJDA().getSelfUser())) {
                    int pos = rawMessage.indexOf(String.format("<@%s>", event.getJDA().getSelfUser().getId()));
                    int lastPos = rawMessage.indexOf('>', pos);
                    rawMessage = rawMessage.substring(lastPos + 1).trim();
                    String flagged = chatGpt.moderationCheck(rawMessage);
                    if (flagged != null) {
                        message.reply(flagged).queue();
                        return;
                    }
                    String res = chatGpt.sendStreamEnabled(event.getChannel().getId(), event.getAuthor().getId(), rawMessage);
                    List<String> responses = splitResponse(res);
                    for (String response : responses) {
                        if (response.startsWith("```")) {
                            message.replyFiles(convertToDiscordFile(response)).queue();
                        } else {
                            message.reply(response).queue();
                        }
                    }
                }
            } catch (Exception e) {
                event.getMessage().reply("Something went wrong, try again later.").queue();
                logger.error(e);
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        try {
            if (chatGpt != null) {
                String subName = event.getSubcommandName();
                switch (subName) {
                    case "reset" -> resetChatHistory(event);
                    case "model" -> switchModel(event);
                    default -> event.reply("Subcommand doesn't exist.").queue();
                }
            } else {
                event.reply("Module is unavailable").queue();
            }

        } catch (Exception e) {
            logger.error(e);
            if (event.isAcknowledged()) {
                event.getHook().sendMessage("Something went wrong, try again later.").queue();
            } else {
                event.reply("Something went wrong, try again later.").setEphemeral(true).queue();
            }
        }

    }

    private void switchModel(SlashCommandInteractionEvent event) throws ExecutionException, InterruptedException {
        String channelId = event.getMessageChannel().getId();
        String userId = event.getMember().getId();
        String model = event.getOption("model").getAsString();
        chatGpt.SwitchModel(channelId, userId, model);
        event.reply(String.format("Switched to %s model.", model)).queue();
    }

    private void resetChatHistory(SlashCommandInteractionEvent event) {
        String channelId = event.getMessageChannel().getId();
        String userId = event.getMember().getId();
        chatGpt.reset(channelId, userId);
        event.reply("Chat history reset.").queue();
    }

    private static List<String> splitResponse(String response) {
        List<String> responses = new ArrayList<>();
        int strlen = response.length();
        int last_index = 0;
        boolean is_code = false;
        while (last_index < strlen) {
            if (last_index == -1) {
                throw new IllegalArgumentException("Split response failed.");
            }
            if (is_code) {
                int end = response.indexOf("```", last_index + 3);
                if (end != -1) {
                    end += 3;
                } else {
                    // Code block not found, handle the error case
                    throw new IllegalArgumentException("Code block not found.");
                }
                responses.add(response.substring(last_index, end));
                // Assuming that it will always have \n after the code block
                last_index = end + 1;
                is_code = false;
            } else {
                if (last_index + 2000 > strlen) {
                    responses.add(response.substring(last_index));
                    break;
                }
                int end = response.indexOf("\n```", last_index);
                if (end == -1 || end - last_index > 2000) {
                    end = response.lastIndexOf('\n', last_index + 2000);
                } else {
                    is_code = true;
                }
                responses.add(response.substring(last_index, end));
                last_index = end + 1;
            }
        }
        return responses;
    }

    private HashMap<String, String> createFileExtensionMap() {
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

    private String detectFileExtension(String first_line) {
        String language = first_line.substring(3);
        return fileExtMap.getOrDefault(language, ".txt");
    }

    private FileUpload convertToDiscordFile(String response) throws IOException {
        int firstNewLine = response.indexOf('\n');
        int lastNewLine = response.lastIndexOf('\n');
        String firstLine = response.substring(0, firstNewLine);
        String extension = detectFileExtension(firstLine);
        return FileUpload.fromData(response.substring(firstNewLine + 1, lastNewLine).getBytes(StandardCharsets.UTF_8), "code" + extension);
    }
}
