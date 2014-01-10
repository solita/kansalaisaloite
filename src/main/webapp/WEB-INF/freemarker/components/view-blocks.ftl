<#import "utils.ftl" as u />

<#escape x as x?html> 

<#--
 * blockHeader
 *
 * Block header for public view.
 * 
 * @param key is for example "initiative.basicDetails.title"
 -->
<#macro blockHeader key cssClass="view">
    <div class="content-block-header ${cssClass}" >
        <h2><@u.message key!"" /></h2>
    </div>
</#macro>

<#--
 * basicDetails
 *
 * Initiative basic details VIEW block.
 * Initiative name, date, proposal type, primary language, proposal, rationale
 -->
<#macro basicDetails> 
        <#-- Initiative title -->
        <div class="initiative-content-row">
            <h4 class="header"><@u.message "initiative.name."+locale /></h4>
            <@u.text initiative.name /> 
        </div>
        
        <div class="initiative-content-row cf">
            <#-- Date -->
            <div class="column">
                <h4 class="header"><@u.message "initiative.startDate" /></h4>
                <@u.localDate initiative.startDate />
            </div>
            
            <#-- Proposal type -->
            <div class="column">
                <h4 class="header"><@u.message "initiative.proposalType" /></h4>
                <@u.message "initiative.proposalType." + initiative.proposalType?lower_case />
            </div>

            <#-- OM acceptance identifier -->
            <#if initiative.acceptanceIdentifier??>
                <div class="column last">
                    <h4 class="header"><@u.message "initiative.acceptanceIdentifier" /></h4>
                    ${initiative.acceptanceIdentifier!""}
                </div>
            </#if>
            
        </div>
        
        <#-- Primary language -->
        <#if initiative.hasTranslation(altLocale) && initiative.hasTranslation(locale)> 
            <div class="initiative-content-row">
                <h4 class="header"><@u.message "initiative.primaryLanguage" /></h4>
                <@u.message "initiative.primaryLanguage." + initiative.primaryLanguage?lower_case />
            </div>
        </#if>

        <#-- Proposal -->
        <div class="initiative-content-row">
            <h4 class="header"><@u.message "initiative.proposal."+locale /></h4>
            <p><@u.text initiative.proposal /></p>
        </div>
        
        <#-- Rationale -->
        <div class="initiative-content-row">
            <h4 class="header"><@u.message "initiative.rationale."+locale /></h4>
            <p><@u.text initiative.rationale /></p>
        </div>
</#macro>  
  
<#--
 * extraDetails
 *
 * Initiative extra details VIEW block.
 * Initiative financial support, support notifications, links 
 -->
