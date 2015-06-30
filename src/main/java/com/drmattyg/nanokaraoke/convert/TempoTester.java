package com.drmattyg.nanokaraoke.convert;
import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.MidiEventHandlers;

public class TempoTester {

	public static void main(String[] args) {
		MidiFile.getInstance(args[0]);
		System.out.println(args[0] + " | " + MidiEventHandlers.TEMPO_HANDLER.getTempoMap().size());

	}

}
