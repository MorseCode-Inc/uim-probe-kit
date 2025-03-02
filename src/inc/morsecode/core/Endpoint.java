package inc.morsecode.core;

import inc.morsecode.NDS;
import inc.morsecode.probes.httpgtw.CGI;
import inc.morsecode.util.json.JsonObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.json.ex.MalformedJsonException;
import util.kits.JsonParser;

import com.nimsoft.nimbus.NimLog;

public abstract class Endpoint extends HttpServlet {
	
	private static final long serialVersionUID = -2776581050963709584L;
	
	protected HttpGateway probe;
	protected NDS nimenv;
	protected NimLog log;

	public Endpoint() {
		super();
	}
	
	public void setProbe(HttpGateway probe, NDS config) {
		this.probe = probe;
		this.log= probe.log;
		loadConfig(config);
	}

	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		long startts= System.currentTimeMillis();
		resp.setHeader("client_key", req.getSession().getId());
		resp.addCookie(new Cookie("client_key", req.getSession().getId()));
		String sig= req.getRemoteHost() +":"+ req.getRemotePort() +" "+ req.getMethod() +" "+ req.getRequestURI() +" > ";
		String logMessage= " "+ req.getRemoteHost() +":"+ req.getRemotePort() +" "+ req.getMethod() +" "+ req.getRequestURI();
		
		String uri= req.getRequestURI();
		String function= uri.replaceAll("^.*/", "");
		
