package com.codingchallenge;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class ApplicationDisconnectTest {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 4000)) {
            try (PrintStream out = new PrintStream(new BufferedOutputStream(socket.getOutputStream()), true)) {
                for (int j = 0; j < 100_000_000; j++) {
                    if (j == 3_000_000) {
                        out.println("0030000"); // invalid input, only 7 digits... the same will occur for length != 9 and any non-digit.
                    } else if (j == 4_000_000) {
                        break; // perform some additional writes to the ServerSocket to verify these values are not getting written.
                    } else {
                        out.println(StringUtils.leftPad(String.valueOf(j), 9, "0"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        try (Socket socket = new Socket("localhost", 4000)) {
            try (PrintStream out = new PrintStream(new BufferedOutputStream(socket.getOutputStream()), true)) {
                out.println("terminate");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
