println "Welcome to the Porneoke Player!  Yes, it's lame. Deal with it."
def files = new File('/media/karaoke').listFiles()
.collect { it.name - '/media/karaoke' }
while(true) {
  def input = System.console().readLine('Search: ').toLowerCase()
  if(input.trim().isEmpty()) continue
  def results = files.grep { it.toLowerCase().contains(input) }
  def i = 1
  results.each { println "${i++}) ${it}" }
  println "q) Quit"
  while(true) {
    def choice = System.console().readLine("Selection: ")
    def choiceNum = null
    if(choice.toLowerCase() == 'q') break
    try { choiceNum = Integer.parseInt(choice.trim()) } catch (Exception ex) { continue }
    if(choiceNum <= results.size()) {
      def process = ["/usr/bin/omxplayer", "-b", "/media/karaoke/" + results[choiceNum - 1]].execute()
      process.consumeProcessOutput(new StringBuffer(), new StringBuffer())
      def writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))
      def isDone = false
      while(!isDone) {
	if(System.console().reader().ready()) {
	  // omx player is really hard to kill!  It spawns a new process, and process.destroy() won't kill it.  Gotta find it manually
	  System.console().readLine()
	  def ps = "ps aux".execute()
	  ps.waitFor()
	  def t = ps.text.split(/\n/).grep { it.contains("omxplayer.bin") }[0]
	  def pid = t.split(/\s+/)[1].trim()
	  "kill $pid".execute()
	  isDone = true
	}
	try {
	  process.exitValue()
	  isDone = true
	} catch(Exception ex) {}
	Thread.sleep(100l);
      }
    } else {
      choiceNum = null
    }
    if(choiceNum != null) break
  }
}
