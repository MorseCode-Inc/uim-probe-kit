package inc.morsecode;

import inc.morsecode.util.json.JsonPrimitive;
import inc.morsecode.util.json.JsonValue;

import java.util.Map;


import com.nimsoft.nimbus.PDS;


public class NDSValue {
	private Object value;
	private DataType type;
	
	public NDSValue(double value) { this.value= value; type= DataType.FLOAT; }
	public NDSValue(int value) { this.value= value; type= DataType.INT; }
	public NDSValue(long value) { this.value= value; type= DataType.LONG; }
	public NDSValue(boolean value) { this.value= value; type= DataType.BOOL; }
	
	protected NDSValue(DataType type) { this.type= type; }
	
	public NDSValue(String value) { 
		this.value= value; type= DataType.STR;
		
		if (value == null) {
			return;
		}
		
		try {
			this.value= Integer.parseInt(value);
			this.type= DataType.INT;
		} catch (NumberFormatException x1) { 
			try {
				this.value= Long.parseLong(value);
				this.type= DataType.LONG;
			} catch (NumberFormatException x2) { 
				
				try {
					this.value= Double.parseDouble(value);
					this.type= DataType.FLOAT;
				} catch (NumberFormatException x3) { 
					
					try {
						
						if ("yes".equalsIgnoreCase(value)) {
							this.value= true;
							this.type= DataType.BOOL;
						} else if ("true".equalsIgnoreCase(value)) {
							this.value= true;
							this.type= DataType.BOOL;
						} else if ("on".equalsIgnoreCase(value)) {
							this.value= true;
							this.type= DataType.BOOL;
						}
						
					} catch (NumberFormatException x4) { 
						
					}
				}
			}
			
		}
	}
	
	public NDSValue(byte[] value) {
		this.value= value;
		this.type= DataType.PDS_BINARY;
	}
	
	public NDSValue(Object value) {
		this.value= value;
		
		if (value instanceof Map) {
			throw new RuntimeException("Unsupported Type: "+ value.getClass());
		} else if (value instanceof String) {
			this.type= DataType.STR;
		} else if (value instanceof Boolean) {
			this.type= DataType.BOOL;
		} else if (value instanceof Long) {
			this.type= DataType.LONG;
		} else if (value instanceof Double) {
			this.type= DataType.FLOAT;
		} else if (value instanceof Float) {
			this.type= DataType.FLOAT;
			
		} else if (value instanceof String[]) {
			this.type= DataType.STR_ARRAY;
			
		} else if (value instanceof PDS[]) {
			this.type= DataType.PDS_ARRAY;
			
		} else {
			throw new RuntimeException("Unsupported Type: "+ value.getClass());
		}
		
	}
	
	public String toString() {
		if (value == null) { return "null"; }
		if (this.type == DataType.PDS_BINARY) {
			return "binary[]";
			// return new String((byte[])value);
		}
		return value.toString();
	}
	
	public Object getValue() { return value; }
	public DataType getType() { return type; }
	
	
	public JsonValue toJsonValue() {
		
		if (type == DataType.BOOL) {
			return new JsonPrimitive((Boolean) getValue());
		} else if (type == DataType.STR) {
			return new JsonPrimitive((String) getValue());
		} else if (type == DataType.INT) {
			return new JsonPrimitive((Integer) getValue());
		} else if (type == DataType.LONG) {
			return new JsonPrimitive((Long) getValue());
		} else if (type == DataType.FLOAT) {
			return new JsonPrimitive((Double) getValue());
		} else if (type == DataType.PDS_ARRAY) {
		} else if (type == DataType.STR_ARRAY) {
		}
		
		return null;	// unsupported
	}
	
	protected void setValue(Object value) {
		this.value= value;
	}
}