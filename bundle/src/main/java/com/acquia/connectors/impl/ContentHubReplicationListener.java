package com.acquia.connectors.impl;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;
import javax.jcr.observation.ObservationManager;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acquia.connectors.ContentHubFactory;
import com.acquia.connectors.ContentHubService;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationEvent;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManager;

@Component(immediate=true) @Service
@Property(name=EventConstants.EVENT_TOPIC,
		value = ReplicationEvent.EVENT_TOPIC)
public class ContentHubReplicationListener implements Runnable, EventHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(ContentHubReplicationListener.class);	
	
	private BundleContext bundleContext;
	
	  //Inject a Sling ResourceResolverFactory
    @Reference
    private ResourceResolverFactory resolverFactory;
    
    private ResourceResolver resourceResolver;
     
    private Session session;
     
    private ObservationManager observationManager;
     
  //Inject a Sling ResourceResolverFactory to create a Session requited by the EventHandler
    @Reference
    private SlingRepository repository;;	

	public void handleEvent(Event eventData) {
		LOG.debug("--handleEvent1");
		ReplicationEvent event = ReplicationEvent.fromEvent(eventData);
		LOG.debug("--handleEvent2");
		ReplicationActionType type = event.getReplicationAction().getType();
		LOG.debug("--type: " + type);
		String[] propertyNames = eventData.getPropertyNames();
		for (int x=0; x<propertyNames.length; x++){
			LOG.debug(">>propertyName: " + propertyNames[x]);
		}
		
		ReplicationAction action = event.getReplicationAction();
		String path = "";
		String pathWithExt = "";
		if (action != null){
			LOG.debug("action.getPath(): " + action.getPath());
			path = action.getPath();
			pathWithExt = action.getPath() + ".ch.html";
		} else {
			LOG.debug(">>No ReplicationAction");
		}
			
		String api = "";
		String secret = "";
		String baseUrl = "";
		String origin = "";			
		String hostDomain = "";
		
		LOG.debug("path = " + path);
		Resource resource = resourceResolver.getResource(path);
		

		InheritanceValueMap pageProperties = new HierarchyNodeInheritanceValueMap(resource);
		
		LOG.debug("pageProperties.size() " + pageProperties.size());
		
		//Get configuration from cloud service configurations
		String[] services = pageProperties.getInherited("cq:cloudserviceconfigs", new String[]{});
		LOG.debug("Services[].length " + services.length);
		ServiceReference serviceRef = bundleContext.getServiceReference(ConfigurationManager.class.getName());
		
		ConfigurationManager cfgMgr = (ConfigurationManager)bundleContext.getService(serviceRef);
		Configuration cfg = cfgMgr.getConfiguration("acquia", services);
	    if(cfg != null) {
	        api = cfg.get("apiKey", null);
	        secret = cfg.get("secretKey", null);
	        baseUrl = cfg.get("serverUrl", null);
	        origin = cfg.get("clientId", null);
	        hostDomain = cfg.get("hostDomain", null);
	    }		
	    LOG.debug("api: " + api);
	    LOG.debug("secret: " + secret);
	    LOG.debug("baseUrl: " + baseUrl);
	    LOG.debug("origin: " + origin);
	    LOG.debug("hostDomain: " + hostDomain);
				
		
		Map<String,String> config = new HashMap<String,String>();
		config.put(ContentHubService.BASE_URL, baseUrl);

		String resourceUrl = hostDomain + pathWithExt;
		LOG.debug("resourceUrl: " + resourceUrl);
		
		switch (type){
			case ACTIVATE:
				LOG.debug("----ACTIVATE");
				try {
					JSONObject result = postData(api, secret, origin, config, resourceUrl);
					LOG.debug(result.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LOG.error("ERROR: " + e.getMessage());
				}
				break;
			case DEACTIVATE:
			case DELETE:
		}
		
	}
	
	private JSONObject postData(String api, String secret, String origin, Map<String,String> config, String resourceUrl) throws Exception {
		// TODO Auto-generated method stub
		ContentHubService service = ContentHubFactory.getInstance();
		service.init(api, secret, origin, config);
		JSONObject result = service.createEntities(resourceUrl);
		//JSONObject result = service.settings();
		return result;
	}

	public void run() {
		// TODO Auto-generated method stub
		LOG.info("ContentHubReplicationListener RUNNING!");
	}

	protected void activate(ComponentContext ctx){
		this.bundleContext = ctx.getBundleContext();
		try {
			this.resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			LOG.info("ERROR" + e);
			e.printStackTrace();
		}
		
	}
}
