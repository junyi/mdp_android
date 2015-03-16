package com.example.mdptool;

public class Config {
    public static final int DISCRIPTOR_DISTANCE_BITLENGTH = 4;
    public static final String AUDRINO_CODE = "h"; // "h" refers to Arduino
    public static final String START_EXPLORE = "h*\n";
    public static final String PC_coord = "pCoordinate\n";
    public static final String PC_EXPLORE = "pExplore\n";


    //DISTANCE
    public static final String DISTANCE = "0001";

    //MOVEMENT
    public final static String MOVEMENT_GO_BACK = "00";
    public final static String MOVEMENT_GO_RIGHT = "01";
    public final static String MOVEMENT_GO_LEFT = "10";
    public final static String MOVEMENT_GO_STRAIGHT = "11";

    //MODE
    public final static String MODE_STOP = "00";
    public final static String MODE_EXPLORE = "01";
    public final static String MODE_SHORTESTPATH = "10";
    public final static String MODE_CALIBRATE = "11";

    public static final String UP = MODE_EXPLORE + MOVEMENT_GO_STRAIGHT + DISTANCE;
    public static final String DOWN = MODE_EXPLORE + MOVEMENT_GO_BACK + DISTANCE;
    public static final String LEFT = MODE_EXPLORE + MOVEMENT_GO_LEFT + DISTANCE;
    public static final String RIGHT = MODE_EXPLORE + MOVEMENT_GO_RIGHT + DISTANCE;
}
