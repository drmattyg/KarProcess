package com.drmattyg.nanokaraoke.video;

import java.util.Collections;
import java.util.List;

import com.drmattyg.nanokaraoke.video.MediaTools.OverlayAudioTool;
import com.drmattyg.nanokaraoke.video.MediaTools.VideoCutter;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IStream;


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
//		// test scaling down video
//		IStream vidStream = MediaTools.getVideoStream("/Users/mgordon/Downloads/star_wars_7_the_force_awakens-teaser2/Star Wars Episode VII - The Force Awakens - Teaser Trailer 2.mp4");
//		int targetWidth = 720;
//		int width = vidStream.getStreamCoder().getWidth();
//		double sf = targetWidth*1.0d/width;
//		int targetHeight = (int) Math.ceil(vidStream.getStreamCoder().getHeight()*sf);
//		ICodec myCodec = ICodec.findEncodingCodecByName("mpeg4");
//
//		System.out.println(myCodec);
//		IMediaWriter writer = MediaTools.makeVideoWriter("/Users/mgordon/test/swtest.mp4", myCodec, targetWidth, targetHeight, VIDEO_STREAM_ID);
//		IMediaReader reader = ToolFactory.makeReader("/Users/mgordon/Downloads/star_wars_7_the_force_awakens-teaser2/Star Wars Episode VII - The Force Awakens - Teaser Trailer 2.mp4");
//		Rescaler r = new Rescaler(targetWidth, targetHeight, VIDEO_STREAM_ID);
//		reader.addListener(r);
//		r.addListener(writer);
//		
//		while(reader.readPacket() == null);
		ICodec myCodec = ICodec.findEncodingCodecByName("mpeg4");
		int targetWidth = 720;
		int targetHeight = Rescaler.getScaledHeight(videoFile, targetWidth);
		IMediaWriter writer = MediaTools.makeVideoWriter("/Users/mgordon/test/tb_out.mp4", myCodec, targetWidth, targetHeight, VIDEO_STREAM_ID);
//		IMediaWriter writer = MediaTools.makeVideoWriter(videoFile, output, VIDEO_STREAM_ID);
		Rescaler r = new Rescaler(targetWidth, targetHeight, VIDEO_STREAM_ID);
		MediaTools.addAudioChannel(writer, audio, AUDIO_STREAM_ID);
		VideoCutter vc = VideoCutter.getInstance(startTime, duration, 5000, writer);
		IMediaReader vidReader = ToolFactory.makeReader(videoFile);
		vc.cutVideo(vidReader, Collections.singletonList(r), VIDEO_STREAM_ID);
//		vc.cutVideo(vidReader, Collections.EMPTY_LIST, VIDEO_STREAM_ID);
		vc.cutVideo(vidReader, VIDEO_STREAM_ID);
		IMediaReader au = ToolFactory.makeReader(audio);
		OverlayAudioTool oat = OverlayAudioTool.getInstance(audio, 5000, writer, AUDIO_STREAM_ID);
		au.addListener(oat);
		while(au.readPacket() == null);
		writer.close();
//
//		writer.close();
	

		
	}

	
}
