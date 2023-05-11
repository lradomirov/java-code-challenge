package com.codingchallenge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

public class Worker implements Runnable {

    private static final String TERMINATE = "terminate";

    private final ServerSocket serverSocket;
    private final IntSet intSet;

    public Worker(ServerSocket serverSocket, IntSet intSet) {
        this.serverSocket = serverSocket;
        this.intSet = intSet;
    }

    @Override
    public void run() {
        try (var socket = this.serverSocket.accept()) {
            System.out.println("worker accepted client connection");
            try (var br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String input;
                while ((input = br.readLine()) != null) {
                    if (TERMINATE.equalsIgnoreCase(input)) {
                        System.exit(0);
                    }

                    if (!NumberValidator.isValid(input)) {
                        return;
                    }

                    int val = Integer.parseInt(input);
                    intSet.add(val);
                }
            }
            System.out.println("worker closed client connection");
        } catch (IOException e) {
            System.out.println("socket exception: " + e);
        }
    }

}
