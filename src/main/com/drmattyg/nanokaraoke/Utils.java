package com.drmattyg.nanokaraoke;

import java.util.Arrays;

public class Utils {

	public static int toInt(byte[] b, int offset) {
		return (b[offset]<<24)&0xff000000|
	       (b[offset+1]<<16)&0x00ff0000|
	       (b[offset+2]<< 8)&0x0000ff00|
	       (b[offset+3]<< 0)&0x000000ff;
	}
	
	public static int toInt(byte[] b, int length, int offset) {
		int val = 0;
		int mask = 0xff;
		int shiftVal = 0;
		for(int i = length - 1; i >= 0; i--) {
			val |= (b[offset + i] << shiftVal)&mask;
			mask = mask << 4;
			shiftVal = shiftVal + 8;
		}
		return val;
	}
	
	public static String byteToString(byte[] b, int size, int offset) {
		return new String(Arrays.copyOfRange(b, offset, offset + size));
	}

}
