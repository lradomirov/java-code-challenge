package com.codingchallenge;

import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.*;

public class Application implements Runnable {

    private static final int MAX_CAPACITY = 1_000_000_000;

    public static void main(String[] args) {
        new Application().run();
    }

    @Override
    public void run() {
        System.out.println("starting up server...");

        Writer logWriter;
        try {
            logWriter = new FileWriter("numbers.log");
        } catch (IOException e) {
            System.out.println("failed to open numbers.logs: " + e);
            throw new RuntimeException(e);
        }

        IntSet intSet = new BooleanIntSet(MAX_CAPACITY, logWriter);

        StatsReporter statsReporter = new StatsReporter(intSet);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> System.out.println(statsReporter.getStatsOutput()), 10, 10, TimeUnit.SECONDS);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(4), new ThreadPoolExecutor.CallerRunsPolicy());

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(4000);
        } catch (IOException e) {
            System.out.println("failed to open server socket: " + e);
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("shutting server down...");
            System.out.println("emptying numbers.log buffer");
            try {
                logWriter.close();
                serverSocket.close();
            } catch (IOException e) {
                System.out.println(e);
            }
            System.out.println("successfully emptied numbers.log buffer");
        }));

        System.out.println("server is now running on port 4000");
        while (!serverSocket.isClosed()) {
            executor.submit(new Worker(serverSocket, intSet));
        }
        System.out.println("server is shutdown");
    }
}
