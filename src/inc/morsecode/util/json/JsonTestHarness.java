package inc.morsecode.util.json;

public class JsonTestHarness {

	public JsonTestHarness() {
	}
	
	public static void main(String[] args) {
		
		JsonObject o= new JsonObject();
		
		JsonMember first= new JsonMember("first", "Brad");
		JsonMember last= new JsonMember("last", "Morse");
		JsonMember age= new JsonMember("age", 31);
		JsonMember street= new JsonMember("street", "6526 Silverleaf Ct.");
		JsonMember city= new JsonMember("city", "Firestone");
		JsonMember state= new JsonMember("state", "CO");
		JsonMember zip= new JsonMember("zip", 80504);
		
		System.out.println(first);
		System.out.println(last);
		System.out.println(age);
		System.out.println(street);
		System.out.println(city);
		System.out.println(state);
		System.out.println(zip);
		
		JsonArray array= new JsonArray();
		array.add(first.getJsonValue());
		array.add(last.getJsonValue());
		array.add(age.getJsonValue());
		array.add(street.getJsonValue());
		array.add(city.getJsonValue());
		array.add(state.getJsonValue());
		array.add(zip.getJsonValue());
		
		System.out.println(array);
		
		
		JsonObject obj= new JsonObject();
		
		obj.set(first);
		obj.set(last);
		obj.set(age);
		obj.set(street);
		obj.set(city.getName(), (String)city.getValue());
		obj.set(state.getName(), (String)state.getValue());
		obj.set(zip.getName(), (Integer)zip.getValue());
		obj.set("array", array);
		
		System.out.println(obj);
		
	}

}
