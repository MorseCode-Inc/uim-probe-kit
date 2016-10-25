package inc.morsecode.nas;

import inc.morsecode.NDS;

public class AlarmFields extends NDS {

    public AlarmFields(NDS data) {
        super(data);
    }
    
    public AlarmFields(UIMAlarmMessage data) {
        super(data.seek("udata", true));
    }
    
    
}
