package com.chat.www;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        ServerController process = new ServerController();
        try {
            process.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
