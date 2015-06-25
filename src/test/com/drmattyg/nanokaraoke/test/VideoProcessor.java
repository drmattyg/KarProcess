package com.drmattyg.nanokaraoke.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.drmattyg.nanokaraoke.convert.ProcessKar;


public class VideoProcessor {

	private static final String VIDEO_FILE = "src/test/resources/tommyboy.mp4";
	private static final String OUTPUT_FILE = "test_output/output.mp4";
	private static final String MIDI_FILE = "src/test/resources/bridge_over_troubled_water.kar";
	@Test
	public void test() {
		try {
			System.out.println("Starting");
			ProcessKar.generateKarVideo(VIDEO_FILE, MIDI_FILE, 10, OUTPUT_FILE);
			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
