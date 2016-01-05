<#import "components/layout.ftl" as l />
<#import "components/utils.ftl" as u />
<#import "components/pagination.ftl" as p />
<#import "components/flow-state.ftl" as flow />

<#escape x as x?html> 

<#if currentSearch.viewPublic>
    <#assign searchMode>public</#assign>
    
<#elseif currentSearch.viewOwn>
    <#assign searchMode>own</#assign>

<#elseif currentSearch.viewOm>
    <#assign searchMode>om</#assign>
    
<#else> 
    <#-- blocked in server side, should never be here --> 
    <#assign searchMode>none</#assign>
</#if>

<#assign pageTitle><@u.messageHTML "initiative.search.${searchMode}.title" /></#assign>

<@l.main "page.search" pageTitle!"">

    <h1>
        ${pageTitle} <span class="icon-small help-tooltip pull-down trigger-tooltip hidden-xs" title="<@u.message key="initiative.search.${searchMode}.description" args=[minSupportCountForSearch!""] />" ></span>
        <#if currentUser.om && !currentSearch.viewOm>
           <span class="switch-view hidden-xs"><a href="${urls.searchOmView()}"><@u.message "user.omSearchView"/></a></span>
        </#if>
        <#if !currentSearch.viewPublic>
           <span class="switch-view hidden-xs"><a href="${urls.search()}"><@u.message "initiative.search.public.link"/></a></span>
        </#if>
        <#if currentUser.registered && !currentSearch.viewOwn>
           <span class="switch-view hidden-xs"><a href="${urls.searchOwnOnly()}"><@u.message "user.myInitiatives"/></a></span>
        </#if>
    </h1>

    <br/>
    
    <#--
     * Search filter and sort states
     * currentSearch.show:      running, ended, sentToParliament, canceled, all
     * currentSearch.orderBy:   id, mostTimeLeft, leastTimeLeft, mostSupports, leastSupports
     * currentSearch.limit:     20, 100 (max: 500)
     * currentSearch.offset
    -->
    
    <#--
     * Search filters for public and OM view
    -->
    <#if currentSearch.viewPublic>
        <span class="search-parameters-title filter hidden-xs"><@u.message "searchOptions.filter" /></span>
        <div class="search-parameters-container js-search-filter-row hidden-xs">
            <div class="search-parameters">
                <@u.searchLink parameter="withStateWaiting" cssClass=(currentSearch.show == "waiting")?string('active','') count=initiativeCounts.waiting />
                <@u.searchLink parameter="withStateRunning" cssClass=(currentSearch.show == "running")?string('active','') count=initiativeCounts.running />
                <@u.searchLink parameter="withStateEnded" cssClass=(currentSearch.show == "ended")?string('active','') count=initiativeCounts.ended />
                <@u.searchLink parameter="withStateSentToParliament" cssClass=(currentSearch.show == "sentToParliament")?string('active','') count=initiativeCounts.sentToParliament />
                <#--<@u.searchLink parameter="withStateCanceled" cssClass=(currentSearch.show == "canceled")?string('active','') count=initiativeCounts.canceled />-->
                <@u.searchLink parameter="withStateAll" cssClass=(currentSearch.show == "all")?string('active','') count=initiativeCounts.all />
            </div>
            <br class="clear" />
        </div>
    <#elseif currentSearch.viewOm>
        <span class="search-parameters-title filter hidden-xs"><@u.message "searchOptions.filter" /></span>
        <div class="search-parameters-container js-search-filter-row hidden-xs">
            <div class="search-parameters">
                <@u.searchLink parameter="withStatePreparation" cssClass=(currentSearch.show == "preparation")?string('active','') count=initiativeCounts.preparation />
                <@u.searchLink parameter="withStateReview" cssClass=(currentSearch.show == "review")?string('active','') count=initiativeCounts.review />
                <@u.searchLink parameter="withStateWaiting" cssClass=(currentSearch.show == "waiting")?string('active','') count=initiativeCounts.waiting />
                <@u.searchLink parameter="withStateRunning" cssClass=(currentSearch.show == "running")?string('active','') count=initiativeCounts.running />
                <@u.searchLink parameter="withStateEnded" cssClass=(currentSearch.show == "ended")?string('active','') count=initiativeCounts.ended />
            </div>
            <div class="search-parameters">
                <@u.searchLink parameter="withStateSentToParliament" cssClass=(currentSearch.show == "sentToParliament")?string('active','') count=initiativeCounts.sentToParliament />
                <#--<@u.searchLink parameter="withStateOmCanceled" cssClass=(currentSearch.show == "omCanceled")?string('active','') count=initiativeCounts.omCanceled />-->
                <@u.searchLink parameter="withStateCloseToTermination" cssClass=(currentSearch.show == "closeToTermination")?string('active','') count=initiativeCounts.closeToTermination />
                <@u.searchLink parameter="withStateOmAll" cssClass=(currentSearch.show == "omAll")?string('active','') count=initiativeCounts.omAll />
            </div>
            <br class="clear" />
        </div>

    </#if>
    
    <#--
     * Search sort
     *
     * Sort only if more than 1 to sort
    -->
    <#if (initiativeCounts[currentSearch.show] > 1)>
        <span class="search-parameters-title sort hidden-xs"><@u.message "searchOptions.sort" /></span>
        <div class="column search-sort">
            <#if currentSearch.show == "running">
                <span class="small-icon icon-search-sort by-time-left"><@u.message "searchOptions.runningTimeLeft" /></span>
                <div class="switch-buttons">
                    <@u.searchLink parameter="withOrderByMostTimeLeft" cssClass=(currentSearch.orderBy == "mostTimeLeft")?string('active','') tooltip=false />
                    <@u.searchLink parameter="withOrderByLeastTimeLeft" cssClass=(currentSearch.orderBy == "leastTimeLeft")?string('active','') tooltip=false />
                </div>
            <#else>
                <span class="small-icon icon-search-sort by-date-accepted">&#160;</span><div class="switch-buttons">
                    <@u.searchLink parameter="withOrderByCreatedNewest" cssClass=(currentSearch.orderBy == "createdNewest")?string('active','') tooltip=false />
                    <@u.searchLink parameter="withOrderByCreatedOldest" cssClass=(currentSearch.orderBy == "createdOldest")?string('active','') tooltip=false />
                </div>
            </#if>
        </div>
        <div class="column search-sort">
            <span class="small-icon icon-search-sort by-support-statements"><@u.message "searchOptions.supportStatements" /></span>
            <div class="switch-buttons">
                <@u.searchLink parameter="withOrderByMostSupports" cssClass=(currentSearch.orderBy == "mostSupports")?string('active','') tooltip=false />
                <@u.searchLink parameter="withOrderByLeastSupports" cssClass=(currentSearch.orderBy == "leastSupports")?string('active','') tooltip=false />
            </div>
        </div>
        <br class="clear" />
    </#if>

    <#--
     * Search pagination
     *
     *  - Do not display in OWN initiatives view.
    -->
    <#if searchMode != "own">
        <@p.pagination currentSearch.limit!500 currentSearch.offset!0 "top" />
    </#if>
    
    
    <#--
     * Toggle initiatives that have under 50 support votes
     *
     *  - Hidden as default
    -->
    <#if !currentSearch.viewOwn &&
         (currentSearch.show == "running" ||  currentSearch.show == "ended" || currentSearch.show == "all"  || currentSearch.show == "omAll" || currentSearch.show == "closeToTermination")>
        <div class="toggle-under-50 hidden-xs">
        <span><@u.message "searchParameters.showUnder50.title" /></span>
        <#if currentSearch.minSupportCount == 0>
            <div class="switch-buttons">
                <a class="" href="${urls.search()}${searchParameters.getWithHideLooserInitiatives()}"><@u.message "searchParameters.showUnder50.hide" /></a>
                <a class="active" href="${urls.search()}${searchParameters.getWithShowLooserInitiatives()}"><@u.message "searchParameters.showUnder50.show" /></a>
            </div>
        <#else>
            <div class="switch-buttons">
                <a class="active" href="${urls.search()}${searchParameters.getWithHideLooserInitiatives()}"><@u.message "searchParameters.showUnder50.hide" /></a>
                <a class="" href="${urls.search()}${searchParameters.getWithShowLooserInitiatives()}"><@u.message "searchParameters.showUnder50.show" /></a>
            </div>
        </#if>
        </div>
    </#if>

    <@u.mobileSearch />

    <div class="search-results">
    <#if initiatives?? && (initiatives?size > 0)>
        <#list initiatives as initiative>
            <#assign totalSupportCount = initiative.totalSupportCount />

            <#assign verifiedByVRK = initiative.verifiedSupportCount gt 0/>
            <#if verifiedByVRK>
                <#assign totalSupportCount = initiative.verifiedSupportCount />
            </#if>


            <#if initiative_index == 0><ul class="hidden-xs"></#if>
            <li <#if initiative_index == 0>class="first"</#if>>
                <a href="${urls.view(initiative.id)}">
            
                <#if    (initiative.state != InitiativeState.DRAFT && initiative.state != InitiativeState.PROPOSAL
                      && initiative.state != InitiativeState.REVIEW && initiative.state != InitiativeState.CANCELED)
                      && flowStateAnalyzer.getFlowState(initiative) != FlowState.ACCEPTED_NOT_STARTED>
                      
                <span class="support-votes-details">

                    <span class="support-votes">${totalSupportCount}</span>

                    <#if (initiative.supportCount > 0)>
                        <#assign args><span class="internal-count">${initiative.supportCount}</span></#assign>
                        <span class="internal-support-votes"><@u.messageHTML key="initiative.search.internalSupportCount" args=[args] /></span>
                    </#if>
                    
                    
                    <#if (initiative.votingDaysLeft > 0 && initiative.totalVotingDays > 0)>
                        <#assign progressBarTooltip><@u.messageHTML key="searchResults.initiative.bar" args=[totalSupportCount, initiative.votingDaysLeft, initiative.totalVotingDays] /></#assign>
                    <#else>
                        <#assign progressBarTooltip><@u.messageHTML key="searchResults.initiative.bar.votingEnded" args=[totalSupportCount] /></#assign>
                    </#if>
                    
                    <span class="progress-bars trigger-tooltip" title="${progressBarTooltip}">
                        <span class="bar-container count ${(totalSupportCount < requiredVoteCount)?string("","completed")}">
                            <#assign countWidth = (100-100*totalSupportCount/requiredVoteCount) />
                            <#if countWidth lt 0>
                                <#assign countWidth = 0 />
                            </#if>
                            <span class="bar js-animate"style="width:${countWidth?string("#")}%;"></span>
                        </span>
                        
                        <#if (initiative.totalVotingDays > 0)>
                            <span class="bar-container time">
                                <#assign timeWidth = (100*initiative.votingDaysLeft/initiative.totalVotingDays) />
                                <#if timeWidth lt 0>
                                    <#assign timeWidth = 0 />
                                </#if>
                                <span class="bar js-animate" style="width:${timeWidth?string("#")}%;"></span>
                            </span>
                        </#if>
                    </span>
                </span>
                </#if>
                
                <#if searchMode == "public"><#assign showTitle="show"></#if>
                <span class="date trigger-tooltip" title="<@u.message "searchResults.initiative."+searchMode+".startDate" />" ><@u.localDate initiative.startDate /></span>
                <span class="title"><span class="name"><@u.text initiative.name /></span></span>
                <span class="info"><@flow.flowStateDescription initiative /></span>
                
                </a>
            </li>
            <#if !initiative_has_next></ul></#if>

            <#if initiative_index == 0><ul class="mobile hidden-sm hidden-md hidden-lg"></#if>
              <li <#if initiative_index == 0>class="first"</#if>>
                <div class="row">
                <#if    (initiative.state != InitiativeState.DRAFT && initiative.state != InitiativeState.PROPOSAL
                && initiative.state != InitiativeState.REVIEW && initiative.state != InitiativeState.CANCELED)
                && flowStateAnalyzer.getFlowState(initiative) != FlowState.ACCEPTED_NOT_STARTED>
                    <div class="col-xs-10">
                      <span class="date" title="<@u.message "searchResults.initiative."+searchMode+".startDate" />" ><@u.localDate initiative.startDate /></span>
                      <span class="info"><@flow.flowStateDescription initiative /></span>
                      <a href="${urls.view(initiative.id)}"><@u.text initiative.name /></a>
                    </div>
                    <div class="col-xs-2">
                      <span class="support-votes">${totalSupportCount}</span>

                          <#if (initiative.votingDaysLeft > 0 && initiative.totalVotingDays > 0)>
                              <#assign progressBarTooltip><@u.messageHTML key="searchResults.initiative.bar" args=[totalSupportCount, initiative.votingDaysLeft, initiative.totalVotingDays] /></#assign>
                          <#else>
                              <#assign progressBarTooltip><@u.messageHTML key="searchResults.initiative.bar.votingEnded" args=[totalSupportCount] /></#assign>
                          </#if>

                        <span class="progress-bars trigger-tooltip" title="${progressBarTooltip}">
                            <span class="bar-container count ${(totalSupportCount < requiredVoteCount)?string("","completed")}">
                                <#assign countWidth = (100-100*totalSupportCount/requiredVoteCount) />
                                <#if countWidth lt 0>
                                    <#assign countWidth = 0 />
                                </#if>
                              <span class="bar js-animate"style="width:${countWidth?string("#")}%;"></span>
                            </span>

                            <#if (initiative.totalVotingDays > 0)>
                              <span class="bar-container time">
                                    <#assign timeWidth = (100*initiative.votingDaysLeft/initiative.totalVotingDays) />
                                  <#if timeWidth lt 0>
                                      <#assign timeWidth = 0 />
                                  </#if>
                                <span class="bar js-animate" style="width:${timeWidth?string("#")}%;"></span>
                              </span>
                            </#if>
                        </span>
                    </div>
                </#if>
                <#if searchMode == "public"><#assign showTitle="show"></#if>
                </div>
              </li>
            <#if !initiative_has_next></ul></#if>
        </#list>
    <#else>
        
        <#--
         * Search results EMPTY
         *
         * 1. No initiatives that are shown in public search view
         * 2. Public search view has initiatives but they all are filtered off
         * 3. No initiatives in Own initiatives search view
         * 4. No initiatives in OM search view
         * 5. OM search view has initiatives but they all are filtered off
        -->
        <p class="title"><@u.message "searchResults.${searchMode}.empty" /></p>
    </#if>
    
    </div>
    
    <#--
     * Search pagination
     *
     *  - Do not display in OWN initiatives view.
    -->
    <#if searchMode != "own">
        <@p.pagination currentSearch.limit currentSearch.offset!0 "bottom" />
    </#if>
</@l.main>

</#escape>