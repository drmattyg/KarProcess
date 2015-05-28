package com.drmattyg.nanokaraoke;




public class Test {
	
	public static void main(String[] args) {
		
		MidiFile mf = MidiFile.createInstance("/Users/mgordon/Downloads/bridge_over_troubled_water_karaoke_songs_NifterDotCom.kar");
		HeaderChunk h = mf.getHeaderChunk();
		for(int i = 0; i < mf.getBytes().length; i++) {
			if(TrackChunk.isTrackChunk(mf, i)) { System.out.println(i); }
		}
		System.out.println("Format = " + h.format + "\nDivisions = " + h.division);

		for(TrackChunk tc : mf) {
//			System.out.println(tc.metaType);
//			if(tc.isText()) {
				System.out.println("Offset: " + tc.offset);
//				System.out.println("Time: " + tc.time.value);
//				System.out.println("Text: " + tc.getTextEvent());
//			}
		}
		
		
		

	}

}
