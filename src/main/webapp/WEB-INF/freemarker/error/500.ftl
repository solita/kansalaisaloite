<#import "../components/layout.ftl" as l />
<#import "../components/utils.ftl" as u />

<#escape x as x?html> 
<@l.error "error.500.title">

    <h1><@u.message "error.500.title"/></h1>
    
    <!-- Error: 500 - Internal Server Error -->
    <#assign errorCaseIdNotNull>${errorCaseId!"no case id"}</#assign>
    <#assign emailSubject><@u.message "error.500.emailSubject"/></#assign>
    <#assign emailBody1><@u.message "error.500.emailBody1"/></#assign>
    <#assign emailBody2><@u.message "error.500.emailBody2"/></#assign>
    <#assign mailtoLink>mailto:${feedbackEmail!""}?subject=${urls.urlPercentEncode(emailSubject)}&amp;body=${urls.urlPercentEncode(emailBody1)}%0A%0A-----%20%0A%0A${urls.urlPercentEncode(emailBody2)}%20${errorCaseIdNotNull}</#assign>
    <#assign feedbackLink = urls.help(HelpPage.CONTACT.getUri(locale)) />
    
    <p><@u.messageHTML key="error.500.report" args=[feedbackLink] /></p>
      
    <p><@u.messageHTML key="error.500.instruction" args=[requestURI, urls.baseUrl] /></p>
   
    <br/>
    <p>
        <script type="text/javascript">
        /*<![CDATA[*/
            document.write("<a id=\"show\" onClick=\"show('extra-details','show','hide');\"><@u.message key='error.500.showDetails' /></a>");
            document.write("<a id=\"hide\" onClick=\"hide('extra-details','hide','show');\" style=\"display:none;\"><@u.message key='error.500.hideDetails' /></a>");    
        /*]]>*/
        </script>
    </p>
    <div class="system-msg msg-info" id="extra-details" style="display:none;">
        <p><@u.messageHTML "error.500.subtitle"/>${errorCaseIdNotNull}</p>
        <i>${errorMessage!}</i>
    </div>

    <#-- Prefer not to use external sources, like jQuery. -->
    <script type="text/javascript">
    /*<![CDATA[*/
        
        function show(id, thisId, thatId){
            document.getElementById(id).style.display="block";
            document.getElementById(thatId).style.display="block";
            document.getElementById(thisId).style.display = "none";
        }
         
        function hide(id, thisId, thatId){
            document.getElementById(id).style.display="none";
            document.getElementById(thatId).style.display="block";
            document.getElementById(thisId).style.display = "none";
        }
    /*]]>*/
    </script>

</@l.error>
</#escape>