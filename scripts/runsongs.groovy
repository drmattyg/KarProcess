#!/usr/local/bin/goovy                                                                                                                                    
import com.drmattyg.nanokaraoke.convert.ProcessKar
import com.drmattyg.nanokaraoke.Utils
import com.drmattyg.nanokaraoke.video.MediaTools

def VIDS = "/Users/mgordon/test/karaoke/video/"
def KARFILE_DIR = "/Users/mgordon/test/karaoke/karfile/"
def OUTDIR = "/Users/mgordon/test/karaoke/output/"
def FADE_TIME = 5
def FFMPEG = "/usr/local/bin/ffmpeg"
System.setProperty("timidity.exec", "/usr/local/bin/timidity")

def songlistfile = args[0]
def songs = new File(songlistfile).readLines().collect { it.trim() } 
def videos = "ls ${VIDS}".execute().text.split(/\n/);

songs.each { song ->
  println "${new Date()} Starting"
  def vid = VIDS + videos[(int)Math.floor(Math.random()*videos.size())]
  song = KARFILE_DIR + song
  def outputFile = OUTDIR + (song.split(/\./)[0] + ".mp4").split(/\//)[-1]
  println outputFile
  try { 

        // cut and convert the file using FFMPEG on the CLI.  This appears to lead to much faster output
        // and fixes some timing problems.  It's a hack.
        println "${new Date()} Converting with FFMPEG"
        def vid_output_temp = "/tmp/vout_${new Date().getTime()}.mp4"
        def vid_duration = Utils.getVideoLength(vid)/1000
        def waveFile = ProcessKar.karToWav(song)
        def song_duration = MediaTools.getWavFileDuration(waveFile)/1000
        def min_offset = 60
        def total_duration = (int)((FADE_TIME*2 + song_duration) + 10 + min_offset)

        // pick a random point in the file

        def start_time = (int)(Math.ceil(Math.random()*(vid_duration - total_duration)))

        def ffmpeg_input_opts = "${FFMPEG} -loglevel quiet -ss ${start_time} -t ${total_duration} -i".split(/ /)
        def ffmpeg_output_opts = "-strict -2".split(/ /)
        def cmd = [ffmpeg_input_opts, vid, ffmpeg_output_opts, vid_output_temp].flatten()
        println cmd.join(" ")
        def p = cmd.execute()
        p.waitFor()
        println "${new Date()} Running generateKarVideo"
        ProcessKar.generateKarVideo(vid_output_temp, song, waveFile.getPath(), 0, outputFile, 720)
        new File(vid_output_temp).delete()
        println "${new Date()} Done"
  } catch(Exception ex) { println "FAILED:"; ex.printStackTrace() }
}