package com.veethebest;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MyClientHandler implements  Runnable{
    public static ArrayList<MyClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;

    public String getClientUserName() {
        return clientUserName;
    }
    public MyClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = bufferedReader.readLine();

            clientHandlers.add(this);
            broadcastMessage( clientUserName + " has entered the chat!");
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }


    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
                if(messageFromClient == null) throw new IOException();
                broadcastMessage(messageFromClient);
            } catch (IOException e){
                closeEverything(socket, bufferedWriter, bufferedReader);
                break;
            }
        }

    }
    public void broadcastMessage(String messageToSend){
        for (MyClientHandler clientHandler : clientHandlers){
            try{
                if (!clientHandler.clientUserName.equals(clientUserName)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }
    }
    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage( clientUserName + " has left the chat!");
    }
    public  void  closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        removeClientHandler();
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
}
