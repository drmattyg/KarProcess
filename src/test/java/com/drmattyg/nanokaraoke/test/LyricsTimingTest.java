package com.drmattyg.nanokaraoke.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Timer;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

import com.drmattyg.nanokaraoke.*;
import com.drmattyg.nanokaraoke.TrackEvent.EventType;

import org.junit.Test;


import junit.framework.TestCase;
import com.drmattyg.nanokaraoke.video.*;

public class LyricsTimingTest extends TestCase {

	private static final String MIDI_FILE = "/Users/mgordon/Downloads/363613mpgordon@alumni.princeton.edu/noy0202.mid";
//	private static final String MIDI_FILE = "/Users/mgordon/Downloads/crazy_little_thing_called_love_karaoke_songs_NifterDotCom.kar"
//	private static final String MIDI_FILE = "/Users/mgordon/Downloads/good_vibrations_karaoke_songs_NifterDotCom.kar";
//	private static final String MIDI_FILE = "/Users/mgordon/test/Big Girls Don't Cry - The Four Seasons.kar";
//	private static final String MIDI_FILE = "/Users/mgordon/test/Sledgehammer - Peter Gabriel.kar";
//	private static final String MIDI_FILE = "/Users/mgordon/test/Black velvet - Alannah Miles.kar";
//	private static final String MIDI_FILE = "/Users/mgordon/test/karaoke/karfile/Have you ever seen the rain - Credence Clearwater Revival.kar";
	int currentLyricIndex;
	List<KaraokeLine> kLines;
	int currentLineIndex;
	@Test
	public void test() {
		MidiFile mf = MidiFile.getInstance(MIDI_FILE);
		List<Integer> deltas = mf.getTextHandler().getSortedTimeOffsets();
		Map<Integer, TextEvent> events = mf.getTextHandler().getTextMap();
		try {
			playFile(MIDI_FILE);
			long startTime = System.currentTimeMillis();
			for(Integer d : deltas) {

				long t = mf.timeOffsetForDelta(d);
				long currentTime;
				while((currentTime = System.currentTimeMillis()  - startTime) < t) {
//					System.out.println(currentTime);
					Thread.sleep(50);
				}
				System.out.println(events.get(d) + " : " + t + " Current time: " + currentTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	
	
	public static void playFile(String fn) throws Exception {
		Sequencer sequencer = MidiSystem.getSequencer();
		sequencer.open();
		InputStream is = new BufferedInputStream(new FileInputStream(new File(fn)));
		sequencer.setSequence(is);
		sequencer.start();

	}
}
