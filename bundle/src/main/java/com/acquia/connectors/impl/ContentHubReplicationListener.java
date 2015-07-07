package com.acquia.connectors.impl;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationEvent;

@Component(immediate=true) @Service
@Property(name=EventConstants.EVENT_TOPIC,
		value = ReplicationEvent.EVENT_TOPIC)
public class ContentHubReplicationListener implements EventHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(ContentHubReplicationListener.class);	

	public void handleEvent(Event eventData) {
		LOG.debug("--handleEvent");
		ReplicationEvent event = ReplicationEvent.fromEvent(eventData);
		ReplicationActionType type = event.getReplicationAction().getType();
		String path = event.getReplicationAction().getPath();
		switch (type){
			case ACTIVATE:
				LOG.debug("----ACTIVATE");
				postData();
				break;
			case DEACTIVATE:
			case DELETE:
		}
		
	}
	
	private void postData(){
		// TODO Auto-generated method stub
		String url = "http://0fc9e05e.ngrok.io";
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		try {
			int code = client.executeMethod(method);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
