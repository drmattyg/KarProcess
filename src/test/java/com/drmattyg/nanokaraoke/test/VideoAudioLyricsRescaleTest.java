package com.drmattyg.nanokaraoke.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;

import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.video.KaraokeScreenMediaTool;
import com.drmattyg.nanokaraoke.video.MediaTools;
import com.drmattyg.nanokaraoke.video.MediaTools.OverlayAudioTool;
import com.drmattyg.nanokaraoke.video.MediaTools.VideoCutter;
import com.drmattyg.nanokaraoke.video.Rescaler;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import junit.framework.TestCase;

public class VideoAudioLyricsRescaleTest extends TestCase {
	private static final String VIDEO_FILE = "src/test/resources/tommyboy.mp4";
	private static final String OUTPUT_FILE = "test_output/lyrics_output.mp4";
	private static final String MIDI_FILE = "src/test/resources/bridge_over_troubled_water.kar";
	private static final String AUDIO_FILE = "src/test/resources/bridge.wav";
	
	@Test
	public void test() {
		try {
			int width = 720;
			Rescaler r = Rescaler.getInstance(width, VIDEO_FILE, MediaTools.OUTPUT_VIDEO_STREAM_ID);
			int height = r.getTargetHeight();
			IMediaWriter writer = MediaTools.makeVideoWriter(OUTPUT_FILE, ICodec.findEncodingCodecByName("mpeg4"), width, height, MediaTools.OUTPUT_VIDEO_STREAM_ID);
			File wf = new File(AUDIO_FILE);
			long wavFileDuration = MediaTools.getWavFileDuration(wf);
			MediaTools.addAudioChannel(writer, wf.getPath(), MediaTools.OUTPUT_AUDIO_STREAM_ID);
			VideoCutter vc = VideoCutter.getInstance(0, 60000, 5000, writer);
			IMediaReader vidReader = ToolFactory.makeReader(VIDEO_FILE);
			MidiFile mf = MidiFile.getInstance(MIDI_FILE);
			KaraokeScreenMediaTool ks = KaraokeScreenMediaTool.getInstance(mf, 5000);
			
			List<MediaToolAdapter> filters = new ArrayList<MediaToolAdapter>();
			// adding the rescaler first
			filters.add(r);
			filters.add(ks);
			IMediaWriter cutWriter =  vc.cutVideo(vidReader, filters, MediaTools.OUTPUT_VIDEO_STREAM_ID);
			
			OverlayAudioTool oat = OverlayAudioTool.getInstance(AUDIO_FILE, 5000, cutWriter, MediaTools.OUTPUT_AUDIO_STREAM_ID);
			IMediaReader au = ToolFactory.makeReader(AUDIO_FILE);
			
			au.addListener(oat);
			while(au.readPacket() == null);
			
			cutWriter.close();
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}			

}
