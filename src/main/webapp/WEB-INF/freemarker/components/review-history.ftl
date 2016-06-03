<#import "utils.ftl" as u />

<#escape x as x?html>

<#--
 * Show review history list
 *
 * @param histories is a list of review comments 
 * @param reviewHistoryDiff is a hashmap with keys diff, oldText. diff and oldText contains row-lists
-->
    <#macro reviewHistories histories reviewHistoryDiff>
        <div class="system-msg msg-summary">
            <h2><@u.message key="review.history.title"/></h2>

			<@u.systemMessage path="review.history.info" type="info" showClose=false />

			<div class="toggle-container">
				<div class="js-open-block hidden-nojs">
	                <a class="small-button gray js-btn-open-block" data-open-block="js-block-container" href="#"><span class="small-icon save-and-send"><@u.message "review.history.add.comment" /></span></a>
	            </div>
				
				<div class="cf js-block-container js-hide">
		            <form action="${springMacroRequestContext.requestUri}" method="POST" id="form-accept" class="sodirty cf">
		                <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
		
		                <div class="input-block-content no-top-margin">
		                    <textarea class="" name="${UrlConstants.ACTION_MODERATOR_ADD_COMMENT}" maxlength="${InitiativeConstants.STATE_COMMENT_MAX?string("#")}"></textarea>
		                </div>

		                <div class="input-block-content">
		                    <button type="submit" name="${UrlConstants.ACTION_COMMENT_BY_OM}" value="true" class="small-button"><span class="small-icon save-and-send"><@u.message "review.history.add.comment" /></span></button>
		                    <a href="#" class="push js-btn-close-block hidden-nojs"><@u.message "action.cancel" /></a>
		                </div>
		            </form>
	            </div>
            </div>

            <ul class="review-history">
            <#list histories as row>                
                <li class="review-history-row">
                	<span class="date"><@u.dateTime row.created/></span>
                	<span class="title">
                		<@u.message key="review.history.type."+row.type/> <#if row.type = "REVIEW_COMMENT"><i class="icon-small lock"></i></#if>
                	</span>
                    <div class="info">
                        <#if row.message.present>
                            <#-- <@u.text row.message.value /> -->
                            <@u.comment row.message.value/>
                        </#if>
                        <#if row.type = "REVIEW_SENT">
                        	<#-- TODO: diff url, maybe like urls.view(initiative.id, row.id) -->
                            <a href="${urls.viewHistoryItem(initiative.id, row.id)}#diff"><@u.message key="review.history.show.diff"/></a>
                        </#if>
                        
                    </div>
                </li>
            </#list>
            </ul>
        </div>
   
		<#if reviewHistoryDiff.present>
			<div class="diff-block cf">
	            <h2 id="diff"><@u.message key="review.history.show.diff"/></h2>
                <div class="diff-col left">
                	<h3><@u.message key="review.history.show.diff.current"/></h3>
                	
                    <ul class="diff-list">
                        <#list reviewHistoryDiff.value.diff as difRow>
                            <#if difRow.modificationType.present && difRow.modificationType.value== "INSERT">
                                <li class="diff-prefix diff-insert">
                            <#elseif difRow.modificationType.present && difRow.modificationType.value == "DELETE">
                                <li class="diff-prefix diff-delete">
                            <#else>
                                <li class="diff-prefix">
                            </#if>
                            ${difRow.line}&nbsp;
                        </li>
                        </#list>
                    </ul>
                </div>

                <div class="diff-col right">
                	<h3><@u.message key="review.history.show.diff.previous"/></h3>
                	
                    <#if reviewHistoryDiff.value.oldText.present>
                        <ul class="diff-list">
                        <#list reviewHistoryDiff.value.oldText.value as oldTextLine>
                            <li>${oldTextLine}&nbsp;</li>
                        </#list>
                        </ul>
                    </#if>
                </div>
			
				<div class="diff-block-colors">
					<span class="diff-color insert"></span>
					<span class="label"><@u.message key="review.history.show.diff.insert"/></span>
					<span class="diff-color delete"></span>
					<span class="label"><@u.message key="review.history.show.diff.delete"/></span>
				</div>
			</div>
		</#if>

    </#macro>

</#escape>