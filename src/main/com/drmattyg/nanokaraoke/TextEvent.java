package com.drmattyg.nanokaraoke;

import com.drmattyg.nanokaraoke.MidiFile.VarLength;

public class TextEvent  { 

	protected TextEvent() { }
	public String text;
	TrackEvent te;
	
	
	// should eventually refactor this to take advantage of the new features like getDataOffset
	public static TextEvent makeTextEvent(TrackEvent te) {
		if(!te.isText()) return null;
		TextEvent txt = new TextEvent();
		txt.te = te;
		byte[] bytes = te.getParent().getParent().getBytes();
		int offset = te.getOffset();
		VarLength v = VarLength.read(bytes, offset + te.time.size + 2);
		int textOffset = offset + te.time.size + 2 + v.size;
		txt.text = Utils.byteToString(bytes, v.value, textOffset);
		return txt;
	}


	
	@Override
	public String toString() { return text; }

}
