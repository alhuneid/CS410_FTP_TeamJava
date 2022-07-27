package com.example;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.lang.String;

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

    void putFile(String fileName, String path) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            ftp.storeFile(path, inputStream);
        }
    }

    void getFile(String fileName, String remotePath) throws IOException {
        String localPath = System.getProperty("user.dir") + "\\src\\main\\resources\\";
        FileOutputStream out = new FileOutputStream(localPath + fileName);
        ftp.retrieveFile(remotePath + fileName, out);
        out.close();
    }

    /**
     * Use this function to return the directories and files included in the
     * current directory (like the "ls" command in Linux). You will need to do
     * System.out.println with the String that's returned back from this method.
     * @return a String with the current files and directories, along with
     *          their sizes and the dates they were last changed
     */
    String getDirectoriesAndFiles() {
        StringBuilder result = new StringBuilder();
        try {
            FTPFile[] files = ftp.listFiles();

            // iterates over the files and prints details for each
            DateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy hh:mm");

            for (FTPFile file : files) {
                String details = file.getName();
                if (file.isDirectory()) {
                    details = "[" + details + "]";
                }
                details += "\t\t" + file.getSize() + "\t\t" + dateFormatter.format(file.getTimestamp().getTime());
                result.append(details).append("\n");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return result.toString();
    }

    /**
     * Use this to go into or out of a directory (like the "cd" command on Linux
     * to go up one level, use 'changeDirectory("..")'
     * to go down one level, use 'changeDirectory("folder-name")'
     * @param path name of folder to drill down into
     */
    void changeDirectory(String path) {
        try {
            boolean success = ftp.changeWorkingDirectory(path);

            if (!success) {
                System.out.println("Failed to change working directory");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    String searchFiles(String dirToSearch, String searchString){
        FTPFile[] result = null;
        String directory = ".";
        StringBuilder returnedText = new StringBuilder();

        // implement FTPFileFilter interface and override accept() method
        FTPFileFilter myFilter = ftpFile -> (
            ftpFile.isFile() && ftpFile.getName().contains(searchString));

        // if the directory is empty then just search in the current directory, else...
        if (!dirToSearch.equals("")) {
            directory = dirToSearch;
        }

        try {
            result = ftp.listFiles(directory, myFilter);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        if (result != null && result.length > 0) {
            returnedText.append("SEARCH RESULT: ");
            for (FTPFile aFile : result) {
                returnedText.append(aFile.getName()).append("\n");
            }
        }

        return returnedText.toString();
    }
}
