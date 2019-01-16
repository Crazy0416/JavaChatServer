package com.chat.www;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ChatRoomTest {

    private ChatRoom room;
    private UserSocket manager;
    private UserSocket user1;
    private String roomId;

    public void createRoomUserManager() {
        try {

            manager = new UserSocket();
            user1 = new UserSocket();
            room = new ChatRoom("testTitle", "test room info", "mid", "mname", manager);
            roomId = room.getRoomId();

        } catch (IOException e) {
            e.printStackTrace();
            fail("ChatRoom 생성에 문제가 발생했습니다.");
        }
    }

    @Test
    public void broadCastMessageTest() {
        // test info
        System.out.println("broadCastMessage: 채팅방에 있는 모든 유저들에게 메세지 전달이 되는 지 확인");

        ChatRoom chatRoom = null;
        File managerFile = new File("manager_message.txt");
        File user1File = new File("user1_message.txt");
        UserSocket manager = null;
        UserSocket user1 = null;

        try {
            if(managerFile.exists())
                managerFile.delete();
            if(user1File.exists())
                user1File.delete();

            managerFile.createNewFile();
            user1File.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
            fail("temporary file create fail");
        }

        try {
            manager = new UserSocket();
            manager.setBr(new BufferedReader(new FileReader("manager_message.txt")));
            manager.setPw(new PrintWriter(new FileWriter("manager_message.txt")));
            chatRoom = new ChatRoom("testTitle", "test info", "mid", "mname", manager);

            user1 = new UserSocket();
            user1.setBr(new BufferedReader(new FileReader("user1_message.txt")));
            user1.setPw(new PrintWriter(new FileWriter("user1_message.txt")));

            chatRoom.addUser("testUid", user1);
        } catch (IOException e) {
            e.printStackTrace();
            fail("chatroom 생성 오류");
        }

        try {
            chatRoom.broadCastMessage("message test");
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("broadcasting 오류");
        }

        String msg = null;

        try {
            msg = user1.getBufferedReader().readLine();
        } catch (IOException e) {
            e.printStackTrace();
            fail("file 읽기 오류");
        }
        assertEquals("message test", msg);

        try {
            msg = manager.getBufferedReader().readLine();
        } catch (IOException e) {
            e.printStackTrace();
            fail("file 읽기 오류");
        }
        assertEquals("message test", msg);

        managerFile.delete();
        user1File.delete();
    }

    @Test
    public void removeUserTest() {
        // test info
        System.out.println("removeUser: 채팅방의 유저가 삭제되는 지 본 후 유저가 0명일 때 방이 사라지는 것도 체크");

        ServerController serverController = new ServerController();

        createRoomUserManager();

        serverController.addRoom(room);
        room.addUser("user1", user1);

        room.removeUser("mid");
        assertEquals(room.getUserCount(), 1);

        room.removeUser("user1");
        assertEquals(ServerController.getRoom(roomId), null);

        try {
            serverController.getServerSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addUserTest() {
        // test info
        System.out.println("addUser: 채팅방에 유저가 추가되는 지 확인");

        createRoomUserManager();
        assertEquals(room.getUserCount(), 1);
        room.addUser("user1", user1);
        assertEquals(room.getUserCount(), 2);

    }
}
