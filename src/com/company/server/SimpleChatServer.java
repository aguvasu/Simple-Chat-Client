package com.company.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by kevin on 10/13/16.
 */
public class SimpleChatServer {
//    List that has hold the clients connections
    ArrayList clientOutputStreams;
    public static void main(String[] args) {
        new SimpleChatServer().go();
    }

    private void go() {
        clientOutputStreams = new ArrayList();

        try {
//            I am using port 5000. Use can assign yours depending on your sys admin
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server is up and running");

//            Keep the server running
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("got a connection");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    Clas handles the clients connections
    private class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;
        public ClientHandler(Socket clientSocket) {
            try {
                sock = clientSocket;
                reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("read " + message);
                    tellEveryone(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    Send broadcast to all connected clients
    private void tellEveryone(String message) {
        System.out.println("sending a broadcast");
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
