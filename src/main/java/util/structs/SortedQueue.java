/**
 * 
 */
package util.structs;


/**
 * @author bmorse
 *
 */
public class SortedQueue<T> extends SimpleQueue<Comparable<T>> {

	public SortedQueue() {
		super();
	}

	public void enqueue(Comparable<T> o) {
		
		aquireMutex();
		
		Element<Comparable<T>> e= new Element<Comparable<T>>(o);
		
		if (head == null) {
			// first element
			head= e;
			tail= e;
		} else {
			
			Element<Comparable<T>> c= head;
			
			if (((Comparable)o).compareTo((Comparable<T>)c.data) < 0) {
				// e is less than c
				insertBefore(c, e);
			} else if (((Comparable)o).compareTo(c.data) == 0) {
				
				insertAfter(c, e);
				
			} else {
				while (((Comparable)o).compareTo(c.data) > 0) {
					if (c.next == null) {
						break;
					}
					c= c.next;
				}
				
				if (((Comparable)o).compareTo(c.data) < 0) {
					insertBefore(c, e);
				} else {
					insertAfter(c, e);
				}
			}
			
			
			
		}
		

		length++;
		
		// release the lock
		releaseMutex();
	}
	
	
	private void insertAfter(Element<Comparable<T>> target, Element<Comparable<T>> e) {
		if (target.next == null) {
			// target is last element
			target.next= e;
			e.previous= target;
			tail= e;
		} else {
			target.next.previous= e;
			e.next= target.next;
			e.previous= target;
			target.next= e;
		}
	}
	
	private void insertBefore(Element<Comparable<T>> target, Element<Comparable<T>> e) {
		if (target.previous == null) {
			// target is first element
			target.previous= e;
			e.next= target;
			head= e;
		} else {
			target.previous.next= e;
			e.previous= target.previous;
			target.previous= e;
			e.next= target;
		}
	}
	

}
