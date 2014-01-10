<#import "utils.ftl" as u />

<#escape x as x?html>

<#--
 * management-messages.ftl contains those functions that are needed in files:
 *  initiative-om.ftl
 *  initiative-author.ftl
-->

<#--
 * supportVoteBatch
 * 
 * Info-box:
 *  Send support vote batches if there is unsend votes.
 *  Verified (by VRK) support vote batches
-->
<#assign supportVoteBatch>
    <#if 0 < supportVoteBatches?size>
        <#assign supportVoteBatchHTML>
            <h4><@u.message key="initiative.supportVoteBatch.info.title" /></h4>
            <ul class="no-style">
            <#list supportVoteBatches as batch>
                <li>
                <#assign batchCreated><@u.localDate batch.created /></#assign>
                <#if initiative.supportStatementsRemoved??>
                    ${batchCreated}
                <#else>
                    <@u.message key="initiative.supportVoteBatch.info" args=[batchCreated, batch.voteCount]/>
                </#if>
                </li>
            </#list>
            </ul>
            
            <#if initiative.verified??>
                <br/>    
                <#assign supportVotesVerified><@u.localDate initiative.verified /></#assign>
                <h4><@u.message key="initiative.supportVotesVerified.info.title" args=[supportVotesVerified]/></h4>
                <ul class="no-style">
                    <li><@u.messageHTML key="initiative.supportVotesVerified.info.count" args=[initiative.verifiedSupportCount]/></li>
                    <li><@u.message key="initiative.supportVotesVerified.info.identifier" args=[initiative.verificationIdentifier]/></li>
                </ul>
                
            </#if>
        </#assign>
        
        <@u.systemMessageHTML html=supportVoteBatchHTML type="info" cssClass="printable" />
    </#if>
</#assign>

 
<#--
 * removeSupportVotes
 *
 * CASE 1: Cannot be removed before initiative is closed
 *  Voting in progress
 *  Voting ended, total support vote count over 50 000, at least 1 unsend support vote. VRK has not confirmed over 50 000.
 *
 * CASE 2: Can be removed but the button is not very visible
 *  Voting ended, support votes sent to VRK, waiting for confirmation
 *
 * CASE 3: Can be removed and the button is clear and visible
 *  Voting ended, total support vote count under 50 000
 *  Voting ended, VRK confirmed over 50 000 
 *  OM User: Less than 2 months of support vote remove deadline.
-->
<#assign removeSupportVotes>
<#if managementSettings.allowRemoveSupportVotes>

    <#assign removeSupportVotesHTML>
        <#if managementSettings.extraWarningforRemoveSupportVotes>
            <div class="minimized">
                <div class="top">
                    <a href="#" class="more toggler hidden" data-alttext="<@u.message "removeSupportVotes.hideDetails" />"><span class="text"><@u.message "removeSupportVotes.showDetails" /></span><span class="icon-small more"></span></a>
                    <h4 class="subtle"><@u.message "removeSupportVotes.title" /></h4>
                </div>
                
                <div class="js-hide toggled">
        </#if>           
                <#assign endDateForVotesRemoval><@u.localDate initiative.getEndDateForVotesRemoval(votesRemovalDuration) /></#assign>
                <p><@u.message key="removeSupportVotes.description" args=[initiative.supportCount, endDateForVotesRemoval] /></p>
                <form action="${springMacroRequestContext.requestUri}" method="POST" >
                    <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
                    <a id="page-${UrlConstants.ACTION_REMOVE_SUPPORT_VOTES}" href="?remove-support-votes=confirm#remove-support-votes" title="<@u.message "removeSupportVotes.btnTitle" />" class="small-button red remove-support-votes"><span class="small-icon delete"><@u.messageHTML "removeSupportVotes.btnTitle" /></span></a>
                </form>        
        
        <#if managementSettings.extraWarningforRemoveSupportVotes>
                </div>
            </div>
        </#if>
    </#assign>
    
    <#if !RequestParameters['remove-support-votes']??>
        <@u.systemMessageHTML html=removeSupportVotesHTML type="info" />
    </#if>
        
<#--
 * removeSupportVotesConfirmHtml
 *
 * Modal: Confirm remove support votes.
 * NOSCRIPT-users gets confirm form by request parameter "send-to-vrk=confirm".
-->  
    <#assign removeSupportVotesConfirmHtml>
        <@compress single_line=true>            
            <form action="${springMacroRequestContext.requestUri}" method="POST" >
                <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
                <p><@u.message "removeSupportVotes.confirm" /></p>
                
                <#-- Show warning in CASE 2 -->
                <#if managementSettings.extraWarningforRemoveSupportVotes>
                    <@u.systemMessage path="removeSupportVotes.warning" type="warning" showClose=false />
                    <br />
                </#if>
                
                <p><label><input type="checkbox" name="confirm" value="true" class="binder" /> <@u.message "removeSupportVotes.confirm.checkbox" /></label></p>
                
                <button type="submit" name="${UrlConstants.ACTION_REMOVE_SUPPORT_VOTES}" id="modal-${UrlConstants.ACTION_REMOVE_SUPPORT_VOTES}" value="<@u.message "removeSupportVotes.btn" />" class="small-button red bind"><span class="small-icon delete"><@u.message "removeSupportVotes.btn" /></button>
                <a href="${springMacroRequestContext.requestUri}" class="push close"><@u.message "action.cancel" /></a>
            </form>
        </@compress>
    </#assign>
    
    <#-- Confirm remove support votes for NOSCRIPT-users -->
    <#if RequestParameters['remove-support-votes']?? && RequestParameters['remove-support-votes'] == "confirm">
        <noscript>
            <div id="remove-support-votes" class="system-msg msg-info">
                <#noescape>
                    <h4><@u.message "removeSupportVotes.confirm.title" /></h4>
                    ${removeSupportVotesConfirmHtml!""}
                </#noescape>
            </div>
            <br/>
        </noscript>
    </#if>

</#if>
</#assign> <#-- /removeSupportVotes -->

</#escape> 