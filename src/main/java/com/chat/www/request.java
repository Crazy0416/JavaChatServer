package com.chat.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class request implements Runnable{

    private Socket m_socket;

    @Override
    public void run() {
        try {
            BufferedReader tmpBuf = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));

            String receiveMsg;

            while(true) {
                receiveMsg = tmpBuf.readLine();

                if(receiveMsg == null) {
                    System.out.println("상대방과 연결이 끊겼습니다.");
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
