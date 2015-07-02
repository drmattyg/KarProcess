import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.MidiEventHandlers;

MidiFile.getInstance(args[0])
println args[0] + " | " + MidiEventHandlers.TEMPO_HANDLER.getTempoMap().size()
