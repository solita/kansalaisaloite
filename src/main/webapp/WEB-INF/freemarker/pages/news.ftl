<#import "../components/layout.ftl" as l />
<#import "../components/utils.ftl" as u />
<#import "../components/wysiwyg-editor.ftl" as editor />

<#if omUser>
    <#import "../components/wysiwyg-editor.ftl" as editor />
    <#global editorStyles><@editor.styles /></#global>
</#if>

<#escape x as x?html>

<#assign pageTitle>${content.subject!""}</#assign>
 
<@l.main "page.news">

    <#if omUser>
        <div class="editor-buttons bootstrap-icons hidden">
            <a href="${urls.helpEdit(pageUri)}" class="btn" href="#"><@u.message "editor.switchToEdit" />&nbsp;&nbsp;<i class="icon-chevron-right"></i></a>
        </div>
    </#if>

    <h1>${content.subject!""}</h1>

   <#noescape>${content.content!""}</#noescape>
    
</@l.main>
</#escape> 