<#macro extraDetails>    
    
        <#-- Financial support -->
        <div class="initiative-content-row">
            <h4 class="header"><@u.message "initiative.financialSupport" /></h4>
            <#if initiative.financialSupport>
                <@u.message "initiative.financialSupport.true" />
            <#else>
                <@u.message "initiative.financialSupport.false" />
            </#if>
        </div>
        
        <#-- Financial support URL --> 
        <#if initiative.financialSupportURL??>
            <div class="initiative-content-row">
                <h4 class="header"><@u.message "initiative.financialSupportURL" /></h4>
                <@u.link href="${initiative.financialSupportURL}" label="${initiative.financialSupportURL}" rel="external" blockStyle=true />
            </div>
        </#if>
    
        <#-- Support notifications -->
        <div class="initiative-content-row ${(initiative.links?size==0)?string('last','')}">
            <div class="column">
            <h4 class="header"><@u.message "initiative.supportNotifications" /></h4>
            <ul>
                <li><@u.message "initiative.supportNotificationsInitiativeService" /></li>
                <#if initiative.supportStatementsInWeb><li><@u.message "initiative.supportStatementsInWeb" /></li></#if>
                <#if initiative.supportStatementsOnPaper><li><@u.message "initiative.supportStatementsOnPaper" /></li></#if>
            </ul>    
            </div>

            <#if initiative.supportStatementsInWeb || initiative.supportStatementsOnPaper>
                <div class="column wide last">
                    <h4 class="header"><@u.messageHTML "initiative.externalSupportCount" /></h4>
                    <p><@u.messageHTML key="initiative.externalSupportCount.description" args=[initiative.externalSupportCount] /></p>
                </div>
            </#if>
            <br class="clear" />
            
            <#if initiative.supportStatementsOnPaper && initiative.supportStatementPdf && votingInfo?? && (votingInfo.allowVotingAction) && !votingInfo.votingTime??>
                <h4 id="support-statement-pdf" class="header"><@u.message "supportStatementPdf.title" /></h4>
                <p><@u.message "supportStatementPdf.description" /></p>
                <div class="column">
                    <p><span class="file-icon pdf"></span><a title="" target="_blank" href="${urls.supportStatementPdf(initiative.id)}"><@u.message "supportStatementPdf.pdf.name" /></a></p>
                </div>
                <div class="column wide last">
                    <p><#noescape>${initiative.supportStatementAddress!""?replace('\n','<br/>')!""}</#noescape></p>
                </div>
                <br class="clear" />
            </#if>
        </div>
         
        <#-- Links -->
        <#if initiative.links?size gt 0>
        <div class="initiative-content-row last">
            <h4 class="header"><@u.message "initiative.links" /></h4>

            <#list initiative.links as link>
                <#if link_index == 0><ul class="no-style"></#if>
                <li><@u.link href="${initiative.links[link_index].uri}" label="${initiative.links[link_index].label}" rel="external" blockStyle=true /></li>
                <#if !link_has_next></ul></#if> 
            </#list>
        </div>
        </#if>
</#macro>

<#--
 * currentAuthorDetails
 *
 * Current author details VIEW block.
 * Author name, municipality, roles, contact details 
 -->
<#macro currentAuthorDetails>
    <#if initiative.currentAuthor??>        
        <div class="view-block">
            <div class="initiative-content-row last">
                <div class="column">
                    <h4 class="header"><@u.message "initiative.currentAuthor.roles" /></h4>
                    
                    <#if initiative.currentAuthor.initiator || initiative.currentAuthor.representative || initiative.currentAuthor.reserve>
                        <ul class="no-style">
                            <#if currentAuthor.initiator><li><@u.message "initiative.currentAuthor.initiator" /></li></#if>
                            <#if currentAuthor.representative><li><@u.message "initiative.currentAuthor.representative" /></li></#if>
                            <#if currentAuthor.reserve><li><@u.message "initiative.currentAuthor.reserve" /></li></#if>
                        </ul>
                    </#if>
        
                </div>
        
                <div class="column wide last">
                    <h4 class="header"><@u.message "initiative.currentAuthor.contactDetails" /></h4>
                    
                    ${currentAuthor.firstNames!""} ${currentAuthor.lastName!""}<br/>
                    <#if currentAuthor.contactInfo.address??>${currentAuthor.contactInfo.address!""}<br/></#if>
                    <#if currentAuthor.contactInfo.phone??>${currentAuthor.contactInfo.phone!""}<br/></#if>
                    <#if currentAuthor.contactInfo.email??>${currentAuthor.contactInfo.email!""}<br/></#if>
                </div>
            
                <br class="clear" />
            </div>
            
        </div>
    </#if>
</#macro>

<#--
 * organizerDetails
 *
 * Initiative organizers VIEW block
 * Initiative organizers: initiator, representative, reserve
 *
 * Uses organizers macro in utils.ftl.
 -->
<#macro organizerDetails>
    <div class="view-block last">
        <@u.organizers path="initiator" />
        <@u.organizers path="representative" />
        <@u.organizers path="reserve" cssClass="last" />
    </div>
</#macro>

</#escape>