package com.drmattyg.nanokaraoke.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collections;

import javax.swing.OverlayLayout;

import org.junit.Test;

import com.drmattyg.nanokaraoke.video.MediaTools;
import com.drmattyg.nanokaraoke.video.MediaTools.OverlayAudioTool;
import com.drmattyg.nanokaraoke.video.MediaTools.VideoCutter;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

public class AudioOverlayTest {

	private static final String VIDEO_FILE = "/System/Library/Compositions/Rollercoaster.mov";
	private static final String OUTPUT_FILE = "test_output/audio_output.mp4";
	private static final String AUDIO_FILE = "/Library/Java/Demos/Sound/JavaSoundDemo/audio/1-welcome.wav";
	private static final long FADE_TIME = 5000;
	
	@Test
	public void test() {
		try {
			IMediaWriter writer = MediaTools.makeVideoWriter(VIDEO_FILE, OUTPUT_FILE, MediaTools.OUTPUT_VIDEO_STREAM_ID);
			File wf = new File(AUDIO_FILE);
			long wavFileDuration = MediaTools.getWavFileDuration(wf);
			MediaTools.addAudioChannel(writer, wf.getPath(), MediaTools.OUTPUT_AUDIO_STREAM_ID);
			VideoCutter vc = VideoCutter.getInstance(0, wavFileDuration + 2*FADE_TIME, FADE_TIME, writer);
			
			IMediaReader vidReader = ToolFactory.makeReader(VIDEO_FILE);
	//		IMediaWriter cutWriter =  vc.cutVideo(vidReader, Collections.singletonList(ks), MediaTools.OUTPUT_VIDEO_STREAM_ID);
			IMediaWriter cutWriter =  vc.cutVideo(vidReader, Collections.EMPTY_LIST, MediaTools.OUTPUT_VIDEO_STREAM_ID);
			
			OverlayAudioTool oat = OverlayAudioTool.getInstance(AUDIO_FILE, 5000, cutWriter, MediaTools.OUTPUT_AUDIO_STREAM_ID);
			IMediaReader au = ToolFactory.makeReader(AUDIO_FILE);
			au.addListener(oat);
			while(au.readPacket() == null);
			cutWriter.close();
			
		} catch(Exception ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}

}
