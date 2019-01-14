package com.chat.www;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class ClientSocketHandler implements Runnable{

    private UserSocket userSocket;
    private BufferedReader uSocketBR;
    private PrintWriter uSocketPW;
    private ChatRoom room;

    public ClientSocketHandler(UserSocket userSocket) {
        this.userSocket = userSocket;
        uSocketBR = userSocket.getBufferedReader();
        uSocketPW = userSocket.getPrintWriter();
    }

    @Override
    public void run() {
        String receiveData;

        // TODO: 클라이언트의 비정상적인 종료에 대한 대처 방안 생각. (graceful 종료?, heartbeat 체크?)
        // TODO: 각 커맨드에 대해 함수로 작성하여 관리하기 쉽게 만들기
        // TODO: 각 커맨드를 if else 문으로 검사하지 않고 해쉬를 사용할 수 있는 지 알아보기.
        try
        {
            while( (receiveData = uSocketBR.readLine()) != null ) {
                if(!isJSONValid(receiveData))   // json validation check
                    continue;

                JsonObject jsonObject = new Gson().fromJson(receiveData, JsonObject.class);
                String command = null;
                if(jsonObject.has("command"))
                    command = jsonObject.get("command").getAsString();

                System.out.println(userSocket.toString() + " json: " + receiveData );

                if (command == null) {

                    if(room != null) {
                        if(jsonObject.has("message")) {
                            String message = jsonObject.get("message").getAsString();
                            room.broadCastMessage(message);
                        }
                    }

                } else if( command.equals( "@quit" ) ) {
                    if(room == null)
                        break;

                    room.removeUser(userSocket.getUid());

                    this.room = null;
                } else if(command.contains("@join")) {
                    if(jsonObject.has("roomId") && jsonObject.has("uid") && jsonObject.has("name") ) {
                        ChatRoom room = (ChatRoom) ServerController.getRoom(jsonObject.get("roomId").getAsString());

                        this.userSocket.setUid(jsonObject.get("uid").getAsString());
                        this.userSocket.setName(jsonObject.get("name").getAsString());
                        this.room = room;
                        room.addUser(this.userSocket.getUid(), userSocket);
                    } else {
                        // TODO: 필드가 제대로 오지 않는다면 클라이언트에게 오류 발생.
                    }
                } else if(command.contains("@create")) {
                    String title        = jsonObject.get("title").getAsString();
                    String roomInfo     = jsonObject.get("roomInfo").getAsString();
                    String managerId    = jsonObject.get("managerId").getAsString();
                    String managerName  = jsonObject.get("managerName").getAsString();

                    this.userSocket.setUid(jsonObject.get("managerId").getAsString());
                    this.userSocket.setName(jsonObject.get("managerName").getAsString());

                    ChatRoom chatRoom = new ChatRoom(title, roomInfo, managerId, managerName, userSocket);
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
                        uSocketPW.println("RoomId: " + roomId + " || Room title: " +  room.getTitle());
                    }

                    uSocketPW.flush();
                }
            }
        }
        catch (Exception e ) {
            e.printStackTrace();
        }
        finally {
            try {
                String clientIp = userSocket.toString();

                // TODO: 아무리 봐도 유저 지울때마다 체크하는게 오바인듯
                if(room != null)
                    room.removeUser(userSocket.getUid());

                if( userSocket != null && !userSocket.isClosed() ) {
                    userSocket.close();
                }
                System.out.println(clientIp + " 클라이언트가 종료되었습니다.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // jsonStr이 json 문자열인지 확인하는 메서드.
    public boolean isJSONValid(String jsonStr) {
        try {
            new Gson().fromJson(jsonStr, JsonObject.class);
        } catch (JsonSyntaxException ex) {
            return false;
        }
        return true;
    }
}
