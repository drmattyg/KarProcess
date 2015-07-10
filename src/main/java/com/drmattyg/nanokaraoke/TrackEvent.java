package com.drmattyg.nanokaraoke;

import com.drmattyg.nanokaraoke.MidiEventHandler.MetaEventType;
import com.drmattyg.nanokaraoke.MidiFile.VarLength;


public class TrackEvent implements Comparable<TrackEvent> {

	public static class Marker {
		public static int META_MARKER = 0xFF;
		public static int META_TEXT_MARKER = 0x01;
		public static int META_TIME_SIG_MARKER = 0x58;
		public static int META_TEMPO_MARKER = 0x51;
		public static int SYSEX_MARKER = 0xF0;
		public static boolean isMetaMarker(byte b) { return (b & 0xFF) == META_MARKER; }
		public static boolean isTextMarker(byte b) { return (b & 0xFF) == META_TEXT_MARKER; }
		public static boolean isSysexMarker(byte b) { return (b & 0xFF) == SYSEX_MARKER; }
		public static boolean isTimeSigMarker(byte b) { return (b & 0xFF) == META_TIME_SIG_MARKER; }
		public static boolean isTempoMarker(byte b) { return (b & 0xFF) == META_TEMPO_MARKER; }
		
		
	}
	VarLength time;
	VarLength eventLength;
	public enum EventType {
		Meta, Sysex, Midi;
	}
	
	private EventType eventType;
	private MetaEventType metaType;
	private TrackChunk parent;
	private int offset;
	private boolean hasStatusByte;
	private int statusByte;
	private TextEvent txt = null;
	private Integer timeOffset;
	public int getOffset() { return offset; }
	public EventType getEventType() { return eventType; }
	public VarLength getEventTime() { return time; }
	public MetaEventType getMetaType() { return metaType; }
	
	
	// Even if this event has no status byte, TrackChunk.Iterator will set the status byte based on the previous status byte,
	// based on the midi running status convention.  This is important because the current status byte determines the size
	// of the midi event.  This means that hasStatusByte may return false, but getStatusByte may still return a valid status byte
	public boolean hasStatusbyte() { return eventType == EventType.Midi && hasStatusByte; }
	public int getStatusByte() { 
		if(eventType != EventType.Midi) {
			return 0;
		} else {
			return statusByte & 0xFF;
		}
	}
	public boolean isMeta() { return eventType == EventType.Meta; } // that's sooooo meta
	public boolean isText() { return isMeta() && metaType == MetaEventType.TEXT; }
	public boolean isLyrics() { return isMeta() && metaType == MetaEventType.LYRICS; }
	public TextEvent getTextEvent() { return isText()  || isLyrics() ? txt : null; }
	private TrackEvent() {}
	public static TrackEvent getInstance(TrackChunk tc, int offset) {
		TrackEvent te = new TrackEvent();
		te.setParent(tc);
		te.offset = offset;
		int myOffset = offset;
		byte[] b = te.getBytes();
		te.time = VarLength.read(b, myOffset);
		myOffset += te.time.size;
		if(Marker.isMetaMarker(b[myOffset])) {
			te.eventType = EventType.Meta;
			myOffset++;
			te.metaType = MetaEventType.getType(b[myOffset] & 0xFF);
			
			myOffset++;
			te.eventLength = VarLength.read(b, myOffset);
//			if(te.isText() || te.isLyrics()) {
//				te.txt = TextEvent.makeTextEvent(te);
//			}
		} else if(Marker.isSysexMarker(b[myOffset])) {
			te.eventType = EventType.Sysex;
			myOffset += 1; // Fixed a bug here that was causing us to lose meta data, including tempo info
			te.eventLength = VarLength.read(b, myOffset);
		} else {
			te.eventType = EventType.Midi;
			if(isStatusByte(b[myOffset])) {
				te.statusByte = b[myOffset]&0xFF;
				te.hasStatusByte = true;
			} else {
				te.hasStatusByte = false;
			}
		} 
		return te;
		
	}
	
	public byte[] getBytes() { return parent.getParent().getBytes(); }
	private static boolean isStatusByte(byte b) { int b1 = b & 0xFF; return b1 >= 0x80 && b1 < 0xF0; }
	public void setStatusByte(int b) { statusByte = b; }

	public int getMidiEventSize() {
		int meLength = 0;
		if(hasStatusByte) {
			meLength++;
		}
		if((statusByte & 0xC0) == 0xC0 || (statusByte & 0xD0) == 0xD0) {
			meLength += 1;
		} else {
			meLength += 2;
		}
		return meLength;
	}
		
	
	public int getEventLength() {
		switch(eventType) {
			case Meta:
				return eventLength.size + eventLength.value + 2; // 2 bytes for meta info, FF and meta type
			case Midi:
				return getMidiEventSize();
			case Sysex:
				return eventLength.size + eventLength.value + 1; // 1 byte for sysex info, F0 or F7
			default:
				return 0; // should never happen
			
		}
	}
	
	
	public TrackChunk getParent() {
		return parent;
	}
	public void setParent(TrackChunk parent) {
		this.parent = parent;
	}
	
	public int getTotalLength() { 
		return time.size + getEventLength();
	}
	public int getTimeOffset() {
		return timeOffset;
	}
	public void setTimeOffset(int timeOffset) {
		this.timeOffset = timeOffset;
	}
	@Override
	public int compareTo(TrackEvent t) {
		return this.timeOffset.compareTo(t.timeOffset);
	}

// this isn't implemented universally so I can't use this
//	public int getDataOffset() {
//		return metaType.dataOffset + time.size;
//	}
	
	public static int getDataOffset(TrackEvent evt) {
		return evt.offset + evt.metaType.dataOffset + evt.time.size;
	}
	public void setTextEvent(TextEvent evt) {
		// really should clone this instead of just copying reference
		this.txt = evt;
		
	}
	
	
	
}
