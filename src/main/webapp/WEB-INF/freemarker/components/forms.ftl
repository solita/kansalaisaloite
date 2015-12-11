<#import "/spring.ftl" as spring />
<#import "utils.ftl" as u />

<#escape x as x?html> 

<#--
 * localizePath
 *
 * Localize path if it is a hash
 *
 * @param value is the spring.status.value of the current field
-->
<#macro localizePath path value="">
    <#if value?? && value?is_hash>
        <#global pathLocale>${path+"."+locale}</#global>
        <#global pathAltLocale>${path+"."+altLocale}</#global>
    <#else>
        <#global pathLocale>${path}</#global>
        <#global pathAltLocale="" />
    </#if>
</#macro>

<#--
 * showError
 *
 * Validation errors that are binded in specific field
 * 
 * @param cssClass for custom styling. Multiple classes are separated with a space
-->
<#macro showError cssClass="">
    <#if spring.status.error>
        <#list spring.status.errorMessages as error>
            <#noescape><div id="${spring.status.expression}" class="system-msg msg-error ${cssClass}">${error!''}</div></#noescape>
        </#list>
    </#if>
</#macro>


<#--
 * formLabel
 *
 * For-attribute needs to be fixed so that it would correspond with input's name (srping.status.expression).
 *  
 * @param path the name of the field to bind to
 * @param required generates an icon and can be used in JS-validation
 * @param optional additional information for label
-->
<#macro formLabel path required optional>
    <#assign labelKey = fieldLabelKey(path) />
    <#if spring.status.value?? && spring.status.value?is_hash>
        <#assign forAttr = spring.status.expression+"."+locale />
    <#else>
        <#assign forAttr = spring.status.expression />
    </#if>
            
    <label class="input-header" for="${forAttr!""}">
        <@u.message labelKey /> <#if required != ""><@u.icon type="required" size="small" /></#if>
        <#if optional>
            <span class="instruction-text"><@u.message labelKey + ".optional" /></span>
        </#if>
    </label>

    <#nested/>
</#macro>

<#--
 * textField
 *
 * Textfield with label and alternative language -option
 *
 * @param path the name of the field to bind to
 * @param required generates an icon and can be used in JS-validation
 * @param optional additional information for label
 * @param cssClass for custom styling. Multiple classes are separated with a space
 * @param attributes for example maxlength=\"7\"
 * @param fieldType text, date, email, ...
 * 
-->
<#macro textField path required optional cssClass="" attributes="" maxLength="" fieldType="text">
    <@spring.bind path />  
    <@localizePath path spring.status.value />
    
    <@formLabel pathLocale required optional>
        <@spring.bind pathLocale />
        <@showError />
        <@spring.formInput pathLocale, 'class="'+cssClass+'" maxlength="'+maxLength+'" '+attributes fieldType />
    </@formLabel>

    <#if pathAltLocale != "">
        <div class="alt-lang pullup ${altLangClass}">
            <@u.message pathAltLocale/>
            <@spring.bind pathAltLocale />
            <@showError />
            <@spring.formInput pathAltLocale, 'class="'+cssClass+'"' fieldType />
        </div>
    </#if>
</#macro>

<#--
 * simpleTextField
 *
 * Simple TextField without label and alternative language -option
 *
 * @param path the name of the field to bind to
 * @param cssClass for custom styling. Multiple classes are separated with a space
 * @param attributes for example 'maxlength="7"'
 * @param cssErrorClass for customization of the error-message
 * 
-->
<#macro simpleTextField path cssClass="" attributes="" maxLength="" cssErrorClass="">
    <@spring.bind path />  
    <@localizePath path spring.status.value />
    
    <@showError cssClass=cssErrorClass />
    
    <@spring.formInput pathLocale, 'class="'+cssClass+'" maxlength="'+maxLength+'" '+attributes />
</#macro>

<#--
 * textarea
 *
 * @param path the name of the field to bind to
 * @param required generates an icon and can be used in JS-validation
 * @param optional additional information for label
 * @param cssClass for custom styling. Multiple classes are separated with a space
 * @param maxLength HTML5 attribute for max length
-->
<#macro textarea path required optional cssClass="" maxLength="">
    <@spring.bind path />
    <@localizePath path spring.status.value />  

    <@formLabel pathLocale required optional>
        <@spring.bind pathLocale />
        <@showError />
        <@spring.formTextarea pathLocale, 'class="'+cssClass+'" maxlength="'+maxLength+'" ' />
    
    </@formLabel>
    
    <#if pathAltLocale != "">
        <div class="alt-lang ${altLangClass}">
            <@warningText "linkWarning" />
            <@u.message pathAltLocale/>
            <@spring.bind pathAltLocale />
            <@showError />
            <@spring.formTextarea pathAltLocale, 'class="'+cssClass+'" maxlength="'+maxLength+'" ' />
        </div>
    </#if>
