/**
 * 
 */
package util.structs;



/**
 * @author bmorse
 *
 */
public class SimpleQueue<T> implements Queue<T> {

	private long lastWait;
	protected Element<T> head;
	protected Element<T> tail;
	protected long length;
	private boolean mutex;
	
	
	
public SimpleQueue() {
	releaseMutex();
	length= 0;
}

/* (non-Javadoc)
 * @see com.intrado.wireline.ux.jutils.IQueue#enqueue(java.lang.Object)
 */
public void enqueue(T o) {
	aquireMutex();
	Element<T> t= new Element<T>(o);
	
	if (head == null) {
		head= t;
	}
	
	if (tail != null) {
		tail.next= t;
	}
	
	tail= t;

	length++;
	releaseMutex();
}

/* (non-Javadoc)
 * @see com.intrado.wireline.ux.jutils.IQueue#dequeue()
 */
public T dequeue() {
	aquireMutex();
	
	Element<T> t= head;
	if (t == null) { releaseMutex(); return null; }
	
	head= t.next;
	length--;
	
	releaseMutex();
	
	lastWait= System.currentTimeMillis() - t.queued;
	
	return t.data;
}
	
public T peek() {
	aquireMutex();
	
	Element<T> t= head;
	
	if (t == null) {
		releaseMutex();
		return null;
	}
	
	releaseMutex();
	return t.data;
}

public boolean isEmpty() {
	return (length == 0);
}
	
public long size() {
	aquireMutex();
	long size= length;
	releaseMutex();
	
	return size;
}

public long lastWaitTime() {
	return lastWait;
}

public long currentWaitTime() {
	if (head == null) {
		return 0;
	}
	
	return System.currentTimeMillis() - head.queued;
}

public long lastArrivalTime() {
	if (tail == null) {
		return 0;
	}
	
	return tail.queued;
}
	
public long peekArrivalTime() {
	if (head == null) {
		return 0;
	}
	
	return head.queued;
}
	


protected synchronized boolean getMutex() {
	return (!mutex ? mutex= true : false);
}

protected void releaseMutex() {
	mutex= false;
}

protected void aquireMutex() {
	// get a lock on the queue
	while (!getMutex()) {
		try { Thread.sleep(10); } catch (InterruptedException ignore) { }
	}
}

public boolean contains(T o) {
	if (o == null) { return false; }
	
	Element<T> e= head;
	
	while (e != null) {
		if (e.data != null) {
			if (o.equals(e.data)) {
				return true;
			}
		}
		e= e.next;
	}
	
	return false;
}




protected class Element<Y> {
	public Element<Y> next;
	public Element<Y> previous;
	public Y data;
	public long queued;
	
	public Element(Y o) {
		queued= System.currentTimeMillis();
		data= o;
	}
}

}
