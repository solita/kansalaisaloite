<#import "utils.ftl" as u />
<#import "forms.ftl" as f />

<#escape x as x?html> 

<#--
 * Toggle the visibility of alternative language fields.
-->
<#global altLangClass="hide" />
<#if initiative.hasTranslation(altLocale) >
    <#global altLangClass="" />
</#if>


<#--
 * blockHeader
 *
 * Block header for management-view. Edit and View uses the same header block.
 * 
 * @param key is for example "initiative.basicDetails.title"
 * @param edit is for example "BASIC"
 * @param disabled toggles edit-button (disabled/enabled)
 -->
<#macro blockHeader key edit="" disabled="">
    <#if key?contains("basicDetails")>
        <#assign enableAltLang=true />
    <#else>
        <#assign enableAltLang=false />
    </#if>
   
    <#if edit="" && managementSettings.editMode != EditMode.NONE && (enableAltLang || managementSettings.editMode != EditMode.FULL) >
        <div class="system-msg msg-summary" id="${key}">
            <div class="system-msg msg-info">
                <@u.messageHTML "initiative.create.intro" />
                <#if enableAltLang>
                    <@u.messageHTML "initiative.create.intro.optional" />
                </#if>        
           </div>
       </div>
   </#if>

    <div class="content-block-header ${(managementSettings.editMode == EditMode.NONE)?string('view','edit')}">
        <h2><@u.message key!"" /></h2>

        <#--
            View-mode: shows edit-button
            Edit-mode: disables other fields and shows alt-lang link if needed                        
        -->
        <#if managementSettings.editMode == EditMode.NONE>
  	         <div class="initiative-content-tools">          
                <#if disabled="disabled">
                    <span class="small-button gray disabled"><@u.message "action.editDisabled" /></span>
                <#elseif managementSettings.editMode != EditMode.NONE>
                    <span class="small-button gray disabled"><span class="small-icon edit"><@u.message "action.edit" /></span></span>
                <#else>
                    <a class="small-button gray" href="?edit=${edit}#${key}"><span class="small-icon edit"><@u.message "action.edit" /></span></a>
                </#if>
            </div>
        </#if>
    </div>    
</#macro>

<#--
 * buttons
 *
 * Save and cancel buttons used in block-edit-mode.
 * 
 * @param validateBlock enables validateForm javascript-function in defined block
 * @param editWarning assigns warning system-message for current block.
 -->
<#macro buttons validateBlock="" editWarning="">
    <div class="input-block-action">
        <#if editWarning == "BASIC">
            <div class="system-msg msg-warning">
                <p><@u.message "initiative.basicDetails.editWarning" /></p>
                <div><@u.message "initiative.basicDetails.editWarning.confirm" /></div>
            </div>
            <br/>
        </#if>
        <button type="submit" name="${UrlConstants.ACTION_SAVE}" value="<@u.messageHTML 'action.save' />" class="small-button green" <#if validateBlock!="">onClick="validateForm('${validateBlock}');return false;"</#if>><span class="small-icon save-and-send"><@u.messageHTML 'action.save' /></span></button>
        <a class="small-button red" href="${springMacroRequestContext.requestUri}"><span class="small-icon cancel"><@u.messageHTML 'action.cancel' /></span></a>
    </div>
</#macro>

<#--
 * basicDetails
 *
 * Initiative basic details EDIT block.
 * Initiative name, date, proposal type, primary language, proposal, rationale
 *
 * Prints help-texts and validation errors in this block
 -->
