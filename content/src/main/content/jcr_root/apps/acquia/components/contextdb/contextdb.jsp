<%@page session="false"%><%@page import="org.apache.sling.api.resource.Resource,
                org.apache.sling.api.resource.ValueMap,
                org.apache.sling.api.resource.ResourceUtil,
                com.day.cq.wcm.webservicesupport.Configuration,
                com.day.cq.wcm.webservicesupport.ConfigurationManager" %>
<%@include file="/libs/foundation/global.jsp" %><%

String[] services = pageProperties.getInherited("cq:cloudserviceconfigs", new String[]{});
ConfigurationManager cfgMgr = (ConfigurationManager)sling.getService(ConfigurationManager.class);
if(cfgMgr != null) {
    String snippetCode = null;
    Configuration cfg = cfgMgr.getConfiguration("generic-tracker", services);
    if(cfg != null) {
        snippetCode = cfg.get("snippetCode", null);
    }

    if(snippetCode != null) {
    %>
    <script type="text/javascript">
    <%= snippetCode %>
    </script><%
    }
}
%>
