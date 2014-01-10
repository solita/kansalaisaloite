<#import "/spring.ftl" as spring />
<#import "components/utils.ftl" as u />

<#escape x as x?html> 
<#assign topContribution>

<#--
 * invitationAcceptHtml
 * 
 * Accept or decline invitation.
 * Launches a modal-window for confirmation.
 *
 * NOSCRIPT-users gets confirmation form by request parameter 'invitation-decline'.
-->
<#assign invitationAcceptHtml>
    <h4><@u.messageHTML 'systemMessage.invitation.title' /></h4>
    
    <#assign href>${urls.help(HelpPage.SECURITY.getUri(locale))}</#assign>
    <@u.messageHTML key="systemMessage.invitation" args=[href] />
    
    <form action="${springMacroRequestContext.requestUri}" method="POST" >
        <input type="hidden" name="invitation" value="${invitationCode}"/>

        <a href="${urls.confirmAcceptInvitation(initiative.id, idHash)}" class="small-button green green save-and-send"><span class="small-icon save-and-send"><@u.message "invitation.accept" /></span></a>
        <a href="?invitation-decline=confirm" title="<@u.message "invitation.decline" />" class="small-button gray cancel invitation-decline-confirm"><@u.message "invitation.decline" /></a>
    </form>
</#assign>

<#if !RequestParameters['invitation-decline']??>
    <@u.systemMessageHTML invitationAcceptHtml "summary" />
</#if>


<#--
 * invitationDeclineConfirmHtml
 * 
 * Modal: Confirm decline invitation.
 *
 * NOSCRIPT-users gets confirmation form by request parameter 'invitation-decline=confirm'.
-->
<#assign invitationDeclineConfirmHtml>
    <@compress single_line=true>
    
        <@u.messageHTML "modal.invitationDecline.confirm" />
        <form action="${springMacroRequestContext.requestUri}" method="POST" >
            <input type="hidden" name="CSRFToken" value="${CSRFToken!}"/>
            <input type="hidden" name="invitation" value="${invitationCode}"/>
            <button type="submit" name="${UrlConstants.ACTION_DECLINE_INVITATION}" value="<@u.message "invitation.decline" />" class="small-button gray cancel"><@u.message "invitation.decline" /></button>
            <a href="${springMacroRequestContext.requestUri}" class="push close"><@u.message "action.cancel" /></a>
        </form>
    
    </@compress>
</#assign>

<#-- Confirm decline invitation for NOSCRIPT-users -->
<#if RequestParameters['invitation-decline']?? && RequestParameters['invitation-decline'] == "confirm">
    <noscript>
        <div class="system-msg msg-info">
           <#noescape>${invitationDeclineConfirmHtml}</#noescape>
        </div>
        <br/>
    </noscript>
</#if> 
    
    
</#assign>


<#assign bottomContribution>

    <#-- Accept invitation -->
    <#if invitationAcceptHtml?? && !RequestParameters['invitation-decline']??>
        <@u.systemMessageHTML invitationAcceptHtml "summary" />
    </#if>

</#assign>

<#include "initiative-public.ftl"/>
</#escape> 
