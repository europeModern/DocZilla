package org.example.fileexchange.service;

import org.example.fileexchange.model.FileMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FileService {
    private static final String FILES_DIRECTORY = "files";
    private final Map<String, FileMetadata> fileMetadataMap;
    private final Path filesPath;

    public FileService() {
        this.fileMetadataMap = new ConcurrentHashMap<>();
        this.filesPath = Paths.get(FILES_DIRECTORY);
        
        try {
            if (!Files.exists(filesPath)) {
                Files.createDirectories(filesPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для файлов", e);
        }
    }

    public String saveFile(byte[] fileData, String originalFileName) throws IOException {
        String linkId = UUID.randomUUID().toString();
        Path filePath = filesPath.resolve(linkId);
        
        Files.write(filePath, fileData);
        
        FileMetadata metadata = new FileMetadata(linkId, originalFileName, fileData.length);
        fileMetadataMap.put(linkId, metadata);
        
        String downloadUrl = "/download/" + linkId;
        System.out.println("[UPLOAD] Файл загружен: " + originalFileName + " | Размер: " + fileData.length + " байт | LinkId: " + linkId + " | URL: http://localhost:8080" + downloadUrl);
        
        return linkId;
    }

    public FileMetadata getFileMetadata(String linkId) {
        return fileMetadataMap.get(linkId);
    }

    public Path getFilePath(String linkId) {
        FileMetadata metadata = fileMetadataMap.get(linkId);
        if (metadata == null) {
            return null;
        }
        return filesPath.resolve(linkId);
    }

    public boolean deleteFile(String linkId) {
        FileMetadata metadata = fileMetadataMap.remove(linkId);
        if (metadata == null) {
            return false;
        }
        
        try {
            Path filePath = filesPath.resolve(linkId);
            Files.deleteIfExists(filePath);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Map<String, FileMetadata> getAllMetadata() {
        return new ConcurrentHashMap<>(fileMetadataMap);
    }
}