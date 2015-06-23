<#import "/spring.ftl" as spring />

<#escape x as x?html> 

<#-- 
 * localizedMap
 * 
 * Prints the value of the map with current locale.
 * If the value is undefined with current locale, alternative locale is used, otherwise empty string.
 * IMPORTANT!
 *   Escape characters first and then replace line-breaks with <br/> and unescape returned value  
 *
 * @param localizedMap is the localization key 
-->
<#macro text localizedMap>
<@compress single_line=true>
    <#assign escapedText>${(localizedMap[locale]!localizedMap[altLocale]!"")}</#assign>
    <#noescape>${escapedText?replace('\n\n','</p><p>')?replace('\n','<br/>')}</#noescape>
</@compress>
</#macro>

<#--
* comment
*
* Print non-localized string value.
*
*
* @Param text is a the non localized string
-->
<#macro comment text>
    <@compress single_line=true>
        <#assign escapedText>${text}</#assign>
        <#noescape>${escapedText?replace('\n\n','</p><p>')?replace('\n','<br/>')}</#noescape>
    </@compress>
</#macro>

<#-- 
 * message
 * 
 * Macro for generating PlainText messages. NOTE that these messages are escaped.
 *
 * @param key is the localization key
 * @param args is the list of arguments for the message 
-->
<#macro message key args=[]>
    <@spring.messageArgsText key args "[" + key + "]"/>
</#macro>

<#-- 
 * messageHTML
 * 
 * Macro for generating HTML messages. NOTE that these messages are unescaped.
 *
 * @param key is the localization key
 * @param args list of arguments for the message 
-->
<#macro messageHTML key args=[]><#noescape>${springMacroRequestContext.getMessage(key, args,"[" + key + "]", false)}</#noescape></#macro>

<#-- 
 * enumDescription
 * 
 * Prints a localized message for an enum.
 * For example enum with name 'FlowState' and value 'DRAFT' generates key 'FlowState.DRAFT'.
 *
 * @param key is the value of the enum, for example 'DRAFT'
 * @param args is the list of arguments for the message 
-->
<#macro enumDescription key args=[]>
    <@message key.class.simpleName + "." + key args />        
</#macro>

<#-- 
 * localDate
 * 
 * Prints date in the format defined in messages.properties.
 * For example 'dd.MM.yyyy'
 *
 * @param date 
-->
<#macro localDate date="">
    <@compress single_line=true>
        <#if date?string!="">
            <#if date?is_hash>
                ${date.toString(springMacroRequestContext.getMessage('date.format'))!""}
            <#else>
                ${date?string(springMacroRequestContext.getMessage('date.format'))!""}
            </#if>
        </#if>
    </@compress>
</#macro>

<#-- 
 * dateTime
 * 
 * Prints date time in the format defined in messages.properties.
 * For example 'dd.MM.yyyy HH:mm:ss'
 *
 * @param date 
-->
<#macro dateTime date="">
    <@compress single_line=true>
        <#if date?string!="">
            <#if date?is_hash>
                ${date.toString(springMacroRequestContext.getMessage('date-time.format'))!""}
            <#else>
                ${date?string(springMacroRequestContext.getMessage('date-time.format'))!""}
            </#if>
        </#if>
    </@compress>
</#macro>

<#-- 
 * icon
 * 
 * Macro for generating an icon with tooltip
 *
 * @param type for example 'help' or 'unconfirmed' is used as a class-name and localization key for the tooltip
 * @param size for example 'small' or 'large' 
-->
<#macro icon type="" size=""><span class="icon-${size} ${type} trigger-tooltip" title="<@message "icon."+type />"></span></#macro>

<#-- 
 * link
 * 
 * Macro for generating a link. Main use for external links.
 * Typed values are first escaped and then assigned to link so that link can be safely escaped.
 *
 * @param href is the target URI
 * @param label is the text for the link
 * @param rel for example 'external'. External link is opened in new browser tab with JavaScript. 
