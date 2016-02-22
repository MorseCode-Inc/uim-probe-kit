package util.kits;

import inc.morsecode.util.StrUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;



/**
 * <pre> SimpleDateFormat meta-characters
Letter  Date or Time Component  Presentation  	Examples  
------	----------------------	------------	--------------
G  		Era designator  		Text  			AD  
y  		Year  					Year  			1996; 96  
M  		Month in year  			Month  			July; Jul; 07  
w  		Week in year  			Number  		27  
W  		Week in month  			Number  		2  
D  		Day in year  			Number  		189  
d  		Day in month  			Number  		10  
F  		Day of week in month  	Number  		2  
E  		Day in week  			Text  			Tuesday; Tue  
a  		Am/pm marker  			Text  			PM  
H  		Hour in day (0-23)  	Number  		0  
k  		Hour in day (1-24)  	Number  		24  
K  		Hour in am/pm (0-11)  	Number  		0  
h  		Hour in am/pm (1-12)  	Number  		12  
m  		Minute in hour  		Number  		30  
s  		Second in minute  		Number  		55  
S  		Millisecond  			Number  		978  
z  		Time zone  General time zone  Pacific Standard Time; PST; GMT-08:00  
Z  		Time zone  RFC 822 time zone  -0800  

Examples:
Date and Time Pattern  					Result
------------------------------			-----------------------------  
"yyyy.MM.dd G 'at' HH:mm:ss z"  		2001.07.04 AD at 12:08:56 PDT  
"EEE, MMM d, ''yy"  					Wed, Jul 4, '01  
"h:mm a"  								12:08 PM  
"hh 'o''clock' a, zzzz"  				12 o'clock PM, Pacific Daylight Time  
"K:mm a, z"  							0:08 PM, PDT  
"yyyyy.MMMMM.dd GGG hh:mm aaa"  		02001.July.04 AD 12:08 PM  
"EEE, d MMM yyyy HH:mm:ss Z"  			Wed, 4 Jul 2001 12:08:56 -0700  
"yyMMddHHmmssZ"  						010704120856-0700  

</pre>

 */
public class DateKit extends Date{
	
	// from java.util.Date
	private static final long serialVersionUID = 7523967970034938905L;
	
	public static final String MYSQL_FMT="";
	public static final String MONTH_YEAR="MMM yyyy";
	public static final String YEAR_MONTH="yyyy-MM";
	public static final String SIMPLE="yyyy-MM-dd HH:mm";
	public static final String SIMPLE_TZ="yyyy-MM-dd HH:mm z";
	public static final String STD_DATE="yyyy-MM-dd";
	public static final String DATE_HR_MIN="yyyy-MM-dd HH:mm";
	public static final String STD_FMT="yyyy-MM-dd HH:mm:ss";
	public static final String ORCL_FMT="yyyy-MM-dd HH:mm:ss.S";
	public static final String ABBR_MO_FMT="yyyy-MMM-dd HH:mm:ss";
	public static final String COMMON_FMT="MM/dd/yyyy HH:mm:ss";
	public static final String COMMON_DATE="MM/dd/yyyy";
	public static final String MONTH_DAY_YEAR="MMMM d, yyyy";
	public static final String TIMEKEY="yyyyMMddHH";
	public static final String ISO8601="yyyyMMdd'T'HH:mm:ss";
	
	public static final String TZ_EASTERN= "US/Eastern";
	public static final String TZ_CENTRAL= "US/Central";
	public static final String TZ_MOUNTAIN= "US/Mountain";
	public static final String TZ_ARIZONA= "US/Arizona";
	public static final String TZ_PACIFIC= "US/Pacific";
	public static final String TZ_ALASKA= "US/Alaska";
	public static final String TZ_ANTARTICA= "Antartica/Vostok";
	public static final String TZ_LONDON= "Europe/London";
	public static final String TZ_NORWAY= "Europe/Norway";
	public static final String TZ_GMT= "GMT";
	
	public static final String[] supportedDateFormats= new String[] { 
		SIMPLE_TZ 
		, STD_FMT 
		, ORCL_FMT 
		, SIMPLE
		, STD_DATE 
		, ABBR_MO_FMT 
		, COMMON_FMT
		, COMMON_DATE
	};
	
