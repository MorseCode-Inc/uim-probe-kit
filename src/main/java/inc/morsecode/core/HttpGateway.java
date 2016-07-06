package inc.morsecode.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Random;

import inc.morsecode.NDS;
import inc.morsecode.NDSValue;
import inc.morsecode.NimLogPrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;


import org.apache.catalina.Context;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.codec.binary.Base64;

import util.kits.DateKit;
import util.kits.DynamicClassKit;
import util.kits.SimpleCalendar;
import util.security.Crypto;
import util.security.Decoder;
import util.security.Encoder;
import util.security.codecs.SecurityCodec;

import com.nimsoft.nimbus.NimAlarm;
import com.nimsoft.nimbus.NimConfig;
import com.nimsoft.nimbus.NimException;
import com.nimsoft.nimbus.NimLog;
import com.nimsoft.nimbus.NimProbe;
import com.nimsoft.nimbus.NimRequest;
import com.nimsoft.nimbus.NimSession;
import com.nimsoft.nimbus.NimUserLogin;
import com.nimsoft.nimbus.PDS;
import com.nimsoft.nimbus.ci.ConfigurationItem;


public abstract class HttpGateway extends NimProbe implements org.apache.catalina.LifecycleListener, CustomProbeInterface {
	
	public final static String PROBE_MANUFACTURER= "MorseCode Incorporated";
	
	public static String SUBSYSTEM_ID= "3.6526.3";
	
	private static NDS persistentData;
	private boolean flushCache;
	private static CustomProbeInterface instance;
	private File persistentCache;
	
	protected NimLog log;
	protected static NDS config;
	private NDS messageTemplates;
	private NDS activeProfiles;
	private NDS controllerInfo= new NDS();
	
	private boolean started= false;
	private Tomcat tomcat= null;
	
	private Thread tomcatThread;
	
	private boolean ready= false;
	private boolean admin= false;
	private boolean startup= true;
	
	
	protected HttpGateway(String name, String version, String[] args) throws NimException {
		this(name, version, PROBE_MANUFACTURER, args);
	}

	protected HttpGateway(String name, String version, String manufacturer, String[] args) throws NimException {
		super(name, version, manufacturer, args);
		HttpGateway.instance= this;
		
		this.log= NimLog.getLogger(this.getClass());
		refreshConfiguration();
		NimLog.setLogLevel(getLogLevel());
		log.setLogSize(getLogSize());
		
		NimLogPrintWriter error= new NimLogPrintWriter(log, NimLog.ERROR);
		NimLogPrintWriter out= new NimLogPrintWriter(log, NimLog.INFO);
		System.setErr(error);
		System.setOut(out);
		
		log.info("Log Level = "+ getLogLevel());
		setStartPort(getProbePort());
		ready= true;
	}
	
	private HttpGateway() throws NimException {
		super("internal", "1.0", PROBE_MANUFACTURER, new String[]{});
	}

	
	/* (non-Javadoc)
	 * @see inc.morsecode.pagerduty.CustomProbeInterface#refreshConfiguration()
	 */
	@Override
	public void refreshConfiguration() throws NimException {
		
		config= NDS.create(NimConfig.getInstance());
		
		persistentCache= new File(config.get("setup/persistent_cache", "data/persist.dat"));
		
		if (persistentCache.exists()) {
			try {
				persistentData= NDS.create(new NimConfig(persistentCache.getAbsolutePath()));
			} catch (NimException nx) {
				if (nx.getCode() == 90) { // Configuration error, No < in the config file: No < in the config file
					// cahce file is corrupt, trash it and start over.
					persistentCache.delete();
					persistentData= new NDS("cache");
				} else {
					throw nx;
				}
			}
		} else {
			persistentData= new NDS("cache");
			try {
				persistentData.writeToFile(persistentCache);
			} catch (IOException iox) {
				
				throw new NimException(NimException.E_ACCESS, "Failed to save persistent cache file: "+ persistentCache.getAbsolutePath() +" ["+ iox.getMessage() +"]");
			}
		}
		
		
	}
	
	
	public void bootstrap() throws NimException {
		
		try {
			licenseCheck();
		} catch (NimException nx) {
			log.error("License Check Failed: "+ nx.getMessage());
			System.exit(1);
		} catch (IOException iox) {
			log.error("License Check Failed: "+ iox.getMessage());
			System.exit(1);
		}
		
		try {
			authenticate();
		} catch (NimException nx) {
			System.out.println(nx.getMessage());
			nx.printStackTrace();
			System.out.flush();
			System.err.flush();
			return;
			// System.exit(1);
		}
		
		try {
			bootTomcat();
		} catch (Throwable e) {
			System.out.println(e.getMessage());
			Throwable cause= e;
			e.printStackTrace();
			cause = stackdump(cause);
			System.exit(1);
		}
  			
		// now it should be safe to make any calls we need to the bus
		updateControllerInfo();
		
	}

