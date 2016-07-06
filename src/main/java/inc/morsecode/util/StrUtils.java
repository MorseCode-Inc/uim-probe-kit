package inc.morsecode.util;
/**
 * 
 */


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import util.kits.StrFieldKit;
import util.security.Crypto;





/**
 * @author bmorse
 *
 */
public class StrUtils {
	
	public static String pad(String field, int width, int align) {
		return pad(field, width, align, ' ');
	}
	
	public static String pad(String field, int width, int align, char padChar) {
		return pad(field, width, align, ""+ padChar);
	}
	
	public static String padLeft(String field, int width, String padding) {
		return pad(field, width, 1, padding);
	}
	public static String pad(String field, int width, int align, String padding) {
		String str= field;
		while (str.length() < width * padding.length()) {
			if (align < 0) {
				str= str + padding;
			} else if (align == 0) {
				str= padding + str + padding;
			} else {
				str= padding + str;
			}
		}
		return str;
	}
	
	public static String sqlSafe(int field) { return ""+ field; }
	public static String sqlSafe(long field) { return ""+ field; }
	public static String sqlSafe(double field) { return ""+ field; }
	public static String sqlSafe(float field) { return ""+ field; }
	public static String sqlSafe(byte field) { return ""+ field; }
	
	public static String sqlSafe(String field) {
		if (field == null) { return ""; }
		String safe= replace(field, "'", "''", true);
		safe= replace(safe, "\\", "\\\\", true);
		return safe;
	}
	
	public static String escapeHtml(String html) {
		html= replace(html, "&", "&amp;", true);
		html= replace(html, "<", "&lt;", true);
		html= replace(html, ">", "&gt;", true);
		return html;
	}
	
	public static String stripHtmlComments(String text) {
		String replaceText = text;

		if (replaceText == null) { return ""; }
		replaceText= replaceText.replaceAll("<!--.*-->", "");
		
		return replaceText;
	}
	
	public static String html2txt(String html) {
		if (html == null) { return ""; }
		
		String text= stripTags(html);
		
		text= replace(text, "&amp;", "&", true);
		text= replace(text, "&nbsp;", " ", true);
		text= replace(text, "&lt;", "<", true);
		text= replace(text, "&gt;", ">", true);
		
		return text.trim();
	}
	
