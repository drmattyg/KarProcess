package com.drmattyg.nanokaraoke.video;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class MediaTools {
	
	public static class VideoCutter extends MediaToolAdapter {

		private VideoCutter() {};
		private long startTime;
		private long duration;
		private IMediaWriter mediaWriter;
		private String outputFile;
		private long fadeTime;
		public static VideoCutter getInstance(long startTimeMillis, long lengthMillis, String outputFile) {
			VideoCutter vc = new VideoCutter();
			vc.startTime = startTimeMillis;
			vc.duration = lengthMillis;
			vc.outputFile = outputFile;
			return vc;
		}
		
		public static VideoCutter getInstance(long startTimeMillis, long lengthMillis, long fadeTime, String outputFile) {
			VideoCutter vc = new VideoCutter();
			vc.startTime = startTimeMillis;
			vc.duration = lengthMillis;
			vc.outputFile = outputFile;
			vc.fadeTime = fadeTime;
			return vc;
		}
		
		@Override
		public void onVideoPicture(IVideoPictureEvent event) {
			long timestamp = event.getTimeStamp(TimeUnit.MILLISECONDS);
			if(timestamp > startTime && timestamp < (startTime + duration)) {
				BufferedImage img = event.getImage();
				if(timestamp < startTime + fadeTime) {
					float fadePercent = (timestamp - startTime)*1.0f/fadeTime;
					img = ImageUtils.fade(img, fadePercent);
				} else if (timestamp > startTime + duration - fadeTime) {
					float fadePercent = (duration - (timestamp - startTime))*1f/fadeTime;
					img = ImageUtils.fade(img, fadePercent);
				}
				
				mediaWriter.encodeVideo(0, img, timestamp - startTime, TimeUnit.MILLISECONDS);
			}
			super.onVideoPicture(event);
		}
		
		public void cutVideo(IMediaReader mediaReader) {
			mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
			mediaReader.addListener(this);
			mediaWriter = ToolFactory.makeWriter(outputFile, mediaReader);
			while (mediaReader.readPacket() == null);
			mediaWriter.close();
		}
	}
}
