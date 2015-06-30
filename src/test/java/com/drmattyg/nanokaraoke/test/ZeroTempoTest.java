package com.drmattyg.nanokaraoke.test;

import java.util.Collections;

import org.junit.Test;


import junit.framework.TestCase;
import com.drmattyg.nanokaraoke.*;

public class ZeroTempoTest extends TestCase {

	private static final String ZERO_TEMPO_TEST_FILE = "src/test/resources/crazy_little_thing_called_love_karaoke_songs_NifterDotCom.kar";
	
	@Test
	public void test() {
		MidiFile mf = MidiFile.getInstance(ZERO_TEMPO_TEST_FILE);
		for(TrackChunk tc : mf) {
			for(TrackEvent te: tc) {
				if(te.isMeta()) {
					System.out.printf("%s : 0x%x\n", te.getMetaType(), te.getOffset());
				}
			}
		}
	}
}
