package com.drmattyg.nanokaraoke.convert;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.video.KaraokeScreenMediaTool;
import com.drmattyg.nanokaraoke.video.MediaTools;
import com.drmattyg.nanokaraoke.video.MediaTools.VideoCutter;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

public class ProcessKar {
	private static final long FADE_TIME = 5000;
	private static class ConversionFailureException extends Exception {

		private static final long serialVersionUID = 1L;

		public ConversionFailureException(String string) {
			super(string);
		}
		
	}
	public static void generateKarVideo(String videoFile, String midiFile, int startTime, String output) throws Exception {
		MidiFile mf = MidiFile.getInstance(midiFile);
		File wf = karToWav(midiFile);
		long wavFileDuration = MediaTools.getWavFileDuration(wf);
		IMediaWriter writer = MediaTools.makeVideoWriter(videoFile, output, MediaTools.OUTPUT_VIDEO_STREAM_ID);
		MediaTools.addAudioChannel(writer, wf.getPath(), MediaTools.OUTPUT_AUDIO_STREAM_ID);
		VideoCutter vc = VideoCutter.getInstance(startTime, wavFileDuration + FADE_TIME*2, FADE_TIME, writer);
		IMediaReader vidReader = ToolFactory.makeReader(videoFile);
		KaraokeScreenMediaTool ks = KaraokeScreenMediaTool.getInstance(mf, FADE_TIME);
//		IMediaWriter cutWriter =  vc.cutVideo(vidReader, Collections.singletonList(ks), MediaTools.OUTPUT_VIDEO_STREAM_ID);
		IMediaWriter cutWriter =  vc.cutVideo(vidReader, Collections.EMPTY_LIST, MediaTools.OUTPUT_VIDEO_STREAM_ID);
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
