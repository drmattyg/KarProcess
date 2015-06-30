package com.drmattyg.nanokaraoke;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	protected TrackChunk() {
	}

	public static TrackChunk getInstance(MidiFile mf, int offset) {

		if (!TrackChunk.isTrackChunk(mf, offset)) {
			throw new IllegalArgumentException("Not a track chunk");
		}
		int myOffset = offset;
		TrackChunk tc = new TrackChunk();
		tc.parent = mf;
		tc.offset = offset;
		byte[] b = mf.getBytes();

		myOffset += 4;
		tc.length = Utils.toInt(b, myOffset);
		tc.parseTrackEvents(); // extract all the track events
		return tc;
	}

	public int getTotalLength() {
		return length + 8;
	}

	private int iteratorOffset;
	int lastStatusByte = 0;

	private int getLastStatusByte() {
		return lastStatusByte;
	}

	private void setLastStatusByte(int b) {
		lastStatusByte = b;
	}

	private List<TrackEvent> eventSet = new ArrayList<TrackEvent>();

	// this is used for looping over the track events internally; externally we
	// expose the TreeSet so
	// that we can add the absolute time offset to each TrackEvent before the
	// user gets them
	public Iterator<TrackEvent> privateIterator() {
		iteratorOffset = offset + 8;

		final MidiFile mf = parent;
		final TrackChunk tc = this;
		return new Iterator<TrackEvent>() {

			@Override
			public boolean hasNext() {
				return iteratorOffset < length + offset
						&& !TrackChunk.isTrackChunk(mf, iteratorOffset); // if
																			// this
																			// is
																			// the
																			// next
																			// track
																			// chunk,
																			// return
																			// false
			}

			@Override
			public TrackEvent next() {
				TrackEvent evt = TrackEvent.getInstance(tc, iteratorOffset);

				if (evt.hasStatusbyte()) {
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

	private void parseTrackEvents() {
		Integer timeOffset = 0;
		Iterator<TrackEvent> privateIterator = privateIterator();
		while (privateIterator.hasNext()) {
			TrackEvent te = privateIterator.next();
			// note that this only works if the tempo never changes!  Need to fix this for future ref
			te.setTimeOffset(timeOffset += te.time.value);
			// pass the event to the event handlers
			for(MidiEventHandler evtHandler : getParent().midiEventHandlers) {
				evtHandler.handleEvent(te);
			}
			eventSet.add(te);
		}
	}

	@Override
	public Iterator<TrackEvent> iterator() {
		return eventSet.iterator();
	}

}