	public static final String USAGE=
		DateKit.class.getName() +" <mode> <arguments>"
		+"\nmodes: -c | -d2es | -es2d | -s"
		+"\n -c (convert a date/time to a different format)"
		+"\n example: -c '2005-Aug-23' 'yyyy-MMM-dd' 'MMMM dd, yyyy'"
		+"\n arguments: <date> <input-fmt> [output-fmt]"
		+"\n"
		
		+"\n -d2es (translate a date to epoch seconds)"
		+"\n example: -d2es 'August 23, 2005 15:20:00' 'MMMM dd, yyyy HH:mm:ss'"
		+"\n arguments: <date> <input-fmt>"
		+"\n"
		
		+"\n -es2d (translate epoch seconds to a date)"
		+"\n example: -es2d 1124912388"
		+"\n arguments: <epoch seconds> [output-fmt]"
		+"\n"
		
//		+"\n -s (subtract from input-date)"
//		+"\n example: -s '2005-08-15' 'yyyy-MM-dd' 2d"
//		+"\n arguments <date> <input-fmt> <n>[d | h | m | s][...]"
//		+"\n\t where:"
////		+"\n\t y = years"
////		+"\n\t M = months"
//		+"\n\t d = days (default if none specified [ie. -s 5])"
//		+"\n\t h = hours"
//		+"\n\t m = minutes"
//		+"\n\t s = seconds"
//		+"\n"
		
		;
	
	
	public static final String FMT_CHART=
		"Date format instruction codes:"
		+"\nLetter\tDate or Time Component\tPresentation\tExamples"  
		+"\n------\t----------------------\t------------\t--------------"
		+"\nG\tEra designator\t\tText\t\tAD"
		+"\ny\tYear\t\t\tYear\t\t1996; 96"  
		+"\nM\tMonth in year\t\tMonth\t\tJuly; Jul; 07"  
		+"\nw\tWeek in year\t\tNumber\t\t27"
		+"\nW\tWeek in month\t\tNumber\t\t2"
		+"\nD\tDay in year\t\tNumber\t\t189"  
		+"\nd\tDay in month\t\tNumber\t\t10"
		+"\nF\tDay of week in month\tNumber\t\t2"
		+"\nE\tDay in week\t\tText\t\tTuesday; Tue"  
		+"\na\tAm/pm marker\t\tText\t\tPM"
		+"\nH\tHour in day (0-23)\tNumber\t\t0"
		+"\nk\tHour in day (1-24)\tNumber\t\t24"  
		+"\nK\tHour in am/pm (0-11)\tNumber\t\t0"
		+"\nh\tHour in am/pm (1-12)\tNumber\t\t12"  
		+"\nm\tMinute in hour\t\tNumber\t\t30"
		+"\ns\tSecond in minute\tNumber\t\t55"
		+"\nS\tMillisecond\t\tNumber\t\t978" 
		+"\n\n"
		+"\nExamples:"
		+"\nDate and Time Pattern\t\tResult"
		+"\n------------------------------\t-----------------------------" 
		+"\n\"yyyy.MM.dd G 'at' HH:mm:ss z\"\t2001.07.04 AD at 12:08:56 PDT"  
		+"\n\"EEE, MMM d, ''yy\"\t\tWed, Jul 4, '01"  
		+"\n\"h:mm a\"\t\t\t12:08 PM"
		+"\n\"hh 'o''clock' a, zzzz\"\t\t12 o'clock PM, Pacific Daylight Time"  
		+"\n\"K:mm a, z\"\t\t\t0:08 PM, PDT"
		+"\n\"yyyyy.MMMMM.dd GGG hh:mm aaa\"\t02001.July.04 AD 12:08 PM"
		+"\n\"EEE, d MMM yyyy HH:mm:ss Z\"\tWed, 4 Jul 2001 12:08:56 -0700"  
		+"\n\"yyMMddHHmmssZ\"\t\t\t010704120856-0700"  
		;
	
	
	protected static final int HELP= -1;
	protected static final int CONV= 1;
	protected static final int D2ES= 2;
	protected static final int ES2D= 3;
	protected static final int SUBT= 4;
	protected int mode= HELP;
	public String errMsg;
	public String msg;
	
