<#import "/spring.ftl" as spring />
<#import "components/utils.ftl" as u />
<#import "components/forms.ftl" as f />

<#escape x as x?html> 
<#assign topContribution>
    
    
<#--
 * invitationAcceptHtml
 * 
 * Modal for accepting invitation.
 *
 * Includes NOSCRIPT
-->
<#assign invitationAcceptHtml>
    <@compress single_line=true>        
        <@u.errorsSummary path="currentAuthor.*" prefix="initiative.currentAuthor."/>
        
        <h4 class="header"><@u.message "initiative.name."+locale /></h4>
        <p><@u.text initiative.name /></p>
        
        <#assign rolesSeq = [] />
        <#if invitation.initiator>
            <#assign rolesSeq = rolesSeq + ["initiator"] />
        </#if>
        <#if invitation.representative>
            <#assign rolesSeq = rolesSeq + ["representative"] />
        </#if>
        <#if invitation.reserve>
            <#assign rolesSeq = rolesSeq + ["reserve"] />
        </#if>

        <h4 class="header"><@u.message key="initiative.currentAuthor.role" args=[rolesSeq?size] /></h4>
        
        <#list rolesSeq as role>  
            <#if role_index==0><p></#if>
            <@u.message "initiative.currentAuthor."+role /><#if role_has_next>, </#if>
            <#if !role_has_next></p></#if>
        </#list>
            
        <div class="column">
            <h4 class="header"><@u.message "initiative.currentAuthor.name" /></h4>
            <p>${currentAuthor.lastName}, ${currentAuthor.firstNames}</p>
        </div>
        
        <div class="column last">
            <h4 class="header"><@u.message "initiative.currentAuthor.homeMunicipality" /></h4>
            <p><@u.text currentAuthor.homeMunicipality /></p>
        </div>
        <br class="clear" />

        <form action="${springMacroRequestContext.requestUri}" method="POST" >
            <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
            <input type="hidden" name="invitation" value="${invitation.invitationCode}"/>
            <input type="hidden" name="action" value="confirm-accept-invitation"/>
            
            <div class="pad margin cf">
                <#assign href>${urls.help(HelpPage.INITIATIVE_STEPS.getUri(locale))}</#assign>
                <@u.messageHTML key="invitation.accept.confirm.contactDetails" args=[href] />
                <@f.currentAuthor path="currentAuthor" mode="modal" prefix="initiative" />
            </div>
            
            <#assign href>${urls.help(HelpPage.ORGANIZERS.getUri(locale))}</#assign>
            <p><@u.messageHTML key="userConfirmation.invitation" args=[href] /></p>
            
            <button type="submit" name="${UrlConstants.ACTION_ACCEPT_INVITATION}" value="<@u.message "invitation.accept.confirm" />" class="small-button green save-and-send"><span class="small-icon save-and-send"><@u.message "invitation.accept.confirm" /></span></button>
            <a href="${springMacroRequestContext.requestUri}" class="push close"><@u.message "action.cancel" /></a>
        </form>                    
    </@compress>
</#assign>

<@u.systemMessageHTML invitationAcceptHtml "info" "noscript" />
    
</#assign>

<#include "initiative-public.ftl"/>
</#escape> 

