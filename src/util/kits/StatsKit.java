package util.kits;

import java.text.NumberFormat;
import java.util.Date;

import util.structs.SimpleQueue;






/**
* 050304 mjs
* StatsKit is a utility which collects statistics as a simple
* average or has a frequence distribution (histogram). Text ouput
* methodes provide a simple output suitable for reporting several
* metrics / parameters in text format.  Layout along line is consistant
* with other lines so text can be stacked for more human readable display
* in logs.
* 
*/
public class StatsKit {

	public static final int MEAN_AVG= 1;
	public static final int HISTOGRAM= 2;

	private static final int DEFAULT_LIMITS[]= { 20,50,100,200,500,1000,2000,5000,10000 };

	private int statistic;			// type of statistic: MEAN, HISTO, ...

	private int buckets[];			// frequency distribution
	private int limits[];			// bucket interval limit[i-1] <= x < limit[i]
	private int bucketDispWidth;		// dispaly width of bucket
	private String bucketSeparator;		// single char string, delimit 'bucket' values on output
	private String limitSeparator;		// single char string, delimit 'limit' values on output

	private int n;				// number of numbers being averaged (denominator)
	private long sigmaX;			// sum of values being averaged
	private long sigmaXsquared;		// sum of values^2
	private long peak;			// maximum data value reported
	private long min;			// minimum data value reported

	private SimpleQueue oneSecondWindow= new SimpleQueue();
	private long pp1s= 0;
	
	private SimpleQueue oneMinuteWindow= new SimpleQueue();
	private long pp1m= 0;
	
	private SimpleQueue fiveMinuteWindow= new SimpleQueue();
	private long pp5m= 0;
	
	private SimpleQueue oneHourWindow= new SimpleQueue();
	private long pp1h= 0;
	
