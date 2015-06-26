package com.drmattyg.nanokaraoke.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;

public class MediaTools {
	public static final int OUTPUT_VIDEO_STREAM_ID = 0;
	public static final int OUTPUT_AUDIO_STREAM_ID = 1;	
	public static class VideoCutter extends MediaToolAdapter {

		protected VideoCutter() {};
		protected long startTime;
		protected long duration;
		protected IMediaWriter mediaWriter;
		protected long fadeTime;
		protected boolean startVideo = false;
		protected boolean endFade = false;
		private int streamId;
		private static long t0 = -1;

		public static VideoCutter getInstance(long startTimeMillis, long lengthMillis, IMediaWriter writer) {
			VideoCutter vc = new VideoCutter();
			vc.startTime = startTimeMillis;
			vc.duration = lengthMillis;
			vc.mediaWriter = writer;
			return vc;
		}
		
		public static VideoCutter getInstance(long startTimeMillis, long lengthMillis, long fadeTime, IMediaWriter writer) {
			VideoCutter vc = new VideoCutter();
			vc.startTime = startTimeMillis;
			vc.duration = lengthMillis;
			vc.fadeTime = fadeTime;
			vc.mediaWriter = writer;
			return vc;
		}
		
		@Override
		public void onVideoPicture(IVideoPictureEvent event) {

			long timestamp = event.getTimeStamp(TimeUnit.MILLISECONDS);
			if(t0 < 0) t0 = timestamp;
			long tStart = t0 + startTime;
			if(timestamp > tStart && timestamp < (tStart + duration)) {
				BufferedImage img = event.getImage();
				if(timestamp < tStart + fadeTime) {
					startVideo = true;
					float fadePercent = (timestamp - tStart)*1.0f/fadeTime;
					img = ImageUtils.fade(img, fadePercent);
				} else if (timestamp > tStart + duration - fadeTime) {
					float fadePercent = 1 - (timestamp - (tStart + duration - fadeTime))*1.0f/fadeTime;
					img = ImageUtils.fade(img, fadePercent);
				} else if(startVideo) endFade = true;
				
				mediaWriter.encodeVideo(0, img, timestamp - startTime, TimeUnit.MILLISECONDS);
			}
			super.onVideoPicture(event);
		}
		
		public IMediaWriter cutVideo(IMediaReader mediaReader, int streamId) {
			this.streamId = streamId;
			mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
			mediaReader.addListener(this);
			while (mediaReader.readPacket() == null);
			return mediaWriter;
		}
		
		public IMediaWriter cutVideo(IMediaReader mediaReader, List<? extends MediaToolAdapter> filters, int streamId) {
			if(filters.isEmpty()) {
				return cutVideo(mediaReader, streamId);
			}
			this.streamId = streamId;
			mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);

			MediaToolAdapter last = filters.get(0);
			mediaReader.addListener(last);
			for(int i = 1; i < filters.size(); i++) {
				last.addListener(filters.get(i));
				last = filters.get(i);
			}
			while (mediaReader.readPacket() == null);
			return mediaWriter;
			
		}
		

	}	
	
	public static class OverlayAudioTool extends MediaToolAdapter {

		protected OverlayAudioTool() {}
		private long startTime;
		private IMediaWriter writer;
		private int audioStreamId;
		private static final IRational myBase = IRational.make(1, 1000); // millis
		public static OverlayAudioTool getInstance(String audioFile, long startTimeOffset, IMediaWriter output, int audioStreamId) {
			OverlayAudioTool oa = new OverlayAudioTool();
			oa.startTime = startTimeOffset;
			oa.writer = output;
			oa.audioStreamId = audioStreamId;

			return oa;
		}
		
		@Override
		public void onAudioSamples(IAudioSamplesEvent event) {
			IAudioSamples s = event.getAudioSamples();
			IRational tb = s.getTimeBase();
			long timestamp = event.getTimeStamp() + tb.rescale(startTime, myBase);
			s.setTimeStamp(timestamp);
			writer.encodeAudio(audioStreamId, s);
			super.onAudioSamples(event);
		}
	}
	
	public static void addAudioChannel(IMediaWriter writer, String audioFile, int streamId) {
		IContainer auContainer = IContainer.make();
		auContainer.open(audioFile, IContainer.Type.READ, null);
		int numChannels = auContainer.getStream(0).getStreamCoder().getChannels();
		int sampleRate = auContainer.getStream(0).getStreamCoder().getSampleRate();
		writer.addAudioStream(streamId, streamId, numChannels, sampleRate);
	}
	
	public static IMediaWriter makeVideoWriter(String input, String output, int streamId) {
		IMediaWriter writer = ToolFactory.makeWriter(output);
		IStream vidStream = getVideoStream(input);
		writer.addVideoStream(streamId, streamId,
				vidStream.getStreamCoder().getWidth(), vidStream.getStreamCoder().getHeight());
		return writer;
	}
	
	public static IMediaWriter makeVideoWriter(String output, ICodec codec, int width, int height, int streamId) {
		IMediaWriter writer = ToolFactory.makeWriter(output);
		writer.addVideoStream(streamId, streamId, codec, width, height);
		return writer;
	}
	
	
	// Supplies a default available MP4 encoder
	public static IMediaWriter makeVideoWriter(String output, int width, int height, int streamId) {
		ICodec codec = ICodec.findEncodingCodecByName("mpeg4");
		return makeVideoWriter(output, codec, width, height, streamId);
	}
	
	public static IStream getVideoStream(String input) {
		IContainer vidContainer = IContainer.make();
		vidContainer.open(input, IContainer.Type.READ, null);
		IStream vidStream = null;
		for(int i = 0; i < vidContainer.getNumStreams(); i++ ) {
			if(vidContainer.getStream(i).getStreamCoder().getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				vidStream = vidContainer.getStream(i);
				break;
			}
		}
		return vidStream;
	}
	
	
	public static long getWavFileDuration(File wavFile) throws UnsupportedAudioFileException, IOException {
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
	    AudioFormat format = audioInputStream.getFormat();
	    long audioFileLength = wavFile.length();
	    int frameSize = format.getFrameSize();
	    float frameRate = format.getFrameRate();
	    float durationInMillis = (audioFileLength / (frameSize * frameRate))*1000;
	    return (long)Math.ceil(durationInMillis);
	}
	
}
