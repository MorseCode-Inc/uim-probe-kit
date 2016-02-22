package util.kits;


public class DynamicClassKit {

	private String errMsg;
	
	public DynamicClassKit() {
		super();
	}
	
	public Object getInstanceOf(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessError, IllegalAccessException, LinkageError {
		Class c= null;
		
		Object obj= null;

		try {
			//c= this.getClass().forName(rsrcClass);
			c= Class.forName(className, true, this.getClass().getClassLoader());
			
			if (c != null) {
				obj= c.newInstance();
			} else {
				errMsg= "class.forName() returned null when attempting to load '"+ className +"'";
				throw new InstantiationException(errMsg);
			}
			
		} catch (ClassNotFoundException cnfx) {
			errMsg= "unkown resource '"+ className +"' [ClassNotFoundException]";
			throw new ClassNotFoundException(errMsg, cnfx);
		} catch (InstantiationException ix) {
			errMsg= "could not instantiate '"+ className +"' ["+ ix.getMessage() +"]";
			throw new InstantiationException(errMsg);
		} catch (IllegalAccessException iax) {
			errMsg= "access denied '"+ className +"' ["+ iax.getMessage() +"]";
			throw new IllegalAccessException(errMsg);
		} catch (ExceptionInInitializerError eiie) {
			errMsg= eiie.getMessage();
			throw new ExceptionInInitializerError(errMsg);
		} catch (LinkageError le) {
			errMsg= le.getMessage();
			throw new LinkageError(errMsg);
		}		
		
		return obj;
	}
	

	public String getErrMsg() {
		return errMsg;
	}

}
