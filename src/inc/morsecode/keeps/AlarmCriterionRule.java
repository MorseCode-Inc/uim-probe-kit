package inc.morsecode.keeps;

import java.util.Map;
import java.util.regex.PatternSyntaxException;

import inc.morsecode.NDS;
import inc.morsecode.nas.UIMAlarmNew;
import inc.morsecode.util.json.JsonObject;

public class AlarmCriterionRule extends NDS {

	/**
	 *              <1>
                   	  	_rule= include
                   	  	prid.matches= netapp|clarion|snmp_get
                     	subsystem_id= 1.1.3
                 	</1>
	 * @param nds
	 */
	public AlarmCriterionRule(NDS nds) {
		super(nds);
	}


	
	public String getRule() { 
		return get("_rule", "include");
	}
	
	public boolean isInclude() { return "include".equals(getRule()); }
	public boolean isExclude() { return "exclude".equals(getRule()); }
	
	public boolean is(String rule) {
		if (rule == null) { return null == getRule(); }
		return rule.equals(getRule());
	}



	public boolean matches(UIMAlarmNew alarm, AlarmFilter trigger) {
		
// System.out.println("Profile Check "+ trigger.getName() +" ["+ alarm.getSubject() +"] service_key="+ trigger.getServiceKey(null));
		
		for (String key : getKeys()) {
			
			if (key.contains(".")) {
				String pattern= get(key);
				String comparison= key.split("\\.")[1];
				key= key.split("\\.")[0];
				
				String value= alarm.getAlarmField(key, null);
				
				if (value == null) {
					System.err.println("Invalid alarm criteria key: '"+ key +"' is null for rule '"+ getName() +"'. Filter may not work as desired.");
					continue;
				}
				
				boolean matches= false;
				if ("matches".equals(comparison)) {
					matches = compare(key, pattern, value);
				} else if ("starts_with".equals(comparison)) {
					matches = value.startsWith(pattern);
				} else if ("ends_with".equals(comparison)) {
					matches = value.endsWith(pattern);
				} else if ("contains".equals(comparison)) {
					matches = value.contains(pattern);
				} else if ("eq".equals(comparison)) {
					matches = value.equalsIgnoreCase(pattern);
				} else if ("not".equals(comparison)) {
					matches = !compare(key, pattern, value);
				}
				
				System.out.println(trigger.getName() + " "+ alarm.getAlarmNimid() +": "+ key +" [ "+ value +" "+ comparison +" "+ pattern +" ] "+ getRule() +" ? "+ matches);
				if (!matches) {
					return false;
				}
				
			} else if (!key.startsWith("_")) {
				// exact equality matching
				
				String value= alarm.getAlarmField(key, null);
				String expected= get(key);
				
				if (value != null) {
					System.out.println(trigger.getName() + ": "+ key +" [ "+ value +" == "+ expected +" ] "+ getRule() +" ? "+ value.equals(expected));
					if (!value.equals(expected)) {
						return false;
					}
				}
				
			}
			
		}
		
		
		return true;
		
	}



	public boolean compare(String key, String pattern, String value) {
		// make sure the pattern is valid
		boolean matches= false;
		try {
			matches= value.matches(pattern);
		} catch (PatternSyntaxException psx) {
			System.err.println("Invalid Regular Expression Pattern ["+ key +" '"+ pattern +"']: "+ psx.getMessage());
			psx.printStackTrace();
			matches= (value.equals(pattern));
		}
		return matches;
	}
	
	
}
