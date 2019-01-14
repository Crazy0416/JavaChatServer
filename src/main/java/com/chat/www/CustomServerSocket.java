package com.chat.www;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;

public class CustomServerSocket extends ServerSocket {
    public CustomServerSocket() throws IOException {
        super();
    }

    public CustomServerSocket(int PORT) throws IOException {
        super(PORT);
    }

    @Override
    public Socket accept() throws IOException{
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isBound())
            throw new SocketException("Socket is not bound yet");
        final Socket s = new UserSocket((SocketImpl) null);
        implAccept(s);
        ((UserSocket) s).setIOStream();
        return s;
    }
}
