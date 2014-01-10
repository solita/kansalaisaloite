<#import "/spring.ftl" as spring />
<#import "utils.ftl" as u />

<#-- 
 * flowStateDescription
 * 
 * Displays status message for defined initiative. 
 *
 * Initiative states:
 *
 * DRAFT:                       Draft, no organizers
 * PROPOSAL:                    Invitations send 
 * REVIEW:                      Sent to OM, but pending acceptance
 * ACCEPTED_NOT_STARTED:        Accepted, voting not yet started
 * ACCEPTED_FIRST_MONTH:        Voting in progress, first month in progress 
 * ACCEPTED_FIRST_MONTH_FAILED: Voting suspended, first month exceeded, less than 50 votes
 * ACCEPTED_RUNNING:            Voting in progress, first month exceeded, at least 50 votes
 * ACCEPTED_UNCONFIRMED:        Voting ended, over 50 000 unconfirmed votes
 * ACCEPTED_FAILED:             Voting ended, less than 50 000 votes
 * ACCEPTED_CONFIRMED_RUNNING:  Voting in progress, over 50 000 confirmed votes
 * ACCEPTED_CONFIRMED:          Voting ended, over 50 000 confirmed votes
 * ACCEPTED_CONFIRMED_FAILED:   Voting ended, over 50 000 confirmed votes,
 *                              but was not sent to parliament within 6 months
 * DONE:                        Sent to parliament
 * CANCELED
    
 * @param initiative
-->
<#macro flowStateDescription initiative>
    <#assign flowState = flowStateAnalyzer.getFlowState(initiative) />

    <#if flowState == FlowState.ACCEPTED_NOT_STARTED>
        <#assign paramDate><@u.localDate initiative.startDate /></#assign>

    <#elseif flowState == FlowState.ACCEPTED_FIRST_MONTH_FAILED>
        <#assign paramDate><@u.localDate initiative.startDate.plus(requiredMinSupportCountDuration) /></#assign>

    <#elseif flowState == FlowState.ACCEPTED_FIRST_MONTH || flowState == FlowState.ACCEPTED_RUNNING
          || flowState == FlowState.ACCEPTED_UNCONFIRMED || flowState == FlowState.ACCEPTED_FAILED
          || flowState == FlowState.ACCEPTED_CONFIRMED_RUNNING || flowState == FlowState.ACCEPTED_CONFIRMED>
        <#assign paramDate><@u.localDate initiative.endDate /></#assign>

    <#elseif flowState == FlowState.DONE >
        <#assign paramDate><@u.localDate initiative.parliamentSentTime /></#assign>

    <#elseif flowState == FlowState.CANCELED>
        <#assign paramDate><@u.localDate initiative.stateDate /></#assign>

    <#elseif flowState == FlowState.ACCEPTED_CONFIRMED_FAILED>
        <#assign paramDate><@u.localDate initiative.getEndDateForSendToParliament(sendToParliamentDuration) /></#assign>

    <#else>
        <#assign paramDate>?</#assign> <#-- when not needed -->
    </#if>
    
    <@u.enumDescription key=flowState args=[paramDate, requiredVoteCount] />
</#macro>
    
<#-- 
 * flowStateIndicator
 * 
 * Displays the flow state indicator for defined initiative
 *
 * Steps:
 *
 * first-step-active:   DRAFT, PROPOSAL, REVIEW
 * second-step-active:  ACCEPTED_NOT_STARTED, ACCEPTED_FIRST_MONTH, ACCEPTED_RUNNING,
 *                      ACCEPTED_CONFIRMED_RUNNING
 * third-step-active:   ACCEPTED_CONFIRMED
 * error-second:        ACCEPTED_FIRST_MONTH_FAILED, ACCEPTED_UNCONFIRMED, ACCEPTED_FAILED
 * error-third:         ACCEPTED_CONFIRMED_FAILED
 * done:                DONE 
 * canceled:            CANCELED 
 *  
 * @param initiative
