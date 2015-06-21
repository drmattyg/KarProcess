package com.drmattyg.nanokaraoke;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

import com.drmattyg.nanokaraoke.convert.ProcessKar;
import com.drmattyg.nanokaraoke.video.KaraokeScreen.KaraokeLine;

public class Test {
	public static String filename = "/Users/mgordon/Downloads/bridge_over_troubled_water_karaoke_songs_NifterDotCom.kar";
	public static String videoFile = "/Users/mgordon/test/tb.mp4";
	public static String output = "/Users/mgordon/test/tb_out.mp4";
	public static void main(String[] args) {

		MidiFile mf = MidiFile.createInstance(filename);
		HeaderChunk h = mf.getHeaderChunk();
		for(KaraokeLine kl : ProcessKar.extractKaraokeLines(mf)) {
			System.out.println(kl);
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
