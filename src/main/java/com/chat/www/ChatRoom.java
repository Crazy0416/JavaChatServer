package com.chat.www;

import java.net.Socket;
import java.util.*;

public class ChatRoom implements Room {
    public static int roomCount = 0;

    private String roomId;      // 채팅방 primary key
    private String title;       // 채팅방 제목
    private String roomInfo;    // 채팅방 정보
    private String managerId;   // 방장 유저 id(uid)
    private String managerName; // 방장 이름
    private Hashtable<String, UserSocket> userList; // 참가한 유저 collection

    public ChatRoom(String title, String roomInfo, String managerId, String managerName, UserSocket client) {
        this.roomId = new Date().getTime() + "||" + String.valueOf(ChatRoom.roomCount);
        this.title = title;
        this.roomInfo = roomInfo;
        this.managerId = managerId;
        this.managerName = managerName;
        userList = new Hashtable<String, UserSocket>();
        // 유저 리스트에 매니저 추가.
        userList.put(managerId, client);
    }

    @Override
    public void broadCastMessage(String message) throws InterruptedException{
        Runnable bcm = new BroadCastMessage(this.userList, message);
        Thread thread = new Thread(bcm);

        thread.start();
        thread.join();
    }

    @Override
    public int getUserCount() {
        return this.userList.size();
    }

    @Override
    public void addUser(String uid, UserSocket socket){
        try {
            this.userList.put(uid, socket);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void removeUser(String uid) {
        try {
            this.userList.remove(uid);
        } catch (NullPointerException ne) {

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        if(this.userList.size() == 0)
            ServerController.deleteRoom(this);
    }

    @Override
    public String toString() {
        return "ChatRoom[roomId=" + roomId + ",title=" + title + "]";
    }

    public static int getRoomCount() {
        return roomCount;
    }

    public static void setRoomCount(int roomCount) {
        ChatRoom.roomCount = roomCount;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(String roomInfo) {
        this.roomInfo = roomInfo;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
}
