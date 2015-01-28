<#import "/spring.ftl" as spring />
<#import "components/forms.ftl" as f />
<#import "components/layout.ftl" as l />
<#import "components/general-messages.ftl" as m />
<#import "components/utils.ftl" as u />
<#import "components/flow-state.ftl" as flow />
<#import "components/view-blocks.ftl" as view />
<#import "components/edit-blocks.ftl" as edit />
<#import "components/some.ftl" as some />

<#escape x as x?html> 

<#if managementSettings.editMode != EditMode.NONE && managementSettings.editMode != EditMode.FULL>
    <#global showExposeMask=true />
</#if>

<#--
 * Layout parameters for HTML-title and navigation.
 * 
 * page = "page.management" or "page.createNew"
 * pageTitle = initiative.name if exists, otherwise empty string
-->
<#if initiative??>
    <#if initiative.name[locale]?? || initiative.name[altLocale]??>
        <#assign page="page.management" />
        <#assign pageTitle><@u.text initiative.name /></#assign>
    <#else>
        <#assign page="page.createNew" />
    </#if>
</#if>

<@l.main page pageTitle!"">

    <h1>
    <#if pageTitle??>
        <#noescape>${pageTitle}</#noescape>
    <#else>
        <@u.message page />
    </#if>
    </h1>
    <#if initiative.startDate??>
    <span class="extra-info"><@u.localDate initiative.startDate /> - <@u.message "page.management" /></span>
    </#if>
    
    <@flow.flowStateIndicator initiative />
    
    <#--
      * Logic for showing info-message wrapper (msg-summary)
      * - Known issue: Shows incorrectly in VRK-view in REVIEW-state
    -->
    <#assign showInfoMsg = false />
    <#if    (initiative.currentAuthor?? && managementSettings.editMode != EditMode.FULL) ||
            (initiative.state != InitiativeState.DRAFT && initiative.state != InitiativeState.PROPOSAL)>
            
        <#assign showInfoMsg = true />
    </#if>
    
    <#-- TOP CONTRIBUTION --> 
    <#if showInfoMsg && (votingInfo?? && votingInfo.votingInProggress || initiative.totalSupportCount gt 0)>
	    <div class="view-block">
		    <@m.initiativeVoteInfo />
		    <@m.supportCountGraph supportCountData />
		</div>
        
        <div class="system-msg msg-summary">
            <#noescape>${topContribution!""}</#noescape>
        </div>
    </#if>
        
    <#noescape>${topContributionVRK!""}</#noescape>  
    
    <#if reviewHistory??><#noescape>${reviewHistory}</#noescape></#if> 
   
    <#-- Full edit form errors summary -->
    <#if managementSettings.editMode == EditMode.FULL>
        <@u.errorsSummary path="initiative.*" prefix="initiative."/>
    </#if> 

    <#-- Keeps the block-mode editing in right place when errors occurs -->
    <#switch managementSettings.editMode>
      <#case EditMode.BASIC>
         <#assign currentBlockHash="#initiative.basicDetails.title" />
         <#break>
      <#case EditMode.EXTRA>
         <#assign currentBlockHash="#initiative.extraDetails.title" />
         <#break>
      <#case EditMode.CURRENT_AUTHOR>
         <#assign currentBlockHash="#initiative.currentAuthorDetails.title" />
         <#break>
     <#case EditMode.ORGANIZERS>
         <#assign currentBlockHash="#initiative.organizerDetails.title" />
         <#break>
      <#default>
         <#assign currentBlockHash="" />
    </#switch>
    
    <#-- FORM. Use class 'sodirty' to enable dirtylisten. -->
    <#if managementSettings.editMode != EditMode.NONE>
        <form action="${springMacroRequestContext.requestUri}${currentBlockHash}" method="POST" id="form-initiative" class="sodirty" >
           <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
           <input type="hidden" name="edit" value="${managementSettings.editMode}" />
    </#if>

        <#-- Parameters for edit: BASIC, EXTRA, ORGANIZERS, CURRENT_AUTHOR -->        
        <#if managementSettings.editMode == EditMode.BASIC || managementSettings.editMode == EditMode.FULL>
            <div <#if showExposeMask??>class="expose"</#if>>
                <@edit.blockHeader "initiative.basicDetails.title" />
                <@edit.basicDetails />
            </div>
        <#else>
            <@edit.blockHeader key="initiative.basicDetails.title" edit="BASIC" disabled=(managementSettings.allowEditBasic)?string('','disabled') />
            <div class="view-block"><@view.basicDetails /></div>
        </#if>

        <#if managementSettings.editMode == EditMode.EXTRA || managementSettings.editMode == EditMode.FULL>
            <div <#if showExposeMask??>class="expose"</#if>>
                <@edit.blockHeader key="initiative.extraDetails.title" />
                <@edit.extraDetails />
            </div>
        <#else>
            <@edit.blockHeader key="initiative.extraDetails.title" edit="EXTRA" disabled=(managementSettings.allowEditExtra)?string('','disabled') />                
            <div class="view-block"><@view.extraDetails /></div>
        </#if>
        
        <#if managementSettings.editMode == EditMode.CURRENT_AUTHOR || managementSettings.editMode == EditMode.FULL>
            <div <#if showExposeMask??>class="expose"</#if>>
                <@edit.blockHeader key="initiative.currentAuthorDetails.title" />
                <@edit.currentAuthorDetails />
            </div>
        <#elseif initiative.currentAuthor??>
            <@edit.blockHeader key="initiative.currentAuthorDetails.title" edit="CURRENT_AUTHOR" disabled=(managementSettings.allowEditCurrentAuthor)?string('','disabled') />
            <@view.currentAuthorDetails />
        </#if>
        
        <#if managementSettings.editMode == EditMode.ORGANIZERS || managementSettings.editMode == EditMode.FULL>
            <div <#if showExposeMask??>class="expose"</#if>>
                <@edit.blockHeader key="initiative.organizerDetails.title" />
                <@edit.organizerDetails />
            </div>
        <#else>
            <@edit.blockHeader key="initiative.organizerDetails.title" edit="ORGANIZERS" disabled=(managementSettings.allowEditOrganizers)?string('','disabled') />    
            <@view.organizerDetails />
        </#if>  
        
        <#if managementSettings.editMode == EditMode.FULL>
            <div id="form-action-panel" class="">
                
                <div class="system-msg msg-summary">
                    <#assign href>${urls.help(HelpPage.ORGANIZERS.getUri(locale))}</#assign>
                    <@u.systemMessage path="userConfirmation.create" type="info" showClose=false args=[href] />
                </div>
                
                <button type="submit" name="${UrlConstants.ACTION_SAVE_AND_SEND_INVITATIONS}" value="true" class="large-button green"><span class="large-icon save-and-send"><@u.messageHTML 'action.saveAndSend' /></span></button>
                <button type="submit" name="${UrlConstants.ACTION_SAVE}" value="<@u.messageHTML 'action.saveAsDraft' />" class="large-button gray"><span class="large-icon save-as-draft"><@u.messageHTML 'action.saveAsDraft' /></span></button>
                <a class="large-button red" href="${urls.baseUrl}/${locale}"><span class="large-icon cancel"><@u.messageHTML 'action.cancelAndReturn' /></span></a>
            </div>
        </#if>
        
    <#if managementSettings.editMode != EditMode.NONE>
        </form>
    </#if>
    
    <#-- BOTTOM CONTRIBUTION -->
    <#if showInfoMsg>
        <div class="system-msg msg-summary">
            <#noescape>${bottomContribution!""}</#noescape>
        </div>
    </#if>
    
    <#-- Some-buttons visible in states: ACCEPTED, DONE, CANCELED -->
    <#if initiative?? && (initiative.state == InitiativeState.ACCEPTED
                      ||  initiative.state == InitiativeState.DONE
                      ||  initiative.state == InitiativeState.CANCELED)>
        <@some.some pageTitle=pageTitle!"" />
    </#if>
    
