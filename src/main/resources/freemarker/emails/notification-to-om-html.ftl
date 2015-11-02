<#import "../components/email-layout-html.ftl" as el />
<#import "../components/email-utils.ftl" as eu />
<#import "../components/email-blocks.ftl" as eb />

<#escape x as x?html>

<#assign titleFi="Kansalaisaloite tarkistettavaksi" />
<#assign titleSv="Ett medborgarinitiativ för granskning" />

<@el.emailHtml "notification-to-om" titleFi>

    <#-- FINNISH -->
    <@eb.emailTemplate "fi" titleFi>
        <@eb.initiativeDetails "fi" "html" />
        
        <p>Siirry käsittelemään aloitetta: <@eu.link viewUrlFi /></p>
        
        <@eb.abstract "fi" "html" />
    </@eb.emailTemplate>
    
    <#-- SWEDISH -->      
    <@eb.emailTemplate "sv" titleSv>
        <@eb.initiativeDetails "sv" "html" />
    
        <p>Övergå till att behandla initiativet: <@eu.link viewUrlSv /></p>
    
        <@eb.abstract "sv" "html" />
    </@eb.emailTemplate>

</@el.emailHtml>

</#escape> 