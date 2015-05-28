package com.drmattyg.nanokaraoke;

import java.util.Iterator;

public class TrackChunk implements Iterator<TrackEvent> {


	public MidiFile getParent() {
		return parent;
	}

	public void setParent(MidiFile parent) {
		this.parent = parent;
	}

	MidiFile parent;
	int length;
//	VarLength time;
//	EventType eventType;
//	int metaType;
	int offset;
	

	public static boolean isTrackChunk(MidiFile mf, int offset) {
		return Utils.byteToString(mf.getBytes(), 4, offset).equals("MTrk");
	}

	protected TrackChunk() {}
	
	public static TrackChunk getInstance(MidiFile mf, int offset) {
		
		if(!TrackChunk.isTrackChunk(mf, offset)) {
			throw new IllegalArgumentException("Not a track chunk");
		}
		int myOffset = offset;
		TrackChunk tc = new TrackChunk();
		tc.parent = mf;
		tc.offset = offset;
		byte[] b = mf.getBytes();

		myOffset += 4;
		tc.length = Utils.toInt(b, myOffset);

		return tc;
	}
	
	public int getTotalLength() { return length + 8; }

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TrackEvent next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
	
}





