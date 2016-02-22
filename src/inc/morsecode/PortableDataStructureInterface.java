package inc.morsecode;

import inc.morsecode.util.json.JsonArray;

import inc.morsecode.util.json.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import com.nimsoft.nimbus.NimException;
import com.nimsoft.nimbus.PDS;

public interface PortableDataStructureInterface {

	public abstract void copy(String key, JsonArray array);

	public abstract void set(String path, NDS section);

	public abstract NDS getSection(String path, NDS ifNull);

	public abstract boolean isNamed(String name);

	public abstract void set(String key, Float value);

	public abstract void set(String key, Long value);

	public abstract void set(String key, Double value);

	public abstract NDSValue delete(String path);

	public abstract void add(NDS section);

	public abstract void set(String path, long value);

	public abstract void set(String path, int value);

	public abstract void set(String path, double value);

	public abstract void set(String path, float value);

	public abstract void set(String path, boolean value);

	public abstract void set(String path, String value);

	public abstract void set(String path, NDSValue value);

	public abstract Object seek(String path, String key);

	public abstract String seek(String path, String key, String ifNull);

	public abstract int seek(String path, String key, int ifNull);

	public abstract double seek(String path, String key, double ifNull);

	public abstract long seek(String path, String key, long ifNull);

	public abstract boolean seek(String path, String key, boolean ifNull);

	public abstract NDS seek(String path);

	public abstract NDS seek(String path, boolean autocreate);

	public abstract long getLastModified();

	public abstract int getDataType(String key);

	public abstract String get(String key);

	public abstract String get(String key, String ifNull);

	public abstract boolean isEmptyString(String key);

	public abstract boolean isNull(String key);

	/**
	 * get integer value
	 */
	public abstract int get(String key, int ifNull);

	/**
	 * get integer value
	 */
	public abstract int get(String key, int ifNull, int min, int max);

	/**
	 * get long value
	 */
	public abstract long get(String key, long ifNull);

	/**
	 * get double value
	 */
	public abstract double get(String key, double ifNull);

	/**
	 * get boolean value
	 */
	public abstract boolean get(String key, boolean ifNull);

	public abstract String getMergeBehavior();

	public abstract void setMergeBehavior(MergeRule rule);

	public abstract void setMergeBehavior(String rule);

	public abstract String toString();

	public abstract PDS toPDS() throws NimException;

	public abstract void setName(int index);

	public abstract void setName(String name);

	public abstract String getName();

	public abstract Iterator<NDS> iterator();

	public abstract Iterable<String> keys();

	public abstract void set(String key, StringBuffer value);

	public abstract JsonObject toJson();

	public abstract Set<String> getKeys();

	public abstract boolean writeToFile(File persistentCache) throws IOException, FileNotFoundException;

	public abstract void clear();

	public abstract boolean isActive();

	public abstract int size();

	public abstract int length();

	public abstract long mass();

	public abstract NDS pop();

}