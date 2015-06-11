package com.drmattyg.nanokaraoke;






public class Test {
	
	public static void main(String[] args) {
		
		MidiFile mf = MidiFile.createInstance("/Users/mgordon/Downloads/bridge_over_troubled_water_karaoke_songs_NifterDotCom.kar");
		HeaderChunk h = mf.getHeaderChunk();
// TODO: Tempo events!
		int tcNum = 1;
		for(TrackChunk tc : mf) {
			System.out.println("Track chunk " + tcNum++);
			int time = 0;
			for(TrackEvent te: tc) {
//				if(te.isText()) {
					time = te.getTimeOffset();
//					System.out.printf("%d: %s\n", time, te.getTextEvent().text);
					System.out.printf("%d: %s\n", time, te.getEventType());
					if(te.isMeta()) {
						System.out.println("Type: " + te.getMetaType());
					}
//				}
			}
			
		}
		
		
		

	}

}
