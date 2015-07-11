package com.drmattyg.nanokaraoke.test;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.junit.Test;

import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.video.KaraokeLine;
import com.drmattyg.nanokaraoke.video.KaraokeScreen;

public class ScreenDraw {

	private static String IMAGE_FILE = "src/test/resources/cheetahs.jpg";
	private static final String MIDI_FILE = "src/test/resources/bridge_over_troubled_water.kar";
//	private static final String MIDI_FILE = "/Users/mgordon/Downloads/363613mpgordon@alumni.princeton.edu/noy0202.mid";

	@Test
	public void test() {
		try {
			BufferedImage img = ImageIO.read(new File(IMAGE_FILE));
			MidiFile mf = MidiFile.getInstance(MIDI_FILE);
			KaraokeScreen sc = KaraokeScreen.getInstance(img, KaraokeLine.toKaraokeLines(mf), 0, 1, 2);
			BufferedImage b = sc.render();
			JFrame f = new JFrame();
			f.setSize(b.getWidth(), b.getHeight());
			ImageIcon icon = new ImageIcon(b);
			JLabel l = new JLabel(icon);
			JPanel p = new JPanel();
			p.add(l);
			f.add(p);
			f.setVisible(true);
			Thread.sleep(10000);
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
