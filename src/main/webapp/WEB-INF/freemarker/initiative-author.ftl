<#import "/spring.ftl" as spring />
<#import "components/utils.ftl" as u />
<#import "components/forms.ftl" as f />
<#import "components/general-messages.ftl" as m />

<#include "components/management-messages.ftl" />

<#escape x as x?html> 

<#--
 * Define dates here so we can easier use them as message args.
-->
<#assign stateDate><@u.localDate initiative.stateDate /></#assign>
<#assign startDate><@u.localDate initiative.startDate /></#assign>
<#assign endDate><@u.localDate initiative.endDate /></#assign>
<#assign endDateForSendToVrk><@u.localDate initiative.getEndDateForSendToVrk(sendToVrkDuration) /></#assign>

<#assign topContribution>

<#--
 * Voting dialog, already voted or voting not started
 * Voting is suspended
 * Voting is ended
-->
<@m.initiativeVote />
<@m.votingSuspended />
<@m.votingEnded />

<#--
 * supportVoteBatch
 * 
 * Defined in management-messages.ftl 
-->
<#noescape>${supportVoteBatch!""}</#noescape>

<#--
 * Invitations and author confirms
 * 
 * Send invitations or send author confirms. Send author confirms sends also invitations. 
 *
 * Initiative state: PROPOSAL
-->
<@spring.bind "initiative.*" />
<#if (managementSettings.editMode != EditMode.FULL) && managementSettings.allowSendInvitations && !spring.status.error>
    <#if (initiative.pendingConfirmationReminders > 0)>
        <#assign sendInvitations>
            <h4><@u.message 'invitation.confirmAuthor.title' /></h4>
            <p><@u.message 'invitation.confirmAuthor' /></p>
            <form action="${springMacroRequestContext.requestUri}" method="POST" >
                <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
                <button type="submit" name="${UrlConstants.ACTION_SEND_INVITATIONS}" value="true" class="small-button green"><span class="small-icon save-and-send"><@u.message 'invitation.confirmAuthor.btn' /></span></button>
            </form>
        </#assign>
    <#else>
        <#assign sendInvitations>
            <h4><@u.message 'invitation.sendInvitations.title' /></h4>
            <p><@u.message 'invitation.sendInvitations' /></p>
            <form action="${springMacroRequestContext.requestUri}" method="POST" >
                <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
                <button type="submit" name="${UrlConstants.ACTION_SEND_INVITATIONS}" value="true" class="small-button green"><span class="small-icon save-and-send"><@u.message 'invitation.sendInvitations.btn' /></span></button>
            </form>
        </#assign>
    </#if>
    
    <@u.systemMessageHTML sendInvitations "info" />
</#if>


<#if managementSettings?? && (managementSettings.editMode != EditMode.FULL)>

<#--
 * Initiative waiting for organizers
 *
 * Send to OM -button: disabled
 * Initiative state: PROPOSAL
-->
<#if !managementSettings.allowSendToOM && managementSettings.allowEditOrganizers>
    <#assign initiativeWaitingForAuthors>            
        <h4><@u.message "initiative.waitingAuthors" /></h4>
        <@u.messageHTML "initiative.waitingAuthors.description" />
        
        <form action="${springMacroRequestContext.requestUri}" method="POST" >
            <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
            <button type="submit" name="" disabled="disabled" value="<@u.message "initiative.sendToOm.btn" />" class="small-button gray disabled"><@u.message "initiative.sendToOm.btn" /></button>
        </form>
    </#assign>
    <@u.systemMessageHTML initiativeWaitingForAuthors "info" />
</#if>
 
<#if managementSettings.allowSendToOM>
<#--
 * sendToOM
 * 
 * Initiative is ready for send to OM. Launches "Send to OM confirm"-modal.
 * NOSCRIPT-users gets confirm form by request parameter "send-to-om=confirm"
 *
 * Send to OM -button: enabled 
 * Initiative state: PROPOSAL
-->   
    <#assign sendToOM>
        <h4><@u.message "initiative.readyForOm" /></h4>
        <@u.messageHTML "initiative.readyForOm.description" />

        <form action="${springMacroRequestContext.requestUri}" method="POST" >
            <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
            <a id="page-${UrlConstants.ACTION_SEND_TO_OM}" href="?send-to-om=confirm#send-to-om" title="<@u.message "initiative.sendToOm.btn" />" class="small-button green send-to-om-confirm"><span class="small-icon save-and-send"><@u.messageHTML "initiative.sendToOm.btn" /></span></a>
        </form>
    </#assign>
          
    <#if !RequestParameters['send-to-om']??>
        <@u.systemMessageHTML sendToOM "info" />
    </#if>

<#--
 * sendToOMConfirmHtml
 * 
 *  Warn the user if there is unconfirmed authors or invitations.
 *
 * Modal: Confirm send to OM
