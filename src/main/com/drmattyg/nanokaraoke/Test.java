package com.drmattyg.nanokaraoke;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.drmattyg.nanokaraoke.convert.ProcessKar;
import com.drmattyg.nanokaraoke.video.KaraokeScreen;
import com.drmattyg.nanokaraoke.video.KaraokeLine;

public class Test {
	public static String filename = "/Users/mgordon/Downloads/bridge_over_troubled_water_karaoke_songs_NifterDotCom.kar";
	public static String videoFile = "/Users/mgordon/test/tb.mp4";
	public static String output = "/Users/mgordon/test/tb_out.mp4";
	public static void main(String[] args) {
		try {
			File f = ProcessKar.karToWav(filename);
			System.out.println(f.getAbsolutePath());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.exit(0);
		MidiFile mf = MidiFile.getInstance(filename);
		HeaderChunk h = mf.getHeaderChunk();
		List<KaraokeLine> lines = KaraokeLine.toKaraokeLines(mf);
	
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
