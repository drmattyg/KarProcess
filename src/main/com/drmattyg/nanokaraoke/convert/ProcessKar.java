package com.drmattyg.nanokaraoke.convert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.TrackChunk;
import com.drmattyg.nanokaraoke.TrackEvent;
import com.drmattyg.nanokaraoke.video.KaraokeScreen.KaraokeLine;

public class ProcessKar {
	public static void generateKarVideo(String videoFile, String midiFile, int startTime) {
		
	}
	
	public static File karToWav(String filename) throws Exception {
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
		p.waitFor();
		if(p.exitValue() != 0) throw new Exception("Timidity failed: " + p.getOutputStream());
		return new File(tempFile.getPath());
	}
	
	private static boolean isLineStart(String s) {
		return s.startsWith("/") || s.startsWith("\\");
	}
	
	public static List<KaraokeLine> toKaraokeLines(MidiFile mf) {
		List<KaraokeLine> lines = new ArrayList<KaraokeLine>();
		KaraokeLine kLine = new KaraokeLine();
		for(TrackChunk tc : mf) {
			for(TrackEvent te : tc) {
				if(te.isText() && te.getTimeOffset() > 0) {
					String text = te.getTextEvent().toString();
					if(isLineStart(text)) {
						if(!kLine.isEmpty()) lines.add(kLine);
						kLine = new KaraokeLine();
					}
					kLine.addLyric(te.getTimeOffset(), text.replaceAll("^[/\\\\]", ""));
				}
			}
		}
		return lines;
	}
	
	
}
