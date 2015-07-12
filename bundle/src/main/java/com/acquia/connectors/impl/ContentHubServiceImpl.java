package com.acquia.connectors.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import com.acquia.connectors.ContentHubService;
import com.acquia.http.HMACHttpRequestInterceptor;

public class ContentHubServiceImpl implements ContentHubService {
	
	private String origin;
	private String api;
	private String secret;
	private Map<String,String> config;
	
	private static final String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";

	public void init(String api, String secret, String origin,
			Map<String, String> config) {
		this.origin = origin;
		this.api = api;
		this.secret = secret;
		this.config = config;		
		
	}
	
	public JSONObject createEntities(String resourceUrl) throws Exception{
		JSONObject request = new JSONObject();
		request.put("resource", resourceUrl);
		JSONObject response = callPostService("/entities", request);
		return response;
	}	
	
	private JSONObject callPostService(String path, JSONObject json) throws Exception {
		
		CloseableHttpClient httpClient = getHttpClient();
		String baseUrl = config.get(BASE_URL);
		HttpPost post = new HttpPost(baseUrl + path);
		String contentType = "application/json";
		StringEntity data = new StringEntity(json.toString(),contentType,"UTF-8");
		post.setEntity(data);		
		processHeaders(post);


		HttpResponse httpResponse = httpClient.execute(post); 
		JSONObject jsonObj = processResponse(httpResponse);
		return jsonObj;	
	}

	private JSONObject callGetService(String path) throws Exception {
		CloseableHttpClient httpClient = getHttpClient();
		String baseUrl = config.get(BASE_URL);
		HttpGet get = new HttpGet(baseUrl + path);
		processHeaders(get);
		get.addHeader("Content-Type","application/json");
		
		HttpResponse httpResponse = httpClient.execute(get);
		JSONObject jsonObj = processResponse(httpResponse);
		return jsonObj;		
	
	}
	
	
		
	private String convertStreamToString(InputStream is) {

	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
	private CloseableHttpClient getHttpClient(){
		HMACHttpRequestInterceptor authorizationInterceptor = new HMACHttpRequestInterceptor("Acquia", api, secret, "SHA256");
		CloseableHttpClient httpClient = HttpClientBuilder.create().addInterceptorLast( authorizationInterceptor ).build();
		return httpClient;
	}
	
	private void processHeaders(HttpRequest request){
		request.addHeader("X-Acquia-Plexus-Client-Id", origin);
		String dateValue = new SimpleDateFormat(DATE_FORMAT).format(new Date());
		request.addHeader("Date", dateValue);	

	}
	
	private JSONObject processResponse(HttpResponse response) throws Exception {
		System.out.println("client response:" + response.getStatusLine().getStatusCode());	
		
		HttpEntity entity = response.getEntity();
		InputStream in = entity.getContent();
		String result = convertStreamToString(in);
		in.close();
		
		JSONObject jsonObj = new JSONObject(result);	
		return jsonObj;
	}

}
