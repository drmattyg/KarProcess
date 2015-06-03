package com.drmattyg.nanokaraoke;

import java.util.Iterator;

public class TrackChunk implements Iterable<TrackEvent> {


	public MidiFile getParent() {
		return parent;
	}

	public void setParent(MidiFile parent) {
		this.parent = parent;
	}

	MidiFile parent;
	int length;
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

	private int iteratorOffset;
	int lastStatusByte = 0;
	private int getLastStatusByte() { return lastStatusByte; }
	private void setLastStatusByte(int b) { lastStatusByte = b; }

	@Override
	public Iterator<TrackEvent> iterator() {
		iteratorOffset = offset + 8;

		final MidiFile mf = parent;
		final TrackChunk tc = this;
		return new Iterator<TrackEvent>() {

			@Override
			public boolean hasNext() {
				return iteratorOffset < length && !TrackChunk.isTrackChunk(mf, iteratorOffset); // if this is the next track chunk, return false
			}

			@Override
			public TrackEvent next() {
				TrackEvent evt = TrackEvent.getInstance(tc, iteratorOffset);

				if(evt.hasStatusbyte()) {
					setLastStatusByte(evt.getStatusByte());
				} else {
					evt.setStatusByte(getLastStatusByte() & 0xff);
				}
				iteratorOffset += evt.getTotalLength();
				return evt;
			}

			@Override
			public void remove() {
				// unimplemented
				
			}
			
		};
	}

}





