package org.example;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.example.dto.FileDto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String password = "password";
        System.out.println("Длина пароля: " + password.length());
        String zipName = "archive";
        String[] fileNameList = {"file1.txt", "file2.txt"};
        List<FileDto> files = new ArrayList<>();
        for (String name: fileNameList){
            files.add(new FileDto(name));
        }

        byte[] compressedZip = compressFiles(files, password, zipName + ".zip");
        byteToZip(compressedZip, zipName + ".zip");
    }

    public static byte[] compressFiles(List<FileDto> files, String password, String zipName) throws IOException {
        ZipOutputStream zos;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (password.length() == 0){
            zos = new ZipOutputStream(baos);
        }
        else {
            zos = new ZipOutputStream(baos, password.toCharArray());
        }
        int readLen;
        byte[] buff = new byte[1024];

        for (FileDto file: files){
            ZipParameters parameters = new ZipParameters();
            if (password.length() != 0){
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
            }
            parameters.setEntrySize(file.getData().length);
            parameters.setFileNameInZip(file.getFileName());
            zos.putNextEntry(parameters);

            try (InputStream inputStream = new ByteArrayInputStream(file.getData())){
                while ((readLen = inputStream.read(buff)) != -1) {
                    zos.write(buff, 0, readLen);
                }
            }
            zos.closeEntry();
        }
        zos.close();
        return baos.toByteArray();
    }

    public static void byteToZip(byte[] zipData, String zipName){
        try(FileOutputStream outputStream = new FileOutputStream(zipName)) {
            outputStream.write(zipData);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}