	public static String stripTags(final String text) {
		String replaceText = text;
		
		if (replaceText == null) { return ""; }
		
		String[][] tagExprs= new String[][] {
			 { "<!--.*?-->", "" }					// comments
			,{ "<!--.*?--&gt;", "" }				// comments
			,{ "&lt;!--.*?-->", "" }				// comments
			,{ "&lt;!--.*?--&gt;", "" }				// comments
			,{ "</[Hh][Tt][Mm][Ll]>", "\n" }		// </html>
			,{ "<[Hh][Tt][Mm][Ll].*?>", "\n" }		// <html.*>
			,{ "<[Mm][Ee][Tt][Aa].*?>", "" }		// <meta.*>
			,{ "</[Mm][Ee][Tt][Aa]>", "" }			// </meta>
			,{ "<[Hh][Ee][Aa][Dd].*?>", "" }		// <head.*>
			,{ "</[Hh][Ee][Aa][Dd]>", "" }			// </head>
			,{ "<[Bb][Oo][Dd][Yy].*?>", "" }		// <body.*>
			,{ "</[Bb][Oo][Dd][Yy]>", "" }			// </body>
			
			// <hN> - headings
			,{ "<[Hh][0-9]>", "" }
			,{ "</[Hh][0-9]>", "" }
			
			// <font>
			,{ "</[Ff][Oo][Nn][Tt]>", "" }
			// ,{ "<[Ff][Oo][Nn][Tt]>", "" }
			,{ "<[Ff][Oo][Nn][Tt].*?>", "" }
			
			// <hr> <br>
			,{ "<[Hh][Rr].*?>", "\n" }
			,{ "<[Bb][Rr].*?>", "\n" }
			
			// <em>
			,{ "<[Ee][Mm].*?>", "\n" }
			
			// <p>
			,{ "<[Pp].*?>", "\n" }
			,{ "</[Pp]>", "\n" }
			// <i> <u> <b> </p>
			,{ "<[IiUuBb].*?>", "" }
			,{ "</[IiUuBb].*?>", "" }
			
			,{ "<em>", "" }
			,{ "</em>", "" }
			
			// <div>
			,{ "</[pP][rR][eE]>", "" }
			// ,{ "<[Dd][Ii][Vv]>", "" }
			,{ "<[pP][rR][eE].*?>", "" }
			
			// <div>
			,{ "</[Dd][Ii][Vv]>", "" }
			// ,{ "<[Dd][Ii][Vv]>", "" }
			,{ "<[Dd][Ii][Vv].*?>", "" }
			
			// <span>
			,{ "</[Ss][Pp][Aa][Nn]>", "" }
			// ,{ "<[Ss][Pp][Aa][Nn]>", "" }
			,{ "<[Ss][Pp][Aa][Nn].*?>", "" }
			
			,{ "<[Aa].*?href=['\".](.*?)['\".]*?>(.*?)</a>", "$2 [$1]" }
			,{ "</[Aa]>", "" }
			//,{ "<[Aa]", " " }
//			,{ "href=['\"].+['\"]>", " " }
			
			,{ "<[sS][tT][rR][oO][nN][gG].*?>", "" }
			,{ "</[sS][tT][rR][oO][nN][gG]>", "" }
			,{ "</[Ll][Ii]>", "" }
			,{ "<[Ll][Ii].*?>", " - " }
			
			// <ol> <ul>
			,{ "</[OoUu][Ll]>", "" }
			,{ "<[OoUu][Ll]>", "" }
			
			,{ "&lt;", "<" }
			,{ "&gt;", ">" }
			,{ "&amp;", "&" }
			,{ "&nbsp;"," " }
			
			// table
			,{ "</[Tt][Aa][Bb][Ll][Ee]>","" }
			// ,{ "<[Tt][Aa][Bb][Ll][Ee]>","" }
			,{ "<[Tt][Aa][Bb][Ll][Ee].*?>","" }
			
			// thead
			,{ "</[Tt][Hh][Ee][Aa][Dd]>","" }
			,{ "<[Tt][Hh][Ee][Aa][Dd]>","" }
			
			// tbody
			,{ "</[Tt][Bb][Oo][Dd][Yy]>","" }
			,{ "<[Tt][Bb][Oo][Dd][Yy]>","" }
			
			// <th> <td> <tr>
			,{ "</[Tt][HhDdRr]>","" }
			// ,{ "<[Tt][HhDdRr]>","" }
			,{ "<[Tt][HhDdRr].*?>","" }
			
			// td
//			,{ "</[Tt][Dd]>"," " }
//			,{ "<[Tt][Dd]>","" }
//			,{ "<[Tt][Dd].+>","" }
			
			// blank lines
//			,{ "  *"," " }
//			,{ "\t\t*"," " }
//			,{ "^ $","" }
			
			// collapse repeating new lines
			// ,{ "[\r\n][\r\n]*","\n" }
			,{ "[\n][\n]*","\n" }
		};
		
		for (String[] exprRepl : tagExprs) {
			
			String expr= exprRepl[0];
			String replaceWith= exprRepl[1];
			
			// old way doesn't do DOTALL
			//replaceText= replaceText.replaceAll(expr, replaceWith);
			
			// new way
			Pattern pattern= Pattern.compile(exprRepl[0], Pattern.DOTALL);
			replaceText= pattern.matcher(replaceText).replaceAll(replaceWith);
			
		}
		
		// <!-- comments -->
		// replaceText= replaceText.replaceAll("<!--.*-->", "");
		
		// <html>
		// replaceText= replaceText.replaceAll("</[Hh][Tt][Mm][Ll]>", "\n");
		// replaceText= replaceText.replaceAll("<[Hh][Tt][Mm][Ll]>", "\n");
		
		// <meta>
		// replaceText= replaceText.replaceAll("</[Mm][Ee][Tt][Aa]>", "");
		// replaceText= replaceText.replaceAll("<[Mm][Ee][Tt][Aa].*?>", "");
		
		// <head>
		// replaceText= replaceText.replaceAll("</[Hh][Ee][Aa][Dd]>", "");
		// replaceText= replaceText.replaceAll("<[Hh][Ee][Aa][Dd].*?>", "");
//		
//		// <body>
//		replaceText= replaceText.replaceAll("</[Bb][Oo][Dd][Yy]>", "");
//		// replaceText= replaceText.replaceAll("<[Bb][Oo][Dd][Yy]>", "");
//		replaceText= replaceText.replaceAll("<[Bb][Oo][Dd][Yy].*?>", "");
//		
//		// <hN> - headings
//		replaceText= replaceText.replaceAll("<[Hh][0-9]>", "");
//		replaceText= replaceText.replaceAll("</[Hh][0-9]>", "");
//		
//		// <font>
//		replaceText= replaceText.replaceAll("</[Ff][Oo][Nn][Tt]>", "");
//		// replaceText= replaceText.replaceAll("<[Ff][Oo][Nn][Tt]>", "");
//		replaceText= replaceText.replaceAll("<[Ff][Oo][Nn][Tt].*?>", "");
//		
//		// <hr> <br>
//		replaceText= replaceText.replaceAll("<[HhBb][Rr].*?>", "\n");
//		
//		// <p>
//		replaceText= replaceText.replaceAll("<[Pp].*?>", "\n");
//		// <i> <u> <b> </p>
//		replaceText= replaceText.replaceAll("<[IiUuBb].*?>", "");
//		replaceText= replaceText.replaceAll("</[IiUuBbPp]>", "");
//		
//		replaceText= replaceText.replaceAll("<em>", "");
//		replaceText= replaceText.replaceAll("</em>", "");
//		
//		// <div>
//		replaceText= replaceText.replaceAll("</[Dd][Ii][Vv]>", "");
//		// replaceText= replaceText.replaceAll("<[Dd][Ii][Vv]>", "");
//		replaceText= replaceText.replaceAll("<[Dd][Ii][Vv].*?>", "");
//		
//		// <span>
//		replaceText= replaceText.replaceAll("</[Ss][Pp][Aa][Nn]>", "");
//		// replaceText= replaceText.replaceAll("<[Ss][Pp][Aa][Nn]>", "");
//		replaceText= replaceText.replaceAll("<[Ss][Pp][Aa][Nn].*?>", "");
//		
//		replaceText= replaceText.replaceAll("</[Aa]>", "");
//		replaceText= replaceText.replaceAll("<[Aa].*?>", " ");
//		//replaceText= replaceText.replaceAll("<[Aa]", " ");
////		replaceText= replaceText.replaceAll("href=['\"].+['\"]>", " ");
//		
//		replaceText= replaceText.replaceAll("<[sS][tT][rR][oO][nN][gG].*?>", "");
//		replaceText= replaceText.replaceAll("</[sS][tT][rR][oO][nN][gG]>", "");
//		replaceText= replaceText.replaceAll("</[Ll][Ii]>", "");
//		replaceText= replaceText.replaceAll("<[Ll][Ii].*?>", " - ");
//		
//		// <ol> <ul>
//		replaceText= replaceText.replaceAll("</[OoUu][Ll]>", "");
//		replaceText= replaceText.replaceAll("<[OoUu][Ll]>", "");
//		
//		replaceText= replaceText.replaceAll("&lt;", "<");
//		replaceText= replaceText.replaceAll("&gt;", ">");
//		replaceText= replaceText.replaceAll("&amp;", "&");
//		replaceText= replaceText.replaceAll("&nbsp;"," ");
//		
//		// table
//		replaceText= replaceText.replaceAll("</[Tt][Aa][Bb][Ll][Ee]>","");
//		// replaceText= replaceText.replaceAll("<[Tt][Aa][Bb][Ll][Ee]>","");
//		replaceText= replaceText.replaceAll("<[Tt][Aa][Bb][Ll][Ee].*?>","");
//		
//		// thead
//		replaceText= replaceText.replaceAll("</[Tt][Hh][Ee][Aa][Dd]>","");
//		replaceText= replaceText.replaceAll("<[Tt][Hh][Ee][Aa][Dd]>","");
//		
//		// tbody
//		replaceText= replaceText.replaceAll("</[Tt][Bb][Oo][Dd][Yy]>","");
//		replaceText= replaceText.replaceAll("<[Tt][Bb][Oo][Dd][Yy]>","");
//		
//		// <th> <td> <tr>
//		replaceText= replaceText.replaceAll("</[Tt][HhDdRr]>","");
//		// replaceText= replaceText.replaceAll("<[Tt][HhDdRr]>","");
//		replaceText= replaceText.replaceAll("<[Tt][HhDdRr].*?>","");
//		
//		// td
////		replaceText= replaceText.replaceAll("</[Tt][Dd]>"," ");
////		replaceText= replaceText.replaceAll("<[Tt][Dd]>","");
////		replaceText= replaceText.replaceAll("<[Tt][Dd].+>","");
//		
//		// blank lines
////		replaceText= replaceText.replaceAll("  *"," ");
////		replaceText= replaceText.replaceAll("\t\t*"," ");
////		replaceText= replaceText.replaceAll("^ $","");
//		
//		// collapse repeating new lines
//		replaceText= replaceText.replaceAll("[\r\n][\r\n]*","\n");
//		replaceText= replaceText.replaceAll("[\n][\n]*","\n");
		
		return replaceText;
	}
	
