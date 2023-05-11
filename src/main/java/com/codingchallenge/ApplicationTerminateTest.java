package com.codingchallenge;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ApplicationTerminateTest {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.execute(() -> {
            try (Socket socket = new Socket("localhost", 4000)) {
                try (PrintStream out = new PrintStream(new BufferedOutputStream(socket.getOutputStream()), true)) {
                    for (int j = 0; j < 100_000_000; j++) {
                        out.println(StringUtils.leftPad(String.valueOf(j), 9, "0"));
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        });

        executorService.execute(() -> {
            try (Socket socket = new Socket("localhost", 4000)) {
                try (PrintStream out = new PrintStream(new BufferedOutputStream(socket.getOutputStream()), true)) {
                    for (int j = 0; j < 100_000_000; j++) {
                        out.println(StringUtils.leftPad(String.valueOf(j), 9, "0"));
                        if (j == 5_000_000) {
                            out.println("terminate");
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        });

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

}
