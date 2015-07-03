package com.drmattyg.nanokaraoke;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import com.drmattyg.nanokaraoke.TrackEvent.EventType;
//1 division = 1 dtu/qn
//tempo = us/qn
//
//1 division / tempo = 1 dtu / qn * qn/us = dtu/us
// 1 qn = division dtus; 1 qn = tempo us ; => division dtus = tempo us => dtu = division/tempo us; time = dtus * division/tempo
//
public class MidiFile implements Iterable<TrackChunk>{	
	
	public static class VarLength {
		public int value; // the value of the var length field
		public int size; // the size in bytes of the var length field
		
		
		// lessons from the bug I fixed below:
		// 1: Copying over the minimal data structure you need is worth the overhead probably; passing around the full byte array
		// invites the kinds of bugs I encountered.  Copying into an immutable array improves safety, and also allows me to trim the amount
		// of data that's being accessed.  I wouldn't have had this issue
		// 2: Basic unit tests here on these functions of course would have caught this.  Bug got introduced when I refactored
		// 3: The resuse of the term "i + offset", even once, should be a tipoff: don't do that.  Create a new variable.  That would have fixed the the error probably, 
		// assuming we didn't do #1, which we should have
		public static VarLength read(byte[] b, int offset) {
			VarLength v = new VarLength();
			v.value = 0;
			for(int i = 0; i < b.length; i++) {
				v.value = (v.value << 7) + (b[i + offset] & 0x7F);
				if((b[i + offset] & 0x80) == 0) {
					v.size = i + 1;
					break;
				}
			}
			return v;
		}
	}
	
	
	byte[] bytes;
	int offset;
	HeaderChunk header;
	private List<TrackChunk> trackChunks = new ArrayList<TrackChunk>();
	private int tempo = -1;
	public Collection<MidiEventHandler> midiEventHandlers = MidiEventHandlers.DEFAULT_HANDLERS;  // going to let users manually handle this for now
	
	private MidiFile() { };
	public static MidiFile getInstance(String s) {
		MidiFile mf = new  MidiFile();
		Path path = Paths.get(s);
		try {
			mf.bytes = Files.readAllBytes(path);
			mf.header = HeaderChunk.getInstance(mf);
			Iterator<TrackChunk> tcIterator = mf.privateIterator();
			while(tcIterator.hasNext()) {
				mf.trackChunks.add(tcIterator.next());
			}
			
			if(MidiEventHandlers.TEMPO_HANDLER.getTempoMap().size() < 2) {
				mf.tempo = MidiEventHandlers.TEMPO_HANDLER.getInitialTempo();
			}
			
			return mf;
		} catch(IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public byte[] getBytes() { return bytes; } // for testing

	
	public HeaderChunk getHeaderChunk() { return HeaderChunk.getInstance(this); }

	private int iteratorOffset;
	
	// this is the firt time a key is pressed; this is the start of the music
	// this is important because the WAV output starts at the start of the music, not the midi time zero
	public int getMusicStartDelta() {
		int startTime = Integer.MAX_VALUE;
		for(TrackChunk tc : this) {
			for(TrackEvent te: tc) {
				if(te.getEventType() == EventType.Midi 
						&& te.getStatusByte() >= 0x90 && te.getStatusByte() <= 0x9F 
						&& te.getTimeOffset() < startTime) {
					startTime = te.getTimeOffset();
				}
			}
		}
		return startTime;
	}
	
	
	public Iterator<TrackChunk> privateIterator() {
		iteratorOffset = header.getTotalLength(); // this is always the header length
		final MidiFile mf = this;
		return new Iterator<TrackChunk>() {

			@Override
			public boolean hasNext() {
				
				return iteratorOffset < bytes.length && TrackChunk.isTrackChunk(mf, iteratorOffset);
			}

			@Override
			public TrackChunk next() {
				TrackChunk tc = TrackChunk.getInstance(mf, iteratorOffset);
				iteratorOffset += tc.getTotalLength();
				return tc;
			}

			@Override
			public void remove() {
				// not implemented
				
			}
			
		};
	}

	@Override
	public Iterator<TrackChunk> iterator() {
		return trackChunks.iterator();
	}
	
	
	
	// it's pretty inefficient adding this up every single time, but it's more modular and easier to maintain
	// also putting in an escape hatch for files with only one tempo (which is most of them)
	public long timeOffsetForDelta(int delta) {
		if(delta == 0) return 0;
		int div = getHeaderChunk().division;

		if(tempo != -1){
			return Utils.deltaToMillis(tempo, div, delta);
		}

		SortedMap<Integer, Integer> tempoMap = MidiEventHandlers.TEMPO_HANDLER.getTempoMap().subMap(0, delta);
		long offset = 0;

		int lastTempo = 0;
		int lastDelta = 0;
		for(Entry<Integer, Integer> e : tempoMap.entrySet()) {
			offset += Utils.deltaToMillis(lastTempo, div, e.getKey() - lastDelta);
			lastTempo = e.getValue();
			lastDelta = e.getKey();
		}
		offset += Utils.deltaToMillis(lastTempo, div, delta - lastDelta);
		return offset;
	}
	
	
}
