package inc.morsecode;

import java.util.Map;

public class ProbeMessage extends NDS {

	public ProbeMessage() {
	}

	public ProbeMessage(String name) {
		super(name);
	}

	public ProbeMessage(Map<String, Object> map) {
		super(map);
	}

	public ProbeMessage(String name, Map<String, Object> map) {
		super(name, map);
	}

	public ProbeMessage(NDS nds) {
		super(nds);
	}

	public ProbeMessage(NDS nds, boolean reference) {
		super(nds, reference);
	}

}