-->
<#macro flowStateIndicator initiative>
    <#assign flowState = flowStateAnalyzer.getFlowState(initiative) />

    <#if flowState == FlowState.DRAFT || flowState == FlowState.PROPOSAL || flowState == FlowState.REVIEW>
        
        <#assign indicatorState="first-step-active" />
        <#assign firstState="active" />
        <#assign indicatorActive=true />
        
    <#elseif flowState == FlowState.ACCEPTED_NOT_STARTED || flowState == FlowState.ACCEPTED_FIRST_MONTH
          || flowState == FlowState.ACCEPTED_RUNNING     || flowState == FlowState.ACCEPTED_UNCONFIRMED>
          
        <#assign indicatorState="second-step-active" />
        <#assign firstState="checked" />
        <#assign secondState="active" />
        <#assign indicatorActive=true />
        
    <#elseif flowState == FlowState.ACCEPTED_CONFIRMED  || flowState == FlowState.ACCEPTED_CONFIRMED_RUNNING>
    
        <#assign indicatorState="third-step-active" />
        <#assign firstState="checked" />
        <#assign secondState="checked" />
        <#assign thirdState="active" />
        <#assign indicatorActive=true />
        
    <#elseif flowState == FlowState.DONE>
    
        <#assign indicatorState="done" />    
        <#assign firstState="checked" />
        <#assign secondState="checked" />
        <#assign thirdState="checked" />
        <#assign indicatorActive=false />
        
    <#elseif flowState == FlowState.ACCEPTED_FIRST_MONTH_FAILED || flowState == FlowState.ACCEPTED_FAILED>
    
         <#assign indicatorState="second-step-active" />
         <#assign firstState="checked" />
         <#assign secondState="failed" />
         <#assign indicatorActive=true />
        
    <#elseif flowState == FlowState.ACCEPTED_CONFIRMED_FAILED>

        <#assign indicatorState="third-step-active" />
        <#assign firstState="checked" />
        <#assign secondState="checked" />
        <#assign thirdState="failed" />
        <#assign indicatorActive=true />
        
    <#elseif flowState == FlowState.CANCELED>
    
        <#assign indicatorState="canceled" />
        <#assign indicatorActive=false />
        
    <#else>
    
        <#assign indicatorState="" />
        <#assign indicatorActive=true />
        
    </#if>
    
    <div class="flow-state-indicator ${indicatorState} cf">
        <#if indicatorActive>
          <div class="flow-state-step ${firstState!""}">
            <div class="flow-state-content">
              <span><@u.message "FlowStateIndicator.first" /></span>
            </div>
          </div>
          <div class="flow-state-step ${secondState!""}">
            <div class="flow-state-content">
              <span><@u.message "FlowStateIndicator.second" /></span>
            </div>
          </div>
          <div class="flow-state-step ${thirdState!""}">
            <div class="flow-state-content">
              <span><@u.message "FlowStateIndicator.third" /></span>
            </div>
          </div>
        <#elseif flowState == FlowState.DONE>
            <div class="flow-state-step ${indicatorState}">
              <div class="flow-state-content">
                  <#assign args><@u.localDate initiative.parliamentSentTime /></#assign>
                  <span class="large-icon save-and-send">
                      <@u.message key="FlowStateIndicator.done" args=[args] />
                  </span>
                  <span class="info">
                    <#if initiative.parliamentURL??>
                        <@u.message key="FlowStateIndicator.done.followInitiative"/>
                        <@u.link href=initiative.parliamentURL label=initiative.parliamentIdentifier rel="external" />
                    </#if>
                  </span>
              </div>
           </div>
        <#elseif flowState == FlowState.CANCELED>
            <div class="flow-state-step ${indicatorState}">
              <div class="flow-state-content">
                  <#assign args><@u.localDate initiative.stateDate /></#assign>
                  <span class="large-icon stop"><@u.message key="FlowStateIndicator.canceled" args=[args] /></span>
              </div>
           </div>
        </#if>
    </div>
    
</#macro>

<#macro flowStateStatus state="" >
    <#--<#if indicatorState = state>
        <span><@flowStateDescription initiative /></span>
    <#else>
        <span>&nbsp;</span>
    </#if>-->
    <span>&nbsp;</span>
</#macro>