<#macro basicDetails>        
    <div id="initiative-basic-details" class="input-block cf">
        <#-- Form helptexts for NOSCRIPT-users. -->
        <noscript>
            <div class="no-js-help-container">
                <@f.helpText "helpName" "noscript" />
                <#assign href>${urls.help(HelpPage.INITIATIVE_STEPS.getUri(locale))}</#assign>
                <@f.helpText path="helpDate" noscript="noscript" href=href />
                <@f.helpText "helpProposalType" "noscript" />
                <@f.helpText "helpProposal" "noscript" />
                <@f.helpText "helpRationale" "noscript" />
            </div>
        </noscript>
        
        <#-- Block-edit errors -->
        <#if managementSettings.editMode != EditMode.FULL>
        <div class="input-block-content no-top-margin">
            <@u.errorsSummary path="initiative.*" prefix="initiative."/>
        </div>
        </#if>
        
        <#if !managementSettings.editFull>
            <div class="input-block-content no-top-margin">
                <@u.systemMessage path="initiative.basicDetails.editWarning" type="warning" showClose=false />
            </div>
        </#if>
    
        <div class="input-block-content no-top-margin">
            <@u.systemMessage path="initiative.basicDetails.description" type="info" showClose=false />
            
            <#-- Show alt language link if not in edit and current block is 'basicDetails' -->
            <#if managementSettings.editMode == EditMode.FULL || managementSettings.editMode == EditMode.BASIC>
                <a id="show-alternative-lang" class="show-alternative-lang hidden" data-alttext="<@u.message "altVersion.hide" />" data-translation="${initiative.hasTranslation(altLocale)?string('true','false')}" ><@u.message "altVersion.add" /></a>
            </#if>
        </div>
        
        <#-- Initiative title -->
        <div class="input-block-content">
            <div class="initiative-header-area">        
                <@f.helpText "helpName" />
                <@f.textField path="initiative.name" required="required" cssClass="large" maxLength=InitiativeConstants.INITIATIVE_NAME_MAX?string("#") optional=true />
            </div>
        </div>
        
        <#-- Date -->
        <div class="input-block-content no-top-margin">
            <div class="initiative-header-date-area">
                <#assign href>${urls.help(HelpPage.INITIATIVE_STEPS.getUri(locale))}</#assign>
                <@f.helpText path="helpDate" href=href />
                <@f.textField path="initiative.startDate" fieldType="text" required="required" cssClass="datepicker" optional=false />
            </div>
        </div>
              
        <#-- Proposal type -->
        <div class="input-block-content">
            <div class="initiative-header-type-area">
                <@f.helpText "helpProposalType" />
                <@f.radiobutton path="initiative.proposalType" required="required" options={"LAW":"initiative.proposalType.law", "PREPARATION":"initiative.proposalType.preparation"} attributes="" />
            </div>
        </div>
        
        <#-- Primary language -->
        <div class="input-block-content alt-lang js-hide">
            <div class="initiative-primary-language-area">        
                <@f.helpText "helpPrimaryLanguage" />
                <@f.radiobutton path="initiative.primaryLanguage" required="" options={locale?upper_case:"initiative.primaryLanguage."+locale, altLocale?upper_case:"initiative.primaryLanguage."+altLocale} attributes="" />
            </div>
        </div>
                 
        <#-- Proposal -->
        <div class="input-block-content">
            <div class="initiative-proposal-area">
                <@f.helpText "helpProposal" />
                <@f.textarea path="initiative.proposal" required="required" optional=true cssClass="textarea-tall" />
            </div>
        </div>   
            
        <#-- Rationale -->
        <div class="input-block-content">
            <div class="initiative-rationale-area">
                <@f.helpText "helpRationale" />
                <@f.textarea path="initiative.rationale" required="required" optional=false cssClass="textarea-tall" />
            </div>
        </div>
        
        <#if !managementSettings.editFull>
            <@buttons editWarning="BASIC" />
        </#if>
    </div>
</#macro>
            
<#--
 * extraDetails
 *
 * Initiative extra details EDIT block.
 * Initiative financial support, support notifications, links 
 *
 * Prints help-texts and validation errors in this block
 -->
