package vn.xuanhung.ELearning_Service.common;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;

public class Base64DecodedMultipartFile implements MultipartFile {

    private final byte[] fileContent;
    private final String contentType;
    private final String fileName;

    public Base64DecodedMultipartFile(byte[] fileContent, String contentType, String fileName) {
        this.fileContent = fileContent;
        this.contentType = contentType;
        this.fileName = fileName;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return fileContent == null || fileContent.length == 0;
    }

    @Override
    public long getSize() {
        return fileContent.length;
    }

    @Override
    public byte[] getBytes() {
        return fileContent;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(fileContent);
    }

    @Override
    public void transferTo(File dest) throws IOException {
        Files.write(dest.toPath(), fileContent);
    }
}
