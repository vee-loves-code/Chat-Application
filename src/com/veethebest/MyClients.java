package com.veethebest;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MyClients {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String myUsername;

    public MyClients(Socket socket, String myUsername) {
        try {
            this.socket = socket;
            this.myUsername = myUsername;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch(IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public  void sendMessage(){
        try {
            bufferedWriter.write(myUsername);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(myUsername +": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }
    public void listenForMessage(){
        new Thread(new Runnable() {
            public void run() {
                String msgFromGroupChat = " ";
                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedWriter, bufferedReader);
                    }
                }
            }
        }).start();
    }
    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        try{
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter you Username: ");

        String myUsername = scanner.nextLine();
        Socket socket = new Socket("localhost", 3333);
        MyClients client = new MyClients(socket, myUsername);
        client.listenForMessage();
        client.sendMessage();

    }
}
