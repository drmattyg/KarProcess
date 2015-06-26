package com.drmattyg.nanokaraoke.test;
import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

import org.junit.Test;

import com.drmattyg.nanokaraoke.MidiEventHandlers;
import com.drmattyg.nanokaraoke.MidiFile;


public class KarLyricsTest {

	public static final String fn = "src/test/resources/bridge_over_troubled_water.kar";
	
	@Test
	public void test() {
		MidiFile mf = MidiFile.getInstance(fn);
		List<Integer> events = MidiEventHandlers.TEXT_HANDLER.getSortedTimeOffsets();
		System.out.println(events);
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
