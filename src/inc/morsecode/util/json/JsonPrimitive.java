package inc.morsecode.util.json;

public class JsonPrimitive extends JsonValue {

	public JsonPrimitive() {
		super();
	}

	// public JsonPrimitive(String value) { super("null".equals(value) ? null : value); }
	public JsonPrimitive(String value) { super(value); }
	public JsonPrimitive(Integer value) { super(value); }
	public JsonPrimitive(Long value) { super(value); }
	public JsonPrimitive(Double value) { super(value); }
	public JsonPrimitive(Boolean value) { super(value); }

}