	private SimpleQueue oneDayWindow= new SimpleQueue();
	private long pp1d= 0;
	
/**
* Initialize a simple (mean) average statistic
*/
public StatsKit() {
	statistic= MEAN_AVG;
} // StatsKit


/**
* Initialize a Historgram statisitic / metrics
* Note: null -> default limits
*/
public StatsKit(int limits[]) {
	this.limits= ((limits==null)? DEFAULT_LIMITS : limits);
	buckets= new int[this.limits.length + 1];
	bucketDispWidth= 6;
	statistic= HISTOGRAM;
	bucketSeparator= "|";
	limitSeparator= "|";
} // StatusKit


/**
* Set the 'bucket' display width to specified number of chars
* Note: chars includes separator between buckets in width.
*/
public int setBucketDispWidth(int chars) {
	int prevWidth= bucketDispWidth;
	bucketDispWidth= chars;
	return (prevWidth);
} // setBucketDispWidth


/**
* Set the separator characters displayed between 'bucket' values
*/
public char setBucketSeparator(char separator) {
	char prevSep= bucketSeparator.charAt(0);
	bucketSeparator= ""+ separator;
	return (prevSep);
} // setBucketSeparator


/**
* Set the separator characters displayed between 'limit' values.
*/
public char setLimitSeparator(char separator) {
	char prevSep= limitSeparator.charAt(0);
	limitSeparator= ""+ separator;
	return (prevSep);
} // setLimitSeparator

/**
* Update 
* 
* for calculating peak number of events over a time interval
* 
*/
public void update(Date time) {
	String ts= DateKit.format(time.getTime());
	
	long millis= time.getTime();
	
	// store strings
	oneSecondWindow.enqueue(ts);
	oneMinuteWindow.enqueue(ts);
	fiveMinuteWindow.enqueue(ts);
	oneHourWindow.enqueue(ts);
	oneDayWindow.enqueue(ts);
	
	String o= null;
	
	while ((o= (String)oneSecondWindow.peek()) != null) {
		Date d= DateKit.safeToDate(o, DateKit.STD_FMT);
		long seconds= (millis - d.getTime()) / 1000;
		if (seconds > 1) {
			oneSecondWindow.dequeue();
		} else {
			break;
		}
	}
	
	while ((o= (String)oneMinuteWindow.peek()) != null) {
		Date d= DateKit.safeToDate(o, DateKit.STD_FMT);
		long seconds= (millis - d.getTime()) / 1000;
		if (seconds > 60) {
			oneMinuteWindow.dequeue();
		} else {
			break;
		}
	}
	
	while ((o= (String)fiveMinuteWindow.peek()) != null) {
		Date d= DateKit.safeToDate(o, DateKit.STD_FMT);
		long seconds= (millis - d.getTime()) / 1000;
		if (seconds > 300) {
			fiveMinuteWindow.dequeue();
		} else {
			break;
		}
	}
	
	while ((o= (String)oneHourWindow.peek()) != null) {
		Date d= DateKit.safeToDate(o, DateKit.STD_FMT);
		long minutes= (millis - d.getTime()) / 1000 / 60;
		if (minutes > 60) {
			oneHourWindow.dequeue();
		} else {
			break;
		}
	}
	
	while ((o= (String)oneDayWindow.peek()) != null) {
		Date d= DateKit.safeToDate(o, DateKit.STD_FMT);
		long hours= (millis - d.getTime()) / 1000 / 60 / 60;
		if (hours > 24) {
			oneDayWindow.dequeue();
		} else {
			break;
		}
	}
	
	if (oneSecondWindow.size() > pp1s) { pp1s= oneSecondWindow.size(); }
	if (oneMinuteWindow.size() > pp1m) { pp1m= oneMinuteWindow.size(); }
	if (fiveMinuteWindow.size() > pp5m) { pp5m= fiveMinuteWindow.size(); }
	if (oneHourWindow.size() > pp1h) { pp1h= oneHourWindow.size(); }
	if (oneDayWindow.size() > pp1d) { pp1d= oneDayWindow.size(); }
	
	
}

/**
* Update frequency statistics (i.e. a bucket)
*/
public void update(long value) {

// if (value < 0 || 100000 < value) {
// System.out.println("wow! "+ value);
// }
	switch (statistic) {
	case MEAN_AVG:
		n++;
		sigmaX+= value;
		sigmaXsquared+= value * value;
		if (peak < value) { 
			peak= value;
		} else if (value < min) {
			min= value;
		}
		break;
	case HISTOGRAM:
		boolean overflowBucket= true;

		n++;
		sigmaX+= value;
		sigmaXsquared+= value * value;
		if (peak < value) { peak= value;
		} else if (value < min) { min= value;
		}

		for (int k= 0; k<limits.length; k++) {
			if (value < limits[k]) {
				buckets[k]++;
				overflowBucket= false;
				break;
			}
		} // for buckets
		if (overflowBucket) {
			buckets[buckets.length-1]++;
		}
		break;
	}

} // update


/**
* Return number of data points
*/
public int n() {
	return (n);
} // n


public String meanAvg() { return(meanAvg(null, false)); }
public String meanAvg(String units) { return(meanAvg(units, false)); }
public String meanAvg(boolean html) { return(meanAvg(null, html)); }
/**
* Report snapshot of results as text line formatted appropriate
* for mean/avg.
*/
public String meanAvg(String units, boolean html) {
	
	NumberFormat nf= NumberFormat.getInstance();
	nf.setMaximumFractionDigits(3);
	nf.setMinimumFractionDigits(3);
	
	String a= "";

	if (n != 0) {
		String avg= ""+ nf.format((float)sigmaX / (float)n);
		// if (6 < avg.length()) { avg= avg.substring(0,7); }
		a= avg;
		if (1 < n) {						// 2 or more data points for var/std-dev
			float var= ((float)sigmaXsquared - ((float)sigmaX * sigmaX / n)) / (n - 1);
			String stdDev= ""+ nf.format((float)Math.sqrt((double)var));
			// if (6 < stdDev.length()) { stdDev= stdDev.substring(0,7); }
			a+= (html ? "<td>" : " ") +"(+/- "+ stdDev +"s)"+ (html ? "</td>" : "");
		}
		a+= (html ? "<td>" : "  ") +"("+ min +"m < "+ sigmaX +"t/"+ n +"n < "+ peak +"p)"+ (html ? "</td>" : "");
	} else {
		a= (html ? "<td>" : "") +"- (n=0, no data)"+ (html ? "</td>" : "");		// no data yet
	}

	if (units != null) {
		a+= (html ? "<td>" : "  ") +"("+ units +")"+ (html ? "</td>" : "");
	}

	return (a);
} // meanAvg


/**
* Report snapshot of results as text line formatted appropriate
* for histogram.
*/
public String histogram() { return(histogram(null)); }
public String histogram(String units) {
//	String h= "|";
	String h= bucketSeparator;

	int sn= 0;
	switch (statistic) {
	case MEAN_AVG:			// n.a.
		break;
	case HISTOGRAM:
		for (int k= 0; k<buckets.length; k++) {
			sn+= buckets[k];
			h+= fmtFreq(buckets[k]);
		}
		break;
	}

	if (n == 0) {
		// no data yet
		h+= "  (n=0, no data)";
	}
	
	if (units != null) {
		h+= "  ("+ units +")";
	}
	if (n != sn) {
		h+= "  ("+ sn +"!="+ n +")";
	}

	return (h);
} // historgram

public String peaks(String units) { return peaks(units, false); }
public String peaks(String units, boolean html) {
	String p= "";
	
	p+= "Peak over 1 second interval: "+ pp1s +" ("+ units +")";
	p+= "\nPeak over 1 minute interval: "+ pp1m +" ("+ units +")";
	p+= "\nPeak over 5 minute interval: "+ pp5m +" ("+ units +")";
	p+= "\nPeak over 1 hour interval  : "+ pp1h +" ("+ units +")";
	p+= "\nPeak over 1 day interval   : "+ pp1d +" ("+ units +")";
	
	return p;
}


/**
* Generate ledgend line aligned for histogram statistics
*/
public String ledgend() { return(ledgend(null)); }
public String ledgend(String units) { 
	String l= "";

	switch (statistic) {
	case MEAN_AVG:		// n.a.
		break;
	case HISTOGRAM:
		l= "";
		for (int k= 0; k<buckets.length; k++) {
			l+= fmtLimit((k==0)? 0 : limits[k-1]);
		}
		if (units != null) {
			l+= "   ("+ units +")";
		}
		break;
	}

	return l;
} // ledgend


/**
* Format a bucket frequency
*/
private String fmtFreq(int freq) {
	String bucket= "";
	
	if (freq == 0) {
		bucket+= ".";
	} else if (freq < 10000) {
		bucket+= freq;
	} else if (freq < 10000000) {
		bucket+= (freq/1000) +"k";
	} else if (freq < 10000000000L) {
		bucket+= (freq/1000000) +"m";
	} else {
		bucket+= (freq/1000000000) +"b";
	}

//	bucket= bucket +"|";
	bucket= bucket + bucketSeparator;
	while (bucket.length() < bucketDispWidth) { 
		bucket= " "+ bucket; 
	}

	return (bucket);
} // fmtFreq


/**
* Format a bucket limit
*/
private String fmtLimit(int limit) {
//	String bucket= "|"+ limit;
	
	String bucket= "";
	
	bucket= limitSeparator + limit;
	while (bucket.length() < bucketDispWidth) { 
		bucket+= " "; 
	}
	return (bucket);
} // fmtLimit


public void clear() {

}

} // class HistogramKit


