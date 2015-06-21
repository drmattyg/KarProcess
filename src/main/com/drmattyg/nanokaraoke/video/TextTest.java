package com.drmattyg.nanokaraoke.video;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TextTest {

	public static void main(String[] args) {
		try {
			BufferedImage b = ImageIO.read(new File("/Users/mgordon/test/cheetahs.jpg"));
			//TextUtils.drawOutlinedText(b, "My baloney has a first name", Color.BLACK, Color.CYAN, 200, 200, new Font("Helevetica", Font.BOLD, 48));
			KaraokeScreen.test(b);
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