-->        
    <#assign sendToOMConfirmHtml>
        <@compress single_line=true>
            <p><@u.message "initiative.sendToOm.confirm" /></p>
            
            <#if (initiative.totalUnconfirmedCount > 0) >        
                <div class="system-msg msg-warning">
                    <#assign args=[initiative.totalUnconfirmedCount] />
                    <h4><@u.message "initiative.sendToOm.pendingInvitations" args /></h4>
                    <@u.messageHTML "initiative.sendToOm.pendingInvitations.description" />
                </div>
            </#if>
            
            <form action="${springMacroRequestContext.requestUri}" method="POST" >
                <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
                <button type="submit" name="${UrlConstants.ACTION_SEND_TO_OM}" id="modal-${UrlConstants.ACTION_SEND_TO_OM}" value="<@u.message "initiative.sendToOm.btn" />" class="small-button green"><span class="small-icon save-and-send"><@u.message "action.send" /></span></button>
                <a href="${springMacroRequestContext.requestUri}" class="push close"><@u.message "action.cancel" /></a>
            </form>
        </@compress>
    </#assign>
    
    <#-- Confirm send to OM for NOSCRIPT-users -->
    <#if RequestParameters['send-to-om']?? && RequestParameters['send-to-om'] == "confirm">
    <noscript>
        <div id="send-to-om" class="system-msg msg-info">
            <#noescape>
                <h4><@u.message "modal.sendToOm.confirm.title" /></h4>
                ${sendToOMConfirmHtml}
            </#noescape>
        </div>
    </noscript>
    </#if>

</#if>
    
</#if>

<#--
 * initiativeWaitingForOM
 *
 * Initiative is waiting for OM to accept or reject initiative
 *
 * Initiative state: REVIEW
--> 
<#if initiative.state == InitiativeState.REVIEW>
    <#assign initiativeWaitingForOM>
        <h4><@u.message "initiative.waitingForOm.title" /></h4>
        
        <#if (initiative.startDate.compareTo(initiative.stateDate.toLocalDate()) gt 0)>
            <#assign args=[stateDate,startDate] />
            <p><@u.messageHTML "initiative.waitingForOm" args /></p>
        <#else>
            <#assign args=[stateDate] />
            <p><@u.messageHTML "initiative.waitingForOm.noDate" args /></p>
        </#if>
        <span class="small-button gray disabled"><span class="small-icon save-and-send"><@u.message "vote.btn" /></span></span>
    </#assign>
    
    <@u.systemMessageHTML initiativeWaitingForOM "info" />
</#if>

<#if managementSettings.allowSendToVRK>
<#--
 * sendToVRK
 *
 * Send to VRK. Launches a confirm modal.
 * NOSCRIPT-users gets confirm form by request parameter "send-to-vrk=confirm"
 *
 * Initiative state: ACCEPT
-->
    <#assign sendToVRK>
        <h4><@u.messageHTML key="initiative.sendToVRK.title" args=[initiative.unsentSupportCount] /></h4>
        <#assign paramDate><@u.localDate initiative.getEndDateForSendToVrk(sendToVrkDuration) /></#assign>
        <p><@u.messageHTML key="initiative.sendToVRK" args=[managementSettings.requiredVoteCount, paramDate] /></p>

        <#assign href>${urls.help(HelpPage.INITIATIVE_STEPS.getUri(locale))}</#assign>
        <p><@u.messageHTML key="initiative.sendToVRK.2" args=[href] /></p>

        <#if votingInfo.votingInProggress>
            <#assign stillRunning><@u.message "initiative.sendToVRK.still.running.notification"/></#assign>
            <@u.systemMessageHTML stillRunning "warning"/>

        </#if>

        <form action="${springMacroRequestContext.requestUri}" method="POST" >
            <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
            <a id="page-${UrlConstants.ACTION_SEND_TO_VRK}" href="?send-to-vrk=confirm#send-to-vrk" title="<@u.message "initiative.sendToVRK.btnTitle" />" class="large-button green send-to-vrk-confirm"><span class="large-icon save-and-send"><@u.messageHTML "initiative.sendToVRK.btn" /></span></a>            
        </form>
    </#assign>

    <#if !RequestParameters['send-to-vrk']??>
        <@u.systemMessageHTML sendToVRK "info" />
    </#if>
 
<#--
 * sendToVRKConfirmHtml
 *
 * Modal: Confirm send to VRK.
 * NOSCRIPT-users gets confirm form by request parameter "send-to-vrk=confirm"
 *
 * Initiative state: ACCEPT