	protected Hashtable parameters;
	
	
	
public DateKit() {
	super(System.currentTimeMillis());
	parameters= new Hashtable();
}

/*
 * parse parameters, set mode flags and setup variables
 */
public boolean parseParms(String[] parms) {
	errMsg= null;
	
	if (parms.length < 1) {
		return false;
	}
	
	int a= 0;
	
	String mode= parms[a++];
	
	if (mode.equals("-?")) {
		this.mode= HELP;
		return true;
	} else if (mode.equals("-c")) {
//		-c (convert a date/time to a different format)"
//		example: -c '2005-Aug-23' 'yyyy-MMM-dd' 'MMMM dd, yyyy'
//		arguments: <date> <input-fmt> [output-fmt]
		this.mode= CONV;
		if ((a+1) >= parms.length) {
			errMsg= "missing required parameters: <date> <input-fmt> [output-fmt]";
			return false;
		}
		
		parameters.put("date", parms[a++]);
		parameters.put("inputFmt", parms[a++]);
	
		if (a >= parms.length) {
			parameters.put("outputFmt", STD_FMT);
		} else {
			parameters.put("outputFmt", parms[a++]);
		}
	} else if (mode.equals("-d2es")) {
//		-d2es (translate a date to epoch seconds)
//		example: -d2es 'August 23, 2005 15:20:00' 'MMMM dd, yyyy HH:mm:ss'
//		arguments: <date> <input-fmt>	
		this.mode= D2ES;
		if ((a+1) >= parms.length) {
			errMsg= "missing required parameters: <date> <input-fmt>";
			return false;
		}
		
		parameters.put("date", parms[a++]);
		parameters.put("inputFmt", parms[a++]);
	
	} else if (mode.equals("-es2d")) {
//		-es2d (translate epoch seconds to a date)
//		example: -es2d 1124912388
//		arguments: <epoch seconds> [output-fmt]
		this.mode= ES2D;
		if ((a) >= parms.length) {
			errMsg= "missing required parameters: <epoch seconds> [output-fmt]";
			return false;
		}
		
		parameters.put("es", parms[a++]);
		
		if (a >= parms.length) {
			parameters.put("outputFmt", STD_FMT);
		} else {
			parameters.put("outputFmt", parms[a++]);
		}

//	} else if (mode.equals("-s")) {
////		-s (subtract from input-date)
////		example: -s '2005-08-15' 'yyyy-MM-dd' 5M2d
////		arguments <date> <input-fmt> <n>[d | h | m | s]n[...]
//		this.mode= SUBT;
//		if ((a+2) >= parms.length) {
//			errMsg= "missing required parameters: <date> <input-fmt> <n>[d | h | m | s][...]";
//			return false;
//		}
//		
//		parameters.put("date", parms[a++]);
//		parameters.put("inputFmt", parms[a++]);
//		parameters.put("subtractTime", parms[a++]);
		
	} else {
		errMsg= "Invalid Mode '"+ mode +"'";
		return false;
	}
	
	return true;
} // parseParms

/*
 * execute 
 */
public int exec() {
	
	String dte;
	String inFmt;
	String outFmt;
	long es;
	
	switch (mode) {
	case HELP:
		System.out.println(USAGE);
		System.out.println(FMT_CHART);
		break;
	case CONV:
		// convert
		dte= (String)parameters.get("date");
		inFmt= (String)parameters.get("inputFmt");
		outFmt= (String)parameters.get("outputFmt");
		try {
			System.out.println(convert(dte, inFmt, outFmt));
		} catch (ParseException px) {
			//System.err.println("ERR "+ px.toString());
			errMsg= px.toString();
			return -1;
		}
		break;
	case D2ES:
		// d2es
		dte= (String)parameters.get("date");
		inFmt= (String)parameters.get("inputFmt");
		
		try {
			System.out.println(toDate(dte, inFmt).getTime() / 1000);
		} catch (ParseException px) {
			//System.err.println("ERR "+ px.toString());
			return -1;
		}
		break;
	case ES2D:
		// es2d
		try {
			es= Long.parseLong((String)parameters.get("es"));
		} catch (NumberFormatException nfx) {
			errMsg= nfx.toString();
			return -2;
		}
		
		outFmt= (String)parameters.get("outputFmt");
		try {
			System.out.println(format(es * 1000, outFmt, TimeZone.getDefault()));
		} catch (IllegalArgumentException iax) {
			//System.err.println("ERR "+ px.toString());
			errMsg= "illegal date format\n"+ iax.toString();
			return -1;
		}
		break;
//	case SUBT:
//		// subtract
//		
//		dte= (String)parameters.get("date");
//		inFmt= (String)parameters.get("inputFmt");
//		
//		StringTokenizer parser= new StringTokenizer((String)parameters.get("subtractTime"), "dhms", true);
//		long time= 0;
//		
//		while (parser.hasMoreTokens()) {
//
//			char fmt;
//			try {
//				
//				int n= Integer.parseInt(parser.nextToken());
//
//				if (parser.hasMoreTokens()) {
//					fmt= parser.nextToken().charAt(0);
//				} else {
//					// did not specify time interval, default is days
//					fmt= 'd';
//				}
//				
//				switch (fmt) {
//				case 'd': // days
//					time+= (n * 60000 * 60 * 24);
//					break;
//				case 'h': // hours
//					time+= (n * 60000 * 60);
//					break;
//				case 'm': // minutes
//					time+= (n * 60000);
//					break;
//				case 's': // seconds
//					time+= (n * 1000);
//					break;
//				}
//				
//			} catch (NumberFormatException nfx) {
//				
//			}
//			
//			
//		}
//		try {
//			Date d= toDate(dte, inFmt);
//			System.out.println(format(d.getTime() - time));
//			
//		} catch (ParseException px) {
//			//System.err.println("ERR "+ px.toString());
//			return -1;
//		}
//		
//		break;
	}
	return 0;	// success
}
/*
 * convert a date with the given format, into 'YYYY-MM-DD HH:MM:SS'
 */
public static String convert(String dte, String fmt) throws ParseException {
	return convert(dte, fmt, STD_FMT);
}
/*
 * convert a date with the given format, into specified fmt
 */
public static String convert(String dte, String fmt, String outFmt) throws ParseException {
	SimpleDateFormat input= new SimpleDateFormat(fmt);
	SimpleDateFormat output= new SimpleDateFormat(outFmt);
	return output.format(new Date(input.parse(dte).getTime()));
}

public static String format(Date d) {
	return format(d, (TimeZone)null);
}

/*
 * format a long value into the default date format (yyyy-MM-dd HH:mm:ss)
 */
public static String format(Date d, TimeZone tz) {
	SimpleDateFormat formatter= new SimpleDateFormat(STD_FMT);
	if (tz == null) {
		tz= TimeZone.getDefault();
	}
	formatter.setTimeZone(tz);
	return format(d, formatter);
}

public static String format(long es) {
	return format(es, (TimeZone)null);
}
/*
 * format a long value into the default date format
 */
public static String format(long es, TimeZone tz) {
	return format(es, STD_FMT, tz);
}

public static String format(long es, String fmt) throws IllegalArgumentException {
	return format(es, fmt, null);
}
/*
 * format a long value into the specified date format
 */
public static String format(long es, String fmt, TimeZone tz) throws IllegalArgumentException {
	SimpleDateFormat formatter = new SimpleDateFormat(fmt);
	if (tz != null) {
		formatter.setTimeZone(tz);
	}
	return format(new Date(es), formatter);
}

public static String format(GregorianCalendar date, String fmt) throws IllegalArgumentException {
	return format(date, fmt, date.getTimeZone());
}

public static String format(GregorianCalendar date, String fmt, TimeZone tz) throws IllegalArgumentException {
	DateFormat formatter= new SimpleDateFormat(fmt);
	if (tz != null) {
		formatter.setTimeZone(tz);
	}
	return formatter.format(date.getTime());
	// return format(date.getTime(), formatter);
}

/*
public static String format(Calendar date) throws IllegalArgumentException {
	return format(date, true);
}
*/

public static String format(Calendar date, boolean withTz) throws IllegalArgumentException {

	int year= date.get(Calendar.YEAR);
	int month= date.get(Calendar.MONTH) + 1;
	int day= date.get(Calendar.DAY_OF_MONTH);
	int hour= date.get(Calendar.HOUR_OF_DAY);
	int minute= date.get(Calendar.MINUTE);
	int second= date.get(Calendar.SECOND);
	String tz= date.getTimeZone().getID();
	
	String y= pad(year, 4);
	String m= pad(month, 2);
	String d= pad(day, 2);
	String h= pad(hour, 2);
	String i= pad(minute, 2);
	String s= pad(second, 2);
	
	if (withTz) {
		return (y +"-"+ m +"-"+ d +" "+ h +":"+ i + ":" + s +" "+ tz);
	} else {
		return (y +"-"+ m +"-"+ d +" "+ h +":"+ i + ":" + s);
	}
}

private static String pad(int n, int zeros) {
	String s= ""+ n;
	while (s.length() < zeros) {
		s= "0"+ s;
	}
	return s;
}

public static String format(Date d, SimpleDateFormat f) {
	if (d == null || f == null) {
		return "";
	}
	return f.format(d);
}

public static Date safeToDate(String dte, String fmt) {
	return safeToDate(dte, fmt, null);
}

/*
 * create a date object from a string, and a format
 */
public static Date safeToDate(String dte, String fmt, TimeZone tz) {
	try {
		return toDate(dte, fmt, tz);
	} catch(ParseException px) {
		return null;
	}
}

/*
 * create a date object from a string, and a format
 */
public static Date toDate(String dte, String fmt) throws ParseException {
	return toDate(dte, fmt, null);
}

/*
 * create a date object from a string, format, and time zone
 */
public static Date toDate(String dte, String fmt, TimeZone tz) throws ParseException {
	SimpleDateFormat parser= new SimpleDateFormat(fmt);
	if (tz != null) {
		parser.setTimeZone(tz);
	}
	return parser.parse(dte);
}



/*
 * create a calendar object from a string, and a format
public static Calendar toCalendar(String dte, String fmt) throws ParseException {
	SimpleDateFormat input= new SimpleDateFormat(fmt);
	return input.parse(dte);
}

 */


public static SimpleCalendar toCalendar(String time) throws ParseException {
	return toCalendar(time, null);
}

public static SimpleCalendar toCalendar(String time, String tz) throws ParseException {
	
	if (time == null) {
		throw new ParseException("Cannot parse NULL date/time", 0);
	}
	
	StringTokenizer parser= new StringTokenizer(time, "-/:. ");
	
	if (tz == null) {
		tz= TimeZone.getDefault().getID();
	}
	
	int step= 0;		// reference for where parsing failed
	try {
		int year= Integer.parseInt((parser.hasMoreTokens() ? parser.nextToken() : "1970"));
		step++;
		int month= Integer.parseInt((parser.hasMoreTokens() ? parser.nextToken() : "1"));
		step++;
		int day= Integer.parseInt((parser.hasMoreTokens() ? parser.nextToken() : "1"));
		step++;
		int hour= Integer.parseInt((parser.hasMoreTokens() ? parser.nextToken() : "0"));
		step++;
		int minute= Integer.parseInt((parser.hasMoreTokens() ? parser.nextToken() : "0"));
		step++;
		int second= Integer.parseInt((parser.hasMoreTokens() ? parser.nextToken() : "0"));
		step++;
		int millis= Integer.parseInt((parser.hasMoreTokens() ? parser.nextToken() : "0"));
		step++;
		
		SimpleCalendar ts= new SimpleCalendar();
		
		// GregorianCalendar.getInstance(TimeZone.getTimeZone(tz));
		ts.setTimeZone(TimeZone.getTimeZone(tz));
		
		ts.set(Calendar.YEAR, year);
		ts.set(Calendar.MONTH, month - 1);
		ts.set(Calendar.DAY_OF_MONTH, day);
		ts.set(Calendar.HOUR_OF_DAY, hour);
		ts.set(Calendar.MINUTE, minute);
		ts.set(Calendar.SECOND, second);
		ts.set(Calendar.MILLISECOND, millis);
		
		return ts;
	} catch (NumberFormatException nfx) {
		throw new ParseException("'"+ time +"' must conform to date format YYYY-MM-DD H24:MI:SS.s", step);
	}
	
	
}


public static GregorianCalendar convertTimezone(Calendar time, String tz) {
	if (tz == null) { tz= "GMT"; }
	// tz= TimeZone.getDefault().getID();
	
	GregorianCalendar timestamp= new GregorianCalendar(TimeZone.getTimeZone(tz));
	timestamp.setTimeInMillis(time.getTimeInMillis());
	return timestamp;
	
}




/**
 * 
 * @param milliSec
 * @return a string representing the human readable time
 * 
 *  Example output: 5h3m52s
 * 
 */
public static String formatToTime(long milliSec){
	if (milliSec < 0) {
		return "n/a ("+ milliSec +")";
	}
	
	if (milliSec < 9999) {
		return milliSec +"ms";
	}
	
	String str= ""; //+ ((float)(milliSec / 1000) / 3600) +"\t";
	
	long s= milliSec / 1000;
	
	long d= s / 86400;
	
	long days= d;
	if (d != 0) { str+= days + "d "; s%= 86400; }
	d= s / 3600;
	
	long hours= d;
	if (days < 10) {
		if (d != 0) { str+= hours + "h "; s%= 3600; }
	}
	d= s / 60;
	
	long minutes= d;
	if (days < 10) {
		if (d != 0) { str+= minutes + "m "; s%= 60; }
	}
	
	if (days < 1 && hours < 1) {
		str+= s + "s";
	}
	
	return str;
}

public static String formatToDuration(long milliSec){
	if (milliSec < 0) {
		return "n/a ("+ milliSec +")";
	}
	
	if (milliSec < 9999) {
		return milliSec +"ms";
	}
	
	String str= ""; //+ ((float)(milliSec / 1000) / 3600) +"\t";
	
	long s= milliSec / 1000;
	
	long d= s / (60 * 60 * 24 * 365 / 12);
	
	long months= d;
	if (d != 0) { str+= months +" month"+ (d != 1 ? "s" : "") +" "; s%= (60 * 60 * 24 * 365 / 12); }
	d= s / (60 * 60 * 24);
	
	long days= d;
	if (d != 0) { str+= days +" day"+ (d != 1 ? "s" : "") +" "; s%= (60 * 60 * 24); }
	d= s / (60 * 60);
	
	long hours= d;
	if (d != 0) { str+= hours + " hour"+ (d != 1 ? "s" : "") +" "; s%= (60 * 60); }
	d= s / 60;
	
	/*
	long minutes= d;
	if (d != 0) { str+= minutes + "m "; s%= 60; }
	
	if (days < 1 && hours < 1) {
		str+= s + "s";
	}
	*/
	
	return str;
}

//In java integer and long division are floored devision therefore 7/2 = 3.
public static String formatToDigitalTime(long ms)
	{
		String sdays = "";
		String shours = "";
		String sminutes = "";
		String sseconds = "";
		String smilliseconds = "";
		long milliSec = ms;
		StringBuilder builder = new StringBuilder();
		if(milliSec <= 0)
			{
				builder.append(0);
			}
		else
			{
				long days = milliSec / 86400000;
				if(days > 0)
					{
						sdays = new Long(days).toString();
						milliSec = milliSec % 86400000;
					}
				long hours = milliSec / 3600000;
				if(hours > 0)
					{
						shours = new Long(hours).toString();
						milliSec = milliSec % 3600000;
					}
				long minutes = milliSec / 60000;
				if(minutes > 0)
					{
						sminutes = new Long(minutes).toString();
						milliSec = milliSec % 60000;
					}
				long seconds = milliSec / 1000;
				if(seconds > 0)
					{
						sseconds = new Long(seconds).toString();
						milliSec = milliSec % 1000;
					}
				smilliseconds = new Long(milliSec).toString();
				if(sdays.equals(""))
					{
						builder.append("");
					}
				else
					{
						builder.append(StrUtils.pad(sdays, 2, 1, '0') + ":");
					}
				if(shours.equals("") )
					{
						builder.append("00:");
					}
				else
					{
						builder.append(StrUtils.pad(shours, 2, 1, '0') + ":");
					}
				if(sminutes.equals(""))
					{
						builder.append("00:");
					}
				else
					{
						builder.append(StrUtils.pad(sminutes, 2, 1, '0') + ":");
					}
				if(sseconds.equals(""))
					{
						builder.append("00:");
					}
				else
					{
						builder.append(StrUtils.pad(sseconds, 2, 1, '0') + ":");
					}
				if(smilliseconds.equals(""))
					{
						builder.append("00");
					}
				else
					{
						// builder.append(smilliseconds);
					}
			}
		return builder.toString();
	}


