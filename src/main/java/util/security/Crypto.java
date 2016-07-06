/**
 * 
 */
package util.security;

import java.util.Hashtable;
import java.util.Random;

import util.kits.DynamicClassKit;
import util.security.codecs.DefaultCodec;
import util.security.codecs.SecurityCodec;





/**
 * @author bmorse
 *
 */
public class Crypto {
	
	
	public static final String RNG = "SHA1PRNG";
	private static final String[] alphabet= new String[] {
			// "\"", "'",
			  " ", "!", ")", "#", "$", "%", "&", "}", "(", "~"		// 0  - 9
			, "*", "+", ",", "-", ".", "/", "0", "1", "2", "3"		// 10 - 19
			, "4", "5", "6", "7", "8", "9", ":", ";", "<", "="		// 20 - 29
			, ">", "?", "@", "A", "B", "C", "D", "E", "F", "G"		// 30 - 39
			, "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q"		// 40 - 49
			, "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "["		// 50 - 59
			, "]", "^", "_", "`", "a", "b", "c", "d", "e", "f"		// 60 - 69
			, "g", "h", "i", "j", "k", "l", "m", "n", "o", "p"		// 70 - 79
			, "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"		// 80 - 89
			, "{", "|", "'", "\"", "\n", "\r", "\t", "\\"			// 90 - 97
	};
	

	public static final String encode(String clearText) { return encode(clearText, new DefaultCodec()); }
	public static final String encode(String clearText, SecurityCodec codec) {
		return f(false, codec, clearText);
	}
	
	public static final String decode(String cypherText) { return decode(cypherText, new DefaultCodec()); }
	public static final String decode(String cypherText, SecurityCodec codec) {
		return f(true, codec, cypherText);
	}
	
	public static final SecurityCodec getCodecByClass(String classPath) {
		
		DynamicClassKit dck= new DynamicClassKit();
		
		try {
			return (SecurityCodec) dck.getInstanceOf(classPath);
		} catch (Exception x) {
			return new DefaultCodec();
		}
	}
	
	private static final String f(boolean mode, SecurityCodec codec, String text) {
		
		if (codec == null) { return text; }
		
		StringBuffer sb= null;
		String chars= codec.getAlphabet(alphabet);
		int rotator= codec.getRotator(alphabet);
		
		if (mode) {
			if (text.length() < 4) { return text; }
			
			sb= new StringBuffer(text);
			int offset= 0;
			for (int i=0; i<sb.length(); i++) {
				int k= chars.indexOf(sb.charAt(i));
				offset= (k - rotator) % chars.length();
				if (offset < 0) offset+= chars.length();
				rotator+= offset;
				sb.setCharAt(i,chars.charAt(offset));
			}
			text= sb.substring(4);
		} else {
			long pause= (long) (Math.random() * 50);
			try { Thread.sleep(pause); } catch (InterruptedException ignore) { }
			
			sb= new StringBuffer("...."+ text);
			long millis= System.currentTimeMillis();
			sb.setCharAt(0,chars.charAt((int)(millis % (chars.length() - 1))));
			sb.setCharAt(1,chars.charAt((int)(millis % 19)));
			sb.setCharAt(2,chars.charAt((int)(millis % 17)));
			sb.setCharAt(3,chars.charAt((int)(millis % 31)));
			
			for (int i=0; i<sb.length(); i++) {
				int k= chars.indexOf(sb.charAt(i));
				rotator+= k;
				sb.setCharAt(i,chars.charAt(rotator % chars.length()));
			}
			text= sb.toString();
		}
		
		return (text);
	}
	
	
// 	public static final String getAlphabet(int codec) {
// 		SecurityCodec c= getCodec(codec);
// 		return c.getAlphabet(alphabet);
// 	}
	

	
	
	public static String generate(String name, String tabooChars) {
		int r= 0;
		String a= "";
		
		Hashtable p= new Hashtable();
		
		Hashtable taboo= new Hashtable();
		for (int i= 0; i < tabooChars.length(); i++) {
			taboo.put(tabooChars.charAt(i)+"", tabooChars.charAt(i)+"");
		}
		
		if (taboo.get("\n") == null) { p.put("1", "\\n"); }
		if (taboo.get("\r") == null) { p.put("2", "\\r"); }
		if (taboo.get("\t") == null) { p.put("3", "\\t"); }
		String esc= "";
		for (int i= 32; i < 127; i++) {
			switch((char)i) {
			case '\'':
			case '\\':
			case '"':
				esc="\\";
				break;
			default:
				esc= "";
			}
			
			if (taboo.get(((char)i)+"") != null) {
				// don't include this taboo character in the alphabet
				// System.err.println("Not including: "+ (char)i);
				continue;
			}
			p.put((i - 32)+"", ""+ esc + ((char)i) + "");
		}
		
		Random rnd= new Random();
		rnd.setSeed(System.currentTimeMillis());
		
		int n= 1;
		while (p.size() > 0) {
			int i= rnd.nextInt(129);
			if (((String)p.get(i+"")) == null) { continue; }
			
//			if (n == 1) {
//				a= "c["+ i +"]";
//			} else {
//				a+= " + c["+ i +"]";
//				if (p.size() % 90 == 0 || p.size() % 55 == 0 || p.size() % 43 == 0) {
//					r+= i * (rnd.nextInt(4) + 4);
//				}
//			}
			
			if (n == 1) {
				a= (String)p.get(i+"");
			} else {
				a+= (String)p.get(i+"");
				if (p.size() % 90 == 0 || p.size() % 55 == 0 || p.size() % 43 == 0) {
					r+= i * (rnd.nextInt(4) + 5) * (rnd.nextInt(8) + 1);
				}
			}
			
			p.remove(i+"");
			
			// if (n % 10 == 0) { a+= "\n\t\t\t"; }
			
			n++;
		}	
		
		String o= "\n/**\n *\n *\n */\n"
			+"public class "+ name +" implements SecurityCodec {\n"
			+"\n\t/**\n\t *\n\t */\n"
			+"\tpublic String getAlphabet(String[] c) {\n"
			+"\t\treturn \""+ a +"\";\n"
			+"\t} /* getAlphabet */\n";
			;
		
		o+= "\n\t/**\n\t *\n\t */\n"
			+"\tpublic int getRotator(String[] c) {\n"
			+"\t\treturn "+ r +";\n"
			+"\t} /* getRotator */\n";
			;
			
		o+= "\n\n}\n";
		
		return o;
	}
	
	
	public static void main(String[] args) {
		
		
		String code= generate("NoCoRocketry", "\r\n\t\"\\'");
		
		
		System.out.println(code);
		
	}
}
