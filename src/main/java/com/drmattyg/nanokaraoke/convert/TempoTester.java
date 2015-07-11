package com.drmattyg.nanokaraoke.convert;
import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.MidiEventHandlers;

public class TempoTester {

	public static void main(String[] args) {
		MidiFile mf = MidiFile.getInstance(args[0]);
		System.out.println(args[0] + " | " + mf.getTempoHandler().getTempoMap().size());

	}

}