-->
<#macro link href label rel="" blockStyle=false>
    <#assign escapedLabel>${label}</#assign>
    <#assign escapedHref>${href}</#assign>
   
    <#if rel == "external">
        <#noescape><a href="${escapedHref?replace('&amp;','&')}" class="${rel} ${blockStyle?string('ellipsis','')} trigger-tooltip" rel="${rel}" title="<@message "common.link.external"/>">${escapedLabel?replace('&amp;','&')}</a></#noescape>
    <#else>
        <#noescape><a href="${escapedHref?replace('&amp;','&')}" title="${escapedLabel}" ${blockStyle?string('class="ellipsis"','')}>${escapedLabel?replace('&amp;','&')}</a></#noescape>
    </#if>
</#macro>

<#-- 
 * image
 * 
 * Macro for generating a link. Main use for external links.
 * Typed values are first escaped and then assigned to link so that link can be safely escaped.
 *
 * @param src is the image source
 * @path is the default path for the image
 * @param alt is the alt text for the image
 * @param cssClass is for custom styling 
-->
<#macro image src path="img/content-images" alt="" cssClass="">
    <#assign escapedAlt>${alt}</#assign>
    <#assign escapedSrc>${src}</#assign>
    
    <#if escapedAlt == "">
        <#assign escapedAlt = escapedSrc />
    </#if>
    
    <#noescape><img src="${urls.baseUrl}/${path}/${escapedSrc}?version=${resourcesVersion}" alt="${escapedAlt}" title="${escapedAlt}" class="${cssClass}" /></#noescape>
</#macro>

<#--
 * systemMessage
 *
 * System message for showing messages like info, success, ...
 *
 * @param path the name of the field to bind to
 * @param type is the type of the message (error, info, success, warning)
 * @param cssClass for custom styling. Multiple classes are separated with space.
 *        Css-class 'auto-hide' removes message in a defined interval if enabled in JavaScript.
 * @param showClose show button for closing message
-->
<#macro systemMessage path type="" cssClass="" showClose=true args=[]>
    <#if type!="">
        <div class="system-msg msg-${type} ${cssClass}">
            <@messageHTML path args /><#if showClose><span class="close-msg">x</span></#if>
        </div>
    <#else>
        <#assign msgType=path?split('.') />
        <div class="system-msg msg-${msgType[0]} ${cssClass}">
            <@messageHTML path args /><#if showClose><span class="close-msg">x</span></#if>
        </div>
    </#if>
</#macro>

<#--
 * systemMessageHTML
 *
 * System message HTML is like system message but is more versatile.
 * NOTE that HTML is unescaped.
 *
 * @param html is the content inside the message wrapper
 * @param noscript creates NOSCRIPT-tag.
-->
<#macro systemMessageHTML html type noscript="" cssClass="">
<#noescape>
    ${(noscript="noscript")?string('<noscript>','')}
    <div class="system-msg msg-${type} ${cssClass}">
        <#if type == 'info' || type == 'summary'><span class="icon-small arrow-right-3 floated"></span></#if>
        ${html}
    </div>
    ${(noscript="noscript")?string('</noscript>','')}
</#noescape>
</#macro>

<#--
 * requestMessage
 *
 * Request message uses systemMessage macro to show messages.
 * Message types are SUCCESS and WARNING (INFO and ERROR might be implemented later on).
 * requestMessage.modal returns true if requestMessage is modal.
 * Case-specific extra-data is appended for modals.
 *
 * @param messageList
