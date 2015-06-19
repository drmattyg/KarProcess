package com.drmattyg.nanokaraoke.video;

import com.drmattyg.nanokaraoke.video.MediaTools.VideoCutter;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IRational;


public class VideoTest {
	public static String videoFile = "/Users/mgordon/test/tb.mp4";
	public static String output = "/Users/mgordon/test/tb_out.mp4";
	private static IMediaWriter mediaWriter;
	private static IRational tb = null;

	static long startTime = 10000; // 10 seconds
	static long duration = 15000; // 15 seconds
	public static void main(String[] args) {
		IMediaReader mediaReader = ToolFactory.makeReader(videoFile);
		VideoCutter vc = VideoCutter.getInstance(startTime, duration, output);
		vc.cutVideo(mediaReader);
		
	}
}
