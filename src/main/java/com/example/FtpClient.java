package com.example;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class FtpClient {

    private String server;
    private int port;
    private String user;
    private String password;
    private FTPClient ftp;

    // constructor
    FtpClient(String server, int port, String user, String password) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    Collection<String> listFiles(String path) throws IOException {
        FTPFile[] files = ftp.listFiles(path);
        return Arrays.stream(files)
                .map(FTPFile::getName)
                .collect(Collectors.toList());
    }

    void open() throws IOException {
        System.out.println("Connecting...");
        ftp = new FTPClient();

        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        ftp.connect(server, port);
        int reply = ftp.getReplyCode();
        System.out.println("Reply : " + reply);
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

        ftp.login(user, password);
    }

    void close() throws IOException {
        System.out.println("Server closing");
        ftp.disconnect();
    }

    // Create a directory
    public boolean createDirectory(String dirPath) throws IOException {
        boolean success = false;
        try {
            success = ftp.makeDirectory(dirPath);
        } catch (IOException ioe) {
            System.out.println("Failed to create directory " + dirPath);
            throw ioe;
        }
        System.out.println("Successfully created: " + dirPath);
        return success;
    }

    // Delete file
    public boolean deleteFile(String dirPath, String fileName) throws IOException {
        String fileToDelete = dirPath + "/" + fileName;
        boolean deleted = false;
        try {
            deleted = ftp.deleteFile(fileToDelete);
        } catch (IOException ioe) {
            System.out.println("File '" + deleted + "' deleted...");
            throw ioe;
        }
        System.out.println("File '" + deleted + "' deleted...");
        return deleted;
    }
}
