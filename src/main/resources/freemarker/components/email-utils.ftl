<#import "/spring.ftl" as spring />
<#import "utils.ftl" as u />

<#escape x as x?html> 

<#-- 
 * localizedMap
 * 
 * Prints the value of the map with current locale. 
 * Line breaks are replaced with HTML line-breaks (<br/>).
 * NOTE that returned value is unescaped.
 *
 * @param localizedMap is the localization key 
-->
<#macro text localizedMap locale="fi">
<@compress single_line=true>
    <#assign escapedText>${(localizedMap[locale]!"")}</#assign>
    <#noescape>${escapedText?replace('\n','<br/>')}</#noescape>
</@compress>
</#macro>


<#--
 * shortenText
 *
 * First chapter of the initiative proposal. Summary length is defined in SummaryMethod.java
 * 
 * @param locale 'fi' or 'sv'
 * @param type 'text' or 'html'
 -->
<#macro shortenText localizedMap locale="fi" type="text">   
<@compress single_line=true>
    <#assign altLocale="sv" />
    <#if locale == "sv"><#assign altLocale="fi" /></#if>
    <#assign inputText>${summaryMethod(localizedMap[locale]!localizedMap[altLocale]!"")}</#assign>
    <#if type == "html">
        <#noescape>${inputText?replace('\n','<br/>')}</#noescape>
    <#else>
        ${inputText}
    </#if>
</@compress>
</#macro>

<#--
 * button
 *
 * Generates a stylish button for HTML emails.
 * 
 * @param message is the label of the button
 * @param url is the URI of the button
 * @param color gray or green. Define more when needed.
 -->
<#macro button message url color="">
    <#if color="green">
        <#assign bgColor="#76b522" />
        <#assign borderColor="#387d0e" />
        <#assign textColor="#ffffff" />
    <#else>
        <#-- Gray as default -->
        <#assign bgColor="#e2e2e2" />
        <#assign borderColor="#cccccc" />
        <#assign textColor="#ffffff" />
    </#if>

    <table border="0" cellspacing="0" cellpadding="0">        
      <tr>
        <td style="background:${bgColor};">
        <a href="${url}" style="color:${textColor}; text-decoration:none">
            <span style="background:${bgColor}; border:1px solid ${borderColor}; font-size:13px; font-family:Arial, sans-serif;">
                &nbsp;&nbsp;&nbsp;${message}&nbsp;&nbsp;&nbsp;
            </span>
        </a>
        </td>
      </tr>
    </table>

</#macro>

<#--
 * titleBlock
 *
 * Generates a colored block for the title
 * NOT in use ATM. IF used restyle for the new visuals
 * 
 * @param title is the text for the title block
 -->
<#macro titleBlock title="">
<#noescape>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">        
        <tr style="color:#fff;">
            <th width="20" style="background:#aaa; border-top-left-radius:5px;"><@spacer "0" /></th>
            <th style="background:#aaa; text-align:left;"><h4 style="font-size:13px; margin:1em 0; font-family:Arial,sans-serif;">${title}</h4></th>
            <th width="20" style="background:#aaa; border-top-right-radius:5px;"><@spacer "0" /></th>
        </tr>
    </table>
</#noescape>
</#macro>

<#--
 * link
 *
 * Generates a link so that we could ensure that link styles wouldn't differ in various email-clients.
 * 
 * @param title is the label of the link
 * @param title is the URI of the link
 *
 * FIXME: Known issue: Gmail changes this anchor to a span and then wraps the span with it's own anchor
 -->
<#macro link url title="">
    <#noescape><a href="${url}" style="color:#0089f2; text-decoration:none;">${(title!="")?string(title,url)}</a></#noescape>
</#macro>

<#--
 * spacer
 *
 * Generates a spacer with defined height.
 * 
 * @param height is the height of the spacer
 * @param cssStyle is for customizing CSS-styles
 -->
<#macro spacer height cssStyle="">
    <div style="min-height:${height}px; font-size:${height}px; line-height:${height}px; ${cssStyle}">&#160;</div>
</#macro>

<#-- 
 * localDate
 * 
 * Prints date in the format defined in messages.properties.
 * For example 'dd.MM.yyyy'
 *
 * @param date 
-->
<#macro localDate date="" lang="">
<@compress single_line=true>
    <#if lang == "sv">
        ${date.toString(dateFormatSv)!""}
    <#else>
        ${date.toString(dateFormatFi)!""}
    </#if>
</@compress>
</#macro>

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

</#escape> 
