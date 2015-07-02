<%@page session="false"%><%--

  ADOBE CONFIDENTIAL
  __________________

   Copyright 2011 Adobe Systems Incorporated
   All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%>
<%@page contentType="text/html"
            pageEncoding="utf-8"
            import="java.util.Iterator,
                    org.apache.sling.api.resource.Resource,
                    org.apache.sling.api.resource.ValueMap,
                    com.day.cq.i18n.I18n,
                    com.day.cq.wcm.api.Page,
                    com.day.cq.wcm.webservicesupport.Service,
                    com.day.cq.wcm.webservicesupport.ConfigurationManager,
                    com.day.cq.wcm.webservicesupport.ConfigurationUtil"%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%

    ConfigurationManager cfgMgr = sling.getService(ConfigurationManager.class);
    Service service = cfgMgr.getService(currentPage.getName());
    String pageTitle =  xssAPI.encodeForHTML(properties.get("jcr:title", "Cloud Services"));
    
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<html>
<head>
    <title>CQ5 Cloud Service | <%= pageTitle %></title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
    <cq:includeClientLib categories="cq.cloudserviceconfigs"/>
</head>
<body> 
    <%
		I18n i18n = new I18n(request);
        Resource pageResource = resource.getParent();
        String id = pageResource.getName();
        ValueMap content = resource.adaptTo(ValueMap.class);
        if (content == null) {
            content = ValueMap.EMPTY;
        }
        String title =  xssAPI.encodeForHTML(content.get("jcr:title", ""));
        String description =  xssAPI.encodeForHTML(content.get("jcr:description", ""));       

        Boolean isEnabled = ConfigurationUtil.hasConfigurations(pageResource);               
        String globalIcnCls = "cq-config-header";
        globalIcnCls += isEnabled ? "-on" : "-off";                
        String status =  isEnabled ? "enabled" : "disabled";

        %>
        <div><cq:include path="trail" resourceType="cq/cloudserviceconfigs/components/trail"/></div>
        
        <div class="cq-cloudservice-dms">
        <h2 class="<%= globalIcnCls %>"><%= title %></h2><%
        %><p><%= description %></p><%
        String thumbnailPath = service.getThumbnailPath();
        if (thumbnailPath == null) {
            thumbnailPath = "/libs/cq/cloudserviceconfigs/widgets/themes/default/widgets/CloudServiceDialog/thumbnail.png";
        }
        %>
         <div class="cq-cloudservice-thumb-empty">
            <img src="<%=thumbnailPath%>" class="thumb" alt="<%=title%>" /> 
        </div> 
        <div style="padding: 0px 0px 0px 15px; width: 522px; float:left;"> 
            <div style="padding: 2px 0px 2px 0px;  border-bottom: 1px solid #f5f5f5; height: 18px; background-color: #f9f9f9;">
                <div style="padding-left: 12px; width: 250px; float: left;"><%= i18n.get("Service is") %></div>
                <div style="padding-left: 10px; width: 100px; float: left;"><strong><%= i18n.getVar(status, "Cloud Service status") %></strong></div>
            </div> 
            <%
            Resource statistics = pageResource.getChild("statistics");
            if (statistics != null) {
                int cnt = 0;
                for(Iterator<Resource> it = statistics.listChildren(); it.hasNext();) {
                    cnt++;
                    Resource stat = it.next();
                    ValueMap stats = stat.adaptTo(ValueMap.class);
                    String name = stats.get("jcr:title",stat.getName());
                    String value = stats.get("total", "0");
                %>
                <div style="padding: 2px 0px 2px 0px;  border-bottom: 1px solid #f5f5f5; height: 18px;  
                        <%= (cnt%2 == 0) ? "background-color: #f9f9f9;" : "" %>;">
                    <div style="padding-left: 12px; width: 250px; float: left;"><%=name%></div>
                    <div style="padding-left: 10px; width: 100px; float: left;"><strong><%=value%></strong></div>
                </div> 
                <%
                }
            }
            %>
        </div>
        </div>       
        <div class="available-configs">
            <h2 style="border: none; margin-top: 10px; padding-left:0px;"><%= i18n.get("Available Configurations") %>
            <% if (content.get("allowMultiple", true)){
                   String resPath = xssAPI.encodeForJSString(resource.getPath().replace("/jcr:content", ""));
            %>
                   [<a href="javascript: CQ.cloudservices.editNewConfiguration('<%=resPath%>','<%=resPath%>', true, CQ.I18n.get('Create Configuration'))" style="color: #336600;" title="<%= i18n.get("Create Configuration") %>"><b>+</b></a>]
            <% } %>
            </h2>
            <%@include file="/libs/cq/cloudserviceconfigs/components/childlist/childlist.jsp"%><%-- cq:include accepts no params --%>
            <%=printChildren(currentPage, request)%>
        </div> 
</body>
</html>