	public static void genDatesTable(Date begin, Date end, PrintWriter out) {
		
		
		GregorianCalendar now= new GregorianCalendar();
		
		now.setTime(begin);
		
		// Hashtable<Date, Hashtable<String, Long>> data= new Hashtable<Date, Hashtable<String, Long>>();
		
		while (now.getTimeInMillis() <= end.getTime()) {
			
			Hashtable<String, Long> record= new Hashtable<String, Long>();
			
			record.put("dayOfMonth", (long)now.get(GregorianCalendar.DAY_OF_MONTH));
			record.put("dayOfYear", (long)now.get(GregorianCalendar.DAY_OF_YEAR));
			record.put("dayOfWeek", (long)now.get(GregorianCalendar.DAY_OF_WEEK));
			record.put("month", (long)now.get(GregorianCalendar.MONTH));
			record.put("year", (long)now.get(GregorianCalendar.YEAR));
			record.put("hour", (long)now.get(GregorianCalendar.HOUR_OF_DAY));
			record.put("weekOfYear", (long)now.get(GregorianCalendar.WEEK_OF_YEAR));
			record.put("weekOfMonth", (long)now.get(GregorianCalendar.WEEK_OF_MONTH));
			record.put("holiday", (long)0);
			record.put("epocSeconds", (now.getTimeInMillis() / 1000));
			record.put("weekend", (long)(now.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY || now.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY ? 1 : 0 ));
			
			record.put("year_key", (long)
				(now.get(GregorianCalendar.YEAR) * 100 
				+ (now.get(GregorianCalendar.MONTH) + 1))
			);
			record.put("month_key", (long)
				(now.get(GregorianCalendar.YEAR) * 100 
				+ (now.get(GregorianCalendar.MONTH) + 1))
			);
			
			record.put("date_key", (long)
				((now.get(GregorianCalendar.YEAR) * 100 
				+ (now.get(GregorianCalendar.MONTH) + 1)) * 100
				+ now.get(GregorianCalendar.DAY_OF_MONTH))
			);
			
			record.put("hour_key", (long)
				((now.get(GregorianCalendar.YEAR) * 100 
				+ (now.get(GregorianCalendar.MONTH) + 1)) * 100
				+ now.get(GregorianCalendar.DAY_OF_MONTH)) * 100
				+ now.get(GregorianCalendar.HOUR_OF_DAY)
			);
			
			now.add(GregorianCalendar.HOUR, 1);
			
			// data.put(new Date(now.getTimeInMillis()), record);
			
			
			Enumeration<String> keys= record.keys();
			
			String insert= "insert into Time (";
			String values= ") values (";
			String endStmt= ")";
			
			while (keys.hasMoreElements()) {
				String key= keys.nextElement();
				insert+= key + (keys.hasMoreElements() ? ", " : "");
				Long value= record.get(key);
				
				if ("month".equals(key)) {
					String str= "January";
					
					switch ((int)value.longValue()) {
					case GregorianCalendar.JANUARY:
						str= "January";
						break;
					case GregorianCalendar.FEBRUARY:
						str= "February";
						break;
					case GregorianCalendar.MARCH:
						str= "March";
						break;
					case GregorianCalendar.APRIL:
						str= "April";
						break;
					case GregorianCalendar.MAY:
						str= "May";
						break;
					case GregorianCalendar.JUNE:
						str= "June";
						break;
					case GregorianCalendar.JULY:
						str= "July";
						break;
					case GregorianCalendar.AUGUST:
						str= "August";
						break;
					case GregorianCalendar.SEPTEMBER:
						str= "September";
						break;
					case GregorianCalendar.OCTOBER:
						str= "October";
						break;
					case GregorianCalendar.NOVEMBER:
						str= "November";
						break;
					case GregorianCalendar.DECEMBER:
						str= "December";
						break;
					}
					
					//System.out.print(" "+ str);
					values+= "'"+ StrUtils.sqlSafe(str) +"'" + (keys.hasMoreElements() ? ", " : "");
				} else if ("dayOfWeek".equals(key)) {
					String str= "Saturday";
					
					switch ((int)value.longValue()) {
					case GregorianCalendar.SATURDAY:
						str= "Saturday";
						break;
					case GregorianCalendar.SUNDAY:
						str= "Sunday";
						break;
					case GregorianCalendar.MONDAY:
						str= "Monday";
						break;
					case GregorianCalendar.TUESDAY:
						str= "Tuesday";
						break;
					case GregorianCalendar.WEDNESDAY:
						str= "Wednesday";
						break;
					case GregorianCalendar.THURSDAY:
						str= "Thursday";
						break;
					case GregorianCalendar.FRIDAY:
						str= "Friday";
						break;
					}
					
					//System.out.print(" "+ str);
					values+= "'"+ StrUtils.sqlSafe(str) +"'" + (keys.hasMoreElements() ? ", " : "");
				} else {
					// System.out.print(" "+ key +": "+ value);
					values+= value + (keys.hasMoreElements() ? ", " : "");
				}
				
				
			}
			out.println(insert + values + endStmt +";");
			
			
		}
		
		// 
		
		
	}
	
	

