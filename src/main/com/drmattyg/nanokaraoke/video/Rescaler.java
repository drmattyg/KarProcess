package com.drmattyg.nanokaraoke.video;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.VideoPictureEvent;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;

public class Rescaler extends MediaToolAdapter {
	private int targetWidth;
	private int targetHeight = 0;
	private IVideoResampler videoResampler;
	private int streamId;
	public Rescaler(int targetWidth, int targetHeight, int streamId) {
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
		this.streamId = streamId;
	}
	
	@Override
	public void onVideoPicture(IVideoPictureEvent event) {

		IVideoPicture pic = event.getPicture();
		
		if (videoResampler == null) {
			videoResampler = IVideoResampler.make(targetWidth, targetHeight, pic.getPixelType(), pic.getWidth(), pic
					.getHeight(), pic.getPixelType());
		}
		IVideoPicture out = IVideoPicture.make(pic.getPixelType(), targetWidth, targetHeight);
		videoResampler.resample(out, pic);
 
		IVideoPictureEvent asc = new VideoPictureEvent(event.getSource(), out, streamId);
		super.onVideoPicture(asc);
		out.delete();
	}
	
	public static int getScaledHeight(String file, int targetWidth) {
		IStream vidStream = MediaTools.getVideoStream(file);
		int width = vidStream.getStreamCoder().getWidth();
		double sf = targetWidth*1.0d/width;
		return (int) Math.ceil(vidStream.getStreamCoder().getHeight()*sf);
	}
	
}
