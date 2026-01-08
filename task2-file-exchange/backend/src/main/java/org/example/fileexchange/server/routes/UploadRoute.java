package org.example.fileexchange.server.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.fileexchange.service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UploadRoute implements HttpHandler {
    private final FileService fileService;

    public UploadRoute(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            handleCors(exchange);
            return;
        }

        if (!exchange.getRequestMethod().equals("POST")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            String fileName = exchange.getRequestHeaders().getFirst("X-File-Name");
            if (fileName == null || fileName.isEmpty()) {
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.contains("filename=")) {
                    fileName = query.split("filename=")[1].split("&")[0];
                } else {
                    fileName = "uploaded_file";
                }
            } else {
                try {
                    fileName = decodeBase64FileName(fileName);
                } catch (Exception e) {
                    fileName = "uploaded_file";
                }
            }

            byte[] fileData = readRequestBody(exchange);
            if (fileData.length == 0) {
                sendResponse(exchange, 400, "Файл пуст");
                return;
            }

            String linkId = fileService.saveFile(fileData, fileName);
            String response = "{\"linkId\":\"" + linkId + "\",\"downloadUrl\":\"/download/" + linkId + "\"}";
            
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    private void handleCors(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);
        exchange.sendResponseHeaders(200, 0);
        exchange.close();
    }

    private void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, X-File-Name");
        exchange.getResponseHeaders().set("Access-Control-Max-Age", "3600");
    }

    private byte[] readRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return inputStream.readAllBytes();
    }

    private String decodeBase64FileName(String encoded) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encoded);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return encoded;
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        setCorsHeaders(exchange);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}

