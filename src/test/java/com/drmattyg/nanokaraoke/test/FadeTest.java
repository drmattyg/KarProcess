package com.drmattyg.nanokaraoke.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collections;

import org.junit.Test;

import com.drmattyg.nanokaraoke.convert.ProcessKar;
import com.drmattyg.nanokaraoke.video.KaraokeScreenMediaTool;
import com.drmattyg.nanokaraoke.video.MediaTools;
import com.drmattyg.nanokaraoke.video.MediaTools.VideoCutter;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;


public class FadeTest {

	private static final String VIDEO_FILE = "/Users/mgordon/test/2054bgb.hd.wmv";
	private static final String OUTPUT_FILE = "test_output/fade_output.mp4";
	@Test
	public void test() {
		try {
			IMediaWriter writer = MediaTools.makeVideoWriter(VIDEO_FILE, OUTPUT_FILE, MediaTools.OUTPUT_VIDEO_STREAM_ID);
//			MediaTools.addAudioChannel(writer, wf.getPath(), MediaTools.OUTPUT_AUDIO_STREAM_ID);
			VideoCutter vc = VideoCutter.getInstance(120000, 1*60*1000, 5000, writer);
			IMediaReader vidReader = ToolFactory.makeReader(VIDEO_FILE);
//			IMediaWriter cutWriter =  vc.cutVideo(vidReader, Collections.singletonList(ks), MediaTools.OUTPUT_VIDEO_STREAM_ID);
			IMediaWriter cutWriter =  vc.cutVideo(vidReader, Collections.EMPTY_LIST, MediaTools.OUTPUT_VIDEO_STREAM_ID);
			cutWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
