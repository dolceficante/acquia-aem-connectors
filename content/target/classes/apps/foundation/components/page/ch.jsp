<%@ page session="false" %><%
%><%@ page import="com.adobe.granite.license.ProductInfo,
                   com.adobe.granite.license.ProductInfoService,
                   com.day.cq.commons.Externalizer,
                   com.day.cq.wcm.api.Page,
                   com.day.cq.wcm.api.WCMMode,
                   java.util.Iterator,
                   java.util.UUID,
                   java.util.Date,
                   java.nio.charset.Charset,
                   java.text.SimpleDateFormat,
                   javax.jcr.Node,
				   com.day.cq.wcm.webservicesupport.Configuration,
				   com.day.cq.wcm.webservicesupport.ConfigurationManager,                  
                   com.day.cq.wcm.api.components.ComponentContext" %><%
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%@ taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
%><%@ taglib prefix="atom" uri="http://sling.apache.org/taglibs/atom/1.0" %>
<%@include file="/libs/foundation/global.jsp"%>
<%
%><cq:defineObjects /><%

    try {
        WCMMode.DISABLED.toRequest(request);
        request.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true);
        Externalizer externalizer = sling.getService(Externalizer.class);
        String feedUrl = externalizer.absoluteLink(slingRequest, slingRequest.getScheme(), currentPage.getContentResource().getPath()) + ".feed";
        String url = externalizer.absoluteLink(slingRequest, slingRequest.getScheme(), currentPage.getPath());

        String link = url + ".html";
        String title = currentPage.getTitle() !=null ?
                currentPage.getTitle() : currentNode.getName();
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date createdDate = properties.get("cq:lastModified", Date.class);
        String createdDateStr = df.format(createdDate);
        
        Date modifiedDate = properties.get("jcr:created", Date.class);
        String modifiedDateStr = df.format(modifiedDate);
        
        
        String path = currentPage.getPath();
        String uuid = UUID.nameUUIDFromBytes(path.getBytes(Charset.forName("UTF-8"))).toString();
        String subTitle = currentPage.getDescription() != null ?
                currentPage.getDescription() : (String)properties.get("jcr:description", null);

		Node imageNode = currentPage.getContentResource("image").adaptTo(Node.class);
		String imageUrl = "http://ch.dolceficante.com" + imageNode.getProperty("fileReference").getString();
		String imageUuid = UUID.nameUUIDFromBytes(imageUrl.getBytes(Charset.forName("UTF-8"))).toString();
		
		String[] services = pageProperties.getInherited("cq:cloudserviceconfigs", new String[]{});

		ConfigurationManager cfgMgr = (ConfigurationManager)sling.getService(ConfigurationManager.class);
		Configuration cfg = cfgMgr.getConfiguration("acquia", services);
		String hostDomain = "";
	    String origin = "";
	    if(cfg != null) {
	        hostDomain = cfg.get("hostDomain", null);
	        origin = cfg.get("clientName", null);
	    }		

	    

        %>
{
    "entities": [
        {
            "uuid": "<%= uuid %>",
            "type": "node",
            "created" : "<%= createdDateStr %>",
            "modified": "<%= modifiedDateStr %>",
            "origin": "<%= origin %>",
			        
            "attributes": {
		        "author": {
		            "type": "reference",
		            "value": {
		                "und": "ef471d12-7952-449c-a545-649e078197d9"
		            }
		        },
		        "body": {
		            "type": "string",
		            "value": {
		                "und": "{\"summary\":\"\",\"value\":\"<%= subTitle %>\",\"format\":\"filtered_html\"}"
		            }
		        },
		        "language": {
		            "type": "string",
		            "value": {
		                "und": "en"
		            }
		        },
		        "field_tags": {
		            "type": "array\u003creference\u003e",
		            "value": {
		                "und": [
		                    "e8f2c3a6-f434-494d-9df2-1530c2597e2f",
		                    "c5e7acf1-1980-43cf-a338-c32ba0cf1bf9"
		                ]
		            }
		        },		        
		        "title": {
		            "type": "string",
		            "value": {
		                "und": "<%= title %>"
		            }
		        },
		        "type": {
		            "type": "string",
		            "value": {
		                "und": "article"
		            }
		        },
		        "url": {
		            "type": "string",
		            "value": {
		                "und": "<%= hostDomain %><%= path %>.html"
		            }
		        }
            }
        }
    ]
}        
        
        
        <%

    } catch (Exception e) {
        log.error("error rendering feed for page", e);
    }
%>