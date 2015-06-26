package com.drmattyg.nanokaraoke.test;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.video.KaraokeScreenMediaTool;
import com.drmattyg.nanokaraoke.video.MediaTools;
import com.drmattyg.nanokaraoke.video.MediaTools.VideoCutter;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

public class VideoLyricsTest {

	private static final String VIDEO_FILE = "src/test/resources/Rollercoaster.mov";
	private static final String OUTPUT_FILE = "test_output/lyrics_output.mp4";
	private static final String MIDI_FILE = "src/test/resources/bridge_over_troubled_water.kar";
	
	@Test
	public void test() {
		IMediaWriter writer = MediaTools.makeVideoWriter(VIDEO_FILE, OUTPUT_FILE, MediaTools.OUTPUT_VIDEO_STREAM_ID);
		VideoCutter vc = VideoCutter.getInstance(0, 60000, 5000, writer);
		IMediaReader vidReader = ToolFactory.makeReader(VIDEO_FILE);
		MidiFile mf = MidiFile.getInstance(MIDI_FILE);
		KaraokeScreenMediaTool ks = KaraokeScreenMediaTool.getInstance(mf, 5000);
		IMediaWriter cutWriter =  vc.cutVideo(vidReader, Collections.singletonList(ks), MediaTools.OUTPUT_VIDEO_STREAM_ID);
		cutWriter.close();
	}

}
