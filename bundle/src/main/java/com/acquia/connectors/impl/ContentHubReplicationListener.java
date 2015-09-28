package com.acquia.connectors.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.json.JSONObject;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acquia.connectors.ContentHubFactory;
import com.acquia.connectors.ContentHubService;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationEvent;

@Component(immediate=true) @Service
@Property(name=EventConstants.EVENT_TOPIC,
		value = ReplicationEvent.EVENT_TOPIC)
public class ContentHubReplicationListener implements EventHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(ContentHubReplicationListener.class);	

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
		if (action != null){
			LOG.debug("action.getPath(): " + action.getPath());
			path = action.getPath() + ".chub.html";
		} else {
			LOG.debug(">>No ReplicationAction");
		}
		String api = "ed7a5373-4198-4cc6-a438-aa8c0d48e7a9";
		String secret = "KydxY1Gb8dngMdCILwBMG1j/mrI+cxprYG1bbk1JoT+D4mM0cN23CDMOXjRAG8x4uyaz2aQ3W1JdiQjddMBc5g==";
		String baseUrl = "http://plexus-beta2-app-580736450.us-east-1.elb.amazonaws.com";
		String origin = "a65e80fe-6a41-428d-4df1-f614f2068aaa";	
		Map<String,String> config = new HashMap<String,String>();
		config.put(ContentHubService.BASE_URL, baseUrl);
		//String resourceUrl = "https://s3.amazonaws.com/plexus-fixtures.acquia.com/entities.json";
		String resourceUrl = "http://19c4d583.ngrok.io" + path;
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
		//JSONObject result = service.createEntities(resourceUrl);
		JSONObject result = service.settings();
		return result;
	}

}
