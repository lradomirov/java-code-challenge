package com.codingchallenge;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ApplicationLoadTest {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // run 10 clients concurrently
        for (int i = 0; i < 10; i ++) {
            executorService.execute(() -> {
                try (Socket socket = new Socket("localhost", 4000)) {
                    try (PrintStream out = new PrintStream(new BufferedOutputStream(socket.getOutputStream()), true)) {
                        for (int j = 0; j < 100_000_000; j++) {
                            out.println(StringUtils.leftPad(String.valueOf(j), 9, "0"));
                            if (j == 5_000_000) { // client should disconnect after some time to cycle through next set of clients.
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

        try (Socket socket = new Socket("localhost", 4000)) {
            try (PrintStream out = new PrintStream(new BufferedOutputStream(socket.getOutputStream()), true)) {
                out.println("terminate");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
