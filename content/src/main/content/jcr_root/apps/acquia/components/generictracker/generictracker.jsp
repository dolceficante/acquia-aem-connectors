<%@page session="false"%><%@page import="org.apache.sling.api.resource.Resource,
                org.apache.sling.api.resource.ValueMap,
                org.apache.sling.api.resource.ResourceUtil,
                com.day.cq.wcm.webservicesupport.Configuration,
                com.day.cq.wcm.webservicesupport.ConfigurationManager" %>
<%@include file="/libs/foundation/global.jsp" %><%

String[] services = pageProperties.getInherited("cq:cloudserviceconfigs", new String[]{});
ConfigurationManager cfgMgr = (ConfigurationManager)sling.getService(ConfigurationManager.class);
if(cfgMgr != null) {
    String acquiaId = null;
    String environment = null;
    Configuration cfg = cfgMgr.getConfiguration("acquia", services);
    if(cfg != null) {
        acquiaId = cfg.get("acquiaAccountId", null);
        environment = cfg.get("acquiaEnvironment", null);
    }

    if(acquiaId != null && environment != null) {
    %>
<script type="text/javascript">

   var _tcaq = _tcaq || [];
   _tcaq.push(['setAccount', '<%= acquiaId %>']);
   (function() {
      function async_load()
      {
         var s = document.createElement('script');
         s.type = 'text/javascript';
         s.async = true;
         s.src = ('https:' == document.location.protocol ? 'https' : 'http') + '://d12au6kkv2cs1v.cloudfront.net/<%= environment %>/<%= acquiaId %>/tc.js';
         var x = document.getElementsByTagName('script')[0];
         x.parentNode.insertBefore(s, x);
      }
      if (window.attachEvent)
         window.attachEvent('onload', async_load);
      else
         window.addEventListener('load', async_load, false);
   })();

</script>    
    <%
    }
}
%>
<div id="lift-recos-home-page"></div>