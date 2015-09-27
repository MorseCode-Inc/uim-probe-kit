package inc.morsecode.core;

import inc.morsecode.probes.httpgtw.CGI;

import java.io.IOException;

public class Gateway extends Endpoint {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4057448745339339541L;


	public Gateway() {
		
	}

	
	public void GET_env(CGI cgi) throws IOException {
		cgi.setContentType("text/plain");
		
		try {
			cgi.println(probe.updateControllerInfo());
		} catch (Exception x) {
			cgi.println(x +": "+ x.getMessage());
		}
		
	}


	@Override
	public void init() {
		
	}
	

	
}
