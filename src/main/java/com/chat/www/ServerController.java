package com.chat.www;

import java.io.IOException;
import java.util.Hashtable;

public class ServerController {
    private int PORT;
    private static Hashtable<String, Room> roomList = new Hashtable<>();    // 멀티 스레드 동기화
    private CustomServerSocket serverSocket;

    public ServerController(){
        PORT = 8888;

        try {
            serverSocket = new CustomServerSocket(PORT);
            System.out.println("서버가 동작중입니다. ip: " + serverSocket.getInetAddress().getHostAddress() + " port: " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void addRoom(ChatRoom room) {
        roomList.put(room.getRoomId(), room);
    }

    public static synchronized void deleteRoom(ChatRoom room) {
        roomList.remove(room.getRoomId());
    }

    public static synchronized Room getRoom(String roomId) {
        Room room = roomList.get(roomId);
        return room;
    }

    public static synchronized Hashtable<String, Room> getRoomList() {
        return roomList;
    }

    public void start() throws IOException {
        ClientSocketHandler sr;
        Thread t;

        while(true) {
            UserSocket socket = (UserSocket) serverSocket.accept();
            if( socket != null ) { //클라이언트 소켓과 연결시
                System.out.println(socket + " 클라이언트가 연결되었습니다.");
                sr = new ClientSocketHandler(socket); //채팅 스레드를 생성합니다.
                t = new Thread(sr); //채팅스레드를 시작합니다.
                t.start();
            }
        }
    }

    public CustomServerSocket getServerSocket() {
        return serverSocket;
    }
}
