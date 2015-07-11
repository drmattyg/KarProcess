package com.drmattyg.nanokaraoke.test;
import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

import org.junit.Test;

import com.drmattyg.nanokaraoke.MidiEventHandlers;
import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.Utils;


public class KarLyricsTest {

	public static final String fn = "src/test/resources/bridge_over_troubled_water.kar";
	
	@Test
	public void test() {
		MidiFile mf = MidiFile.getInstance(fn);
		List<Integer> events = mf.getTextHandler().getSortedTimeOffsets();
		List<Long> eventMillis = new ArrayList<Long>();
		assertEquals(mf.getTempoHandler().getTempoMap().size(), 1); // dont currently handle more than one tempo
		int tempo = mf.getTempoHandler().getTempoMap().values().iterator().next();
		assertTrue(tempo != 0);
		int div = mf.getHeaderChunk().getDivision();
		assertTrue(div != 0);
		for(Integer e : events) { 
			eventMillis.add((long)Utils.deltaToMillis(tempo, div, e));
		}
		System.out.println(eventMillis);
		assertTrue(23700 == eventMillis.get(1));
	}

	
	public static void playFile(String fn) {
		try {
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.open();
			InputStream is = new BufferedInputStream(new FileInputStream(new File(fn)));
			sequencer.setSequence(is);
			sequencer.start();
		} catch (MidiUnavailableException | IOException | InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
