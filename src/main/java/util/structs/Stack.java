/*
 * Created on Jun 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package util.structs;


/**
 * @author bcmorse
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Stack<T> {

	private int size;
	
	private class Element<T> {
		public Element(T data) {
			this.data= data;
		}
		public T data;
		public Element<T> next;
		public Element<T> previous;
	}
	
	Element<T> top;
	
public Stack() {
	size= 0;
}
	
public void push(T o) {
	Element<T> e= new Element<T>(o);
	
	if (top != null) {
		e.next= top;
		top.previous= e;
	} else {
		
	}
	size++;
	
	top= e;
}

public T pop() {
	Element<T> e= top;
	
	if (top != null) {	
		top= top.next;
	}
	size--;
	
	return e.data;	
}

public T peek() {
	if (top != null) {	
		return top.data;
	}
	
	return null;
}

public int size() {
	return size;
}

}
