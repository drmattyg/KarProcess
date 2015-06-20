package com.drmattyg.nanokaraoke.video;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class MediaTools {
	
	public static class VideoCutter extends MediaToolAdapter {

		protected VideoCutter() {};
		protected long startTime;
		protected long duration;
		protected IMediaWriter mediaWriter;
		protected long fadeTime;
		protected boolean startVideo = false;
		protected boolean endFade = false;
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
		
		public void cutVideo(IMediaReader mediaReader) {
			mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
			mediaReader.addListener(this);
			while (mediaReader.readPacket() == null);
//			mediaWriter.close();
		}
	}	
}
