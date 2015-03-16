package com.example.mdptool;

import android.util.Log;

import java.util.Arrays;

public class MapDescriptor {

    public static final int UNEXPLORED = 0;
    public static final int WALKABLE = 1;
    public static final int OBSTACLE = 2;
    int[][] fakeMap = new int[15][20];
    int[] fakeRobotPos = new int[3];
    int[][] trueMap = new int[15][20];
    int[] trueRobotPos = new int[3];

    public static void printMapDescriptor(int[][] map) {
        System.out.println("Explored/Unexplored state");
        System.out.println("=========================");
        System.out.println(encodeExploredUnexplored(map));

        System.out.println();

        System.out.println("Obstacle state");
        System.out.println("==============");
        System.out.println(encodeObstacle(map));
    }

    public static String encodeExploredUnexplored(int[][] map) {

        String encodedMapInBinary = "";
        String encodedMap = "";

        encodedMapInBinary += "11";

        for (int x = map[0].length - 1; x >= 0; x--) {
            for (int y = map.length - 1; y >= 0; y--) {

                switch (map[y][x]) {
                    case UNEXPLORED:
                        encodedMapInBinary += "0";
                        break;
                    default:
                        encodedMapInBinary += "1";
                }
            }
        }

        encodedMapInBinary += "11";

        for (int i = 0; i < encodedMapInBinary.length(); i += 4) {
            encodedMap += convertBinaryToHex(encodedMapInBinary.substring(i, i + 4));
        }

        return encodedMap;
    }

    public static String encodeObstacle(int[][] map) {

        String encodedMapInBinary = "";
        String encodedMap = "";

        for (int x = map[0].length - 1; x >= 0; x--) {
            for (int y = map.length - 1; y >= 0; y--) {

                switch (map[y][x]) {
                    case OBSTACLE:
                        encodedMapInBinary += "1";
                        break;
                    case UNEXPLORED:
                        continue;
                    default:
                        encodedMapInBinary += "0";
                }
            }
        }

        // Padding zero at the end of the stream to make it full byte lengths
        if ((encodedMapInBinary.length() % 8) > 0) {
            String zeros_padding = "00000000";
            int padding_length = ((int) (encodedMapInBinary.length() / 8) + 1) * 8 - encodedMapInBinary.length();

            encodedMapInBinary += zeros_padding.substring(0, padding_length);
        }

        for (int i = 0; i < encodedMapInBinary.length(); i += 4) {
            encodedMap += convertBinaryToHex(encodedMapInBinary.substring(i, i + 4));
        }

        return encodedMap;
    }

    public void decode(String receivedString, boolean check) {
        //String explorationState = "FFC07F80FF01FE03FFFFFFF3FFE7FFCFFF9C7F38FE71FCE3F87FF0FFE1FFC3FF87FF0E0E1C1F";
        //String obstacleState = "00000100001C80000000001C0000080000060001C00000080000";
        Log.d("exploration", receivedString);
        String exploration = convertHexToBinary(receivedString.substring(0, 76));
        Log.d("exploration", exploration + " " + exploration.length());
        String obstacle = "";
        if (receivedString.length() > 81) {
            obstacle = convertHexToBinary(receivedString.substring(76, (receivedString.length())));
        }
        int explorationPosition = 2;
        int obstaclePosition = 0;
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 15; x++) {
                if ((exploration.charAt(explorationPosition)) == '0') {
                    fakeMap[x][y] = 0;
                    explorationPosition++;
                } else {
                    if ((obstacle.charAt(obstaclePosition) == '0')) {
                        fakeMap[x][y] = 1;
                        obstaclePosition++;
                        explorationPosition++;
                    } else {
                        fakeMap[x][y] = 2;
                        obstaclePosition++;
                        explorationPosition++;
                    }
                }

            }
        }
        int index = 0;
        if (receivedString.contains("WEST")) {
            fakeRobotPos[2] = 1;
            index = receivedString.indexOf("WEST");
        } else if (receivedString.contains("NORTH")) {
            fakeRobotPos[2] = 2;
            index = receivedString.indexOf("NORTH");
        } else if (receivedString.contains("EAST")) {
            fakeRobotPos[2] = 3;
            index = receivedString.indexOf("EAST");
        } else if (receivedString.contains("SOUTH")) {
            fakeRobotPos[2] = 4;
            index = receivedString.indexOf("SOUTH");
        }

        fakeRobotPos[0] = Integer.parseInt(receivedString.substring(index - 4, index - 2));
        fakeRobotPos[1] = Integer.parseInt(receivedString.substring(index - 2, index));
        Log.d("robotpos", "" + fakeRobotPos[0] + " " + fakeRobotPos[1]);

        if (check) {
            updateValue();
        }
    }

    private static String convertBinaryToHex(String binary) {
        String hex = "";

        for (int i = 0; i < binary.length(); i += 4) {

            int b3 = Integer.parseInt(binary.charAt(i) + "");
            int b2 = Integer.parseInt(binary.charAt(i + 1) + "");
            int b1 = Integer.parseInt(binary.charAt(i + 2) + "");
            int b0 = Integer.parseInt(binary.charAt(i + 3) + "");

            int decimalValue = b3 * 8 + b2 * 4 + b1 * 2 + b0;

            hex += convertDecimalToHex(decimalValue);
        }

        return hex;
    }

    private static String convertDecimalToHex(int decimalValue) {
        String hexString = new String();

        if (decimalValue > 9) {
            switch (decimalValue) {
                case 10:
                    hexString = "A";
                    break;
                case 11:
                    hexString = "B";
                    break;
                case 12:
                    hexString = "C";
                    break;
                case 13:
                    hexString = "D";
                    break;
                case 14:
                    hexString = "E";
                    break;
                case 15:
                    hexString = "F";
                    break;
            }
        } else {
            hexString = decimalValue + "";
        }

        return hexString;
    }

    private String convertHexToBinary(String hex) {
        String binary = "";
        for (int i = 0; i < hex.length(); i++) {
            switch (hex.charAt(i)) {

                case '0':
                    binary = binary + "0000";
                    break;
                case '1':
                    binary = binary + "0001";
                    break;
                case '2':
                    binary = binary + "0010";
                    break;
                case '3':
                    binary = binary + "0011";
                    break;
                case '4':
                    binary = binary + "0100";
                    break;
                case '5':
                    binary = binary + "0101";
                    break;
                case '6':
                    binary = binary + "0110";
                    break;
                case '7':
                    binary = binary + "0111";
                    break;
                case '8':
                    binary = binary + "1000";
                    break;
                case '9':
                    binary = binary + "1001";
                    break;
                case 'a':
                    binary = binary + "1010";
                    break;
                case 'b':
                    binary = binary + "1011";
                    break;
                case 'c':
                    binary = binary + "1100";
                    break;
                case 'd':
                    binary = binary + "1101";
                    break;
                case 'e':
                    binary = binary + "1110";
                    break;
                case 'f':
                    binary = binary + "1111";
                    break;
                default:
                    binary = binary + "1111";
                    break;
            }
        }
        return binary;
    }

    public int[][] getMap() {
        return trueMap;
    }

    public int[] getRobotPos() {
        return trueRobotPos;
    }

    public void updateValue() {
        trueRobotPos = Arrays.copyOf(fakeRobotPos, fakeRobotPos.length);
        for (int i = 0; i < trueMap.length; i++)
            trueMap[i] = Arrays.copyOf(fakeMap[i], fakeMap[i].length);
    }


}