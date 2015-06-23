package com.drmattyg.nanokaraoke.convert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.TrackChunk;
import com.drmattyg.nanokaraoke.TrackEvent;
import com.drmattyg.nanokaraoke.video.KaraokeScreen.KaraokeLine;

public class ProcessKar {
	
	private static class ConversionFailureException extends Exception {

		private static final long serialVersionUID = 1L;

		public ConversionFailureException(String string) {
			super(string);
		}
		
	}
	public static void generateKarVideo(String videoFile, String midiFile, int startTime) throws Exception {
		MidiFile mf = MidiFile.getInstance(midiFile);
		File wf = karToWav(midiFile);
		
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
