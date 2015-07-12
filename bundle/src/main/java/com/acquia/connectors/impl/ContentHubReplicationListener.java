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
		String api = "AAAAAAAAAAAAAAAAAAAA";
		String secret = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String baseUrl = "http://plexus-abarriosr-hybris-1722555236.us-east-1.elb.amazonaws.com";
		String origin = "9e23d436-3d10-49de-63ce-2308b7a9895e";	
		Map<String,String> config = new HashMap<String,String>();
		config.put(ContentHubService.BASE_URL, baseUrl);
		String resourceUrl = "https://s3.amazonaws.com/plexus-fixtures.acquia.com/entities.json";

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
		return result;
	}

}