	public static String format(Date date, String format) {
		return format(date, format, null);
	}

	public static String format(Date date, String format, TimeZone tz) {
		return format(date.getTime(), format, tz);
	}

	/**
	 * @param args
	 *
	public static void main(String[] args) {
		DateKit du= new DateKit();
		
		if (!du.parseParms(args)) {
			System.out.print("ERR: "+ (du.errMsg != null ? du.errMsg +"\n" : "" ) +"\n");
			System.out.println(USAGE);
			return;
		}
		
		int ret= 0;
		
		if ((ret= du.exec()) != 0) {
			System.out.println("ERR"+ du.msg);
			return;
		} else {
			System.exit(ret);
		}
		
	} /* main */
	
	public static String format(Calendar calendar) {
		//yyyy-MM-dd HH:mm
		String simple;
		simple = calendar.get(GregorianCalendar.YEAR) + "-";
		int month = calendar.get(GregorianCalendar.MONTH) + 1;
		if(month < 10) {
			simple += "0" + month;
		} else {
			simple += month;
		}
		
		simple += "-";
		
		int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);
		if(day < 10) {
			simple += "0" + day;
		} else {
			simple += day;
		}
		
		simple += " ";
		
		int hour = calendar.get(GregorianCalendar.HOUR_OF_DAY);
		if(hour < 10) {
			simple += "0" + hour;
		} else {
			simple += hour;
		}
		
		simple += ":";
		
		int minute = calendar.get(GregorianCalendar.MINUTE);
		if(minute < 10) {
			simple += "0" + minute;
		} else {
			simple += minute;
		}
		return simple;
	}
	
	
	public static void genDatesTable(Date start, Date end, String outputFilename) throws IOException {
		
		//Date start= DateKit.safeToDate("2001-01-01", STD_DATE);
		////Date end= DateKit.safeToDate("2020-12-31", STD_DATE);
		//end= DateKit.safeToDate("2001-12-31", STD_DATE);
		//end= DateKit.safeToDate("2001-01-31", STD_DATE);
		
		//genDatesTable(start, end, new PrintWriter(System.out));
		
		FileOutputStream fout= new FileOutputStream(outputFilename);
		
		PrintWriter pw= new PrintWriter(fout);
		genDatesTable(start, end, new PrintWriter(fout));
		
		pw.flush();
		fout.flush();
		fout.close();
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		long now= System.currentTimeMillis();
		
		Date end= DateKit.safeToDate("2010-04-14", STD_DATE);
		
		System.out.println(formatToDuration(Math.abs(now - end.getTime())));
		
	}
}
