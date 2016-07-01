package inc.morsecode.keeps;

import inc.morsecode.NDS;

import java.util.ArrayList;
import java.util.List;

public class AlarmFilter {

	protected NDS definition;
	
	public AlarmFilter() {
		this.definition= new NDS();
	}

	public AlarmFilter(NDS definition) {
		this.definition= definition;
	}

	public boolean isActive() {
		return definition.isActive();
	}

	public String getName() {
		return definition.getName();
	}

	public List<AlarmCriterionRule> getExcludeCriteria() {
		AlarmCriteria criteria= new AlarmCriteria(definition.seek("criteria"));
		List<AlarmCriterionRule> rules= new ArrayList<AlarmCriterionRule>();
	
		for (AlarmCriterionRule rule : criteria) {
			if (rule.isExclude()) {
				rules.add(rule);
			}
		}
		
		return rules;
	}

	public List<AlarmCriterionRule> getIncludeCriteria() {
		AlarmCriteria criteria= new AlarmCriteria(definition.seek("criteria"));
		List<AlarmCriterionRule> rules= new ArrayList<AlarmCriterionRule>();
	
		for (AlarmCriterionRule rule : criteria) {
			if (rule.isInclude()) {
				rules.add(rule);
			}
		}
		
		return rules;
	}

}