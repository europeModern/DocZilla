package org.example.fileexchange.server;

import com.sun.net.httpserver.HttpServer;
import org.example.fileexchange.service.FileService;
import org.example.fileexchange.service.CleanupService;
import org.example.fileexchange.server.routes.UploadRoute;
import org.example.fileexchange.server.routes.DownloadRoute;

import java.io.IOException;
import java.net.InetSocketAddress;

public class FileServer {
    private static final int PORT = 8080;
    private final FileService fileService;
    private final CleanupService cleanupService;
    private HttpServer server;

    public FileServer() {
        this.fileService = new FileService();
        this.cleanupService = new CleanupService(fileService);
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        server.createContext("/upload", new UploadRoute(fileService));
        server.createContext("/download/", new DownloadRoute(fileService));
        
        server.setExecutor(null);
        server.start();
        
        cleanupService.start();
        
        System.out.println("File Server запущен на http://localhost:" + PORT);
        System.out.println("Endpoints:");
        System.out.println("  POST /upload - загрузка файла");
        System.out.println("  GET /download/{linkId} - скачивание файла");
    }

    public void stop() {
        cleanupService.stop();
        if (server != null) {
            server.stop(0);
            System.out.println("File Server остановлен");
        }
    }

    public static void main(String[] args) {
        FileServer fileServer = new FileServer();
        try {
            fileServer.start();
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                fileServer.stop();
            }));
            
            System.out.println("Нажмите Enter для остановки сервера...");
            System.in.read();
            fileServer.stop();
        } catch (IOException e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}





