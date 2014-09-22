package com.example.mdptool;

public class MapDescriptor {

	public static final int UNEXPLORED = 0;
	public static final int WALKABLE = 1;
	public static final int OBSTACLE = 2;

	
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

		for(int x = map[0].length-1; x>=0; x--) {
			for(int y = map.length-1; y>=0; y--) {

				switch(map[y][x]) {
					case UNEXPLORED:
						encodedMapInBinary += "0";
						break;
					default:
						encodedMapInBinary += "1";
				}
			}
		}

		encodedMapInBinary += "11";

		for(int i=0; i<encodedMapInBinary.length(); i+=4) {
			encodedMap += convertBinaryToHex(encodedMapInBinary.substring(i, i+4));
		}

		return encodedMap;
	}

	public static String encodeObstacle(int[][] map) {

		String encodedMapInBinary = "";
		String encodedMap = "";

		for(int x = map[0].length-1; x>=0; x--) {
			for(int y = map.length-1; y>=0; y--) {

				switch(map[y][x]) {
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
		if((encodedMapInBinary.length() % 8) > 0) {
			String zeros_padding = "00000000";
			int padding_length = ((int)(encodedMapInBinary.length() / 8) + 1 ) * 8 - encodedMapInBinary.length();
			
			encodedMapInBinary += zeros_padding.substring(0, padding_length);
		}

		for(int i=0; i<encodedMapInBinary.length(); i+=4) {
			encodedMap += convertBinaryToHex(encodedMapInBinary.substring(i, i+4));
		}

		return encodedMap;
	}

//	public static int[][] decode(String encodedMap) {
//		String explorationState = "FFC07F80FF01FE03FFFFFFF3FFE7FFCFFF9C7F38FE71FCE3F87FF0FFE1FFC3FF87FF0E0E1C1F";
//		String obstacleState = "00000100001C80000000001C0000080000060001C00000080000";
//
//		return 
//	}

	private static String convertBinaryToHex(String binary) {
		String hex = "";

		for(int i=0; i<binary.length(); i+= 4) {

			int b3 = Integer.parseInt(binary.charAt(i) + "");
			int b2 = Integer.parseInt(binary.charAt(i+1) + "");
			int b1 = Integer.parseInt(binary.charAt(i+2) + "");
			int b0 = Integer.parseInt(binary.charAt(i+3) + "");

			int decimalValue = b3 * 8 + b2 * 4 + b1 * 2 + b0;

			hex += convertDecimalToHex(decimalValue);
		}

		return hex;
	}

	private static String convertDecimalToHex(int decimalValue) {
		String hexString = new String();

		if(decimalValue > 9) {
			switch(decimalValue) {
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

//	private static String convertHexToDecimal(String hex) {
//		
//	}


}