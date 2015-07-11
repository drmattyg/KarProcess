package com.drmattyg.nanokaraoke.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.drmattyg.nanokaraoke.MidiEventHandlers;
import com.drmattyg.nanokaraoke.MidiFile;

public class MidLyricsTest {
	private static final String MIDI_FILE = "/Users/mgordon/Downloads/363613mpgordon@alumni.princeton.edu/noy0202.mid";
//	private static final String MIDI_FILE = "src/test/resources/bridge_over_troubled_water.kar";
	@Test
	public void test() {
		MidiFile mf = MidiFile.getInstance(MIDI_FILE);
		for(Integer d : mf.getTextHandler().getSortedTimeOffsets()) {
			System.out.println("L: " + mf.getTextHandler().getTextMap().get(d));
		}
	}

}
