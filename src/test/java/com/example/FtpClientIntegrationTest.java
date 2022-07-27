package com.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.io.PrintStream;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class FtpClientIntegrationTest {

    private FakeFtpServer fakeFtpServer;

    private FtpClient ftpClient;
    private static String dirPath = "/data";
    private static String fileName = "foobar.txt";
    private static String fromFilePath;
    private static String toFilePath;

    /**
     *  private variables for utilizing streams to check for system outputs
     */
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setup() throws IOException {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", dirPath));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry(dirPath));
        fileSystem.add(new FileEntry(dirPath +"/"+fileName, "abcdef 1234567890"));
        fileSystem.add(new DirectoryEntry("/test"));
        fileSystem.add(new FileEntry("/test/sample.txt", "1234567890 abcdef"));

        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(0);

        fakeFtpServer.start();

        ftpClient = new FtpClient("localhost", fakeFtpServer.getServerControlPort(), "user", "password");
        ftpClient.open();
    }

    @After
    public void teardown() throws IOException {
        System.out.println("Calling Teardown");
        ftpClient.close();
        fakeFtpServer.stop();
    }

    @Test
    public void givenRemoteFile_whenListingRemoteFiles_thenItIsContainedInList() throws IOException {
        Collection<String> files = ftpClient.listFiles("");
        assertTrue(files.contains("foobar.txt"));
    }

    @Test
    public void createDirectory_in_remote_server() throws IOException {
        boolean created = ftpClient.createDirectory("/subdir");
        assertTrue(created);
    }

    @Test
    public void deleteFileInDirectory_in_remote_server() throws IOException {
        boolean deleted = ftpClient.deleteFile(dirPath, fileName);
        assertTrue(deleted);
    }

    @Test
    public void rename_file_on_local_machine() throws IOException {
        String sourceFile = "srcfile.txt";
        String destFile = "destfile.txt";
        File srcFile = new File(sourceFile);
        srcFile.createNewFile();
        boolean renamed = ftpClient.renameLocalFile(sourceFile, destFile);
        assertTrue(renamed);
        boolean deleted = new File(destFile).delete();
        assertTrue(deleted);
    }
    
    @Test
    public void change_permission_on_remote_server() throws IOException {
        boolean changePermission = ftpClient.changePermissionOnRemoteFile(dirPath);
        assertTrue(changePermission);
    }

    @Test
    public void putFileTest() throws IOException {
        String fileName = "helloworld.txt";
        String path = "/";

        ftpClient.putFile(fileName, path);
        assertTrue(fakeFtpServer.getFileSystem().exists("/helloworld.txt"));
    }

    @Test
    public void putMultipleFilesTest() throws IOException {
        String fileName1 = "helloworld.txt";
        String path1 = "/";
        String fileName2 = "foobar.txt";
        String path2 = "/";

        HashMap<String, String> map = new HashMap<>();
        map.put(fileName1, path1);
        map.put(fileName2, path2);

        ftpClient.putMultipleFiles(map);
        assertTrue(fakeFtpServer.getFileSystem().exists("/helloworld.txt"));
        assertTrue(fakeFtpServer.getFileSystem().exists("/foobar.txt"));
    }

    @Test
    public void getFileTest() throws IOException {
        String fileName = "foobar.txt";
        String remotePath = "/data/";
        //Ensure to use file seperator to work on all operation system
        String fileSep = System.getProperty("file.separator");
        String localPath = System.getProperty("user.dir") + fileSep + "src" + fileSep + "main" + fileSep +"resources"+ fileSep;
        ftpClient.getFile(fileName, remotePath);
        File file = new File(localPath + fileName);
        System.out.println(file.getAbsolutePath());
        assertTrue(file.exists());
    }

    @Test
    public void getMultipleFilesTest() throws IOException {
        String fileName1 = "foobar.txt";
        String remotePath = "/data/";
        String remotePath2 = "/test/";
        String fileName2 = "sample.txt";
        String fileSep = System.getProperty("file.separator");
        String localPath = System.getProperty("user.dir") + fileSep + "src" + fileSep + "main" + fileSep +"resources"+ fileSep;

        HashMap<String, String> map = new HashMap<>();
        map.put(fileName1, remotePath);
        map.put(fileName2, remotePath2);

        ftpClient.getMultipleFiles(map);
        File file = new File(localPath + fileName1);
        File file2 = new File(localPath + fileName2);
        assertTrue(file.exists());
        assertTrue(file2.exists());
    }

    @Test
    public void getDirectoriesAndFilesTest() {
        ftpClient.changeDirectory("..");
        String result = ftpClient.getDirectoriesAndFiles();
        assertTrue(result.contains("data"));

        ftpClient.changeDirectory("/data");
        result = ftpClient.getDirectoriesAndFiles();
        assertTrue(result.contains("foobar.txt"));

        ftpClient.changeDirectory("../test");
        result = ftpClient.getDirectoriesAndFiles();
        assertTrue(result.contains("sample.txt"));
    }

    @Test
    public void searchFilesTestReturnsValidFile() {
        String dirToSearch = "/data";
        String searchString = "foo";
        String fullTextFileName = "foobar.txt";

        String result = ftpClient.searchFiles(dirToSearch, searchString);
        assertTrue(result.contains(searchString));
        assertTrue(result.contains(fullTextFileName));
    }

    @Test
    public void searchFilesWithEmptyDirectoryReturnsValidFile() throws IOException {
        String fileName = "sample.txt";
        ftpClient.changeDirectory("../test");
        String result = ftpClient.getDirectoriesAndFiles();
        assertTrue(result.contains(fileName));

        String searchResult = ftpClient.searchFiles("", fileName);
        assertTrue(result.contains(fileName));
    }
}
