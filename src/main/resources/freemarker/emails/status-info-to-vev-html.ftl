<#import "../components/email-layout-html.ftl" as el />
<#import "../components/email-utils.ftl" as eu />
<#import "../components/email-blocks.ftl" as eb />

<#escape x as x?html>

<#-- Use statusTitleHTMLFi and statusInfoHTMLFi for HTML message -->
<#include "../includes/status-info.ftl" />

<@el.emailHtml "status-info-to-vev" "Jee okei">

    <#-- FINNISH -->
    <@eb.emailTemplate "fi" "Jotain tekstiä">
        <@eb.initiativeDetails "fi" "html" />
        <#noescape>"Otsake</#noescape>
        <@eb.emailBottom "fi" "html" />
    </@eb.emailTemplate>
    
    <#-- SWEDISH -->      
    <@eb.emailTemplate "sv" statusTitleHTMLSv!"">
        <@eb.initiativeDetails "sv" "html" />
        <#noescape>${statusInfoHTMLSv!""}</#noescape>
        <@eb.emailBottom "sv" "html" />
    </@eb.emailTemplate>
    
</@el.emailHtml>

</#escape> 