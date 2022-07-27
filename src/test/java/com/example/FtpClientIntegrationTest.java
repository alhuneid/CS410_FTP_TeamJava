package com.example;

import org.junit.Before;
import org.junit.After;
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
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class FtpClientIntegrationTest {

    private FakeFtpServer fakeFtpServer;

    private FtpClient ftpClient;

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
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/data"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/data"));
        fileSystem.add(new FileEntry("/data/foobar.txt", "abcdef 1234567890"));
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
        ftpClient.close();
        fakeFtpServer.stop();
    }


    @Test
    public void givenRemoteFile_whenListingRemoteFiles_thenItIsContainedInList() throws IOException {
        Collection<String> files = ftpClient.listFiles("");
        assertTrue(files.contains("foobar.txt"));
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
        String localPath = System.getProperty("user.dir") + "\\src\\main\\resources\\";

        ftpClient.getFile(fileName, remotePath);
        File file = new File(localPath + fileName);
        assertTrue(file.exists());
        assertTrue(file.delete());
    }

    @Test
    public void getMultipleFilesTest() throws IOException {
        String fileName1 = "foobar.txt";
        String remotePath = "/data/";
        String remotePath2 = "/test/";
        String fileName2 = "sample.txt";
        String localPath = System.getProperty("user.dir") + "\\src\\main\\resources\\";

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