-->
<#macro requestMessage messageList>
    <#list messageList as requestMessage>
        <#if requestMessage.modal>
            <#global requestMessageModalTitle = requestMessage />
        
            <#-- NOTE: successMessageModalHTML needs to be global for modals. -->
            <#global requestMessageModalHTML>
                <@compress single_line=true>
                    <@messageHTML requestMessage />
                    
                    <#-- Save initiative and send invitations -->
                    <#if requestMessage == "success.save-and-send-invitations">
                        <a class="small-button gray close hidden"><@message "modal.close" /></a>
                    </#if>
                    <#-- Voted successfully -->
                    <#if requestMessage == "success.confirm-vote">
                        <a href="${urls.logout()}" class="small-button gray"><span class="small-icon logout"><@message "common.logout" /></span></a><a href="${urls.baseUrl}/${locale}" class="small-button gray push close"><@message "modal.continueBrowsing" /></a>
                    </#if>
                    
                </@compress>
            </#global>
            <@systemMessageHTML requestMessageModalHTML "info" "noscript" />
            
        <#else>
            <#if requestMessage.type == RequestMessageType.SUCCESS>
                <#--
                    Use cssClass 'auto-hide' if message is removed automatically with a delay.
                    Requires function to be enabled from JavaScript.
                -->
                <@systemMessage path=requestMessage type="success" cssClass="" />
            <#else>
                <@systemMessage path=requestMessage type=requestMessage.type?lower_case />
            </#if>
        </#if>
    </#list>
</#macro>

<#--
 * frontpageRequestMessage
 *
 * Request message uses systemMessage macro to show messages.
 * Message types are SUCCESS and WARNING (INFO and ERROR might be implemented later on).
 *
 * @param messageList
-->
<#macro frontpageRequestMessage messageList>
    
    <#list messageList as requestMessage>
        <div class="front-system-msg js-close-msg-target reveal-msg">
            <div class="container">
    
                <#if requestMessage.type == RequestMessageType.SUCCESS>
                    <@systemMessage path=requestMessage type="success" cssClass="wide" showClose=true />
                <#else>
                    <#-- NOT in use for frontpage at this moment. Some styling is needed -->
                    <@systemMessage path=requestMessage type=requestMessage.type?lower_case />
                </#if>

            </div>
        </div>
    </#list>
            
</#macro>

<#--
 * shortenText
 *
 * First chapter of the initiative proposal. Summary length is defined in SummaryMethod.java
 * 
 * @param locale 'fi' or 'sv'
 * @param type 'text' or 'html'
 -->
<#macro shortenText localizedMap type="text">   
<@compress single_line=true>
    <#assign inputText>${summaryMethod(localizedMap[locale]!localizedMap[altLocale]!"")}</#assign>
    <#if type == "html">
        <#noescape>${inputText?replace('\n','<br/>')}</#noescape>
    <#else>
        <#noescape>${inputText}</#noescape>
    </#if>
</@compress>
</#macro>

<#--
 * modalTemplate
 * 
 * General template for all modals.
 * #modal-container -div is moved to layout.ftl
 * 
 * Uses JsRender: https://github.com/BorisMoore/jsrender
-->
<#macro modalTemplate>    
    <script id="modal-template" type="text/x-jsrender">
        
        <div class="initiative-modal">
            <div class="modal-head">
                <span class="modal-title">{{:title}}</span>
                <a class="close" title="<@message "action.close" />">x</a>
            </div>
            <div class="modal-content">
                {{:content}}
            </div>
        </div>
        
    </script>
</#macro>
 

<#--
 * organizers
 * 
 * Prints organizers-list in initiative's view-template. Edit-template is provided in forms.ftl as invitations-macro.
 *
 * @param path the name of the field to bind to
 * @param cssClass is for column styling. Multiple classes are separated with a space.
