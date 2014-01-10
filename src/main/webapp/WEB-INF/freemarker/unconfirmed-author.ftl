<#import "components/utils.ftl" as u />

<#escape x as x?html> 

<#assign topContribution>


<#--
 * confirmCurrentAuthor
 * 
 * Info box for confirming current author role (accept/decline).
 *
-->
<#assign confirmCurrentAuthor>
    <h4><@u.message "initiative.confirmAuthor.title" /></h4>
    <p><@u.message "initiative.confirmAuthor.description" /></p>

    <form action="${springMacroRequestContext.requestUri}" method="POST" >
        <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
        <button type="submit" name="${UrlConstants.ACTION_CONFIRM_CURRENT_AUTHOR}" value="${UrlConstants.ACTION_CONFIRM_CURRENT_AUTHOR}" class="small-button green"><span class="small-icon save-and-send"><@u.message "initiative.confirmAuthor.btn" /></span></button>
        <button type="submit" name="${UrlConstants.ACTION_DELETE_CURRENT_AUTHOR}" value="${UrlConstants.ACTION_DELETE_CURRENT_AUTHOR}" class="small-button red"><span class="small-icon cancel"><@u.message "initiative.deleteAuthor.btn" /></span></button>
    </form>
</#assign>

<@u.systemMessageHTML confirmCurrentAuthor "summary" />
        
</#assign>

<#assign bottomContribution>
    <@u.systemMessageHTML confirmCurrentAuthor "summary" />
</#assign>

<#include "initiative-public.ftl"/>
</#escape> 
