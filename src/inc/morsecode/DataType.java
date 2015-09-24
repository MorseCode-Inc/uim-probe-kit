package inc.morsecode;

import com.nimsoft.nimbus.PDS;


public enum DataType {
	STR(PDS.PDS_PCH, String.class)
	, FLOAT(PDS.PDS_F, Double.class)
	, LONG(PDS.PDS_I64, Long.class)
	, BOOL(PDS.PDS_PCH, Boolean.class)
	, INT(PDS.PDS_I, Integer.class)
	, NDS(PDS.PDS_PDS, NDS.class)
	, STR_ARRAY(PDS.PDS_PPCH, String[].class)		// array of strings
	, PDS_ARRAY(PDS.PDS_PPDS, PDS[].class)			// array of pds'
	;
	
	private int constant;
	
	DataType(int nimConstant, Class<?> javaClass) {
		this.constant= nimConstant;
	}
	
	public int getConstant() {
		return constant;
	}
}