-->
<#macro organizers path cssClass="">
    <#assign organizerList=initiative[path + "s"] />
    <#if initiative.currentAuthor??>
        <#assign invitationsList=initiative[path + "Invitations"] />    
        <#assign sentInvitationsList=initiative[path + "SentInvitations"] />
    </#if>

    <div class="initiative-content-row ${cssClass}">
        <h4 class="header"><@message "initiative."+path+"s" /></h4>
        <#list organizerList as organizer>
            <div class="column ${((organizer_index + 1) % 3 == 0)?string("last","")}">
                <p>
                    <#if initiative.currentAuthor?? && !organizer.confirmed??><@icon type="unconfirmed" size="small" /> </#if>${organizer.firstNames!""} ${organizer.lastName!""}
                    <#if (votingInfo?? && votingInfo.votingInProggress) || (currentUser.om || (initiative.state != 'ACCEPTED' && initiative.state != 'DONE'))>
                        <#if (organizer.dateOfBirth)??><@localDate organizer.dateOfBirth/>, </#if>
                        <@text organizer.homeMunicipality />
                        <br/>
                        <#if (organizer.contactInfo.address)??>${organizer.contactInfo.address}<br/></#if>
                        <#if (organizer.contactInfo.email)??><@scrambleEmail organizer.contactInfo.email!"" /><br/></#if>
                        <#if (organizer.contactInfo.phone)??>${organizer.contactInfo.phone!""}<br/></#if>
                    </#if>
                </p>  
            </div>
            <#if !organizer_has_next><br class="clear" /></#if>
        </#list>
        <#if organizerList?size == 0><@message "initiative."+path+"s.empty" /><br/><br/></#if>
        <#if initiative.currentAuthor?? && (sentInvitationsList?size > 0)>
            <#assign args = [sentInvitationsList?size]/>
            <@message "initiative.invitationsSent" args /><br />
        </#if>
        <#if initiative.currentAuthor?? && (invitationsList?size > 0)>
            <#assign args = [invitationsList?size]/>
            <@message "initiative.invitationsPending" args />
        </#if>
    </div>
    
</#macro>

<#--
 * errorsSummary
 *     
 * Errors summary prints the validation error list 
 * 
 * @param path the name of the field to bind to
 * @param prefix can be used for messages
-->
<#macro errorsSummary path prefix>
    <#global hasErrors=false />
    <@spring.bind path />
    <#if spring.status.error>
        <#global hasErrors=true />
        <div id="errors-summary" class="system-msg msg-error expose">
            <@message "formError.summary.title" />
            <ul>
                <#list spring.status.errors.allErrors as error>
                    <li><#if error.field??><strong><a href="#${error.field}"><@message prefix+fieldLabelKey(error.field) /></a>:</strong> </#if><#noescape>${springMacroRequestContext.getMessage(error)}</#noescape></li>
                </#list>
            </ul>
        </div>
    </#if>
</#macro>

<#--
 * scrambleEmail
 *     
 * Scrambles email address. 
 * Uses the method 2 explained in:
 * http://techblog.tilllate.com/2008/07/20/ten-methods-to-obfuscate-e-mail-addresses-compared/
 * 
 * @param email is the email address
-->
<#macro scrambleEmail email>
${email?split("@")?first}&#064;<span class="hide">null</span>${email?split("@")?last}
</#macro>

<#--
 * searchLink
 *     
 * Generates search filter or sort link.
 * Compress to remove whitespaces 
 *
 * @param parameter: parameters for filtering or sorting
 * @param cssClass: active
 * @param count: initiative count, do not display if not defined
-->
<#macro searchLink parameter cssClass="" count=-1 tooltip=true>
<@compress single_line=true>
    <a href="${urls.search()}${searchParameters[parameter]}" class="${cssClass}<#if tooltip> trigger-tooltip</#if>" <#if tooltip>title="<@message "searchParameters."+parameter+".tooltip" />"</#if>><@message "searchParameters."+parameter /><#if (count > -1)><span class="count">${count}</span></#if></a>
</@compress>
</#macro>

<#--
 * faqItem
 *  
 * Produces FAQ item.   
 *  
 * @param item number of FAQ item
 * @param topic optional topic for the question set
-->
<#macro faqItem item topic="">
    <#if topic!="0">
        <div class="content-block-header view faq">
            <h2><@message "page.help.faq.topic"+topic /></h2>
        </div>
    </#if>
    <div class="faq-item view-block">
        <h4 id="faq-item-${item}">${item}. <@message "page.help.faq.q"+item /></h4>
        <p><@message "page.help.faq.a"+item /></p>
    </div>
</#macro>

</#escape>