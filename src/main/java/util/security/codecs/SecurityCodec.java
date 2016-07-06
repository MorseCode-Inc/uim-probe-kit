/**
 * 
 */
package util.security.codecs;

/**
 * @author bmorse
 *
 */
public interface SecurityCodec {
	public String getAlphabet(String[] c);
	public int getRotator(String[] c);
}
