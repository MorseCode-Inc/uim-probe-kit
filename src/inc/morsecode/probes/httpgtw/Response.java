package inc.morsecode.probes.httpgtw;


import inc.morsecode.NDS;
import inc.morsecode.util.json.JsonObject;

public class Response extends Throwable {
	
	private static final long serialVersionUID = 851237839233368653L;
	private NDS response;
	private JsonObject payload;

	public Response(int code, String message) {
		this.response= new NDS();
		response.set("response/code", code);
		response.set("response/message", message);
		setPayload(new JsonObject());
	}

	public void setPayload(JsonObject payload) {
		this.payload= payload;
	}
	
	public void setPayload(NDS payload) {
		this.payload= payload.toJson();
	}
	
	
	public int getStatusCode() {
		return response.seek("response", "code", -1);
	}
	
	public String getMessage() {
		return response.get("response/message", "no message");
	}
	
	public NDS getNDS() {
		return response;
	}
	
	public NDS getPayload() {
		return response.getSection("payload", new NDS("payload"));
	}

	
	public String toString() {
		JsonObject json= response.toJson();
		json.set("payload", payload);
		return json.toString();
	}
	
}
