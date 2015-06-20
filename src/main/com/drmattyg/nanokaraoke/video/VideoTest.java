package com.drmattyg.nanokaraoke.video;

import java.util.concurrent.TimeUnit;

import com.drmattyg.nanokaraoke.video.MediaTools.VideoCutter;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IAudioSamples.Format;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IRational;


public class VideoTest {
	public static String videoFile = "/Users/mgordon/test/tb.mp4";
	public static String output = "/Users/mgordon/test/tb_out.mp4";
	public static String audio = "/Library/Java/Demos/Sound/JavaSoundDemo/audio/1-welcome.wav";

	// timidity -Ow -o output.wav input.kar
	IMediaReader ar = ToolFactory.makeReader(audio);
	
	static long startTime = 10000; // 10 seconds
	static long duration = 40000; // 15 seconds
	public static void main(String[] args) {
		IMediaReader mediaReader = ToolFactory.makeReader(videoFile);
		IMediaWriter writer = ToolFactory.makeWriter(output);
		VideoCutter vc = VideoCutter.getInstance(startTime, duration, 5000, writer);
		vc.cutVideo(mediaReader);
		
		IMediaReader au = ToolFactory.makeReader(audio);
		IContainer auContainer = IContainer.make();
		auContainer.open(audio, IContainer.Type.READ, null);
//		int numStreams = auContainer.getNumStreams();
		int numChannels = auContainer.getStream(0).getStreamCoder().getChannels();
		int sampleRate = auContainer.getStream(0).getStreamCoder().getSampleRate();
		writer.addAudioStream(0, 0, numChannels, sampleRate);
//		System.out.printf("%d, %d, %d\n", numStreams, numChannels, bitrate);
		AddAudio aa = new AddAudio();
		aa.writer = writer;
		au.addListener(aa);
		while(au.readPacket() == null);
		writer.close();
		
		
		
		
	}
	
	static class AddAudio extends MediaToolAdapter {
		public long startTime = 15000;
		private static final IRational myBase = IRational.make(1, 1000); // millis
		public IMediaWriter writer;
		@Override
		public void onAudioSamples(IAudioSamplesEvent event) {
//			long samples[] = new long[(int) event.getMediaData().getNumSamples()];
//			for(int i = 0; i < event.getMediaData().getNumSamples(); i++) samples[i] = event.getMediaData().getSample(i, 0, Format.FMT_NONE);
			IAudioSamples s = event.getAudioSamples();
			IRational tb = s.getTimeBase();
			long timestamp = event.getTimeStamp() + tb.rescale(startTime, myBase);
			s.setTimeStamp(timestamp);
			writer.encodeAudio(0, s);
			super.onAudioSamples(event);
		} 
		
	}

	
}