<#--
 * Management VIEW and EDIT modals
 * 
 * Uses jsRender for templating.
 * Same content is generated for NOSCRIPT and for modals.
 *
 * Modals:
 *  Request message (defined in macro u.requestMessage)
 *  Confirm send to OM
 *  Respond by OM
 *  Confirm send to VRK
 *  Confirm remove support votes
 *  Confirm cancel initiative
 *  Form modified notification (dirtyform)
-->
<@u.modalTemplate />

<script type="text/javascript">
    var modalData = {};
    
    <#-- Modal: Request messages. Also in public-view. Check for components/utils.ftl -->
    <#if requestMessageModalHTML??>    
        modalData.requestMessage = function() {
            return [{
                title:      '<@u.message requestMessageModalTitle+".title" />',
                content:    '<#noescape>${requestMessageModalHTML!""?replace("'","&#39;")}</#noescape>'
            }]
        };
    </#if>
    
    <#-- Modal: Confirm send to OM -->
    <#if managementSettings.allowSendToOM>    
        modalData.sendToOmConfirm = function() {
            return [{
                title:      '<@u.message "modal.sendToOm.confirm.title" />',
                content:    '<#noescape>${sendToOMConfirmHtml!""?replace("'","&#39;")!""}</#noescape>'
            }]
        };
    </#if>
    
    <#-- Modal: Respond by OM -->
    <#if managementSettings.allowRespondByOM>
        modalData.omAcceptInitiative = function() {
            return [{
                title:      '<@u.message "initiative.acceptInitiative" />',
                content:    '<#noescape>${omAcceptInitiativeHtml!""?replace("'","&#39;")!""}</#noescape>'
            }]
        };
        modalData.omRejectInitiative = function() {
            return [{
                title:      '<@u.message "initiative.rejectInitiative" />',
                content:    '<#noescape>${omRejectInitiativeHtml!""?replace("'","&#39;")!""}</#noescape>'
            }]
        };
    </#if>
    
    <#-- Modal: Confirm send to VRK -->
    <#if managementSettings.allowSendToVRK>
        modalData.sendToVRKConfirm = function() {
            return [{
                title:      '<@u.message "modal.sendToVRK.confirm.title" />',
                content:    '<#noescape>${sendToVRKConfirmHtml!""?replace("'","&#39;")!""}</#noescape>'
            }]
        };
    </#if>

    <#-- Modal: Confirm remove Support votes -->
    <#if managementSettings.allowRemoveSupportVotes>    
        modalData.removeSupportVotesConfirm = function() {
            return [{
                title:      '<@u.message "removeSupportVotes.confirm.title" />',
                content:    '<#noescape>${removeSupportVotesConfirmHtml!""?replace("'","&#39;")!""}</#noescape>'
            }]
        };
    </#if>

    <#-- Modal: Form modified notification. Uses dirtyforms jQuery-plugin. -->
    modalData.formModifiedNotification = function() {
        return [{
            title:      '<@u.message "form.modified.notification.title" />',
            content:    '<@u.messageHTML "form.modified.notification" />'
        }]
    };
