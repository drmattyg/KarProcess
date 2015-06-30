package com.drmattyg.nanokaraoke.convert;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ProcessKarCli {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("v", true, "Input video");
		options.addOption("m", true, "Input midi file");
		options.addOption("o", true, "Output video file");
		options.addOption("s", true, "Time offset into source video");
		options.addOption("w", true, "Target width (default 720)");

		CommandLineParser parser = new GnuParser();
		try {
			CommandLine cli = parser.parse(options, args);
			String v = cli.getOptionValue("v");
			String m = cli.getOptionValue("m");
			String o = cli.getOptionValue("o");
			String w = cli.getOptionValue("w");
			String s = cli.getOptionValue("s");
			if(v == null || m == null || o == null) {
				HelpFormatter hf = new HelpFormatter();
				hf.printHelp("karConverter", options);
				System.exit(1);
			}
			int width = 720;
			if(w != null) {
				width = Integer.parseInt(w);
			}
			int startTime = 0;
			if(s != null) {
				startTime = Integer.parseInt(s);
			}
			ProcessKar.generateKarVideo(v, m, startTime, o, width);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
