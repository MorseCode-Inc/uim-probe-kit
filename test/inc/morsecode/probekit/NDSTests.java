package inc.morsecode.probekit;
import inc.morsecode.NDS;

import org.junit.Assert;
import org.junit.Test;

public class NDSTests {

	private static final double EPSILON = 0.000001;


	@Test
	public void testName() {
		NDS nds= new NDS("name");
		Assert.assertEquals("name", nds.getName());
		nds.setName("different name");
		Assert.assertEquals("different name", nds.getName());
	}

	@Test
	public void testIntName() {
		NDS nds= new NDS(5);
		Assert.assertEquals("5", nds.getName());
	}

	@Test
	public void testString() {
		NDS nds= new NDS();
		nds.set("value", "hello world");
		String value= nds.get("value");
		Assert.assertEquals("hello world", value);
	}

	@Test
	public void testInteger() {
		NDS nds= new NDS();
		nds.set("value", "7");
		int value= nds.get("value", -1);
		Assert.assertEquals(7, value);

		nds.set("value", 12);
		value= nds.get("value", -1);
		Assert.assertEquals(12, value);
	}
	
	@Test
	public void testDouble() {
		NDS nds= new NDS();
		nds.set("value", "1.23");
		double value= nds.get("value", -1.45);
		Assert.assertEquals(1.23, value, EPSILON);

		nds.set("value", 7.64);
		value= nds.get("value", -1.45);
		Assert.assertEquals(7.64, value, EPSILON);
	}

	@Test
	public void testFloat() {
		NDS nds= new NDS();
		nds.set("value", "1.44");
		double value= nds.get("value", (float)-1.45);
		Assert.assertEquals(1.44, value, EPSILON);

		nds.set("value", (float)4.11);
		value= nds.get("value", (float)-1.45);
		Assert.assertEquals(4.11, value, EPSILON);
	}
	
	@Test
	public void testLong() {
		NDS nds= new NDS();
		long ts= System.currentTimeMillis();
		nds.set("value", ""+ ts);
		long value= nds.get("value", -1L);
		Assert.assertEquals(ts, value);

		ts= System.currentTimeMillis() + 5;
		nds.set("value", ts);
		value= nds.get("value", -1L);
		Assert.assertEquals(ts, value);
	}

	@Test
	public void testBoolean() {
		NDS nds= new NDS();
		nds.set("value", true);
		boolean value= nds.get("value", false);
		Assert.assertEquals(true, value);

		nds.set("true", "true");
		value= nds.get("true", false);
		Assert.assertEquals(true, value);

		nds.set("yes", "yes");
		value= nds.get("yes", false);
		Assert.assertEquals(true, value);

		nds.set("on", "on");
		value= nds.get("on", false);
		Assert.assertEquals(true, value);

		nds.set("one", "1");
		value= nds.get("one", false);
		Assert.assertEquals(true, value);

		nds.set("other", "not_true");
		value= nds.get("other", true);
		Assert.assertEquals(false, value);
	}

	@Test
	public void testBooleanFalse() {
		NDS nds= new NDS();
		nds.set("value", false);
		boolean value= nds.get("value", true);
		Assert.assertEquals(false, value);
	}
	
	@Test
	public void testIfNullString() {
		NDS nds= new NDS();
		String value= nds.get("key", "default");
		Assert.assertEquals("default", value);
	}

	@Test
	public void testIfNullInteger() {
		NDS nds= new NDS();
		int value= nds.get("key", 20);
		Assert.assertEquals(20, value);
	}

	@Test
	public void testIfNullDouble() {
		NDS nds= new NDS();
		double value= nds.get("key", (double)2.66);
		Assert.assertEquals((double)2.66, value, EPSILON);
	}

	@Test
	public void testNotInteger() {
		NDS nds= new NDS();
		nds.set("name", "value");
		int value= nds.get("name", 4);
		Assert.assertEquals(-1, value);
	}

	@Test
	public void testNotDouble() {
		NDS nds= new NDS();
		nds.set("name", "value");
		double value= nds.get("name", 4.78);
		Assert.assertEquals(-1, value, EPSILON);
	}
	
	@Test
	public void testSeek() {
		NDS nds= new NDS();
		nds.set("section/key", "value");
		NDS section= nds.seek("section");
		String value= section.get("key");
		Assert.assertEquals("value", value);
	}

	@Test
	public void testSetNDS() {
		
		NDS person= new NDS();
		person.set("firstname", "Jack");
		person.set("lastname", "Jones");
		
		NDS address = getMockAddress();

		person.set("address", address);
		
		Assert.assertEquals("123 main", person.get("address/address1"));
		Assert.assertEquals("suite 200", person.get("address/address2"));
		Assert.assertEquals("Beverly Hills", person.get("address/city"));
		Assert.assertEquals("CA", person.get("address/state"));
		Assert.assertEquals("90210", person.get("address/zip"));

	}
	
	@Test
	public void testKeys() {
		NDS nds= getMockAddress();

		String[] keys= new String[] {"address1", "address2", "city", "state", "zip"};
		
		for (String key : nds.getKeys()) {
			boolean found= false;
			for (String expected : keys) {
				if (key.equals(expected)) {
					found= true;
					break;
				}
			}
			Assert.assertTrue("Key "+ key +" not found", found);
		}
	}

	
	@Test
	public void testDeleteKey() {
		
		NDS nds= new NDS();
		nds.set("name", "value");
		nds.delete("name");
		
		Assert.assertNull(nds.get("name"));
		
	}

	@Test
	public void testDeleteSection() {
		
		NDS nds= new NDS();
		nds.set("address", getMockAddress());
		nds.delete("address");
		
		Assert.assertNull(nds.seek("address"));
		
	}
	
	@Test
	public void testCloneReference() {
		NDS nds= new NDS();
		nds.set("name", "value");
		
		NDS clone= new NDS(nds, true);
		
		nds.set("name", "another value");
		
		Assert.assertEquals("another value", clone.get("name"));
		
	}

	@Test
	public void testCloneCopy() {
		NDS nds= new NDS();
		nds.set("name", "value");
		
		NDS clone= new NDS(nds);

		nds.set("name", "another value");
		
		Assert.assertNotEquals("another value", clone.get("name"));
		
	}
	

	private NDS getMockAddress() {
		NDS address= new NDS();
		address.set("address1", "123 main");
		address.set("address2", "suite 200");
		address.set("city", "Beverly Hills");
		address.set("state", "CA");
		address.set("zip", "90210");
		return address;
	}
	
	
	
	
	
}
