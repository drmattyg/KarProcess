package com.drmattyg.nanokaraoke;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

public class Test {
	public static String filename = "/Users/mgordon/Downloads/bridge_over_troubled_water_karaoke_songs_NifterDotCom.kar";
	public static void main(String[] args) {
		MidiFile mf = MidiFile.createInstance(filename);
		HeaderChunk h = mf.getHeaderChunk();
		int tempo = MidiEventHandlers.TEMPO_HANDLER.getTempoMap().values().iterator().next();
		int division = h.division;
//		playFile(filename);
		try {
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.open();
			InputStream is = new BufferedInputStream(new FileInputStream(new File(filename)));
			sequencer.setSequence(is);
			long t0 = System.currentTimeMillis();
			sequencer.start();
			for(Integer time: MidiEventHandlers.TEXT_HANDLER.getSortedTimeOffsets()) {
				while(time >= sequencer.getTickPosition()) {
					Thread.sleep(100);
				}
				System.out.printf("%s", MidiEventHandlers.TEXT_HANDLER.getTextMap().get(time));
			}

			System.out.println(h.division);
	//		int tcNum = 1;


		} catch(Exception ex) {
			ex.printStackTrace();
		}
		

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
