package org.example.fileexchange.server.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.fileexchange.model.FileMetadata;
import org.example.fileexchange.service.FileService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadRoute implements HttpHandler {
    private final FileService fileService;

    public DownloadRoute(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String linkId = path.substring(path.lastIndexOf("/") + 1);

        if (linkId.isEmpty()) {
            sendError(exchange, 400, "Link ID не указан");
            return;
        }

        FileMetadata metadata = fileService.getFileMetadata(linkId);
        if (metadata == null) {
            sendError(exchange, 404, "Файл не найден");
            return;
        }

        Path filePath = fileService.getFilePath(linkId);
        if (filePath == null || !Files.exists(filePath)) {
            sendError(exchange, 404, "Файл не найден на диске");
            return;
        }

        try {
            metadata.updateLastDownloadTime();
            
            byte[] fileData = Files.readAllBytes(filePath);
            
            System.out.println("[DOWNLOAD] Файл скачан: " + metadata.getOriginalFileName() + " | LinkId: " + linkId + " | Размер: " + fileData.length + " байт");
            
            setCorsHeaders(exchange);
            exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
            
            String fileName = metadata.getOriginalFileName();
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
            String contentDisposition = String.format("attachment; filename=\"%s\"; filename*=UTF-8''%s", 
                fileName.replace("\"", "\\\""), encodedFileName);
            exchange.getResponseHeaders().set("Content-Disposition", contentDisposition);
            
            exchange.getResponseHeaders().set("Content-Length", String.valueOf(fileData.length));
            
            exchange.sendResponseHeaders(200, fileData.length);
            
            OutputStream os = exchange.getResponseBody();
            os.write(fileData);
            os.close();
        } catch (IOException e) {
            sendError(exchange, 500, "Ошибка при чтении файла: " + e.getMessage());
        }
    }

    private void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, X-File-Name");
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        setCorsHeaders(exchange);
        String response = "{\"error\":\"" + message + "\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = response.getBytes();
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}