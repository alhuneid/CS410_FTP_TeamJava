package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
     * @param args The arguments of the program.
     */

     //Was having various crashes when each function had their own scanner.
     //This seems to fix it.
    private static Scanner input  = new Scanner(System.in);


     //This class essentially acts as a struct to hold all the info
     //needed to connect to the FTP server.
     static class loginInfo
     {
        String username;
        String password;
        String server;
        int port;
        public loginInfo(){
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
     };
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
        loginInfo obj = new loginInfo();;
        String inPort;
        String userInput;
        boolean fileExists = false;

        File connectionInfo = new File("connectionInfo.txt");



        
            if (connectionInfo.isFile())
            {
                System.out.println("Saved connection information found. Would you like to load? Y/N");
                fileExists = true;

                userInput = input.nextLine().toUpperCase();
                if (userInput.equals("Y"))
                {
                    obj = loadConnectionInfo();
                    //Make sure that loaded info has a username, server and port.
                    if (!obj.isValid())
                    {
                        System.out.println("Connection info bad or not valid, please enter connection info manually.");
                    }
                    else
                    {
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

            if (userInput.equals("Y"))
            {
                //If no file exists, create one now:
                if (!fileExists)
                {
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

        System.out.println(
            "What would you like to do?\n"
                + "Press 1 - upload file\n"
                + "Press 2 - download file\n"
                + "Press 3 - list all files in current directory\n"
                + "Press 4 - exit\n"
        );
       
        String userChoice = input.nextLine();


        if (Objects.equals(userChoice, "1")) {
            uploadOption(ftp);
        } else if (Objects.equals(userChoice, "2")){
            downloadOption(ftp);
        } else if (Objects.equals(userChoice, "3")){
            listDirectoriesAndFiles();
        } else {
            System.out.println("Exiting");
        }
    
    }

    private static void uploadOption(FtpClient ftp) throws IOException, URISyntaxException {
       // Scanner input = new Scanner(System.in);

        System.out.println("Enter file name");
        String fileName = input.nextLine();
        System.out.println("File name is: " + fileName);
        System.out.println("Enter path");
        String path = input.nextLine();
        System.out.println("Path is: " + path);
        //input.close();

        ftp.putFile(fileName, path + fileName);
    }

    private static void downloadOption(FtpClient ftp) throws IOException {
        //Scanner input = new Scanner(System.in);

        System.out.println("Enter file name");
        String fileName = input.nextLine();
        System.out.println("File name is: " + fileName);
        System.out.println("Enter remote path");
        String path = input.nextLine();
        System.out.println("Path is: " + path);
        //input.close();

        ftp.getFile(fileName, path);
    }

    /**
     * List all files and directories in the current directory only
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
}
