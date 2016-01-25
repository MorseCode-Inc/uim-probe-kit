package inc.morsecode;

import java.io.PrintStream;

import com.nimsoft.nimbus.NimLog;

public class NimLogPrintWriter extends PrintStream {
	
	NimLog log= NimLog.getLogger(NimLogPrintWriter.class);
	int severity= NimLog.INFO;
	
	public NimLogPrintWriter(NimLog log, int severity) {
		super(new RedirectedOutputStream(null));
		this.severity= severity;
		this.log= log;
	}
	
	
	@Override
	public void println() { log(""); }
	
	@Override
	public void println(int x) { log(""+ x); }
	public void println(float x) { log(""+ x); }
	public void println(double x) { log(""+ x); }
	public void println(long x) { log(""+ x); }
	public void println(boolean x) { log(""+ x); }
	public void println(String x) { log(""+ x); }
	public void println(Object o) { log(""+ o); }
	
	
	@Override
	public void print(int i) { log(""+ i); }
	public void print(float i) { log(""+ i); }
	public void print(double i) { log(""+ i); }
	public void print(long i) { log(""+ i); }
	public void print(boolean i) { log(""+ i); }
	public void print(String i) { log(""+ i); }
	public void print(Object i) { log(""+ i); }
	public void print(char i) { log(""+ i); }
	public void print(char[] i) { log(""+ new String(i)); }
	
	private void log(String string) {
		
		for(String line : string.split("\r\n")) {
			log.log(severity, line);
		}
	}


	
	
	
	
	
}
