package com.drmattyg.nanokaraoke;

import com.drmattyg.nanokaraoke.TrackEvent.EventType;





public class Test {
	
	public static void main(String[] args) {
		
		MidiFile mf = MidiFile.createInstance("/Users/mgordon/Downloads/bridge_over_troubled_water_karaoke_songs_NifterDotCom.kar");
		HeaderChunk h = mf.getHeaderChunk();
// TODO: Tempo events!
		for(TrackChunk tc : mf) {
			int time = 0;
			for(TrackEvent te: tc) {
				if(te.isText()) {
					time += te.time.value;
					System.out.printf("%d: %s\n", time, te.getTextEvent().text);
				}
			}
			
		}
		
		
		

	}

}
