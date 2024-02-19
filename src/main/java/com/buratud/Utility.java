package com.buratud;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jetbrains.annotations.NotNull;


public class Utility {
    public static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    public class Base64WithoutPadding {
        public static String encode(String input) {
            return Base64.getEncoder().withoutPadding().encodeToString(input.getBytes());
        }

        public static String decode(String input) {
            return new String(Base64.getDecoder().decode(input));
        }

        public static String downloadImageToBase64WithoutPadding(String urlString) throws IOException {
            // Create a URL object from the given URL string
            URL url = new URL(urlString);
            // Create a buffered input stream from the URL connection
            ByteArrayOutputStream out = getByteArrayOutputStream(url);
            // Encode the image bytes to base64 without padding
            byte[] imageBytes = out.toByteArray();
            // Return the base64 encoded image without padding
            return Base64.getEncoder().withoutPadding().encodeToString(imageBytes);
        }
    }

    @NotNull
    private static ByteArrayOutputStream getByteArrayOutputStream(URL url) throws IOException {
        BufferedInputStream in = new BufferedInputStream(url.openStream());
        // Create a byte array output stream to write the downloaded image to
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // Read the image bytes into the output stream
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        // Close the input and output streams
        in.close();
        out.close();
        return out;
    }

    public static class MimeTypeDetector {
        public static String defaultReturn = "application/octet-stream";

        public static String getMimeType(String url) {
            String urlNoQueryString = url.split("\\?")[0];
            String extension = urlNoQueryString.substring(url.lastIndexOf(".") + 1).toLowerCase();
            return switch (extension) {
                case "png" -> "image/png";
                case "jpg", "jpeg" -> "image/jpeg";
                case "webp" -> "image/webp";
                case "heic" -> "image/heic";
                case "heif" -> "image/heif";
                default -> defaultReturn;
            };
        }
    }
}
