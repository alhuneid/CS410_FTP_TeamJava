package com.example;

import java.io.IOException;
import java.net.URISyntaxException;
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
    public static void main(String[] args) {

        System.out.println("Hello World!");

        Scanner input = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter username");
        String userName = input.nextLine();
        System.out.println("Username is: " + userName);

        System.out.println("Enter password");
        String password = input.nextLine();
        System.out.println("Password is: " + password);

        System.out.println("Enter server");
        String server = input.nextLine();
        System.out.println("Server is: " + server);

        System.out.println("Enter port");
        String inPort = input.nextLine();
        System.out.println("Port is: " + inPort);

        int port = Integer.parseInt(inPort);

        FtpClient ftp = new FtpClient(server, port, userName, password);

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

    private static void chooseOption(FtpClient ftp) throws IOException, URISyntaxException {
        Scanner input = new Scanner(System.in);

        System.out.println(
            "What would you like to do?\n"
                + "Press 1 - upload file\n"
                + "Press 2 - download file\n"
                + "Press 3 - exit\n"
        );

        String userChoice = input.nextLine();

        if (Objects.equals(userChoice, "1")) {
            uploadOption(ftp);
        } else if (Objects.equals(userChoice, "2")){
            downloadOption(ftp);
        }
        else System.out.println("Exiting");
    }

    private static void uploadOption(FtpClient ftp) throws IOException, URISyntaxException {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter file name");
        String fileName = input.nextLine();
        System.out.println("File name is: " + fileName);
        System.out.println("Enter path");
        String path = input.nextLine();
        System.out.println("Path is: " + path);

        ftp.putFile(fileName, path + fileName);
    }

    private static void downloadOption(FtpClient ftp) throws IOException {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter file name");
        String fileName = input.nextLine();
        System.out.println("File name is: " + fileName);
        System.out.println("Enter remote path");
        String path = input.nextLine();
        System.out.println("Path is: " + path);

        ftp.getFile(fileName, path);
    }
}