<#macro extraDetails>
    <div class="input-block cf">
        <#-- Form helptexts for NOSCRIPT-users. -->
        <noscript>
            <div class="no-js-help-container">
                <@f.helpText "helpFinancialSupport" "noscript" />
                <@f.helpText "helpSupportNotifications" "noscript" />
                <@f.helpText "helpSupportNotificationsCount" "noscript" />
                <@f.helpText "helpLinks" "noscript" />
            </div>
        </noscript>
    
        <#-- Block-edit errors -->
        <#if managementSettings.editMode != EditMode.FULL>
        <div class="input-block-content no-top-margin">
            <@u.errorsSummary path="initiative.*" prefix="initiative."/>
        </div>
        </#if>
    
        <div class="input-block-content no-top-margin">
            <@u.systemMessage path="initiative.extraDetails.description" type="info" showClose=false />
        </div>
            
        <#-- Financial support -->
        <div class="input-block-content">
            <div class="initiative-finance-area">
                <@f.helpText "helpFinancialSupport" />
                <@f.radiobutton path="initiative.financialSupport" required="required" options={"false":"initiative.financialSupport.false", "true":"initiative.financialSupport.true"} attributes="" />
                
                <div class="initiative-finance-url-area js-hide">
                    <@f.textField path="initiative.financialSupportURL" required="" cssClass="large" maxLength=InitiativeConstants.FINANCIAL_SUPPPORT_URL_MAX?string("#") optional=false />
                </div>
            </div>
        </div>
        

        <#-- Support notifications -->
        <div class="input-block-content">
            <div class="initiative-support-notifications-area">
                <@f.helpText "helpSupportNotifications" />
                <div class="input-header">
                    <@u.message "initiative.supportNotifications" /> <@u.icon type="required" size="small" />
                </div>
            
                <label><input type="checkbox" disabled="disabled" checked="checked" /><@u.message "initiative.supportNotificationsInitiativeService" /></label>
                <@f.formCheckbox path="initiative.supportStatementsInWeb" />
                <@f.formCheckbox path="initiative.supportStatementsOnPaper" />
            </div>
        </div>
        
        <div class="input-block-content no-top-margin">    
            <div class="initiative-external-support-count js-hide">
                <@f.helpText "helpSupportNotificationsCount" />
                <@f.textField path="initiative.externalSupportCount" required="" cssClass="x-small" optional=false  maxLength=7 />
            </div>
        </div>
        
        <div class="input-block-content no-top-margin">
            <div class="initiative-support-statement-pdf js-hide">
                <@f.helpText "helpSupportStatementPdf" />
                <div class="input-header">
                    <@u.message "initiative.supportStatementPdf.title" />
                </div>
                
                <@f.formCheckbox path="initiative.supportStatementPdf" />
                
                <div class="top-margin">
                    <#--<@f.textField path="initiative.supportStatementAddress" required="" cssClass="large" optional=false />-->
                    <@f.textarea path="initiative.supportStatementAddress" required="" optional=false cssClass="address-field noresize" maxLength=InitiativeConstants.SUPPORT_STATEMENT_ADDRESS_MAX?string("#") />
                </div>
            </div>
            
        </div>
        
            
        <#-- Links -->
        <div class="input-block-content">
            <div class="initiative-links-area">
                <@f.helpText "helpLinks" />
                <div class="input-header"><@u.message "initiative.links" /></div>
                <span class="instruction-text"><@u.message "initiative.links.instruction" /></span>
                
                <#list initiative.links as link>
                    <div class="add-link cf">
                        <input type="hidden" name="links[${link_index}].id" value="${link.id!''}"/>
                        <div class="link-input-description">
                            <@f.textField path="initiative.links[${link_index}].label" required="" cssClass="medium" maxLength=InitiativeConstants.LINK_LABEL_MAX?string("#") optional=false />
                        </div>
                        <div class="link-input-url">
                            <a class="remove-link trigger-tooltip ignoredirty hidden" title="<@u.message "link.remove" />" href="#">x</a>
                            <@f.textField path="initiative.links[${link_index}].uri" required="" cssClass="medium" maxLength=InitiativeConstants.LINK_URI_MAX?string("#") optional=false />
                        </div>
                    </div>
                </#list>
                
                <#-- Add one new link for no-script users -->
                <#assign newLinkIndex=initiative.links?size />
                <noscript>
                    <div class="add-link cf">
                        <div class="link-input-description">
                            <@f.textField path="initiative.links[${newLinkIndex}].label" required="" cssClass="medium" maxLength=InitiativeConstants.LINK_LABEL_MAX?string("#") optional=false />
                        </div>
                        <div class="link-input-url">
                            <@f.textField path="initiative.links[${newLinkIndex}].uri" required="" cssClass="medium" maxLength=InitiativeConstants.LINK_URI_MAX?string("#") optional=false />
                        </div>
                    </div>
                </noscript>
                
                <div id="link-container" data-index="${newLinkIndex}"></div>
                <#-- Template for new link line -->
                <script id="linkTemplate" type="text/x-jsrender">
    
                    <div id="link-holder-base" class="add-link cf">        
                        <div class="link-input-description">
                            <label>
                                <@u.message "initiative.links.label"/>
                                <input type="text" class="medium" value="" id="links[{{>linkIndex}}].label" name="links[{{>linkIndex}}].label" maxlength="${InitiativeConstants.LINK_LABEL_MAX?string("#")}" />
                            </label>
                        </div>
                        <div class="link-input-url">
                            <label>
                                <@u.message "initiative.links.uri"/>
                                <input type="text" class="medium" value="" id="links[{{>linkIndex}}].uri" name="links[{{>linkIndex}}].uri" maxlength="${InitiativeConstants.LINK_URI_MAX?string("#")}" />
                                <a class="remove-link trigger-tooltip ignoredirty hidden" title="<@u.message "link.remove" />" href="#">x</a>
                            </label>
                        </div>
                    </div>
                </script>
                          
                <a id="add-new-link" class="small-button gray hidden ignoredirty" href="#"><span class="small-icon add"><@u.message "link.add" /></span></a>
                
    
            </div>
        </div>
        
        <#if !managementSettings.editFull>
            <@buttons />
        </#if>
    </div>
</#macro>

