package com.drmattyg.nanokaraoke.video;

import com.drmattyg.nanokaraoke.video.MediaTools.OverlayAudioTool;
import com.drmattyg.nanokaraoke.video.MediaTools.VideoCutter;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;


public class VideoTest {
	public static String videoFile = "/Users/mgordon/test/tb.mp4";
	public static String output = "/Users/mgordon/test/tb_out.mp4";
	public static String audio = "/Library/Java/Demos/Sound/JavaSoundDemo/audio/1-welcome.wav";
	static final int VIDEO_STREAM_ID = 0;
	private static final int AUDIO_STREAM_ID = 1;

	// timidity -Ow -o output.wav input.kar
	IMediaReader ar = ToolFactory.makeReader(audio);
	
	static long startTime = 10000; // 10 seconds
	static long duration = 40000; // 15 seconds
	public static void main(String[] args) {
		IMediaWriter writer = MediaTools.makeVideoWriter(videoFile, output, VIDEO_STREAM_ID);
		MediaTools.addAudioChannel(writer, audio, AUDIO_STREAM_ID);
		VideoCutter vc = VideoCutter.getInstance(startTime, duration, 5000, writer);
		IMediaReader vidReader = ToolFactory.makeReader(videoFile);
		vc.cutVideo(vidReader, VIDEO_STREAM_ID);
		IMediaReader au = ToolFactory.makeReader(audio);
		OverlayAudioTool oat = OverlayAudioTool.getInstance(audio, 5000, writer, AUDIO_STREAM_ID);
		au.addListener(oat);
		while(au.readPacket() == null);

		writer.close();
	

		
	}

	
}
