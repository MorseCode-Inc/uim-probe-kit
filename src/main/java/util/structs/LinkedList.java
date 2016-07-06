package util.structs;

public class LinkedList<T> {
	
	private Element<T> head;
	private Element<T> middle;
	private Element<T> tail;
	
	private long length;
	
	public LinkedList() {
		length= 0;
	}
	
	private LinkedList(Element<T> head, Element<T> tail, long length) {
		this.head= head;
		this.tail= tail;
		this.length= length;
		
		Element<T> e= head;
		
		long cycles= 0;
		while (e != null && e.right != null && cycles++ < length / 2) {
			
			e= e.right;
		}
		
		if (e == null) {
			// couldn't find middle ?? means that head must be null, therefore tail should be null and length should be 0
			this.head= null;
			this.middle= null;
			this.tail= null;
			this.length= 0;
			
		} else {
			this.middle= e;
		}
		
	}
	
	protected Element<T> getHead() {
		return head;
	}
	
	protected Element<T> getMiddle() {
		return middle;
	}
	
	protected Element<T> getTail() {
		return tail;
	}
	
	
	public synchronized long add(T o) {
		Element<T> e= new Element<T>(o);
		return add(e);
	}
	
	public synchronized long add(Element<T> e) {
		
		if (head == null) {
			head= e;
			tail= e;
			middle= e;
			head.countOff(0);
			
		} else {
			
			tail.right= e;
			e.left= tail;
			
			tail= e;
			
			tail.left.countOff(tail.left.index);
			
		}
		
		
		if (length > 3) {
			long m= (long) (length / 2);
			while (middle.right != null && middle.index < m) {
				middle= middle.right;
			}
		}
		
		length++;
		return tail.index;
	}
	
	public T get(long index) {
		
		// maybe the index is close to the beginning or end (within 5 elements means close)
		if (index <= head.index + 50) {
			// desired element is within the first five of the list
			Element<T> e= head;
			while (e != null) {
				if (e.index == index) {
					// quick match
					return e.data;
				}
				e= e.right;
			}
			return null;
		} else if (index >= tail.index - 50) {
			// desired element is within the last of the list
			Element<T> e= tail;
			while (e != null) {
				if (e.index == index) {
					return e.data;
				}
				e= e.left;
			}
			return null;
			
		} else if (index >= middle.index - 50 && index <= middle.index + 50) {
			// the desired index is within the middle section
			
			// figure out if its left or right of the middle, search in that direction
			
			Element<T> e= middle;
			
			if (index <= middle.index) {
				
				while (e.left != null) {
					if (e.index == index) {
						return e.data;
					}
					e= e.left;
				}
				
				// the index search above should always find the element
				// should not ever return here
				return null;
			} else {
				while (e.right != null) {
					if (e.index == index) {
						return e.data;
					}
					e= e.right;
				}
				
				// the index search above should always find the element
				// should not ever return here
				return null;
			}
			
		}
		
		
		
		// recursive code...
		
		
		
		LinkedList<T> sublist= null;
		
		if (index <= middle.index) {
			sublist= new LinkedList<T>(head, middle, (long)length / 2);
			
		} else { // if (index > (qtr * 2)) {
			sublist= new LinkedList<T>(middle, tail, (long)length / 2);
			
		}
		
//		System.out.println("sublist: "+ sublist);
		
		// recursion call
		return sublist.get(index);
		
		
	}
	
	public String toString() {
		String list= ":linked list\n";
		
		Element<T> e= head;
		
		while (e != null) {
			list+= "+-["+ e.index +"]-- ";
			list+= e.data.toString();
			list+= "\n";
			e= e.right;
		}
		
		list+= ": linked list [" +length +" entr"+ (length == 1 ? "y" : "ies") +"]\n";
		
		return list;
		
	}
	
	public long size() {
		return length;
	}
	
	private class Element<Y> {
		Element<Y> left;
		Element<Y> right;
		Y data;
		long index;
		
		public Element(Y data) {
			this.data= data;
		}
		
		public void countOff(long i) {
			index= i;
			if (right != null) {
				right.countOff(i + 1);
			}
		}
	}

}
