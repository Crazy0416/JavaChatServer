package com.chat.www;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class ServerSocketThread implements Runnable{

    Socket socket;
    BufferedReader ois;
    PrintWriter oos;
    ChatRoom room;
    String uid;

    public ServerSocketThread(Socket socket) {
        this.socket = socket;
        try {

            ois = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            oos = new PrintWriter(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String receiveData;

        try
        {
            while( (receiveData = ois.readLine()) != null ) {
                if(!isJSONValid(receiveData))
                    continue;

                JsonObject jsonObject = new Gson().fromJson(receiveData, JsonObject.class);
                String command = null;
                if(jsonObject.has("command"))
                    command = jsonObject.get("command").getAsString();

                System.out.println(socket.toString() + " json: " + receiveData );

                if (command == null) {

                    if(room != null) {
                        if(jsonObject.has("message")) {
                            String message = jsonObject.get("message").getAsString();
                            room.broadCastMessage(message);
                        }
                    }

                } else if( command.equals( "@quit" ) ) {
                    if(room.getUserCount() == 1 )
                        ServerController.deleteRoom(this.room);
                    else
                        room.removeUser(uid);

                    this.room = null;
                } else if(command.contains("@join")) {
                    if(jsonObject.has("roomId")) {
                        ChatRoom room = (ChatRoom) ServerController.getRoom(jsonObject.get("roomId").getAsString());

                        if(jsonObject.has("uid")) {
                            String uid  = jsonObject.get("uid").getAsString();
                            room.addUser(uid, socket);

                            this.uid = uid;
                            this.room = room;
                        }
                    }

                } else if(command.contains("@create")) {
                    String title        = jsonObject.get("title").getAsString();
                    String roomInfo     = jsonObject.get("roomInfo").getAsString();
                    String managerId    = jsonObject.get("managerId").getAsString();
                    String managerName  = jsonObject.get("managerName").getAsString();

                    ChatRoom chatRoom = new ChatRoom(title, roomInfo, managerId, managerName, socket);
                    this.room = chatRoom;

                    ServerController.addRoom(chatRoom);
                } else if(command.contains("@list")) {
                    Hashtable<String, Room> roomList = ServerController.getRoomList();
                    Set<String> keys = roomList.keySet();

                    //Obtaining iterator over set entries
                    Iterator<String> itr = keys.iterator();
                    String roomId;

                    while(itr.hasNext()) {
                        roomId = itr.next();
                        ChatRoom room = (ChatRoom) roomList.get(roomId);
                        oos.println("RoomId: " + roomId + " || Room title: " +  room.getTitle());
                    }

                    oos.flush();
                }
            }
        }
        catch (Exception e ) {
            e.printStackTrace();
        }
        finally {
            try {
                if( socket != null && !socket.isClosed() ) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public boolean isJSONValid(String test) {
        try {
            new Gson().fromJson(test, JsonObject.class);
        } catch (JsonSyntaxException ex) {
            return false;
        }
        return true;
    }
}
