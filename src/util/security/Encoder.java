package util.security;

public interface Encoder {
	public String encode(String text, String entropy);
	public String encode(String text);
}
