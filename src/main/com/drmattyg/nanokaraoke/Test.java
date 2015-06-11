package com.drmattyg.nanokaraoke;

import java.util.Collections;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;

public class Test {
	public static String filename = "/Users/mgordon/Downloads/bridge_over_troubled_water_karaoke_songs_NifterDotCom.kar";
	public static void main(String[] args) {
		
		MidiFile mf = MidiFile.createInstance(filename);
		HeaderChunk h = mf.getHeaderChunk();
		int tcNum = 1;
		for(Integer time: MidiEventHandlers.TEXT_HANDLER.getSortedTimeOffsets()) {
			System.out.println(time + " : " + MidiEventHandlers.TEXT_HANDLER.getTextMap().get(time).toString());
		}
		
		

	}

}