</#macro>

<#--
 * formCheckbox
 *
 * @param path the name of the field to bind to
 * @param attributes an additional string of arbitrary tags or text to be included within the HTML tag itself
 * @param prefix for custom messages
-->
<#macro formCheckbox path attributes="" prefix="">
    <@spring.bind path />
    <#assign id="${spring.status.expression}">
    <#assign isSelected = spring.status.value?? && spring.status.value?string=="true">
    <input type="hidden" name="_${id}" value="on"/>   
    
    <@showError />
     
    <label class="inline">
        <input type="checkbox" id="${id}" name="${id}"<#if isSelected> checked="checked"</#if> ${attributes}/>
        <@u.message (prefix!="")?string(prefix+".",'')+path /><br />
    </label>
</#macro>


<#--
 * radiobutton
 *
 * @param path the name of the field to bind to
 * @param options a Map of all the available values that can be selected from in the input field.
 * @param required generates an icon and can be used in JS-validation
 * @param attributes an additional string of arbitrary tags or text to be included within the HTML tag itself
-->
<#macro radiobutton path options required="" attributes="">
    <@spring.bind path />  
 
    <div class="input-header">
        <@u.message path /><#if required != ""> <@u.icon type="required" size="small" /></#if>
    </div>
    
    <#list options?keys as value>
        <label class="inline">
            <input type="radio" id="${options[value]}" name="${spring.status.expression}" value="${value}"
                <#if spring.stringStatusValue == value>checked="checked"</#if> ${attributes}
            <@spring.closeTag/>
            <@u.message "${options[value]}" /><br />
        </label>
    </#list>
</#macro>

<#--
 * helpText
 *
 * Help texts for the edit-form
 *
 * @param path the name of the field to bind to
 * @param noscript use 'noscript' if message for noscript-users
-->
<#macro helpText path noscript="" href="">
    <#if noscript!="noscript">
    <span class="icon-small help trigger-tooltip hidden-nojs" title="<@u.message path+".iconTitle" />" data-name="${path!""}"></span>
    </#if>    
    <div class="input-block-extra ${path!""} js-hide ${(noscript="noscript")?string('','hidden-nojs')}">
        <div class="input-block-extra-content">
            <h4><@u.message path+".title" /></h4>
            <@u.messageHTML key=path+".description" args=[href] />
        </div>
    </div>
</#macro>

<#--
 * warningText
 *
-->
<#macro warningText path noscript="" href="">
    <div class="input-block-extra-warning ${path!""} js-hide ${(noscript="noscript")?string('','hidden-nojs')}">
        <div class="input-block-extra-content">
            <h4><@u.message path+".title" /></h4>
            <@u.messageHTML key=path+".description" args=[href] />
            <ul></ul>
        </div>
    </div>
</#macro>

<#--
 * invitations
 *
 * Organizer Invitations
 * 
 * @param path the name of the field to bind to (initiator, representative, reserve)
 * @param cssClass for styling. Multiple classes are separated with a space
-->
<#macro invitations path cssClass="">
    <#assign invitationsPath>${"initiative."+path+"Invitations"}</#assign>    
    <#assign sentInvitationsList=initiative[path + "SentInvitations"] />
    <@spring.bind invitationsPath />
    <#assign invitationsList=spring.status.value />

    <div class="column email-field ${cssClass}" data-name="${spring.status.expression}" data-sent-invitations="${sentInvitationsList?size}">
        
        <div class="input-header no-top-margin">
            <@u.message invitationsPath />
        </div>
        
        <#assign invitationIndex=invitationsList?size />
        <div class="invitation-data" data-index="${invitationIndex}"></div>
    
        <#if (sentInvitationsList?size > 0)>
            <#assign args = [sentInvitationsList?size]/>
            <span class="instruction-text"><@u.message "initiative.invitationsWaiting" args /></span>
        <#elseif (initiative.sentInvitations?size > 0)>
            <span class="instruction-text"><@u.message "initiative.noSentInvitations" /></span>
        </#if>
        
        <#list invitationsList as invitation>
           <@simpleTextField path="${invitationsPath}[${invitation_index}].email" cssClass="medium js-hide" cssErrorClass="small" maxLength=InitiativeConstants.INVITATION_EMAIL_MAX?string("#") />
        </#list>
    
        <noscript>
            <@simpleTextField path="${invitationsPath}[${invitationIndex}].email" cssClass="medium" cssErrorClass="small" maxLength=InitiativeConstants.INVITATION_EMAIL_MAX?string("#") />
        </noscript>    
    </div>
