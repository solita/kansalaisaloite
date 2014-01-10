<#import "utils.ftl" as u />


<#--
 * Social media buttons
-->
<#macro some pageTitle="">
    <#assign shareURL>${urls.urlPercentEncode(currentUri)}</#assign>
    <#assign shareTitle>${urls.urlPercentEncode(pageTitle)}</#assign>

    <div class="some-links">
        <#-- Facebook -->
        <a href="https://www.facebook.com/sharer.php?u=${shareURL}&amp;t=${shareTitle}" target="_blank" title="<@u.message "some.facebook" />" class="icon-some facebook trigger-tooltip"> </a>
        
        <#--
         * Google
         * https://developers.google.com/+/plugins/share/#sharelink
        -->
        <a href="https://plus.google.com/share?url=${shareURL}" onclick="javascript:window.open(this.href,'', 'menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=600,width=600');return false;" title="<@u.message "some.google" />" class="icon-some google trigger-tooltip"> </a> 
        
        <#-- Twitter -->
        <a href="https://twitter.com/home?status=${shareTitle}:%20${shareURL}" target="_blank" title="<@u.message "some.twitter" />" class="icon-some twitter trigger-tooltip"> </a>
    </div>
</#macro>