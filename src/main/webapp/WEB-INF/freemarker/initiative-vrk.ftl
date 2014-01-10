<#import "components/edit-blocks.ftl" as edit />
<#import "components/utils.ftl" as u />
<#import "components/forms.ftl" as f />
<#import "components/utils.ftl" as u />
<#import "components/view-blocks.ftl" as view />

<#escape x as x?html> 
<#assign topContributionVRK>

<#--
 * VRK resolution
 * 
 * - Download link for support vote batch-file(s)
 * - If support votes are removed:
 *      - VRK user cannot download batch-files.
 *      - VRK user is still able to confirm votes
 * - Confirm form with date, support vote count and diary number
-->
<#if managementSettings?? && managementSettings.allowRespondByVRK>

    <@u.errorsSummary path="initiative.*" prefix="initiative."/>
    <form action="${urls.view(initiative.id)}" method="post">
        <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
        <@view.blockHeader "initiative.vrkResolution.title" "edit" />
        <div id="initiative-vrk-resolution" class="input-block cf">
            <div class="input-block-content">
	            <#if initiative.verified??>
	                <div class="system-msg msg-info">
		                <#assign supportVotesVerified><@u.localDate initiative.verified /></#assign>
		                <h4><@u.message key="initiative.vrkResolution.info.title" args=[supportVotesVerified]/></h4>
		                <div><@u.messageHTML key="initiative.vrkResolution.info.count" args=[initiative.verifiedSupportCount]/></div>
		                <div><@u.message key="initiative.vrkResolution.info.identifier" args=[initiative.verificationIdentifier]/></div>
	                </div>
                    <br />
	            </#if>

                <#list supportVoteBatches as batch>
                    <#if batch_index == 0><ul class="no-style"></#if>
                        <li>
                        <#assign batchCreated><@u.localDate batch.created /></#assign>
                        <#assign fileName>Aloite-${initiative.id}-era-${batch_index + 1}.txt</#assign>
                        <#if initiative.supportStatementsRemoved??>
                            ${batchCreated}: <span>${fileName}</span>
                        <#else>
                            <@u.message key="initiative.supportVoteBatch.info" args=[batchCreated, batch.voteCount]/>: <a href="${urls.downloadVotes(batch.id, fileName)}" rel="external">${fileName}</a>
                        </#if>
                        </li>
                    <#if !batch_has_next></ul></#if>
                </#list>
            </div>
            
            <div class="input-block-content">
                <div class="initiative-header-area">
                    <@f.helpText "helpVerifiedSupportCount" />
                    <@f.textField path="initiative.verifiedSupportCount" required="required" cssClass="x-small" maxLength=7 optional=false />
                </div>
            </div>
            
            <div class="input-block-content no-top-margin">
                <div class="initiative-header-area">
                    <@f.helpText "helpVerified" />
                    <@f.textField path="initiative.verified" fieldType="date" required="required" cssClass="datepicker" optional=false />
                </div>
            </div>
            
            <div class="input-block-content">
                <div class="initiative-header-area">
                    <@f.helpText "helpVerificationIdentifier" />
                    <@f.textField path="initiative.verificationIdentifier" required="required" cssClass="large" maxLength=InitiativeConstants.VERIFICATION_IDENTIFIER_MAX?string("#") optional=false />
                </div>
            </div>

            
            <div class="input-block-content no-top-margin">
            <@u.systemMessage path="initiative.vrkResolution.info.email" type="info" showClose=false />
                <button type="submit" name="${UrlConstants.ACTION_UPDATE_VRK_RESOLUTION}" value="true" class="small-button green"><span class="small-icon save-and-send"><@u.messageHTML 'action.save' /></span></button>
            </div>
        </div>
    </form>

</#if>

</#assign>

<#include "initiative-management.ftl"/>
</#escape> 
