package inc.morsecode.nas;

import inc.morsecode.NDS;
import inc.morsecode.core.UIMMessage;

public class UIMAlarmClose extends UIMMessage {

	public UIMAlarmClose(NDS nds) { super(nds); }
	public UIMAlarmClose(UIMMessage message) { super(message); }

	
	public String getOneLiner() {
		String str= "";
		
		str+= "[alarm_close:"+ getNimid() +"]";
		
		return str;
	}
	

	
	
}
