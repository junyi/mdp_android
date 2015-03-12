package com.example.mdptool;

public class Helper {
    public static String convert(String code) {
        String asciiCode = Config.AUDRINO_CODE;
        char nextChar;
        nextChar = (char) Integer.parseInt(code.substring(0, 4), 2);
        asciiCode += nextChar;
        if (code.length()>4) {
            nextChar = (char) Integer.parseInt(code.substring(4, 8), 2);
            asciiCode += nextChar;
        }
        return asciiCode;
    }
}
