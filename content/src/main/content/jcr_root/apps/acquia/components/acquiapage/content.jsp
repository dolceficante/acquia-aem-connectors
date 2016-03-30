<%@page contentType="text/html"
            pageEncoding="utf-8"%>
<%@ page import="java.util.Map,
				java.util.HashMap,
				org.json.JSONObject,
				org.apache.sling.api.resource.ModifiableValueMap,
				com.acquia.connectors.ContentHubFactory,
				com.acquia.connectors.ContentHubService" %>            
            <%@include file="/libs/foundation/global.jsp"%>
<%

	String clientName = properties.get("clientName",null);
	String clientId = properties.get("clientId",null);
	String apiKey = properties.get("apiKey",null);
	String secretKey = properties.get("secretKey",null);
	String baseUrl = properties.get("serverUrl",null);
	String uuid = null;
	
	Map<String,String> chConfig = new HashMap<String,String>();
		chConfig.put(ContentHubService.BASE_URL, baseUrl);
	
	if (clientName != null && clientId == null){
		ContentHubService service = ContentHubFactory.getInstance();
		service.init(apiKey, secretKey, "", chConfig);
		uuid = service.register(clientName);
		ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
		map.put("clientId", uuid);
		resource.getResourceResolver().commit();
	}
	if (uuid != null){
		clientId = uuid;
	}
%>            


<div>
    <h3>Acquia Settings</h3>   
    <ul>
        <li><div class="li-bullet"><strong>Client Name: </strong><br><%= clientName %></div></li>
        <li><div class="li-bullet"><strong>Origin UUID: </strong><br><%= clientId %></div></li>
        <!--<li><div class="li-bullet"><strong>UUID: </strong><br><%= uuid %></div></li>-->
    </ul>
</div>

