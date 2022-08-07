package com.example;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;


/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     *
     * @param args The arguments of the program.
     */

    //Was having various crashes when each function had their own scanner.
    //This seems to fix it.
    private static Scanner input = new Scanner(System.in);


    //This class essentially acts as a struct to hold all the info
    //needed to connect to the FTP server.
    static class loginInfo {
        String username;
        String password;
        String server;
        int port;

        public loginInfo() {
            username = "";
            password = "";
            server = "";
            port = 0;
        }

        boolean isValid() {
            if (username.equals("") || server.equals("") || port == 0)
                return false;
            return true;
        }
    }

    ;

    public static void main(String[] args) {

        loginInfo obj = getConnectionInfo();
        FtpClient ftp = new FtpClient(obj.server, obj.port, obj.username, obj.password);

        try {
            ftp.open();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            chooseOption(ftp);
        } catch (IOException | URISyntaxException e) {
            System.err.println(e.getMessage());
        }

    }

    //This function gets all the connection info from the user, and
    //gives them the option to save it to a file for next time.
    public static loginInfo getConnectionInfo() {
        loginInfo obj = new loginInfo();
        String inPort;
        String userInput;
        boolean fileExists = false;

        File connectionInfo = new File("connectionInfo.txt");


        if (connectionInfo.isFile()) {
            System.out.println("Saved connection information found. Would you like to load? Y/N");
            fileExists = true;

            userInput = input.nextLine().toUpperCase();
            if (userInput.equals("Y")) {
                obj = loadConnectionInfo();
                //Make sure that loaded info has a username, server and port.
                if (!obj.isValid()) {
                    System.out.println("Connection info bad or not valid, please enter connection info manually.");
                } else {
                    return obj;
                }
            }
        }


        System.out.println("Enter username");
        obj.username = input.nextLine();

        System.out.println("Enter password");
        obj.password = input.nextLine();

        System.out.println("Enter server");
        obj.server = input.nextLine();

        System.out.println("Enter port");
        inPort = input.nextLine();
        obj.port = Integer.parseInt(inPort);

        System.out.println("Would you like to save connection information for next time? Y/N");
        userInput = input.nextLine().toUpperCase();

        // input.close();

        if (userInput.equals("Y")) {
            //If no file exists, create one now:
            if (!fileExists) {
                try {
                    connectionInfo.createNewFile();
                } catch (IOException e) {
                    System.out.println("Error: Could not create new connection info file.");
                }
            }
            try {
                FileWriter myWriter = new FileWriter("connectionInfo.txt");
                myWriter.write(obj.username + "\n");
                myWriter.append(obj.password + "\n");
                myWriter.append(obj.server + "\n");
                myWriter.append(inPort);
                myWriter.close();
                System.out.println("Successfully saved connection info.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

        }

        return obj;
    }

    //Loads info from file.
    public static loginInfo loadConnectionInfo() {
        loginInfo obj = new loginInfo();

        try {
            Scanner scanner = new Scanner(new File("connectionInfo.txt"));
            if (scanner.hasNextLine()) {
                obj.username = scanner.nextLine();
            }
            if (scanner.hasNextLine()) {
                obj.password = scanner.nextLine();
            }
            if (scanner.hasNextLine()) {
                obj.server = scanner.nextLine();
            }
            if (scanner.hasNextLine()) {
                obj.port = Integer.parseInt(scanner.nextLine());
            }
            while (scanner.hasNextLine())
                scanner.nextLine();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private static void chooseOption(FtpClient ftp) throws IOException, URISyntaxException {
        String userChoice = "";

        System.out.println(
            "What would you like to do?\n"
                + "Press 1 - upload file(s)\n"
                + "Press 2 - download file(s)\n"
                + "Press 3 - list all files in current directory on server\n"
                + "Press 4 - search for files in directory on server\n"
                + "Press 5 - list all files in current directory on local machine\n"
                + "Press 6 - search for files in directory on local machine\n"
                + "Press 7 - create directory on remote server\n"
                + "Press 8 - delete file on remote server\n"
                + "Press 9 - rename file on local machine\n"
                + "Press 10 - change file permissions on remote server\n"
                + "Press 0 - exit\n"
        );

        userChoice = input.nextLine();

        while (!Objects.equals(userChoice, "0")) {
            switch (userChoice) {
                case "0":
                    System.out.println("exiting");
                    return;
                case "1":
                    uploadOption(ftp);
                    break;
                case "2":
                    downloadOption(ftp);
                    break;
                case "3":
                    System.out.print(ftp.getDirectoriesAndFiles());
                    break;
                case "4":
                    searchFilesOption(ftp);
                    break;
                case "5":
                    System.out.println(listDirectoriesAndFiles());
                    break;
                case "6":
                    searchOption();
                    break;
                case "7":
                    // create directory on remote server
                    break;
                case "8":
                    // delete file on remote server
                    break;
                case "9":
                    // rename file on local machine
                    break;
                case "10":
                    // change file permissions on remote server
                    break;
                default:
                    System.out.println("Invalid input. Please try again.\n\n");
            }

            System.out.println(
                "What would you like to do?\n"
                    + "Press 1 - upload file(s)\n"
                    + "Press 2 - download file(s)\n"
                    + "Press 3 - list all files in current directory on server\n"
                    + "Press 4 - search for files in directory on server\n"
                    + "Press 5 - list all files in current directory on local machine\n"
                    + "Press 6 - search for files in directory on local machine\n"
                    + "Press 7 - create directory on remote server\n"
                    + "Press 8 - delete file on remote server\n"
                    + "Press 9 - rename file on local machine\n"
                    + "Press 10 - change file permissions on remote server\n"
                    + "Press 0 - exit\n"
            );

            userChoice = input.nextLine();
        }

        if (Objects.equals(userChoice, "0")) {
            System.out.println("exiting");
        }
    }

    private static void uploadOption(FtpClient ftp) throws IOException, URISyntaxException {
        int count = 0;
        HashMap<String, String> map = new HashMap<>();

        System.out.println("How many files would you like to upload?");
        count = Integer.parseInt(input.nextLine());

        while (count < 0) {
            System.out.println("Invalid response. Please input an integer greater than zero.");
            System.out.println("How many files would you like to upload?");
            count = Integer.parseInt(input.nextLine());
        }

        for (int i = 1; i <= count; i++) {
            System.out.println("Enter file name " + i);
            String fileName = input.nextLine();
            System.out.println("File name " + i + " is: " + fileName);
            System.out.println("Enter path for file " + i);
            String path = input.nextLine();
            System.out.println("Path for file " + i + " is: " + path);
            map.put(fileName,path);
        }

        System.out.println("Uploading files...");

        ftp.putMultipleFiles(map);
    }

    private static void downloadOption(FtpClient ftp) throws IOException {
        int count = 0;
        HashMap<String, String> map = new HashMap<>();

        System.out.println("How many files would you like to download?");
        count = Integer.parseInt(input.nextLine());

        while (count < 0) {
            System.out.println("Invalid response. Please input an integer greater than zero.");
            System.out.println("How many files would you like to upload?");
            count = Integer.parseInt(input.nextLine());
        }

        for (int i = 1; i <= count; i++) {
            System.out.println("Enter file name " + i);
            String fileName = input.nextLine();
            System.out.println("File name " + i + " is: " + fileName);
            System.out.println("Enter remote path for file " + i);
            String path = input.nextLine();
            System.out.println("Remote path for file " + i + " is: " + path);
            map.put(fileName,path);
        }

        ftp.getMultipleFiles(map);
    }

    private static void searchFilesOption(FtpClient ftp) throws IOException  {
        System.out.println("Enter file name");
        String fileName = input.nextLine();
        System.out.println("File name is: " + fileName);
        System.out.println("Enter path or blank for current directory");
        String path = input.nextLine();
        System.out.println("Path is: " + path);

        System.out.println(ftp.searchFiles(path, fileName));
    }

    private static void searchOption() {
        System.out.println("Enter file name");
        String fileName = input.nextLine();
        System.out.println("File name is: " + fileName);
        System.out.println("Enter path or blank for current directory");
        String path = input.nextLine();
        System.out.println("Path is: " + path);

        System.out.println(searchFiles(path, fileName));
    }

    /**
     * List all files and directories in the current directory only
     *
     * @return a String that you need to capture in a variable or print out
     * todo: align printed out text if there is time
     */
    public static String listDirectoriesAndFiles() {
        StringBuilder result = new StringBuilder();

        // Creates a new File instance by converting the given pathname string
        // into an abstract pathname
        File[] files = new File(".").listFiles();

        DateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy hh:mm");

        // For each pathname in the files array
        if (files != null) {
            for (File file : files) {
                String details = file.getName();
                if (file.isDirectory()) {
                    details = "[" + details + "]";
                }
                details += "\t\t\t" + dateFormatter.format(file.lastModified());
                result.append(details).append("\n");
            }
        }
        return result.toString();
    }

    public static String searchFiles(String dirToSearch, String searchString) {
        String directory = ".";
        StringBuilder returnedText = new StringBuilder();

        // if the directory is empty then just search in the current directory, else...
        if (!dirToSearch.equals("")) {
            directory = dirToSearch;
        }

        File dir = new File(directory);
        File[] files = dir.listFiles((dir1, name) -> name.contains(searchString));

        if (files != null && files.length > 0) {
            returnedText.append("SEARCH RESULT: ");
            for (File aFile : files) {
                returnedText.append(aFile.getName()).append("\n");
            }
        }

        return returnedText.toString();
    }
}
