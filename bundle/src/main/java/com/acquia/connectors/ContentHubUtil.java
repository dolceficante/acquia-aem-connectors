package com.acquia.connectors;

import java.util.HashMap;
import java.util.Map;

public class ContentHubUtil {
	private String apiKey;
	private String secretKey;
	private String baseUrl;
	
	public ContentHubUtil(String apiKey, String secretKey, String baseUrl){
		this.apiKey = apiKey;
		this.secretKey = secretKey;
		this.baseUrl = baseUrl;
	}
	
	
	public String registerClient(String clientName) throws Exception {
		ContentHubService service = ContentHubFactory.getInstance();
		Map<String,String> chConfig = new HashMap<String,String>();
		chConfig.put(ContentHubService.BASE_URL, baseUrl);
		service.init(apiKey, secretKey, "", chConfig);
		String uuid = service.register(clientName);
		return uuid;
	}

}
