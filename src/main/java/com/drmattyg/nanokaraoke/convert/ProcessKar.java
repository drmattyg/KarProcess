package com.drmattyg.nanokaraoke.convert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.video.KaraokeScreenMediaTool;
import com.drmattyg.nanokaraoke.video.MediaTools;
import com.drmattyg.nanokaraoke.video.Rescaler;
import com.drmattyg.nanokaraoke.video.MediaTools.OverlayAudioTool;
import com.drmattyg.nanokaraoke.video.MediaTools.VideoCutter;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

public class ProcessKar {
	private static final long FADE_TIME = 5000;
	private static final int TARGET_WIDTH = 720;
	private static class ConversionFailureException extends Exception {

		private static final long serialVersionUID = 1L;

		public ConversionFailureException(String string) {
			super(string);
		}
		
	}
	public static void generateKarVideo(String videoFile, String midiFile, int startTime, String output, int targetWidth) throws Exception {
		MidiFile mf = MidiFile.getInstance(midiFile);
		File wf = karToWav(midiFile);
		System.out.println(wf.getPath());
		Rescaler r = Rescaler.getInstance(targetWidth, videoFile, MediaTools.OUTPUT_VIDEO_STREAM_ID);
		int height = r.getTargetHeight();
		IMediaWriter writer = MediaTools.makeVideoWriter(output, ICodec.findEncodingCodecByName("mpeg4"), targetWidth, height, MediaTools.OUTPUT_VIDEO_STREAM_ID);
		long wavFileDuration = MediaTools.getWavFileDuration(wf);
		MediaTools.addAudioChannel(writer, wf.getPath(), MediaTools.OUTPUT_AUDIO_STREAM_ID);
		VideoCutter vc = VideoCutter.getInstance(startTime, wavFileDuration + 2*FADE_TIME, FADE_TIME, writer);
		IMediaReader vidReader = ToolFactory.makeReader(videoFile);
		KaraokeScreenMediaTool ks = KaraokeScreenMediaTool.getInstance(mf, 5000);
		
		List<MediaToolAdapter> filters = new ArrayList<MediaToolAdapter>();
		// adding the rescaler first
		filters.add(r);
		filters.add(ks);
		IMediaWriter cutWriter =  vc.cutVideo(vidReader, filters, MediaTools.OUTPUT_VIDEO_STREAM_ID);
		System.out.println(wf.getPath());
		OverlayAudioTool oat = OverlayAudioTool.getInstance(wf.getPath(), 5000, cutWriter, MediaTools.OUTPUT_AUDIO_STREAM_ID);
		IMediaReader au = ToolFactory.makeReader(wf.getPath());
		
		au.addListener(oat);
		while(au.readPacket() == null);
		
		cutWriter.close();
		
	}
	
	public static File karToWav(String filename) throws IOException, ConversionFailureException {
		String timidity = System.getProperty("timidity.exec");
		if(timidity == null) {
			throw new IllegalArgumentException("timidity.exec property must be specified");
		}
		File tf = new File(timidity);
		if(!tf.exists() || !tf.isFile() || !tf.canExecute()) {
			throw new IllegalArgumentException("timidity.exec must specify the timidity executable");
		}
		File karFile = new File(filename);
		if(!karFile.exists() || !karFile.isFile() || !karFile.canRead()) {
			throw new IllegalArgumentException("Kar file is not readable");
		}
		File tempFile = File.createTempFile("kpout", ".wav");
		String tempFilename = tempFile.getPath();
		tempFile.delete();
		String command = timidity + " -Ow -o " + tempFilename + " " + filename;
		Process p = Runtime.getRuntime().exec(command);
		while(true) {
			try {
				p.waitFor();
				break;
			} catch (InterruptedException e) {}
		}
		if(p.exitValue() != 0) throw new ConversionFailureException("Timidity failed: " + p.getOutputStream());
		return new File(tempFile.getPath());
	}
	
	
	
}
