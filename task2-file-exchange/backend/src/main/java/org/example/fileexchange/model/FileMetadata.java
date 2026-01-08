package org.example.fileexchange.model;

import java.time.LocalDateTime;

public class FileMetadata {
    private final String linkId;
    private final String originalFileName;
    private final long fileSize;
    private final LocalDateTime uploadTime;
    private LocalDateTime lastDownloadTime;

    public FileMetadata(String linkId, String originalFileName, long fileSize) {
        this.linkId = linkId;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.uploadTime = LocalDateTime.now();
        this.lastDownloadTime = null;
    }

    public String getLinkId() {
        return linkId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public LocalDateTime getLastDownloadTime() {
        return lastDownloadTime;
    }

    public void updateLastDownloadTime() {
        this.lastDownloadTime = LocalDateTime.now();
    }

    public boolean isStale(int daysThreshold) {
        if (lastDownloadTime == null) {
            return uploadTime.isBefore(LocalDateTime.now().minusDays(daysThreshold));
        }
        return lastDownloadTime.isBefore(LocalDateTime.now().minusDays(daysThreshold));
    }

    public boolean isStaleMinutes(int minutesThreshold) {
        if (lastDownloadTime == null) {
            return uploadTime.isBefore(LocalDateTime.now().minusMinutes(minutesThreshold));
        }
        return lastDownloadTime.isBefore(LocalDateTime.now().minusMinutes(minutesThreshold));
    }
}