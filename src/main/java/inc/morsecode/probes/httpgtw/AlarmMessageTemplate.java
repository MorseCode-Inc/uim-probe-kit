package inc.morsecode.probes.httpgtw;

import inc.morsecode.NDS;

public class AlarmMessageTemplate {
	
	private NDS template;

	public AlarmMessageTemplate(NDS template) {
		this.template= template;
	}

	
	public String getSeverity() {
		return template.get("severity", "information");
	}
	
	public String getSubsystem() {
		return template.get("subsystem", "1.1");
	}
	
	public String getTag() {
		return template.get("tag", "#"+ template.getName());
	}
}
