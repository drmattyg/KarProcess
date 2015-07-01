package com.drmattyg.nanokaraoke.test;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.drmattyg.nanokaraoke.MidiEventHandlers;
import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.TextEvent;
import com.drmattyg.nanokaraoke.Utils;
import com.drmattyg.nanokaraoke.convert.ProcessKar;
import java.io.*;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.*;

import junit.framework.TestCase;

public class LyricsWavTimingTest extends TestCase {

	private static final String MIDI_FILE = "/Users/mgordon/Downloads/blueberry_hill_karaoke_songs_NifterDotCom.kar";
	@Test
	public void test() {
		MidiFile mf = MidiFile.getInstance(MIDI_FILE);
		int tempo = MidiEventHandlers.TEMPO_HANDLER.getTempoMap().values().iterator().next();
		int div = mf.getHeaderChunk().getDivision();
		List<Integer> deltas = MidiEventHandlers.TEXT_HANDLER.getSortedTimeOffsets();
		Map<Integer, TextEvent> events = MidiEventHandlers.TEXT_HANDLER.getTextMap();
		try {
			File wf = ProcessKar.karToWav(MIDI_FILE);
			AudioInputStream stream = AudioSystem.getAudioInputStream(wf);
			AudioFormat format = stream.getFormat();
		    DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();
			System.out.println("Starting");
			long startTime = System.currentTimeMillis();
			for(Integer d : deltas) {
				long t = Utils.deltaToMillis(tempo, div, d);
				long currentTime;
//				while(System.currentTimeMillis()  - startTime < t) {
				while((currentTime = System.currentTimeMillis()  - startTime) < t) {
					System.out.println(currentTime);
					Thread.sleep(50);
				}
				System.out.println(events.get(d) + " : " + t);
			}
			
		} catch (Exception e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}
}
