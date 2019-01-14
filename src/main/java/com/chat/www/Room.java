package com.chat.www;

import java.net.Socket;

public interface Room {

    // 방에 속한 모든 유저에게 발생한 메세지 전송
    public void broadCastMessage(String message) throws InterruptedException;

    // 방에 속한 유저의 수 리턴
    public int getUserCount();

    // 유저 소켓 방에 추가 후 성공여부 리턴
    public void addUser(String uid, UserSocket socket);

    // 유저 소켓 방에서 삭제 후 성공여부 리턴
    public void removeUser(String uid);
}
