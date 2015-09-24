package inc.morsecode.etc;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class ArrayUtils {

	
	
	/**
	 * 
	 * @param string
	 * @param delimiter
	 * @param collapse will remove empty elements before returning the array
	 * @return
	 */
	public static String[] split(String string, char delimiter, boolean collapse) {
		ArrayList<String> tokens= new ArrayList<String>();
		
		if (string == null) { return null; }
		
		string= string.trim();
		
		if (string.length() == 0) { return null; }
		
		if (!string.contains(""+ delimiter)) {
			return new String[]{string};
		}
		
		StringTokenizer parser= new StringTokenizer(string, ""+ delimiter, true);
		
		boolean delim= false;		// assume the first token is not a delimiter
		
		while (parser.hasMoreTokens()) {
			String token= parser.nextToken().trim();
			
			if (token.equals(""+ delimiter)) {
				if (delim && !collapse) {
					tokens.add("");
				}
				delim= true;
				if (collapse) { continue; }
			} else {
				delim= false;
				if (collapse && "".equals(token)) { continue; }
				tokens.add(token);
			}
		}
		
		return tokens.toArray(new String[]{});
		
	}
	
	
	public static void print(String[] array) {
		String delim= "";
		System.out.print("[");
		for (String item : array) {
			System.out.print(delim);
			System.out.print(item);
			delim= " | ";
		}
		System.out.print("]");
		System.out.println();
	}


	public static String join(String[] pathElements, String delimiter, int startIndex) {
		int endIndex= pathElements.length;
		return join(pathElements, delimiter, startIndex, endIndex);
	}
	
	public static String join(String[] pathElements, String delimiter, int startIndex, int endIndex) {
		if (startIndex < pathElements.length && startIndex >= 0) {
			String string= "";
			String delim= "";
			for (int i= startIndex; i <= endIndex && i < pathElements.length; i++) {
				string+= delim + pathElements[i];
				delim= delimiter;
			}
			return string;
		} else {
			// illegal index
			return null;
		}
	}


	public static String prefixMultiline(String text, String prefix) {
		
		StringTokenizer parser= new StringTokenizer(text, "\n", true);
		String prefixed= "";
		
		while (parser.hasMoreTokens()) {
			String line= parser.nextToken();
			
			if (line.equals("\n")) {
				prefixed+= line;
			} else {
				prefixed+= prefix + line;
			}
		}
		
		return prefixed;
	}


	public static void print(long[] array) {
		String delim= "";
		System.out.print("[");
		for (long item : array) {
			System.out.print(delim);
			System.out.print(item);
			delim= " | ";
		}
		System.out.print("]");
		System.out.println();
	}
	
	
	public static long[] join(long[] array1, long[] array2) {
		if (array1 == null) { array1= new long[0]; }
		if (array2 == null) { array2= new long[0]; }
		
		long[] joined= new long[array1.length + array2.length];
		
		for (int i= 0; i < array1.length; i++) {
			joined[i]= array1[i];
		}
		
		for (int i= 0; i < array2.length; i++) {
			joined[i + array1.length]= array2[i];
		}
		
		return joined;
		
	}


	public static String toString(long[] array) {
		String delim= "";
		String text= "";
		
		for (long item : array) {
			text+= delim;
			text+= item;
			delim= " | ";
		}
		
		text+= "";
		
		return text;
		
	}
	
}