	public void authenticate() throws NimException {
		if (config.get("setup/admin/enabled", false)) {
			String token= config.get("setup/admin/token", null);
			String key= config.get("setup/admin/key", null);
			
			if (token == null || key == null) {
				System.out.println("MSG001 Probe admin authentication enabled, but configuration is incomplete.  Run set_admin using the probe utility.");
			} else {
				String user= getDecoder().decode(token);
				String pass= getDecoder().decode(key);
				try {
					NimUserLogin.login(user, pass);
					admin= true;
				} catch (NimException nx) {
					switch (nx.getCode()) {
					case 12:
						System.out.println("MSG002 Probe admin authentication: failed to login as user '"+ user +"'. Try running set_admin callback again using the probe utility.");
						break;
					default:
						throw nx;
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see inc.morsecode.pagerduty.CustomProbeInterface#updateControllerInfo()
	 */
	@Override
	public NDS updateControllerInfo() throws NimException {
		this.controllerInfo= getControllerInformation();
		return controllerInfo;
	}
	
	public NDS getConfig() { return config; }

	public abstract void probeCycle();
	
	
	public void execute() {
		
		if (ready && startup) {
			startup= false;
			try {
				bootstrap();
			} catch (Throwable cause) {
				cause.printStackTrace();
				stackdump(cause);
				System.exit(1);
			}
			
	        // Ensure process isn't left running if it actually failed to start
	        if (LifecycleState.FAILED.equals(this.tomcat.getConnector().getState())) {
	            System.err.println("Tomcat connector in failed state, check that port "+ getListenPort() +" is not already in use.");
	            System.exit(1);
	        }
	        
		} 
		
		
		if (flushCache && 900 * 1000 < (System.currentTimeMillis() - persistentData.get("last_write", 0L))) {
			// the cache was recently updated
			flushCacheNow();
		}
		
		/*
		try {
			// NDS controllerInfo= call("controller", "get_info", new String[]{});
			// System.out.println(controllerInfo);
		} catch (NimException nx) {
			log.error(nx.getMessage());
		}
		*/
		
		try {
			probeCycle();
		} catch (Throwable anything) {
			log.error("Error during probe execution cycle: "+ anything.getClass());
			log.error(anything.getMessage());
			anything.printStackTrace();
		}
		
	}

	private void flushCacheNow() {
		log.debug("Flush cache ("+ persistentData.mass() +") to disk > "+ persistentCache.getAbsolutePath());
		// log.trace("Cache Data\n"+ persistentData);
		try {
			persistentData.writeToFile(persistentCache);
			persistentData.set("last_write", System.currentTimeMillis());
		} catch (FileNotFoundException e) {
			System.err.println("ERR002 Failed to save runtime data file: "+ persistentCache +" ["+ e.getMessage() +"]");
		} catch (IOException e) {
			System.err.println("ERR003 Failed to save runtime data file: "+ persistentCache +" ["+ e.getMessage() +"]");
		}
	}

	private Throwable stackdump(Throwable cause) {
		log.error(cause.getClass().getSimpleName() +": "+ cause.getMessage());
		while (cause.getCause() != null) {
			cause= cause.getCause();
			// log.debug(" ERR Exception: "+ cause.getMessage() +" in "+ cause.getStackTrace()[0].getMethodName());
			for (StackTraceElement ste : cause.getStackTrace()) {
				// log.trace("\t at "+ ste.getFileName() +":"+ ste.getLineNumber());
			}
		}
		return cause;
	}

	private void bootTomcat() {
		NDS endpoints= config.seek("setup/endpoints");
		if (!started || tomcat == null) {
		   	  tomcat= new Tomcat();
		   	  
		   	  int port= getListenPort();
		   	  
		   	  if (port > 65535 || port <= 0) {
		   		  // invalid port
		   		  throw new RuntimeException("Invalid listener port specified, must be between 1-65535 ("+ port +")");
		   	  }
		   	  tomcat.setPort(port);
		   	  
		   	  // Connector c= new Connector("HTTP/1.1");
		   	  // c.setPort(port);
		   	  String bindIp= config.seek("setup/listener", "ip", "0.0.0.0");
		   	  // tomcat.getServer().setAddress(bindIp);
		   	  // tomcat.getConnector().setAttribute("address", bindIp);
		   	  // c.setAttribute("address", bindIp);
		   	  // tomcat.setHost();
		   	  // tomcat.setConnector(c);
		   	  

		   	  tomcat.setBaseDir("."); // /home/bcmorse/workspaces/probe-marketplace/embedded_tomcat");
		   	  tomcat.getHost().setAppBase(".");

		   	  String contextRoot = config.get("setup/endpoints/context", "/pd");
		   	  String contextPath = endpoints.get("context", contextRoot);
		   	  
		   	  if ("".equals(contextPath)) {
		   		  // fail
		   		  log.fatal("context must be specified, cannot be empty and must begin with /.  example: /nimbus");
		   		  System.exit(-1);
		   	  }
		   	  
		   	  String warpath= "srvr/pagerduty";
		   	  
		   	  System.out.println("Context Path: "+ contextPath);
		   	  System.out.println("Webapp Path: "+ warpath);
		   	  System.out.println("Listen Port: "+ port);
		   	  // System.out.println("Listen Address: "+ tomcat.getServer().getAddress());

		   	  // Add AprLifecycleListener
		   	  StandardServer server = (StandardServer)tomcat.getServer();
		   	  AprLifecycleListener listener = new AprLifecycleListener();
		   	  server.addLifecycleListener(listener);
		   	  Catalina catalina = new Catalina();
		 
		   	  tomcat.getServer().setCatalina(catalina);
		   	  
		   	  // tomcat.getServer().getCatalina().setConfigFile("conf/server.xml");
		   	  // URL contextxml= ClassLoader.getSystemResource("context.xml");
		   	  
		   	  try {
		   		  //Context ctx= tomcat.addWebapp(contextPath, "srvr/ROOT");
		   		  // Context gateway= tomcat.addWebapp("/nimbus", "srvr/http_gateway");
		   		  
		   		  Context gateway= tomcat.addWebapp(contextPath, warpath);
		   		  
		   		  NDS root= new NDS("root");
		   		  root.set("servlet", Gateway.class.getName());
		   		  root.set("url_path", "/*");
		   		  endpoints.add(root);
		   		  
		   		  for (NDS webapp : endpoints) {
		   			  if (!webapp.get("active", true)) {
		   				  continue;
		   			  }
		   			  Wrapper web= gateway.createWrapper();
		   			  web.setName(webapp.getName());
		   			  web.setLoadOnStartup(1);
		   			  
		   			  String servletClass = webapp.get("servlet", ""); // "inc.morsecode.probes.http_gateway.Gateway");
		   			  
		   			  if (!servletClass.startsWith("inc.morsecode.") && !servletClass.contains(".")) {
		   				  servletClass= "inc.morsecode.endpoints." + servletClass;
		   			  }
		   			  
		   			  web.setServletClass(servletClass);
		   			  
		   			  // create a new one
			   		  DynamicClassKit dck= new DynamicClassKit();
			   		  try {
							Endpoint endpoint= (Endpoint)dck.getInstanceOf(servletClass);
							endpoint.setProbe(this, webapp);
							web.setServlet(endpoint);
							
			   			  	gateway.addChild(web);
			   			  	web.addInitParameter("debug", "0");
			   			  	System.out.println("Loading "+ web.getName() +" ("+ web.getServletClass() +") "+ webapp.get("url_path", "[null url_path]"));
			   			  	do_stuff(gateway, webapp.getName(), webapp, "/");
			   			  	
			   			  	// endpoint.loadConfig(webapp);
			   			  	// gateway.addServletMapping("/alarm*", "alarm-gateway", true);
						} catch (IllegalAccessError e) {
							log.error(e.getMessage());
							stackdump(e);
						} catch (ClassNotFoundException e) {
							log.error(e.getMessage());
							stackdump(e);
						} catch (InstantiationException e) {
							log.error(e.getMessage());
							stackdump(e);
						} catch (IllegalAccessException e) {
							log.error(e.getMessage());
							stackdump(e);
						} catch (LinkageError e) {
							log.error(e.getMessage());
							stackdump(e);
						}
		   			  
		   			  
		   		  }
		   		  

		   	  	try {
		   			System.out.println("Starting HTTP Server");
					tomcat.start();
					started= true;
				} catch (LifecycleException e) {
					Throwable cause= stackdump(e);
					System.out.println(cause.getClass());
				}
			} catch (ServletException e1) {
				stackdump(e1);
			}
		   	  
		   	  
		   	  tomcatThread= new Thread() {
		   		  public void run() {
		   			  tomcat.getServer().await();
		   		  }
		   	  };
		   	  
		   	  tomcatThread.start();
		   	  

		}
	}

	private int getListenPort() {
		return config.seek("setup/listener", "port", 3801);
	}

	private void do_stuff(Context gateway, String servlet, NDS webapp, String base) {
		
		String pattern= webapp.get("url_path", base + webapp.getName() +"/*");
		gateway.addServletMapping(pattern, servlet, false);
		
		for (NDS page : webapp) {
			// do_stuff(gateway, servlet, page, base +"/"+ webapp.getName());
		}
	}
	
	/* (non-Javadoc)
	 * @see inc.morsecode.pagerduty.CustomProbeInterface#shutdown()
	 */
	@Override
	public void shutdown() {
		
		flushCacheNow();
		
		// need to signal tomcat that we are shutting down
		try {
			tomcat.stop();
		} catch (LifecycleException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Shutdown");
			System.out.flush();
			System.err.flush();
		}
		
	}
	
	public void reload(NimSession session, PDS args) throws NimException {
		log.info("** RELOAD **");
		refreshConfiguration();
		session.sendReply(0, new PDS());
	}
	
	
	public void set_admin(NimSession session, String user, String password, String confirm, PDS args) throws NimException {
		NDS response= new NDS();
		
		if (user == null || "".equals(user)) {
			response.set("status", "Error: user cannot be empty or null.");
			session.sendReply(0, response.toPDS());
			return;
		}
		
		if (password == null || confirm == null || "".equals(password) || "".equals(confirm)) {
			response.set("status", "Error: password cannot be empty or null.");
			session.sendReply(0, response.toPDS());
			return;
		}
		
		if (!password.equals(confirm)) {
			response.set("status", "Error: password and confirm does not match.");
			session.sendReply(0, response.toPDS());
			return;
		}
		
		try {
			NimUserLogin.login(user, password);
			admin= true;
		} catch (NimException error) {
			response.set("status", "Error: "+ error.getMessage());
			session.sendReply(0, response.toPDS());
			return;
		}
		
		// we logged in, need to write to the configuration file somehow now.
		NimRequest controller= controllerRequest();
		
		NDS status = writeConfig("/setup/admin", "token", getEncoder().encode(user), controller);
		status = writeConfig("/setup/admin", "key", getEncoder().encode(password), controller);
		status = writeConfig("/setup/admin", "enabled", "yes", controller);
		status.setName("detail");
		
		System.out.println(status);
		
		response.add(status);
		
		response.set("status", "OK");
		response.set("token", getEncoder().encode(user));
		response.set("key", getEncoder().encode(password));
		
		controller.close();
		
		
		session.sendReply(0, response.toPDS());
	}

	public NimRequest controllerRequest() {
		return new NimRequest("controller", "get_info");
	}
	
	public NimRequest spoolerRequest(String method, PDS args) {
		return new NimRequest("spooler", method, args);
	}
	

	protected NDS writeConfig(String section, String key, String value, NimRequest controller) throws NimException {
		NDS nds= new NDS();
		
		nds.set("name", getProbeName());		// probe name
		nds.set("section", section);		// 
		nds.set("key", key);				// 
		nds.set("value", value);	// 
		nds.set("lockid", 1);	// 
		nds.set("robot", "/"+ controllerInfo.get("domain") +"/"+ controllerInfo.get("hubname") +"/"+ controllerInfo.get("robotname"));	// 
		
		NDS status= NDS.create("response", controller.send("probe_config_set", nds.toPDS()));
		return status;
	}
	
	
	protected void writeConfig(String path, String name, List<NDS> data, NimRequest controllerRequest) throws NimException {
		
		for (NDS nds : data) {
			writeConfig(path +"/"+ name, nds.getName().replaceAll("//*", "_"), nds, controllerRequest);
		}
		
	}
	
	protected void writeConfig(String path, String name, NDS data, NimRequest controllerRequest) throws NimException {
		
		for (String key : data.keys()) {
			writeConfig(path +"/"+ name, key.replaceAll("//*", "_"), data.get(key), controllerRequest);
		}
		
		for (NDS tag : data) {
			writeConfig(path +"/"+ name, tag.getName().replaceAll("//*", "_"), tag, controllerRequest);
		}
		
	}
	
	static public long getInterval() {
		long ifNull= config.get("setup/run.interval", 10);
		long cycle= config.get("setup/cycle", ifNull);
		if (cycle < 5) {
			cycle= 5;
		}
		return cycle * 1000;
	}
	
	/**
	 * 
	 * @return sample rate is the interval in seconds
	 */
	static public int getSampleRate() {
		return (int)(getInterval() / 1000);
	}
	
	static public int getProbePort() {
		int level= config.get("setup/port", 48005);
		return level;
	}
	
	static public int getLogLevel() {
		int level= config.get("setup/loglevel", NimLog.INFO, 0, 10);
		return level;
	}
	
	static public long getLogSize() {
		long level= config.get("setup/logsize", 10240L);
		return level;
	}
	
	
	/* (non-Javadoc)
	 * @see inc.morsecode.pagerduty.CustomProbeInterface#getSubsystemId()
	 */
	@Override
	public String getSubsystemId() {
		return config.get("setup/subsystem_id", config.get("setup/subsystem", SUBSYSTEM_ID));
	}
	
	

	/* (non-Javadoc)
	 * @see inc.morsecode.pagerduty.CustomProbeInterface#getQoSDefinitions()
	 */
	@Override
	public NDS getQoSDefinitions() {
		return config.seek("QOS_DEFINITIONS", true);
	}

	public String getDefaultCIPath() {
		return "2.1.1";
	}
	
	public NimLog getLogger() {
		return log;
	}
	
	/* (non-Javadoc)
	 * @see inc.morsecode.pagerduty.CustomProbeInterface#getRobotName()
	 */
	@Override
	public String getRobotName() {
		return controllerInfo.get(NIM_ROBOT_NAME);
	}

	/* (non-Javadoc)
	 * @see inc.morsecode.pagerduty.CustomProbeInterface#getSource()
	 */
	@Override
	public String getSource() {
		return controllerInfo.get(NIM_SOURCE, getRobotName());
	}
	
    
    
    
    @Override
    public void lifecycleEvent(LifecycleEvent arg0) {
    	
    	System.out.println("Lifecycle Event: "+ arg0);
    }

	public static CustomProbeInterface getInstance() {
		return instance;
	}
    
	private NDS call(String address, String command, String[] ... params) throws NimException {
		
		NDS args= new NDS();
		
		for (String[] arg: params) {
			if (arg.length == 2) {
				args.set(arg[0], arg[1]);
			}
		}
		
		NimRequest request= new NimRequest(address, command, args.toPDS());
		
		NDS response= NDS.create("response", request.send());
		
		request.close();
		return response;
	}
    
	
	/* (non-Javadoc)
	 * @see inc.morsecode.pagerduty.CustomProbeInterface#call(java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	public NDS call(String address, String command, int retries, String[] ... params) throws NimException {
		while (retries-- >= 0) {
			try {
				return call(address, command, params);
			} catch (NimException nx) {
				if (retries <= 0) {
					throw nx;
				}
			}
		}
		return null;
	}
	
	
	private NDS getControllerInformation() throws NimException {
		
		NDS info= call("controller", "get_info", 1);
		
		return info;
		
	}

	public boolean defaultSource() {
		return config.get("setup/default_source", true);
	}
	
	
	
	protected void licenseCheck() throws NimException, IOException {
		
		SimpleCalendar now= new SimpleCalendar();
		SimpleCalendar tenDays= new SimpleCalendar();
		SimpleCalendar fourtyFiveDays= new SimpleCalendar();
		
		fourtyFiveDays.advanceDay(config.get("setup/license/expire_warning", 45, 0, 180));
		tenDays.advanceDay(config.get("setup/license/expire_error", 10, 0, 90));
		
		String key= config.get("setup/license/key", "");
		
		ProbeLicense license= HttpGateway.readLicense(getProbeName() +"_license", key);
		
		if (license == null) {
			System.err.println("Unable to verify license (null).");
			System.exit(3);
		}
		
		if (license.getInstances() <= 0) {
			System.out.println("License does not permit this instance. Contact sales@morsecode-inc.com.");
			System.exit(3);
		}
		
		String validThru= license.getValidUntil();
		
		SimpleCalendar expires;
		try {
			expires= DateKit.toCalendar(validThru);
			
			System.out.println("License valid until: "+ validThru);
		
			int severity= 0;
			String message= "Probe License will Expire on "+ expires;
			
			if (now.after(expires)) {
				// trial has expired
				System.out.println("License has Expired. Contact sales@morsecode-inc.com.");
				message= "Probe License Expired on "+ expires;
				severity= 5;
			} else if (expires.before(tenDays)) {
				severity= config.get("setup/license/expire_warning_severity", 1, 0, 5);
			} else if (expires.before(fourtyFiveDays)) {
				severity= config.get("setup/license/expire_error_severity", 4, 0, 5);
			}
			
			
			String ci_path= "10.3";
			
			try {
				ConfigurationItem ci= new ConfigurationItem(ci_path, getSource());
				int metricId= 2;
				NimAlarm alarm= new NimAlarm(severity, message, getSubsystemId(), "license/epiration", getSource(), ci, ci_path +":"+ metricId);
				alarm.send();
				alarm.close();
			} catch (NimException nx) {
				log.error("Failure to send license expiration alarm: "+ nx.getMessage());
				log.error("\tMake sure the robot is running and healthy on this host.");
			}
			
		} catch (ParseException e) {
			System.out.println("License key is missing, damaged, or corrupt. Contact sales@morsecode-inc.com.");
			log.error(e.getMessage());
			System.exit(3);
		}
		
		
	}
	
	
	static ProbeLicense readLicense(String name, String key) {
		
		try {
			String info= ((HttpGateway)instance).getDecoder().decode(key);
			
			System.out.println("Reading License: "+ info);
			
			String[] pieces= info.split("\\|");
			
			int instances= 0;
			
			try {
				instances= (Integer.parseInt(pieces[1]));
			} catch (NumberFormatException nfx) {
				System.err.println("ERR Reading License Instances: "+ nfx.getMessage());
			}
			
			String issuedTo= pieces[0];
			
			ProbeLicense license;
			try {
				license = new ProbeLicense(name, issuedTo, instances, DateKit.toCalendar(pieces[2]));
				return license;
			} catch (ParseException e) {
				System.err.println("ERR Reading License Date: "+ e.getMessage());
			}
		} catch (NullPointerException npx) {
			System.err.println("ERR Read License Failure: NULL POINTER in "+ npx.getStackTrace()[0].getMethodName());
		} catch (Throwable error) {
			System.err.println("ERR Read License Failure: "+ error.getMessage());
		}
		
		return null;
	}
	
	static ProbeLicense create(ProbeLicense license) {
		String sauce= license.getIssuedTo();
		
		sauce+= "|"+ license.getInstances();
		sauce+= "|"+ license.getValidUntil();
		
		try {
			HttpGateway instance= new HttpGateway() { public void probeCycle() { } };
			license.setKey(((HttpGateway)instance).getEncoder().encode(sauce));
			return license;
		} catch (NimException e) {
			e.printStackTrace();
		}
		
		return null;
	}
		
	
	public void writeCache(Endpoint endpoint, String key, NDS value) {
		writeCache(endpoint, key, value, false);
	}
	
	public void writeCache(Endpoint endpoint, String key, NDS value, boolean flush) {
		String namespace = endpoint.getClass().getName() +"/"+ key;
		writeCache(namespace, value, flush);
	}

	public void writeCache(String namespace, NDS value, boolean flush) {
		// System.err.println("WRITE Namespace = "+ namespace +", value.name = "+ value.getName());
		persistentData.seek(namespace, true).add(value);
		// persistentData.set(namespace, value);
		flushCache= flush;		// signal that an update was made to the cache and it should be written to disk
	}
	
	/**
	 * Signal that an update was made to the cache and should be written to disk.
	 */
	public void flushCache() { flushCache= true; }
	
	public NDS readCache(Endpoint endpoint, String key) {
		return persistentData.seek(endpoint.getClass().getCanonicalName() +"/"+ key, false);
	}
	public NDS readCache(Endpoint endpoint, String key, boolean autoCreate) {
		return persistentData.seek(endpoint.getClass().getCanonicalName() +"/"+ key, autoCreate);
	}
	public NDS readCache(String namespace, String key, boolean autoCreate) {
		return persistentData.seek(namespace +"/"+ key, autoCreate);
	}
	
	public NDS deleteCache(Endpoint endpoint) {
		try {
			NDSValue deleted= persistentData.delete(endpoint.getClass().getCanonicalName());
			return (NDS)deleted;
		} catch (ClassCastException ignore) {
			return null;
		}
	}
	
	public NDS deleteCache(Endpoint endpoint, String key) {
		try {
			NDSValue deleted= persistentData.delete(endpoint.getClass().getCanonicalName() +"/"+ key);
			return (NDS)deleted;
		} catch (ClassCastException ignore) {
			return null;
		}
	}
	
	public boolean isAuthorizedClient(HttpServletRequest req) {
		
		boolean authorized= false;
		
		NDS authorizedClients = config.seek("setup/security/authorized_clients", true);
		
		// security/authorized_clients is disabled, allow any client
		if (!authorizedClients.isActive()) { return true; }
		
		boolean any= false;
		
		for (NDS auth : authorizedClients) {
			
			if (!auth.isActive()) { continue; }
			
			any= true;
			
			String ip= req.getRemoteAddr();
			String pattern= auth.get("ip");
			
			if (auth.isActive()) {
				if (ip.matches(pattern)) {
					return true;
				}
			}
			
		}
		
		// didn't process any client ip address sections, so allow everyone
		if (!any) { return true; }
		
		return authorized;
	}
	
	/* (non-Javadoc)
	 * @see inc.morsecode.pagerduty.CustomProbeInterface#getMessage(java.lang.String)
	 */
	@Override
	public NDS getMessage(String name) {
		NDS config= HttpGateway.config;
		NDS message= config.seek("messages/"+ name);
		return message;
	}
	

	
	public boolean isAdmin() {
		return admin;
	}
	
	
	
	protected Encoder getEncoder() { return new HttpGateway.Encode(); }
	protected Decoder getDecoder() { return new HttpGateway.Decode(); }
	

	private final class Encode implements Encoder {
	
		public final String encode(String text) { return encode(text, "skSqz]JgYxN-,)2@(3wV"); }
		public final String encode(String text, String entropy) {
			text= add(text, entropy);
			text= new String(Base64.encodeBase64((text).getBytes()));
			String phase2 = phase2(text);
			
			return new String(Base64.encodeBase64((Crypto.encode(phase2, new Secret()).getBytes())));
		}
	
		private String phase2(String text) {
			return Crypto.encode(text);
		}
		
		private final String add(String text, String entropy) {
			if (entropy == null) { entropy= "t\\y>\'IFn5?s"; }
			return entropy.replaceAll(":", "_") +":"+ text;
		}
		

		private class Secret implements SecurityCodec {
		
	
			public String getAlphabet(String[] c) {
				return "hi8_#94/*%X+=dr[WUfOt\\y>\'IFn5?skSqz]JgYxN-,)2@(3wV<C1Rb\"{Dcup:L MGBZP6~aH;Em}.^QKjloA`Tv0$e|&7!";
			}
	
			public int getRotator(String[] c) {
				
				String a= getAlphabet(c);
				int r= (int)a.charAt(a.charAt(3 % a.length()));
				r*= (int)a.charAt(a.charAt(67 % a.length()));
				r+= (int)a.charAt(a.charAt(104 % a.length()));
				
				return r % 9999;
				
			}
		
		
		}
	
	
	}
	
	private final class Decode implements Decoder {
		
		public final String decode(String cypherText) {
			cypherText= new String(Base64.decodeBase64((cypherText).getBytes()));
			String phase1 = phase1(cypherText);
			String clear= Crypto.decode(phase1);
			return remove(new String(Base64.decodeBase64((clear).getBytes())));
		}
	
	
		private String phase1(String cypherText) {
			return Crypto.decode(cypherText, new Secret());
		}
		
		
		private final String remove(String salted) {
			return salted.substring(salted.indexOf(':') + 1);
		}
	
		private class Secret implements SecurityCodec {
			
			
			public String getAlphabet(String[] c) {
				// return "y>\'IFn5?shifOt\\kSqz]JgYxN-,)2@(3wV<Dcup:L MGBZP6~aH;Em8_#94/*%X+=dC1Rb\"{r[WU}.^QKjloA`Tv0$e|&7!";
				return "hi8_#94/*%X+=dr[WUfOt\\y>\'IFn5?skSqz]JgYxN-,)2@(3wV<C1Rb\"{Dcup:L MGBZP6~aH;Em}.^QKjloA`Tv0$e|&7!";
			}
	
			public int getRotator(String[] c) {
				String a= getAlphabet(c);
				int r= (int)a.charAt(a.charAt(3 % a.length()));
				r*= (int)a.charAt(a.charAt(67 % a.length()));
				r+= (int)a.charAt(a.charAt(104 % a.length()));
				
				return r % 9999;
			}
		
		
		}
	
	}



}