-->       
    <#assign sendToVRKConfirmHtml>
        <@compress single_line=true>
        
            <p><@u.message "modal.sendToVRK.confirm" /></p>
            <#if votingInfo.votingInProggress>
                <#assign stillRunning><@u.message "initiative.sendToVRK.still.running.notification"/></#assign>
                <@u.systemMessageHTML stillRunning "warning"/>
            </#if>
            
            <form action="${springMacroRequestContext.requestUri}" method="POST" >
                <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
                <button type="submit" name="${UrlConstants.ACTION_SEND_TO_VRK}" id="modal-${UrlConstants.ACTION_SEND_TO_VRK}" value="<@u.message "initiative.sendToVRK.btnTitle" />" class="large-button green"><span class="large-icon save-and-send"><@u.messageHTML "initiative.sendToVRK.btn" /></button>
                <a href="${springMacroRequestContext.requestUri}" class="push close"><@u.message "action.cancel" /></a>
            </form>
        </@compress>
    </#assign>

    <#-- Confirm send to VRK for NOSCRIPT-users -->
    <#if RequestParameters['send-to-vrk']?? && RequestParameters['send-to-vrk'] == "confirm">
    <noscript>
        <div id="send-to-vrk" class="system-msg msg-info">
            <#noescape>
                <h4><@u.message "modal.sendToVRK.confirm.title" /></h4>
                ${sendToVRKConfirmHtml}
            </#noescape>
        </div>
    </noscript>
    </#if>
 
 
<#--
 * sendToVRKNotEnough
 *
 * Initiative did not get 50 000 votes within the 6 months.
 *
 * Send to VRK button: disabled
 * Initiative state: ACCEPT
-->       
    <#elseif votingInfo.votingEnded && (initiative.totalSupportCount < requiredVoteCount)>
        <#assign sendToVRKNotEnough>
            <h4><@u.messageHTML key="initiative.sendToVRK.notEnough.title" /></h4>
            
            <#assign endDate><#if initiative.startDate??><@u.localDate initiative.endDate /></#if></#assign>
            <p><@u.messageHTML key="initiative.sendToVRK.notEnough" args=[requiredVoteCount,endDate] /></p>
            <span class="large-button gray disabled"><span class="large-icon save-and-send"><@u.messageHTML "initiative.sendToVRK.btn" /></span></span>
        </#assign>
        <@u.systemMessageHTML sendToVRKNotEnough "info" />
    </#if>  
    
    
<#--
 * removeSupportVotes
 * 
 * Defined in management-messages.ftl 
-->
<#noescape>${removeSupportVotes!""}</#noescape>

<#--
 * Support votes are removed
-->
<@m.supportStatementsRemoved />

</#assign>

<#--
 * Assign the messages also to the bottom of the page.
-->
<#assign bottomContribution>
    <#--
     * Voting dialog, already voted or voting not started
     * Voting is suspended
     * Voting is ended
    -->
    <@m.initiativeVote />
    <@m.votingSuspended />
    <@m.votingEnded />

    <#-- Support vote batch -->
    <#noescape>${supportVoteBatch!""}</#noescape>

    <#-- Send author invitations -->
    <#if sendInvitations??>
        <@u.systemMessageHTML sendInvitations "info" />
    </#if>
        
    <#-- Initiative waiting for organizers (State: PROPOSAL) -->
    <#if initiativeWaitingForAuthors??>
        <@u.systemMessageHTML initiativeWaitingForAuthors "info" />
    </#if>
        
    <#-- Send initiative to OM -->
    <#if sendToOM?? && !RequestParameters['send-to-om']??>
        <@u.systemMessageHTML sendToOM "info" />
    </#if>
    <#-- Confirm send to OM for NOSCRIPT-users -->
    <#if RequestParameters['send-to-om']?? && RequestParameters['send-to-om'] == "confirm">
    <noscript>
        <div id="send-to-om" class="system-msg msg-info">
            <#noescape>
                <h4><@u.message "modal.sendToOm.confirm.title" /></h4>
                ${sendToOMConfirmHtml}
            </#noescape>
        </div>
    </noscript>
    </#if>
    
    <#-- Initiative is ready for send to OM (State: PROPOSAL) -->
    <#if initiativeReadyForOm??>
        <@u.systemMessageHTML initiativeReadyForOm "info" />
    </#if>

    <#-- Initiative is waiting for OM (State: REVIEW) -->
    <#if initiativeWaitingForOM??>
        <@u.systemMessageHTML initiativeWaitingForOM "info" />
    </#if>
    
    <#-- Initiative is ready for send to VRK (State: ACCEPTED) -->
    <#if sendToVRK?? && !RequestParameters['send-to-vrk']??>
        <@u.systemMessageHTML sendToVRK "info" />
    </#if>
    <#-- Confirm send to VRK for NOSCRIPT-users -->
    <#if RequestParameters['send-to-vrk']?? && RequestParameters['send-to-vrk'] == "confirm">
    <noscript>
        <div id="send-to-vrk" class="system-msg msg-info">
            <#noescape>
                <h4><@u.message "modal.sendToVRK.confirm.title" /></h4>
                ${sendToVRKConfirmHtml}
            </#noescape>
        </div>
    </noscript>
    </#if>
 
    <#if sendToVRKNotEnough??>
    <@u.systemMessageHTML sendToVRKNotEnough "info" />
    </#if>
    
    <#-- Remove support votes (management-messages.ftl) -->
    <#noescape>${removeSupportVotes!""}</#noescape>
    
    <#-- Support votes are removed -->
    <@m.supportStatementsRemoved />
    
</#assign>

<#include "initiative-management.ftl" />

</#escape> 

