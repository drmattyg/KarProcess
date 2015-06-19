package com.drmattyg.nanokaraoke;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.drmattyg.nanokaraoke.video.MediaTools.VideoCutter;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

public class Test {
	public static String filename = "/Users/mgordon/Downloads/bridge_over_troubled_water_karaoke_songs_NifterDotCom.kar";
	public static String videoFile = "/Users/mgordon/test/tb.mp4";
	public static String output = "/Users/mgordon/test/tb_out.mp4";
	public static void main(String[] args) {
		try {
			BufferedImage b = ImageIO.read(new File("/Users/mgordon/Desktop/IMG_0455.JPG"));
			JFrame f = new JFrame();
			f.setSize(b.getWidth(), b.getHeight());
			
			Graphics2D g = b.createGraphics();
			
			AlphaComposite a = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,  1f);
//			g.setBackground(Color.BLACK);
//			g.setComposite(a);
			BufferedImage b1 = new BufferedImage(b.getWidth(), b.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g1 = b1.createGraphics();
			g1.setBackground(Color.BLACK);
			g1.clearRect(0, 0, b1.getWidth(), b1.getHeight());
			g1.setComposite(a);
			g1.drawImage(b, 0, 0, null);
			ImageIcon icon = new ImageIcon(b1);
			JLabel l = new JLabel(icon);
			JPanel p = new JPanel();
			p.add(l);
			f.add(p);
			f.setVisible(true);
			
			System.out.println(f.getSize());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		MidiFile mf = MidiFile.createInstance(filename);
//		HeaderChunk h = mf.getHeaderChunk();
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
