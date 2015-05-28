package com.drmattyg.nanokaraoke;

import com.drmattyg.nanokaraoke.MidiFile.VarLength;

public class TextEvent  { // wrapper class for TrackEvent

	protected TextEvent() { }
	public String text;
	TrackEvent te;
	
	public static TextEvent getTextEvent(TrackEvent te) {
		if(!te.isText()) return null;
		TextEvent txt = new TextEvent();
		txt.te = te;
		byte[] bytes = te.getParent().getParent().getBytes();
		int offset = te.getParent().offset;
		VarLength v = VarLength.read(bytes, offset);
		int textOffset = te.time.size + 2;
		txt.text = Utils.byteToString(bytes, v.value, textOffset);
		return txt;
	}


	
	@Override
	public String toString() { return text; }

}
