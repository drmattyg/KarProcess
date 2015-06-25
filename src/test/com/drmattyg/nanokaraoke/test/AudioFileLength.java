package com.drmattyg.nanokaraoke.test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;

import com.drmattyg.nanokaraoke.video.MediaTools;


public class AudioFileLength {

	@Test
	public void test() {
		System.out.println(System.getProperty("user.dir"));
		File f = new File("src/test/resources/bridge.wav");
		assertTrue(f.exists() && f.canRead());
		try { 
			assertTrue(MediaTools.getWavFileDuration(f) > 0);
		} catch(IOException | UnsupportedAudioFileException ex) {
			fail(ex.getMessage());
		}
	}

}
