package com.drmattyg.nanokaraoke.test;

import java.awt.event.TextEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.drmattyg.nanokaraoke.MidiEventHandlers;
import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.video.KaraokeScreenMediaTool;
import com.drmattyg.nanokaraoke.video.KaraokeLine;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.VideoPictureEvent;
import com.xuggle.xuggler.IPixelFormat.Type;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import junit.framework.TestCase;
import static org.junit.Assert.*;

public class LyricsOutputTest extends TestCase {
	
	
//	private static final String MIDI_FILE = "src/test/resources/in_the_ghetto.kar";
	private static final String MIDI_FILE = "/Users/mgordon/test/Beautiful - Christina Aguilera.kar";

	@Test
	public void test() {
		try {
			BufferedImage img = ImageIO.read(new File("src/test/resources/cheetahs.jpg"));
			MidiFile mf = MidiFile.getInstance(MIDI_FILE);
			KaraokeScreenMediaTool ks = KaraokeScreenMediaTool.getInstance(mf, 5000);
			Map<Integer, com.drmattyg.nanokaraoke.TextEvent> m = MidiEventHandlers.TEXT_HANDLER.getTextMap();
			List<KaraokeLine> lines = KaraokeLine.toKaraokeLines(mf);
			long duration = 1000*60*5;
			IConverter conv = ConverterFactory.createConverter(img, Type.ABGR);
			for(long t = 0; t < duration; t += 100) {
				IMediaReader reader = ToolFactory.makeReader("Foo.mp4");
				IVideoPicture vp = conv.toPicture(img, t*1000);
				IVideoPictureEvent e = new VideoPictureEvent(reader, vp, 0);
				
				ks.onVideoPicture(e);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		}

	}
}