	public static String replace(String within, String find, String replacement, boolean global) {
		
		String replaced= "";
		if(within == null) {
			return "";
		}
		// quick scan for meta characters
		if (within.indexOf(find) == -1) {
			// no replacement needed
			return within;
		}
		
		// replace all meta chars with proper escape sequences
		if (global) {
			int k= -1;
			while ((k= within.indexOf(find)) >= 0) {
				replaced+= within.substring(0, k) + replacement;
				within= within.substring(k + find.length());
			}
			
			replaced+= within;		// remainder
		} else {
			// replace first occurance
			int k= within.indexOf(find);
			replaced+= within.substring(0, k) + replacement;
			within= within.substring(k + find.length());
			replaced+= within;		// remainder
		}
		
		return replaced;
	}
	
	public static String bashSafe(final String varible) {
		String bashSafeVarible = varible.toUpperCase();
		
		String[][] taboo= new String[][] {
			 { ".", "_" }	
			,{ " ", "_" }	
			,{ "?", "_" }	
			,{ "~", "_" }	
			,{ "`", "_" }	
			,{ "!", "_" }	
			,{ "@", "_" }	
			,{ "#", "_" }	
			,{ "$", "_" }	
			,{ "%", "_" }	
			,{ "^", "_" }	
			,{ "&", "_" }	
			,{ "*", "_" }	
			,{ "(", "_" }	
			,{ ")", "_" }	
			,{ "[", "_" }	
			,{ "]", "_" }	
			,{ "{", "_" }	
			,{ "}", "_" }	
			,{ "<", "_" }	
			,{ ">", "_" }	
			,{ ";", "_" }	
			,{ ":", "_" }	
			,{ "'", "_" }	
			,{ "\"", "_" }	
			,{ "\\", "_" }	
			,{ "/", "_" }	
			,{ "-", "_" }
		};
		
		for (String[] pair : taboo) {
			bashSafeVarible = StrUtils.replace(bashSafeVarible, pair[0], pair[1], true);
		}
		
		/*
		bashSafeVarible = StrUtils.replace(bashSafeVarible, ".", "_", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, " ", "_", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "?", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "(", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, ")", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "-", "_", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "$", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "[", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "]", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "{", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "}", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "|", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "<", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, ">", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, ":", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, ";", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "'", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "~", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "@", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "#", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "%", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "\\", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "\"", "", true);
		bashSafeVarible = StrUtils.replace(bashSafeVarible, "/", "_", true);
		*/
		
		return bashSafeVarible;
	}
	public static String prefixText(String prefix, String text) {
		if (text == null) {
			text= "{null}";
		}
		
		if (prefix.equals("")) {
			return text;
		}
		
		
		int k= text.indexOf('\n');
		if (k == -1) {
			// no new lines
			return prefix +" "+ text;
		}
		
		StrFieldKit parser= new StrFieldKit(text, '\n');
		parser.keepWhitespace();
		String msg= "";
		
		String[] lines= parser.fields();
		
		for (int i= 0; i < lines.length; i++) {
			msg+= prefix +" "+ lines[i] + (i < (lines.length - 1) ? "\n" : "");
			
		}
		
		return msg;
	}
	
