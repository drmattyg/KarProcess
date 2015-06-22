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
import com.drmattyg.nanokaraoke.video.KaraokeScreen.KaraokeLine;

public class Test {
	public static String filename = "/Users/mgordon/Downloads/bridge_over_troubled_water_karaoke_songs_NifterDotCom.kar";
	public static String videoFile = "/Users/mgordon/test/tb.mp4";
	public static String output = "/Users/mgordon/test/tb_out.mp4";
	public static void main(String[] args) {

		MidiFile mf = MidiFile.createInstance(filename);
		HeaderChunk h = mf.getHeaderChunk();
		List<KaraokeLine> lines = ProcessKar.toKaraokeLines(mf);
		try {
			BufferedImage b1 = ImageIO.read(new File("/Users/mgordon/test/cheetahs.jpg"));
			KaraokeScreen s = KaraokeScreen.getInstance(b1, lines);
			BufferedImage b = s.render(7, 0, 2);
			JFrame f = new JFrame();
			f.setSize(b.getWidth(), b.getHeight());
			ImageIcon icon = new ImageIcon(b);
			JLabel l = new JLabel(icon);
			JPanel p = new JPanel();
			p.add(l);
			f.add(p);

			f.setVisible(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
