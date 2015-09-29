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
		String imageUrl = "http://19c4d583.ngrok.io" + imageNode.getProperty("fileReference").getString();
		String imageUuid = UUID.nameUUIDFromBytes(imageUrl.getBytes(Charset.forName("UTF-8"))).toString();

        %>
{
    "entities": [
        {
            "uuid": "<%= uuid %>",
            "type": "node",
            "created" : "<%= createdDateStr %>",
            "modified": "<%= modifiedDateStr %>",
            "origin": "a65e80fe-6a41-428d-4df1-f614f2068aaa",
			        
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
		        "comments": {
		            "type": "array\u003creference\u003e",
		            "value": {
		                "und": []
		            }
		        },
		        "field_category": {
		            "type": "reference",
		            "value": {
		                "und": "8f9ec3c5-9292-4050-a11a-0173dfabce39"
		            }
		        },
		        "field_image": {
		            "type": "string",
		            "value": {
		                "und": "[<%= imageUuid %>]"
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
		        "language": {
		            "type": "string",
		            "value": {
		                "und": "en"
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
		                "und": "http:\/\/19c4d583.ngrok.io<%= path %>.html"
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