<#--
 * currentAuthorDetails
 *
 * Current author details EDIT block.
 * Author name, municipality, roles, contact details 
 *
 * Prints help-texts and validation errors in this block
 -->
<#macro currentAuthorDetails>
    <div class="input-block cf">
    
        <#-- Form helptexts for NOSCRIPT-users. -->
        <noscript>
            <div class="no-js-help-container">
                <@f.helpText "helpOwnRoles" "noscript" />
                <@f.helpText "helpOwnDetails" "noscript" />
            </div>
        </noscript>
    
        <#-- Block-edit errors -->
        <#if managementSettings.editMode != EditMode.FULL>
        <div class="input-block-content">
            <@u.errorsSummary path="initiative.*" prefix="initiative."/>
        </div>
        </#if>
    
        <div class="input-block-content <#if managementSettings.editMode != EditMode.FULL>no-top-margin</#if>">
            <div class="column auto-scale">
                <h4 class="header"><@u.message "initiative.currentAuthor.name" /></h4>
                <span>${initiative.currentAuthor.firstNames!""} ${initiative.currentAuthor.lastName!""}</span>
            </div>
            
            <div class="column last">
                <h4 class="header"><@u.message "initiative.currentAuthor.homeMunicipality" /></h4>
                <span><@u.text initiative.currentAuthor.homeMunicipality /></span>
            </div>
        </div>
    
        <div class="input-block-content">
            <@u.systemMessage path="initiative.currentAuthorDetails.description" type="info" showClose=false />
        </div>
                        
        <@f.currentAuthor path="initiative.currentAuthor" realPath=initiative.currentAuthor mode="full" />                
        
        <#if !managementSettings.editFull>
            <@buttons />
        </#if>
    </div>
</#macro>
      
<#--
 * organizerDetails
 *
 * Initiative organizers EDIT block
 * Initiative organizers: initiator, representative, reserve
 * Uses validateForm() javascript-function 
 *
 * Prints help-texts and validation errors in this block
 -->
<#macro organizerDetails>
    <div class="input-block cf">
        <#-- Form helptexts for NOSCRIPT-users. -->
        <noscript>
            <div class="no-js-help-container">
                <@f.helpText "helpOrganizers" "noscript" />
            </div>
        </noscript>
        
        <#-- Block-edit errors -->
        <#if managementSettings.editMode != EditMode.FULL>
        <div class="input-block-content no-top-margin">
            <@u.errorsSummary path="initiative.*" prefix="initiative."/>
        </div>
        </#if>
    
        <div class="input-block-content no-top-margin">
            <@u.systemMessage path="initiative.organizerDetails.description" type="info" showClose=false/>
        </div>
       
        <#if (initiative.initiators?size + initiative.representatives?size + initiative.reserves?size > 0) >
            <div class="input-block-content">
                <@f.organizers path="initiator" />
                <@f.organizers path="representative" />
                <@f.organizers path="reserve" cssClass="last" />
            </div>
        <#elseif managementSettings.editMode == EditMode.FULL>
            <div class="input-block-content">
            
                <div class="column"> 
                    <h4 class="input-header no-top-margin"><@u.message "initiative.initiatorInvitations" /></h4>
                    <ul class="organizers no-style">
                        <li id="user-role-initiator" class="user-role">${currentUser.firstNames!""} ${currentUser.lastName!""}</li>  
                    </ul>
                </div>
                <div class="column"> 
                    <h4 class="input-header no-top-margin"><@u.message "initiative.representativeInvitations" /></h4>
                    <ul class="organizers no-style">
                        <li id="user-role-representative" class="user-role">${currentUser.firstNames!""} ${currentUser.lastName!""}</li>  
                    </ul>
                </div>
                <div class="column last"> 
                    <h4 class="input-header no-top-margin"><@u.message "initiative.reserveInvitations" /></h4>
                    <ul class="organizers no-style">
                        <li id="user-role-reserve" class="user-role" style="display:none;">${currentUser.firstNames!""} ${currentUser.lastName!""}</li>  
                    </ul>
                </div>
            
            </div>
        </#if>
        
        <div class="input-block-content">  
            <h4><@u.message "initiative.inviteMoreOrganizers" /></h4>
        </div>
        
        <div class="input-block-content no-top-margin">            
            <@f.helpText "helpOrganizers" />
            
            <div class="initiative-authors-area">
                <@f.invitations path="initiator" />
                <@f.invitations path="representative" />
                <@f.invitations path="reserve" cssClass="last" />
            </div>
        </div>
        
        <#if !managementSettings.editFull>
            <@buttons validateBlock="organizers" />
        </#if>
    </div>
</#macro>

</#escape> 

