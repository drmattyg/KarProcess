package com.drmattyg.nanokaraoke;

import java.util.Arrays;

public class HeaderChunk {
	
	byte[] bytes;
	int length;
	int format;
	int numTracks;
	int division;

	public static HeaderChunk getInstance(MidiFile mf) { return new HeaderChunk(mf); }
	private HeaderChunk(MidiFile mf) {
		bytes = Arrays.copyOfRange(mf.getBytes(), 0, 14);
		assert(new String(Arrays.copyOfRange(bytes, 0, 4)).equals("MThd"));
		length = Utils.toInt(bytes, 4, 4);
		format = Utils.toInt(bytes, 2, 8);
		numTracks = Utils.toInt(bytes, 2, 10);
		division = Utils.toInt(bytes, 2, 12);
	}
	public int getTotalLength() { return length + 8; } // this should always return 14
}
