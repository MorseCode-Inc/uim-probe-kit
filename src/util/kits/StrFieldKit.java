package util.kits;

import java.text.ParseException;
import java.util.Date;
import java.util.Vector;


/**
* StrFieldKit is a collection of helper functions
* for parsing string into fields
*/
public class StrFieldKit {

	public String errMsg;

	private String parseStr;
	private char parseDelimiter= ' ';
	private boolean trimWhitespace= true;


/**
* String Parser Constructor w/ space delimiter
*/
public StrFieldKit(String str) {
	parseStr= str;
} // StrFieldKit


/**
* String Parser constructed w/ specified delimeter
*/
public StrFieldKit(String str, char delimiter) {
	parseStr= str;
	parseDelimiter= delimiter;
} // StrFieldKit


/**
* Disable trimming of whitespace in field content
* Note: may not give desired result if delimiter = ' '
*/
public void keepWhitespace() {
	trimWhitespace= false;
} // keepWhitespace

public void trimWhitespace() {
	trimWhitespace= true;
} // trimWhitespace


/**
* Convert string into array of field strings
*/
public String[] fields() {
	Vector v= new Vector();
	String f;
	while ((f= nextField()) != null) { v.add(f); }

	String phields[]= new String[v.size()];
	for (int i= 0; i<phields.length; i++) {
		phields[i]= (String)v.elementAt(i);
	}

	return (phields);
} // fields


/**
* Extract (parse) next field from string and return as a long.
*/
public long nextLong() {
	errMsg= null;
	long r= -1;
	String str= null;
	try {
		str= ((trimWhitespace)? nextField() : nextField().trim());
		r= Long.parseLong(str);
	} catch (NumberFormatException nfx) {
		errMsg= str +": invalid int fmt";
		return (-1);
	}
	return (r);
} // nextInt


/**
* Extract (parse) next field from string and return as a double.
*/
public double nextDouble() {
	errMsg= null;
	double r= -1;
	String str= null;
	try {
		str= ((trimWhitespace)? nextField() : nextField().trim());
		r= Double.parseDouble(str);
	} catch (NumberFormatException nfx) {
		errMsg= str +": invalid int fmt";
		return (-1);
	}
	return (r);
} // nextInt


/**
* Extract (parse) next field from string and return as int.
*/
public int nextInt() {
	errMsg= null;
	int r= -1;
	String str= null;
	try {
		str= ((trimWhitespace)? nextField() : nextField().trim());
		if (str.startsWith("+")) {
			str= str.substring(1);
		}
		r= Integer.parseInt(str);
	} catch (NumberFormatException nfx) {
		errMsg= str +": invalid int fmt";
		return (-1);
	}
	return (r);
} // nextInt


/*
* Extract (Parse) next space separated field from str
* Note: Avoids StringTokenize has problem with empty fields.
*/
public String nextField() {

	if (parseStr == null) return(null);		// bailout - no str to parse

	String field= null;
	if (trimWhitespace) { parseStr= parseStr.trim(); }
	int k= parseStr.indexOf(parseDelimiter);

	if (k != -1) {					// extract first field
		field= parseStr.substring(0,k);
		if (trimWhitespace) { field= field.trim(); }
		parseStr= parseStr.substring(k+1);
	} else {					// last field being returned
		field= parseStr;
		parseStr= null;				// return null after last field
	}

	return (field);
} // nextField


/**
* Return remainder of unparsed text
*/
public String remainder() {
	return (parseStr);
} // remainder


public float nextFloat() {
	errMsg= null;
	float r= -1.0F;
	String str= null;
	try {
		str= ((trimWhitespace)? nextField() : nextField().trim());
		r= Float.parseFloat(str);
	} catch (NumberFormatException nfx) {
		errMsg= str +": invalid float fmt";
		return (-1F);
	}
	return (r);
} // nextInt


public Date nextDate(String fmt) {
	String str= ((trimWhitespace)? nextField() : nextField().trim());
	try {
		Date d= DateKit.toDate(str, fmt);
		return d;
	} catch (ParseException px) {
		errMsg= str +": invalid date ["+ px.getMessage() +"]";
	}
	return null;
}


} // class StrFieldKit