</script>


<#if managementSettings.editMode != EditMode.NONE>
<script type="text/javascript">
    modalData.sessionHasEnded = function() {
        return [{
            title:      '<@u.message "modal.sessionHasEnded.title" />',
            content:    '<@u.messageHTML "modal.sessionHasEnded" />'
        }]
    };

    var keepaliveTimeout, maxKeepAlive, maxTimes, i = 0;

    keepaliveTimeout = 1000 * 60 * 3; // 3 minutes
    maxKeepAlive = 1000 * 60 * 60 * 2; // 2 hours
    maxTimes = maxKeepAlive / keepaliveTimeout; // 40 

    function keepSessionAlive() {
      $.post(
          "${urls.baseUrl}${UrlConstants.KEEPALIVE}", 
          "CSRFToken=${CSRFToken}",
          function (ok) {
              if (ok && i <= maxTimes) {
                  setTimeout("keepSessionAlive()", keepaliveTimeout);
                  i++;
              } else {
                  generateModal(modalData.sessionHasEnded(), 'minimal');
              }
          }
      ).error(function () {
          setTimeout("keepSessionAlive()", keepaliveTimeout);
          i++;
      });
    }
    setTimeout("keepSessionAlive()", keepaliveTimeout);
</script>
</#if>
    
</@l.main>
</#escape> 
