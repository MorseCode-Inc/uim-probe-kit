package inc.morsecode.etc;

public class Mutex {

	private boolean lock;
	
	public Mutex() {
		this.lock= false;
	}
	
	
	synchronized
	public boolean get() {
		return lock= (lock ? false : true);
	}
	
	public void release() {
		lock= false;
	}

	
	public boolean lock() {
		return lock(3);
	}
	
	public boolean lock(int timeout) {
		
		int interval= 30;				// frequency to check the lock
		int maxWait= timeout * 1000;	// maximum time to try and get the lock before giving up
		int cycles= 0;
		
		boolean locked= false;
		while (!(locked= this.get())) {
			try {
				Thread.sleep(interval);
			} catch (InterruptedException interrupted) {
				
			}
			if (cycles++ * interval > maxWait) {
				throw new RuntimeException("Possible deadlock, unable to get internal mutex.");
			}
		}
		
		return locked;
	}
	
}
