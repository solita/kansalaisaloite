<#import "components/layout.ftl" as l />
<#import "components/utils.ftl" as u />
<#import "components/flow-state.ftl" as flow />
<#import "components/general-messages.ftl" as m />
<#import "components/view-blocks.ftl" as view />
<#import "components/some.ftl" as some />
<#import "components/forms.ftl" as f />

<#escape x as x?html>

<#--
 * Layout parameters for HTML-title and navigation.
 *
 * page = "page.initiative.public" or "page.initiative.unnamed"
 * pageTitle = initiative.name if exists, otherwise empty string
-->
<#if initiative??>
    <#if initiative.name[locale]?? || initiative.name[altLocale]??>
        <#assign page="page.initiative.public" />
        <#assign pageTitle><@u.text initiative.name /></#assign>
    <#else>
        <#assign page="page.initiative.unnamed" />
        <#assign pageTitle="" />
    </#if>
</#if>

<#assign showFollow = initiative.state = InitiativeState.ACCEPTED/>
<#assign showFollowFormAlreadyOpen = showFollow && ((RequestParameters['formError']?? && RequestParameters['formError'] == "follow") || (RequestParameters['follow']?? && RequestParameters['follow'] == "true"))/>

<#if showFollow && followInitiative??>
    <#assign followInitiativeFormHTML><@compress single_line=true>
        <@u.message key="followInitiative.text"/>
    <form action="${springMacroRequestContext.requestUri}?formError=follow" method="POST">

        <@f.errorsSummary "followInitiative.*" "followInitiative."/>

        <div class="input-block-content">
            <@f.textField path="followInitiative.email" required="" optional=true cssClass="large" maxLength=InitiativeConstants.AUTHOR_EMAIL_MAX showErrors=false/>
            <input type="hidden" id="CSRFToken" name="CSRFToken" value="${CSRFToken!""}"/>
        </div>
        <button id="participate" type="submit"  value="true" name="action-follow" class="small-button"><span class="small-icon save-and-send"><@u.message "action.save" /></span></button>
        <a href="${springMacroRequestContext.requestUri}" class="push close"><@u.message "action.cancel" /></a>
        <div class="g-recaptcha" data-sitekey="${recaptchaSiteKey}"></div>
    </form>
    </@compress></#assign>
</#if>


<@l.main page pageTitle>

    <h1 class="name"><@u.text initiative.name /></h1>
    <#if initiative.startDate??>
        <span class="extra-info"><@u.localDate initiative.startDate /></span>
    </#if>

    <@flow.flowStateIndicator initiative />

    <#--
      * Logic for showing info-message wrapper
      * - Same as in general-messages.ftl
    -->
    <#assign showInfoMsg = false />

    <#if votingInfo??>
        <#if votingInfo.votingTime?? || votingInfo.allowVotingAction || (initiative.state == InitiativeState.ACCEPTED && (votingInfo?? &&!votingInfo.votingStarted))>
            <#assign showInitiativeVote = true />
        </#if>

        <#if votingInfo.votingSuspended>
            <#assign showVotingSuspended = true />
        </#if>

        <#if votingInfo.votingStarted && votingInfo.votingEnded>
            <#assign showVotingEnded = true />
        </#if>
    </#if>

    <#if initiative.supportStatementsRemoved??>
        <#assign showSupportStatementsRemoved = true />
    </#if>

    <#if showInitiativeVote?? || showVotingSuspended?? || showVotingEnded?? || showSupportStatementsRemoved??>
        <#assign showInfoMsg = true />
    </#if>



    <#-- TOP CONTRIBUTION -->
    <#if votingInfo?? && votingInfo.votingInProggress || initiative.totalSupportCount gt 0>
	    <div class="view-block">
		    <@m.initiativeVoteInfo />
		    <#if supportCountData??><@m.supportCountGraph supportCountData /></#if>
		</div>
	</#if>

    <#if showInfoMsg>
        <div class="system-msg msg-summary">
            <@m.initiativeVote />
            <@m.votingSuspended />
            <@m.votingEnded />
            <@m.supportStatementsRemoved />
            <@m.followInitiative />
        </div>
    </#if>
    <#if topContribution??><#noescape>${topContribution}</#noescape></#if>

    <#-- VIEW BLOCKS -->
    <div class="view-block public">
        <@view.basicDetails />
        <@view.extraDetails />
    </div>

    <@view.blockHeader "initiative.organizerDetails.title" />
    <@view.organizerDetails />


    <#-- BOTTOM CONTRIBUTION -->
    <#if showInfoMsg>
        <div class="system-msg msg-summary hidden-xs hidden-sm">
            <@m.initiativeVote />
            <@m.votingSuspended />
            <@m.votingEnded />
            <@m.supportStatementsRemoved />
            <@m.followInitiative />
        </div>
    </#if>
    <#if bottomContribution??><#noescape>${bottomContribution}</#noescape></#if>

    <#-- Some-buttons visible in states: ACCEPTED, DONE, CANCELED -->
    <#if initiative?? && (initiative.state == InitiativeState.ACCEPTED
                      ||  initiative.state == InitiativeState.DONE
                      ||  initiative.state == InitiativeState.CANCELED)>
        <@some.some pageTitle=pageTitle!"" />
    </#if>

<#--
 * Public VIEW modals
 *
 * Uses jsRender for templating.
 * Same content is generated for NOSCRIPT and for modals.
 *
 * Modals:
 *  Request message (defined in macro u.requestMessage)
 *  Confirm accept invitation
 *  Confirm decline invitation
-->
<@u.modalTemplate />

<script type="text/javascript">
    var modalData = {};

    <#-- Modal: Request messages. Also in management-view. Check for components/utils.ftl -->
    <#if requestMessageModalHTML??>
        modalData.requestMessage = function() {
            return [{
                title:      '<@u.message requestMessageModalTitle+".title" />',
                content:    '<#noescape>${requestMessageModalHTML!""?replace("'","&#39;")}</#noescape>'
            }]
        };
    </#if>

    <#-- Modal: Confirm accept invitation -->
    <#if currentAuthor?? && invitation??>
        modalData.invitationAccept = function() {
            return [{
                title:      '<@u.message "modal.invitationAccept.confirm.title" />',
                content:    '<#noescape>${invitationAcceptHtml!""?replace("'","&#39;")}</#noescape>'
            }]
        };
    </#if>

    <#-- Modal: Confirm decline invitation -->
    <#if invitationDeclineConfirmHtml??>
        modalData.invitationDecline = function() {
            return [{
                title:      '<@u.message "modal.invitationDecline.confirm.title" />',
                content:    '<#noescape>${invitationDeclineConfirmHtml!""?replace("'","&#39;")}</#noescape>'
            }]
        };
    </#if>

    <#if followInitiativeFormHTML??>
    modalData.followForm = function() {
        return [{
            title:     '<@u.message "followInitiative.title"/>',
            content:   '<#noescape>${followInitiativeFormHTML?replace("'","&#39;")}</#noescape>'
        }]
    };
    <#if showFollowFormAlreadyOpen>
    modalData.followFormAutoLoad = modalData.followForm;
    </#if>

    </#if>


</script>

<#if showFollowFormAlreadyOpen>
    <script src='https://www.google.com/recaptcha/api.js'></script>
</#if>

</@l.main>
</#escape>

