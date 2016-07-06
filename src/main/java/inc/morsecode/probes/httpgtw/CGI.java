package inc.morsecode.probes.httpgtw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import inc.morsecode.NDS;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CGI {

	public HttpServletRequest req;
	public HttpServletResponse resp;
	
	public NDS vars;
	public NDS meta;
	
	public OutputStream out;
	public InputStream in;
	
	public CGI(HttpServletRequest req, HttpServletResponse response) throws IOException {
		
		this.req= req;
		this.resp= response;
		
		this.meta= new NDS("meta");
		this.vars= new NDS("vars");
		
		meta.set("request/uri", req.getRequestURI());
		meta.set("request/url", req.getRequestURL());
		meta.add(vars);
		meta.set("request/requested_session_id", req.getRequestedSessionId());
		meta.set("request/session_id", req.getSession().getId());
		meta.set("context", req.getContextPath());
		
		
		// copy the GET parameters from the servlet request into an NDS for convenience and type-casting
		Set<String> keySet = req.getParameterMap().keySet();
		for (String param : keySet) {
			String value= req.getParameter(param);
			vars.set(param, value);
		}
		
		this.in= req.getInputStream();
		this.out= resp.getOutputStream();
		
	}
	
	public void setContentType(String value) {
		resp.setContentType(value);
	}

	public void println(Object o) throws IOException {
		out.write((o +"\n").getBytes());
	}

	public String getRequestHeader(String name, String ifNull) {
		
		String header= req.getHeader(name);
		
		if (header == null) { 
			return ifNull;
		}
		
		return header;
		
	}

	public String getSanitizedRemoteHost() {
		
		String host= req.getRemoteHost();
		if ("0:0:0:0:0:0:0:1".equals(host) || "127.0.0.1".equals(host)) {
			return "localhost";
		}
		
		return host.toLowerCase();
	
	}

	public String getSanitizedRemoteIp() {
		
		String host= req.getRemoteAddr();
		if ("0:0:0:0:0:0:0:1".equals(host) || "127.0.0.1".equals(host)) {
			return "127.0.0.1";
		}
		
		return host.toLowerCase();
	
	}

}
