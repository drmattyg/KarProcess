package com.drmattyg.nanokaraoke;

import com.drmattyg.nanokaraoke.TrackEvent.EventType;





public class Test {
	
	public static void main(String[] args) {
		
		MidiFile mf = MidiFile.createInstance("/Users/mgordon/Downloads/bridge_over_troubled_water_karaoke_songs_NifterDotCom.kar");
		HeaderChunk h = mf.getHeaderChunk();
		for(int i = 0; i < mf.getBytes().length; i++) {
			if(TrackChunk.isTrackChunk(mf, i)) { System.out.println(i); }
		}
		int chunkNum = 1;
		for(TrackChunk tc1 : mf) {
			System.out.println("Chunk num: " + chunkNum++);
			System.out.printf("Chunk offset: 0x%x\n", tc1.offset);
			if(chunkNum != 5) continue;
			int tcLength = tc1.getTotalLength();
			for(TrackEvent te : tc1) {
				System.out.printf("--- Track Event offset: 0x%x\n", te.getOffset());
				System.out.println("--- Track Event type: " + te.getEventType());
				System.out.println("--- Track Event delta: " + te.getEventTime().value );
				System.out.printf("--- Track event status: %s 0x%x\n", te.hasStatusbyte() ? "t" : "f", te.getStatusByte() & 0xff);

				if(te.getEventType() == EventType.Meta) System.out.printf("--- Meta type: 0x%x\n", te.getMetaType());
				if(te.getEventType() == EventType.Midi) System.out.println();
				if(te.getOffset() > tcLength + tc1.offset) {
					System.out.println("Overrun @ " + te.getOffset());
					break;
				}
			}
		}
		System.exit(1);
		System.out.println("Format = " + h.format + "\nDivisions = " + h.division);

		for(TrackChunk tc : mf) {
			System.out.println("Track Chunk Offset: " + tc.offset);
			for(TrackEvent te: tc) {
				System.out.println("--- Track Event offset: " + te.getOffset());
				System.out.println("--- Track Event type: " + te.getEventType());
				System.out.println("--- Track Evenet delta: " + te.getEventTime().value );
				System.out.println("");
			}
			
		}
		
		
		

	}

}
