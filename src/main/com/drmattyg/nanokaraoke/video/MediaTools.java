package com.drmattyg.nanokaraoke.video;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

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
	
	public static class VideoCutter extends MediaToolAdapter {

		protected VideoCutter() {};
		protected long startTime;
		protected long duration;
		protected IMediaWriter mediaWriter;
		protected long fadeTime;
		protected boolean startVideo = false;
		protected boolean endFade = false;
		private int streamId;
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
			if(timestamp > startTime && timestamp < (startTime + duration)) {
				BufferedImage img = event.getImage();
				if(timestamp < startTime + fadeTime) {
					startVideo = true;
					float fadePercent = (timestamp - startTime)*1.0f/fadeTime;
					img = ImageUtils.fade(img, fadePercent);
				} else if (timestamp > startTime + duration - fadeTime) {
					float fadePercent = (duration - (timestamp - startTime))*1f/fadeTime;
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
		

	}	
	
	static class OverlayAudioTool extends MediaToolAdapter {

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
		IContainer vidContainer = IContainer.make();
		vidContainer.open(input, IContainer.Type.READ, null);
		IStream vidStream = null;
		for(int i = 0; i < vidContainer.getNumStreams(); i++ ) {
			if(vidContainer.getStream(i).getStreamCoder().getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				vidStream = vidContainer.getStream(i);
				break;
			}
		}
		writer.addVideoStream(0, 0,
				vidStream.getStreamCoder().getWidth(), vidStream.getStreamCoder().getHeight());
		return writer;
	}
	
}
