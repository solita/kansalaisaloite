<#import "../components/layout.ftl" as l />
<#import "../components/utils.ftl" as u />

<#if omUser>
    <#import "../components/wysiwyg-editor.ftl" as editor />
    <#global editorStyles><@editor.styles /></#global>
</#if>

<#escape x as x?html>

<#assign pageTitle>${content.subject}</#assign>

<#--
 * Navigation for subpages (public view)
 *
 * @param map is the hashMap for navigation items
 * @param titleKey is for navigation block title
-->
<#macro navigation map titleKey="">
    <#if titleKey?has_content><h3 class="navi-title"><@u.message titleKey /></h3></#if>
    <ul class="navi block-style">
        <#list map as link>
            <li ><a href="${urls.help(link.uri)}" <#if link.uri == helpPage>class="active"</#if>>${link.subject}</a></li>
        </#list>
    </ul>
</#macro>

<#--
 * Layout parameters for HTML-title
 *
 * @param page is for example "page.help.general.title"
 * @param pageTitle used in HTML title.
-->
<@l.main "page.help" pageTitle!"">
    <#if omUser>
    <div class="editor-buttons bootstrap-icons hidden-nojs">
      <a href="${urls.helpEdit(helpPage)}" class="btn" href="#"><@u.message "editor.switchToEdit" />&nbsp;&nbsp;<i class="icon-chevron-right"></i></a>
    </div>
    </#if>

    <div class="container-fluid">
        <div class="col-sm-3">
            <@navigation categoryLinksMap['MAIN'] "" />
            <@navigation categoryLinksMap['KANSALAISALOITE_FI'] "help.service.title" />
            <@navigation categoryLinksMap['KANSALAISALOITE'] "help.general.title" />
        </div>
        <div class="col-sm-9">
            <h1>${content.subject!""}</h1>
            <#noescape>${content.content!""}</#noescape>
        </div>
    </div>

</@l.main>
</#escape>

