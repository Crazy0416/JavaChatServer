package com.chat.www;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class BroadCastMessage implements Runnable {

    private Hashtable<String, Socket> userList;
    private String message;

    public BroadCastMessage(Hashtable<String, Socket> userList, String message) {
        this.userList = userList;
        this.message = message;
    }

    @Override
    public void run() {
        Set<String> keys = this.userList.keySet();
        String str;

        Iterator<String> itr = keys.iterator();

        while (itr.hasNext()) {
            str = itr.next();
            PrintWriter pw;

            try { // TODO: 한명에게 오류가 발생했을 때 어떻게 오류처리 할 것인지 생각하기.
                Socket userSocket = this.userList.get(str);
                pw = new PrintWriter(new OutputStreamWriter(userSocket.getOutputStream()));

                pw.println(message);
                pw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("메세지 전송 완료");
    }
}
