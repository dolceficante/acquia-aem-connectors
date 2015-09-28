package com.acquia.connectors;

import java.util.Map;

import org.json.JSONObject;

public interface ContentHubService {
	
	public static final String BASE_URL = "baseUrl";
	public void init(String api, String secret, String origin, Map<String,String> config);
	public JSONObject createEntities(String resourceUrl) throws Exception;
	public JSONObject settings() throws Exception;
}
