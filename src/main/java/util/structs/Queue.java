/**
 * 
 */
package util.structs;

/**
 * @author bmorse
 *
 */
public interface Queue<T> {

	public abstract void enqueue(T o);

	public abstract T dequeue();

	public abstract boolean isEmpty();
	
	public abstract long size();

}