		try {
		
			CGI cgi= new CGI(req, resp);
			
			if (!probe.isAuthorizedClient(req)) {
				logMessage+= " Client IP Address Denied: "+ req.getRemoteAddr();
				cgi.resp.sendError(401, "Unauthorized");
				return;
			}
			
			String httpMethod= req.getMethod().toUpperCase();
				
			try {
					
				if ("GET".equals(httpMethod)
					|| "DELETE".equals(httpMethod)
					|| "HEAD".equals(httpMethod)) {
					Method method= this.getClass().getMethod(httpMethod +"_"+ function, cgi.getClass());
					Object result= method.invoke(this, cgi);
					if (result != null && result instanceof JsonResponse) {
						JsonResponse response= (JsonResponse)result;
						cgi.println(response);
					}
				} else {
					// PUT, POST .. need to deal with an input stream
					httpMethod= "POST";
						
					if ("application/json".equalsIgnoreCase(cgi.req.getContentType())) {
						try {
							JsonObject json = JsonParser.parse(cgi.in, req.getContentLength());
							Method method= this.getClass().getMethod(httpMethod +"_"+ function, cgi.getClass(), json.getClass());
							JsonResponse response= (JsonResponse)method.invoke(this, cgi, json);
							
							if (response != null) {
								cgi.println(response);
								logMessage+= " | "+  ""+ response.getStatusCode() +":"+ response.getMessage();
							} else {
								JsonResponse error= new JsonResponse(500, "Method "+ function +" returned NULL");
								cgi.out.write(error.toString().getBytes());
							}
						} catch (MalformedJsonException e) {
							logMessage+= " | "+  e.getMessage();
							resp.setStatus(400);		// 400 = Bad Request
							JsonResponse error= new JsonResponse(400, e.getMessage());
							cgi.out.write(error.toString().getBytes());
						}
					} else {
							
					}
						
				}
				
				
			} catch (InvocationTargetException itx) {
				JsonResponse response= packError(resp, sig, itx, 500, itx.getMessage());
				cgi.println(response);
			} catch (NoSuchMethodException x) {
				JsonResponse response= packError(resp, sig, x, 404, x.getClass().getSimpleName() +": "+ function +" does not exist, check the name and try again.");
				cgi.println(response);
			} catch (NoSuchMethodError x) {
				JsonResponse response= packError(resp, sig, x, 404, x.getClass().getSimpleName() +": "+ function +" does not exist, check the name and try again.");
				cgi.println(response);
			} finally {
			}
		
		} catch (NullPointerException npx) {
			int line= npx.getStackTrace()[0].getLineNumber();
			String file= npx.getStackTrace()[0].getFileName();
				
			String message= ("Null Pointer @ "+ file +":"+ line +" ["+ npx.getMessage() +"]");
			System.err.println(message);
			npx.printStackTrace();
			resp.setStatus(500);
		} catch (RuntimeException rx) {
			System.err.println(rx.getMessage());
			rx.printStackTrace();
			resp.setStatus(500);
		} catch (Exception x) {
			System.err.println(x.getMessage());
			x.printStackTrace();
			resp.setStatus(500);
		} catch (Error e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			resp.setStatus(500);
		} catch (Throwable t) {
			System.err.println(t.getMessage());
			t.printStackTrace();
			// just in case
			resp.setStatus(500);
		} finally {
			logMessage+= " < HTTP "+ resp.getStatus();
			long runtime= System.currentTimeMillis() - startts;
			System.out.println(logMessage +" > "+ (runtime) +"ms");
		}
		
	}

	private JsonResponse packError(HttpServletResponse resp, String sig, Throwable x, int code, String msg) {
		int line= x.getStackTrace()[0].getLineNumber();
		String file= x.getStackTrace()[0].getFileName();
		String message= (x.getClass().getSimpleName() +" @ "+ file +":"+ line +" ["+ msg +"]");
		System.err.println(sig + message);
		resp.setStatus(code);
		return new JsonResponse(code, x.getClass().getSimpleName() +": "+ msg);
	}


	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			
			NDS vars= new NDS();
			
			// copy the GET parameters from the servlet request into an NDS for convenience and type-casting
			for (String param : req.getParameterMap().keySet()) {
				String value= req.getParameter(param);
				vars.set(param, value);
			}
			
			if ("GET".equalsIgnoreCase(req.getMethod())) {
				index(new CGI(req, resp));
			}
		
		} catch (RuntimeException rx) {
			NimLog.getLogger(this.getClass()).error(rx.getMessage());
			
			resp.setStatus(500);
		} catch (Exception x) {
			NimLog.getLogger(this.getClass()).error(x.getMessage());
			
			resp.setStatus(500);
		} catch (Error e) {
			NimLog.getLogger(this.getClass()).error(e.getMessage());
			
			resp.setStatus(500);
		} catch (Throwable t) {
			NimLog.getLogger(this.getClass()).error(t.getMessage());
			// just in case
			resp.setStatus(500);
		}
	}

	
	
	public void index(CGI cgi) throws ServletException, IOException {
		
		HttpServletRequest req= cgi.req;
		HttpServletResponse resp= cgi.resp;
		NDS meta= cgi.meta;
		
		System.out.println(req.getRequestURI() +" "+ this.getClass());
		
		cgi.setContentType("text/plain");
		cgi.out.write(meta.toString().getBytes());
		
	}


	public abstract void init();
	
	public void loadConfig(NDS config) {
		
	}


	public int getSeverity(String severity) {
		if (severity == null) { return 2; }
		
		severity= severity.toLowerCase().trim();
		try {
			int sev= Integer.parseInt(severity);
			sev= Math.min(sev, 5);
			sev= Math.max(sev, 0);
			return sev;
		} catch (NumberFormatException nfx) {}
		
		if ("critical".equalsIgnoreCase(severity)) { return 5;}
		if ("major".equalsIgnoreCase(severity)) { return 4;}
		if ("minor".equalsIgnoreCase(severity)) { return 3;}
		if ("warn".equalsIgnoreCase(severity)) { return 2; }
		if ("warning".equalsIgnoreCase(severity)) { return 2; }
		if ("info".equalsIgnoreCase(severity)) { return 1; }
		if ("information".equalsIgnoreCase(severity)) { return 1; }
		if ("informational".equalsIgnoreCase(severity)) { return 1; }
		if ("clear".equalsIgnoreCase(severity)) { return 0; }
		
		log.warn("Invalid Alarm Severity: "+ severity);
		return 2;
	}

}