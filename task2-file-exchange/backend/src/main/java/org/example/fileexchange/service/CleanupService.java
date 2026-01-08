package org.example.fileexchange.service;

import org.example.fileexchange.model.FileMetadata;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CleanupService {
    private static final int CLEANUP_INTERVAL_MINUTES = 5;
    private static final int STALE_MINUTES_THRESHOLD = 5;
    
    private final FileService fileService;
    private final ScheduledExecutorService scheduler;

    public CleanupService(FileService fileService) {
        this.fileService = fileService;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void start() {
        scheduler.scheduleAtFixedRate(
            this::cleanupStaleFiles,
            0,
            CLEANUP_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        );
        System.out.println("CleanupService запущен. Очистка каждые " + CLEANUP_INTERVAL_MINUTES + " минут(ы)");
    }

    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("CleanupService остановлен");
    }

    private void cleanupStaleFiles() {
        try {
            List<String> staleFileIds = fileService.getAllMetadata().values().stream()
                .filter(metadata -> metadata.isStaleMinutes(STALE_MINUTES_THRESHOLD))
                .map(FileMetadata::getLinkId)
                .collect(Collectors.toList());

            if (staleFileIds.isEmpty()) {
                return;
            }

            System.out.println(staleFileIds.size() + " устаревших файлов найдено");
            
            int deletedCount = 0;
            for (String linkId : staleFileIds) {
                if (fileService.deleteFile(linkId)) {
                    deletedCount++;
                }
            }
            
            System.out.println("Удалено " + deletedCount + " устаревших файлов (не скачивались более " + 
                             STALE_MINUTES_THRESHOLD + " минут(ы))");
        } catch (Exception e) {
            System.err.println("Ошибка при очистке файлов: " + e.getMessage());
            e.printStackTrace();
        }
    }

}