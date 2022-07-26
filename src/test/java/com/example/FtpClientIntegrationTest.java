package com.example;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.platform.engine.support.descriptor.FilePosition;
import org.junit.platform.engine.support.descriptor.FileSource;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.IOException;
import java.nio.file.FileStore;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class FtpClientIntegrationTest {

    private FakeFtpServer fakeFtpServer;

    private FtpClient ftpClient;

    private static String dirPath = "/data";
    private static String fileName = "foobar.txt";
    private static String fromFilePath;
    private static String toFilePath;

    @Before
    public void setup() throws IOException {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", dirPath));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry(dirPath));
        fileSystem.add(new FileEntry(dirPath +"/"+fileName, "abcdef 1234567890"));
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
    public void createDirectory_in_remote_server() throws IOException {
        boolean created = ftpClient.createDirectory(dirPath);
        assertTrue(created);
    }

    @Test
    public void deleteFileInDirectory_in_remote_server() throws IOException {
        boolean deleted = ftpClient.deleteFile(dirPath, fileName);
        assertTrue(deleted);
    }
    @Test
    public void rename_file_on_local_machine() throws IOException {
        boolean renamed = ftpClient.renameLocalFile(fromFilePath,toFilePath);
        assertTrue(renamed);
        
        }
    
    @Test
    public void change_permission_on_remote_server() throws IOException {
        boolean changePermission = ftpClient.changePermissionOnRemoteFile(dirPath);
        assertTrue(changePermission);
    }
}