	public static String wordWrapHtml(String text) {
		return wordWrapHtml(text, 40);
	}
	
	public static String wordWrapHtml(String text, int lineLength) {
		if (text == null) { return ""; }
		
		String wrapped= "";
		
		int ll= lineLength;
		
		text= text.replaceAll("&nbsp;", " ");
		text= text.replaceAll("\r", "");
		text= text.replaceAll("<[bB][rR].*?>", "\n");
		text= text.replaceAll("<[bB][rR]/>", "\n");
		text= text.replaceAll("<[pP].*?>", "\n");
		text= text.replaceAll("</[pP]>", "\n");
		
		String[] lines= text.split("\n");
		
		String nl= "";
		int length= 0;
		
		for (String line : lines) {
			if (line.length() <= ll) {
				wrapped= wrapped + nl + line.trim();
				nl= "\n";
			} else {
				//log.info("processing line: "+ line);
				StringTokenizer st= new StringTokenizer(line, " <>", true);
				// String[] words= line.split(" <>");
				
				String fragment= "";
				boolean inTag= false;
				
				while (st.hasMoreTokens()) {
					String word= st.nextToken();
					//log.info("processing word: "+ word);
					//log.info("inTag: "+ inTag);
					if (!inTag) {
						// if (" ".equals(word)) { fragment+= word; continue; }
						if ("<".equals(word)) { inTag= true; fragment+= word; continue; }
						length+= word.length();
					} else {
					// if (inTag) {
						if (">".equals(word)) { inTag= false; fragment+= word; continue; }
						fragment+= word;
						continue;
					}
					
					fragment+= word;
					//log.info("fragment("+ length +"): "+ fragment);
					
					if (length > ll) {
						wrapped= wrapped + nl + fragment.trim();
						fragment= "";
						length= 0;
						nl= "\n";
					}
				}
				
				if (length < 2) {
					wrapped+= fragment;
				} else {
					wrapped= wrapped + nl + fragment.trim();
					nl= "\n";
				}
				length= 0;
			}
			
			
			
			nl= "\n";
		}
		
		
		wrapped= wrapped.trim().replaceAll("\n", "<br>");
		
		
		return wrapped;
	}

	
	public static String wrap(String text, int len) {
		
		int n= 0;
		for (int i= 0; i < text.length(); i++) {
			switch (text.charAt(i)) {
			case '\n':
			case '\r':
				n= 0;
				break; // next char
			default:
						
				// have we seen 'len' chars in a row with no \r or \n?
				if (n++ % len == (len - 1)) {
					// insert a new line
					// scan for first available ws.
					while (i < text.length()) {
						switch (text.charAt(i)) {
						case '\n':
						case '\r':
							n= 0;
							break;
						case '\t':
						case ' ':
							// first \t or ' ' after wrap limit
							text= text.substring(i) +"\n"+ text.substring(i, text.length() - 1);
							n= 0;
						}
						
						if (n == 0) {
							break; // break from while
						}
						i++;
					}
					
				} // if
					
			} // switch
		} // for
		
		return text;
		
	}
	
	
	public static String SHA(String s) {
		// if (s == null) { s= ""+ Math.random(); }
		if (s == null) { s= "null"; }
		char[] hexChars = { '0', '1', '2', '3', '4', '5', '6',
	        '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

			try
				{
					MessageDigest md = MessageDigest.getInstance("SHA");
					md.update(s.getBytes(), 0, s.getBytes().length);
					byte[] hash = md.digest();
					StringBuilder sb = new StringBuilder();
					int msb;
					int lsb = 0;
					int i;
					for (i = 0; i < hash.length; i++)
						{
							msb = (hash[i] & 0x000000FF) / 16;
							lsb = (hash[i] & 0x000000FF) % 16;
							sb.append(hexChars[msb]);
							sb.append(hexChars[lsb]);
						}
					return sb.toString();
				}
			catch (NoSuchAlgorithmException e)
				{
					return null;
				}

	}
	
	public static String substitute(Properties dictionary, String value) {
		
		// scan through value, looking for {[a-Z].[0-9a-Z_$].*}
		
		if (value == null) { return ""; }
		
		String buff= "";			// a buffer for storing temporary data about
		String output= "";			
		//int LITERAL= 0;
		//int parsing= LITERAL;
		boolean literal= true;		// a state of the parser - indicates if last read character was interpreted or not (literal means no interpretation was done)
		
		StringTokenizer st= new StringTokenizer(value, "{}", true);
		
		while (st.hasMoreTokens()) {
			
			String token= st.nextToken();
			if (token.length() == 0) { continue; }
			
			if (!st.hasMoreTokens()) { 
				// if this was the last token 
				// write it and bail
				// no need to see if its a meta char (in this implementation)
				output+= token;
				break;
			}
			
			char c= token.charAt(0);			// grab the first character of the token
			
			switch (c) {
			case '{':
				// if we're building a keyword in the buffer, and hit the { symbol, it no longer follows the naming standards
				buff= st.nextToken();		// this will not throw an exception because of the check made earlier (!st.hasMoreTokens)
				
				if (!st.hasMoreTokens()) { 
					output+= token + buff;
					break;
				}
				
				String close= st.nextToken();		// this will not throw an exception because of the check maide earlier (!st.hasMoreTokens)
				
				if (!close.equals("}")) {
					// this does not adhere to the naming standards, write everything out literally
					output+= token + buff + close;
				} else {
					// TODO: infinite loop detection / prevention
					// find a way to detect infinite recursion
					// could happen if the config is not properly defined.
					String v= substitute(dictionary, interpret(dictionary, buff));
					if (value == null) {
						output+= buff;
					} else {
						output+= v;
					}
				}
				
				buff= "";
				// flush the buffer and keep the parser literal flag false incase this is the beginning of a new 
				break;
			
			default:
				
				output+= token;
			}
		}
		
		// write any additional information in the buffer 
		//output+= buff;
		
		return output;
		
		/*
		 * this is the bad way to do this... but it was a start
		 *
		for (int i= 0; i < env.length; i++) {
			String n= env[i][0];
			String v= env[i][1];
			value= StrUtils.replace(value, "{"+ n +"}", v, true);
		}
		return value;
		*/
		
	}
	
	
	public static String interpret(Properties env, String name) {
		SecureRandom rnd= null;
		
		try {
			rnd = SecureRandom.getInstance(Crypto.RNG);
		} catch (NoSuchAlgorithmException nax) {
			return "NIL";
		}
		
		String value= null;
		
		if (name.equalsIgnoreCase("random.int")) {
			value= rnd.nextInt() +"";
		} else if (name.equalsIgnoreCase("random.long")) {
			value= rnd.nextLong() +"";
		} else if (name.equalsIgnoreCase("random.double")) {
			value= rnd.nextDouble() +"";
		} else if (name.equalsIgnoreCase("random.hex")) {
			value= Integer.toHexString(rnd.nextInt());
		} else if (name.equalsIgnoreCase("random.hash")) {
			value= SHA(Long.toHexString(rnd.nextLong()));
		} else if (name.equalsIgnoreCase("sys.date")) {
			value= System.currentTimeMillis() +"";
		} else {
		
			Object o= env.get(name);
			if (o == null) {
				
			} else if (o instanceof Integer) {
				value= ((Integer)env.get(name)).toString();
			} else if (o instanceof Long) { 
				value= ((Long)env.get(name)).toString();
			} else if (o instanceof Double) {
				value= ((Double)env.get(name)).toString();
			} else if (o instanceof Long) { 
				value= ((Long)env.get(name)).toString();
			} else if (o instanceof String) { 
				value= (String)env.get(name);
			} else {
				value= o.toString();
			}
			
		}
		
		if (value == null) {
			value= "";
		}
		
		// log.debug("interpreted "+ name +"="+ value +".");
		return value;
	}

	public static String safeFileName(String name) {
		return safeFileName(name, true);
	}
	
	public static String safeFileName(String name, boolean allowWitespace) {
		if (name == null) { return "null"; }
		
		String safe= name.trim();
		
		safe= safe.replaceAll("&", "");
		safe= safe.replaceAll(":", "");
		safe= safe.replaceAll("\\*", "");
		safe= safe.replaceAll("/", "");
		safe= safe.replaceAll("\\\\", "");
		safe= safe.replaceAll("@", "");
		safe= safe.replaceAll("\\?", "");
		safe= safe.replaceAll("<", "");
		safe= safe.replaceAll(">", "");
		safe= safe.replaceAll("\\[", "");
		safe= safe.replaceAll("\\]", "");
		safe= safe.replaceAll("\"", "");
		safe= safe.replaceAll("'", "");
		safe= safe.replaceAll("|", "");
		safe= safe.replaceAll("\\$", "");		
		safe= safe.replaceAll("\t", " ");		// tab
		safe= safe.replaceAll("\n", " ");		// newline
		safe= safe.replaceAll("\r", " ");		// return
		safe= safe.replaceAll("\b", "");		// backspace
		
		if (!allowWitespace) {
			safe= safe.replaceAll(" ", "_");
		}
		
		if (safe.equals("")) { safe= "empty"; }
		
		return safe;
	}

	public static String upperFirstChar(String word) {
		if (word == null) { return ""; };
		if (word.length() == 0) { return ""; }
		return word.replaceFirst(".", (""+ word.charAt(0)).toUpperCase());
	}

	
    public static String join(String[] list, String delimiter, boolean terminated) {
        String joined= "";
        String delim= "";
       
        for (String s : list) {
            joined+= delim + s;
            delim= delimiter;
        }
       
        if (terminated) {
            joined+= delimiter;
        }
       
        return joined;
    }
    
	public static String[] split(String str, String delim) {
		return split(str, delim, false, false);
	}
	
	public static String[] split(String str, String delim, boolean preserveDelim) {
		return split(str, delim, preserveDelim, false);
	}
	
    public static String[] split(String str, String delim, boolean preserveDelim, boolean collapseNullFields) {
        ArrayList<String> pieces= new ArrayList<String>();
       
        int k= -1;
        while ((k= str.indexOf(delim)) >= 0) {
            String left= str.substring(0, k);
            if (!collapseNullFields || k > 0) {
                pieces.add(left);
            }
            str= str.substring(k + delim.length());
            if (preserveDelim) {
                pieces.add(delim);
            }
        }
       
        pieces.add(str);
       
        return pieces.toArray(new String[]{});
    }

    public static String[] split(String str, String[] delimiters, boolean preserveDelimiters, boolean collapseNullFields) {
        return split(new String[]{str}, delimiters, preserveDelimiters, collapseNullFields);
    }
   
    public static String[] split(String[] strings, String[] delimiters, boolean preserveDelimiters, boolean collapseNullFields) {
        ArrayList<String> pieces= new ArrayList<String>();
       
        if (delimiters.length == 0) { return strings; }
       
        String oper= delimiters[0];
        String[] remainingDelimiters= new String[delimiters.length - 1];
        System.arraycopy(delimiters, 1, remainingDelimiters, 0, remainingDelimiters.length);
       
        for (String s : strings) {
            String[] tokens= split(s, oper, preserveDelimiters, collapseNullFields);
            for (String t : tokens) {
                pieces.add(t);
            }
        }
       
        if (remainingDelimiters.length == 0) { return pieces.toArray(new String[]{}); }
       
        strings= pieces.toArray(new String[]{});
        pieces.clear();
       
        String[] parts= split(strings, remainingDelimiters, preserveDelimiters, collapseNullFields);
        for (String p : parts) {
            pieces.add(p);
        }
       
        return pieces.toArray(new String[]{});
    }
	
}
