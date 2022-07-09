package com.example;

import java.io.IOException;
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
        
    }
}