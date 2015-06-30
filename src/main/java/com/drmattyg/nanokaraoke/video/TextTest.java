package com.drmattyg.nanokaraoke.video;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.drmattyg.nanokaraoke.MidiFile;

public class TextTest {

	private static String IMAGE_FILE = "src/test/resources/cheetahs.jpg";
	private static final String MIDI_FILE = "src/test/resources/bridge_over_troubled_water.kar";
	public static void main(String[] args) {
		try {
			BufferedImage img = ImageIO.read(new File(IMAGE_FILE));
			MidiFile mf = MidiFile.getInstance(MIDI_FILE);
			KaraokeScreen sc = KaraokeScreen.getInstance(img, KaraokeLine.toKaraokeLines(mf), 4, 5, 2);
			System.out.println(KaraokeLine.toKaraokeLines(mf).get(5).lyrics);
			BufferedImage b = sc.render();
			JFrame f = new JFrame();
			f.setSize(b.getWidth(), b.getHeight());
			ImageIcon icon = new ImageIcon(b);
			JLabel l = new JLabel(icon);
			JPanel p = new JPanel();
			p.add(l);
			f.add(p);
			f.setVisible(true);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

	}

}
