package com.chat.www;

import java.io.*;
import java.net.Socket;
import java.net.SocketImpl;

public class UserSocket extends Socket {
    private String uid;
    private String name;
    private PrintWriter pw;
    private BufferedReader br;

    public UserSocket(SocketImpl socket) throws IOException {
        super(socket);
    }

    public void setIOStream() throws IOException{
        br = new BufferedReader(new InputStreamReader(this.getInputStream()));
        pw = new PrintWriter(new OutputStreamWriter(this.getOutputStream()));
    }

    public PrintWriter getPrintWriter() {
        return pw;
    }

    public BufferedReader getBufferedReader() {
        return br;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
