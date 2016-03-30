package com.acquia.connectors.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acquia.connectors.ContentHubFactory;
import com.acquia.connectors.ContentHubService;
import com.acquia.http.HMACHttpRequestInterceptor;

public class ContentHubServiceImpl implements ContentHubService {
	
	private String origin;
	private String api;
	private String secret;
	private Map<String,String> config;
	
	private static final String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";
	private static final Logger LOG = LoggerFactory.getLogger(ContentHubServiceImpl.class);	

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
		JSONObject response = callPutService("/entities", request);
		return response;
	}	
	
	public JSONObject settings() throws Exception {
		JSONObject request = new JSONObject();
		JSONObject response = callGetService("/settings");
		return response;
	}
	
	private JSONObject callPutService(String path, JSONObject json) throws Exception {
		
		LOG.debug("callPutService");
		LOG.debug("path: " + path);
		LOG.debug("json: " + json);
		
		CloseableHttpClient httpClient = getHttpClient();
		String baseUrl = config.get(BASE_URL);
		HttpPut put = new HttpPut(baseUrl + path);
		LOG.debug("calling " + baseUrl + path);
		
		String jsonString = json.toString();
		LOG.debug(jsonString);
		LOG.debug("jsonString length:" + jsonString.length());
		StringEntity data = new StringEntity(json.toString(),"UTF-8");
		data.setContentType("application/json");	
		put.setEntity(data);
		processHeaders(put);

		HttpResponse httpResponse = httpClient.execute(put); 
		JSONObject jsonObj = processResponse(httpResponse);
		return jsonObj;	
	}
	
	private JSONObject callPostService(String path, JSONObject json) throws Exception {
		
		LOG.debug("callPostService");
		LOG.debug("path: " + path);
		LOG.debug("json: " + json);
		
		CloseableHttpClient httpClient = getHttpClient();
		String baseUrl = config.get(BASE_URL);
		HttpPost post = new HttpPost(baseUrl + path);
		
		StringEntity data = new StringEntity(json.toString(),"UTF-8");
		data.setContentType("application/json");
		post.setEntity(data);
		processHeaders(post);

		HttpResponse httpResponse = httpClient.execute(post); 
		JSONObject jsonObj = processResponse(httpResponse);
		LOG.debug("return: " + jsonObj);
		return jsonObj;	
	}	

	private JSONObject callGetService(String path) throws Exception {
		CloseableHttpClient httpClient = getHttpClient();
		String baseUrl = config.get(BASE_URL);
		HttpGet get = new HttpGet(baseUrl + path);
		System.out.println("callGetService:" + baseUrl + path);
		processHeaders(get);
		get.addHeader("Content-Type","application/json");
		
		HttpResponse httpResponse = httpClient.execute(get);
		LOG.debug("HttpResponse: " + httpResponse.getStatusLine().getStatusCode());
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
		request.removeHeaders("Accept-Encoding");
		request.addHeader("User-Agent", "AEM-Client/1.0.0");
	}
	
	private JSONObject processResponse(HttpResponse response) throws Exception {
		LOG.debug("client response:" + response.getStatusLine().getStatusCode());	
		
		HttpEntity entity = response.getEntity();
		InputStream in = entity.getContent();
		String result = convertStreamToString(in);
		in.close();
		
		JSONObject jsonObj = new JSONObject(result);	
		return jsonObj;
	}

	public String register(String clientName) throws Exception {
		JSONObject request = new JSONObject();
		request.put("name", clientName);
		JSONObject response = callPostService("/register", request);
		String uuid = response.optString("uuid");
		return uuid;
	}

}