</#macro>

<#--
 * organizers
 *
 * Organizers' list in edit-form
 *
 * @param cssClass for styling. Multiple classes are separated with a space
-->
<#macro organizers path cssClass="">
    <#assign organizerList=initiative[path + "s"] />

    <div class="column ${cssClass}"> 
        <h4 class="input-header no-top-margin"><@u.message "initiative."+path+"Invitations" /></h4>
        
        <ul class="organizers no-style">
            <#list organizerList as organizer>
                <li id="user-role-${path}" class="user-role">${organizer.firstNames!""} ${organizer.lastName!""} <#if initiative.state != InitiativeState.DRAFT><#if organizer.confirmed??><@u.icon type="confirmed" size="small" /><#else><@u.icon type="unconfirmed" size="small" /></#if></#if></li>
            </#list>
            <#-- We need to be able to toggle all user roles in DRAFT-mode as user clicks checboxes in currentAuthor-block -->
            <#if managementSettings.editMode == EditMode.FULL && !initiative.currentAuthor[path]>
                <li id="user-role-${path}" class="user-role" style="display:none;">${currentUser.firstNames!""} ${currentUser.lastName!""}</li>
            </#if>
        </ul>
    </div>
</#macro>


<#--
 * currentAuthor
 *
 * Prints the edit block for current author's roles and contact details
 *
 * @param path is a string "initiative.currentAuthor"
 * @param realPath is a variable initiative.currentAuthor
 * @param mode is either 'modal' or 'full'
 * @param prefix for custom messages
 * @param cssClass for styling. Multiple classes are separated with a space
-->
<#macro currentAuthor path realPath="" mode="" prefix="" cssClass="">

    <#if !managementSettings?? || (managementSettings?? && managementSettings.allowEditOrganizers)>
        <#assign editRoles=true />
    <#elseif realPath != "">
        <#assign editRoles=false />
    </#if>

    <div class="input-block-content top-margin">
        <div class="input-header">
            <@u.message "initiative.currentAuthor.myRoles" /> <#if editRoles?? && editRoles><@u.icon type="required" size="small" /></#if>
        </div>
        <div class="initiative-own-roles-area validate-roles">    
            <#if mode == "full">
                <@f.helpText "helpOwnRoles" />
            </#if>
            
            <@spring.bind path+".roles" />
            <@showError cssClass="" />
         
            <#if editRoles?? && editRoles>
                <@formCheckbox path=path+".initiator" prefix=prefix />
                <@formCheckbox path=path+".representative" prefix=prefix />
                <@formCheckbox path=path+".reserve" prefix=prefix />
            <#elseif !editRoles>
                <ul class="no-style">
                    <#if realPath["initiator"]><li><@u.message "initiative.currentAuthor.initiator" /></li></#if>
                    <#if realPath["representative"]><li><@u.message "initiative.currentAuthor.representative" /></li></#if>
                    <#if realPath["reserve"]><li><@u.message "initiative.currentAuthor.reserve" /></li></#if>
                </ul>
            </#if>
        </div> 
    </div>
        
    <div class="input-block-content">
        <div class="input-header">
            <@u.message "initiative.currentAuthor.contactDetails" /> <@u.icon type="required" size="small" />
        </div>
        
        <#if mode == "full">
            <@f.helpText "helpOwnDetails" />
        </#if>
        
        <@spring.bind path+".contactInfo" />
        <@f.showError />
        
        <div class="initiative-own-details-area">
            <div class="column col-1of2">
                <label>
                    <@u.message "initiative.currentAuthor.contactInfo.email" />
                    <@spring.formInput path+'.contactInfo.email', 'class="medium" maxlength="'+InitiativeConstants.AUTHOR_EMAIL_MAX?string("#")+'"' />
                </label>
                
                <label>
                    <@u.message "initiative.currentAuthor.contactInfo.phone" />
                    <@spring.formInput path+'.contactInfo.phone', 'class="medium" maxlength="'+InitiativeConstants.AUTHOR_PHONE_MAX?string("#")+'"' />
                </label>
            </div>
            
            <div class="column col-1of2 last">
                <label>
                    <@u.message "initiative.currentAuthor.contactInfo.address" />
                    <#--<@spring.formTextarea path+'.contactInfo.address', 'class="address-field noresize" maxlength="'+InitiativeConstants.AUTHOR_ADDRESS_MAX?string("#")+'"' />-->
                    <#-- NOTE: maxlength 1024 will cause an error -->
                    <@spring.formTextarea path+'.contactInfo.address', 'class="address-field noresize" maxlength="1000"' />
                </label>
            </div>
        
        </div>
    </div>
        
</#macro>


</#escape> 
