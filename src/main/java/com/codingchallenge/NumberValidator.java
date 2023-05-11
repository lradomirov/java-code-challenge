package com.codingchallenge;

import java.util.Set;

public class NumberValidator {

    private static final int REQUIRED_LENGTH = 9;
    private static final Set<Character> VALID_INPUTS = Set.of('1', '2', '3', '4', '5', '6', '7', '8', '9', '0');

    public static boolean isValid(String input) {
        if (input == null || input.length() != REQUIRED_LENGTH) {
            return false;
        }

        for (char c : input.toCharArray()) {
            if (!VALID_INPUTS.contains(c)) {
                return false;
            }
        }

        return true;
    }

}
