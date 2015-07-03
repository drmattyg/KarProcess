package com.drmattyg.nanokaraoke.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
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

//	private static final String MIDI_FILE = "/Users/mgordon/Downloads/all_shook_up_karaoke_songs_NifterDotCom.kar";
//	private static final String MIDI_FILE = "/Users/mgordon/Downloads/crazy_little_thing_called_love_karaoke_songs_NifterDotCom.kar";
//	private static final String MIDI_FILE = "/Users/mgordon/Downloads/good_vibrations_karaoke_songs_NifterDotCom.kar";
	private static final String MIDI_FILE = "/Users/mgordon/test/In the ghetto - Elvis Presley.kar";
	
	int currentLyricIndex;
	List<KaraokeLine> kLines;
	int currentLineIndex;
	@Test
	public void test() {
		SortedMap<Integer, TrackEvent> midiOffsets = new TreeMap<Integer, TrackEvent>();
		MidiFile mf = MidiFile.getInstance(MIDI_FILE);

		for(TrackEvent te : midiOffsets.values()) {
			if(te.getEventType() == EventType.Midi) {
				System.out.println(te.getTimeOffset() + " / " + String.format("0x%x", te.getStatusByte()));
			}
		}
		int tempo = MidiEventHandlers.TEMPO_HANDLER.getTempoMap().values().iterator().next();
		
		int div = mf.getHeaderChunk().getDivision();
		List<Integer> deltas = MidiEventHandlers.TEXT_HANDLER.getSortedTimeOffsets();
		Map<Integer, TextEvent> events = MidiEventHandlers.TEXT_HANDLER.getTextMap();
		
		try {
			playFile(MIDI_FILE);
			long startTime = System.currentTimeMillis();
			for(Integer d : deltas) {

				long t = mf.timeOffsetForDelta(d);
				System.out.println("Time = " + t);
				long currentTime;
//				while(System.currentTimeMillis()  - startTime < t) {
				while((currentTime = System.currentTimeMillis()  - startTime) < t) {
					Thread.sleep(50);
				}
				System.out.println(events.get(d) + " : " + t);

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
