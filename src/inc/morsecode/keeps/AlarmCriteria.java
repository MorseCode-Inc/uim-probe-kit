package inc.morsecode.keeps;

import java.util.ArrayList;
import java.util.Iterator;

import inc.morsecode.NDS;;

public class AlarmCriteria implements Iterable<AlarmCriterionRule> {
	
	private NDS data;
	
	/**
	 *        <criteria>
                   	<0>
                   	  	_rule= exclude
                   	  	origin.matches= DEV|TEST|STAGE
                 	</0>
                   	<1>
                   	  	_rule= include
                   	  	prid.matches= netapp|clarion|snmp_get
                     	subsystem_id= 1.1.3
                 	</1>
              </criteria>
	 */
	public AlarmCriteria(NDS definition) {
		this.data= definition;
	}
	

	@Override
	public Iterator<AlarmCriterionRule> iterator() {
		
		
		final ArrayList<AlarmCriterionRule> triggers= new ArrayList<AlarmCriterionRule>();
		
		for (NDS t : data) {
			AlarmCriterionRule trigger= new AlarmCriterionRule(t);
			triggers.add(trigger);
		}
		
		return new Iterator<AlarmCriterionRule>() {
			
			private ArrayList<AlarmCriterionRule> items= triggers;
			private int i= 0;
			
			@Override
			public boolean hasNext() {
				return i < items.size();
			}
			
			@Override
			public AlarmCriterionRule next() {
				return items.get(i++);
			}
			
			@Override
			public void remove() { }
		};
	}

}
