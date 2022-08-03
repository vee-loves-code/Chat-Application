package com.veethebest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MyServer {
    private ServerSocket serverSocket;
    public static ArrayList<MyClientHandler> client = new ArrayList<>();

    public MyServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public  void startServer(){
        try {
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                MyClientHandler clientHandler = new MyClientHandler(socket);
                client.add(clientHandler);
                System.out.println(clientHandler.getClientUserName() + " has connected");

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
        } finally {
            closeServerSocket();
        }
    }

    public void closeServerSocket(){
        try {
            if (serverSocket != null){
                serverSocket.close();
                System.out.println("closed");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(3333);
        MyServer server = new MyServer(serverSocket);
        server.startServer();
    }
}
