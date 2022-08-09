package com.example;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
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
        System.out.println("PATH ===> " + path);
        FTPFile[] files = ftp.listFiles(path);
        return Arrays.stream(files)
                .map(FTPFile::getName)
                .collect(Collectors.toList());
    }

    void open() throws IOException {
        System.out.println("Connecting...");
        ftp = new FTPClient();

        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        try {
            ftp.connect(server, port);
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

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
        System.out.println("Successfully created: " + dirPath + " flag=" + success);
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

    // rename file on local machine
    public boolean renameLocalFile(String fromFilePath, String toFilePath) throws IOException {
        boolean renamed = false;
        File fromFile = new File(fromFilePath);
        File toFile = new File(toFilePath);

        System.out.println("from file -----> " + fromFile.getAbsolutePath());
        renamed = fromFile.renameTo(toFile);
        System.out.println("File " + fromFile + " renamed to " + toFile + " renamed=" + renamed);
        return renamed;
    }

    // change permission on remote server
    public boolean changePermissionOnRemoteFile(String dirPath) throws IOException {
        boolean changePermission = false;
        try {
            changePermission = ftp.sendSiteCommand("chmod " + "755 " + dirPath);
        } catch (IOException ioe) {
            System.out.println("Unable to change permission for  " + dirPath);
            throw ioe;
        }
        System.out.println("Permission updated for " + dirPath);
        return changePermission;
    }

    /**
     * This method will upload the designated file to the ftp server
     *
     * @param fileName name of the file
     * @param path     path of the file
     * @throws IOException
     */
    void putFile(String fileName, String path) throws IOException {
        String fullPath = path + fileName;

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            ftp.storeFile(fullPath, inputStream);
        }
    }

    /**
     * This method will upload multiple files to the ftp server.
     * File names must be unique.
     *
     * @param files map of (file name, path)
     */
    void putMultipleFiles(Map<String, String> files) throws IOException {
        for (String file : files.keySet()) {
            String path = files.get(file);
            putFile(file, path);
        }
    }

    /**
     * This method will download the designated file from the ftp server
     *
     * @param fileName   name of the file
     * @param remotePath remote path of the file
     * @throws IOException
     */
    void getFile(String fileName, String remotePath) throws IOException {
        String localPath = System.getProperty("user.dir") + "/src/main/resources/";
        FileOutputStream out = new FileOutputStream(localPath + fileName);
        boolean download = ftp.retrieveFile(remotePath + fileName, out);
        System.out.println("downloaded = " + download);
        out.close();
    }

    /**
     * This method will download multiple files from the ftp server.
     * File names must be unique.
     * Files will download to resources folder.
     *
     * @param files map of (file name, remote path)
     */
    void getMultipleFiles(Map<String, String> files) throws IOException {
        for (String file : files.keySet()) {
            String path = files.get(file);
            getFile(file, path);
        }
    }

    /**
     * Use this function to return the directories and files included in the
     * current directory (like the "ls" command in Linux). You will need to do
     * System.out.println with the String that's returned back from this method.
     * 
     * @return a String with the current files and directories, along with
     *         their sizes and the dates they were last changed
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
     * 
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

    String searchFiles(String dirToSearch, String searchString) {
        FTPFile[] result = null;
        String directory = ".";
        StringBuilder returnedText = new StringBuilder();

        // implement FTPFileFilter interface and override accept() method
        FTPFileFilter myFilter = ftpFile -> (ftpFile.isFile() && ftpFile.getName().contains(searchString));

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

    void deleteDirectoryFromRemote(String Path) {
        try {
            boolean removeDir = ftp.removeDirectory(Path);
            if (removeDir) {
                System.out.println("Directory successfully removed!");
            } else {
                System.out.println("Could not remove directory");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
