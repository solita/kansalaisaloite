<#import "../components/email-layout-html.ftl" as el />
<#import "../components/email-utils.ftl" as eu />
<#import "../components/email-blocks.ftl" as eb />

<#escape x as x?html>

<#assign titleFi="Kannatusilmoitusten määrän vahvistuspyyntö" />
<#assign titleSv="Ansökan om bekräftelse av stödförklaringar" />

<@el.emailHtml "notification-to-vrk" titleFi>

    <#-- FINNISH -->
    <@eb.emailTemplate "fi" titleFi>
        <@eb.initiativeDetails "fi" "html"/>
        
        <p>Pyyntö kannatusilmoitusten vahvistukseksi</p>
        <p>Aloitteelle on kerätty kannatusilmoituksia yhteensä ${initiative.totalSupportCount} kpl, joista kansalaisaloite.fi-palvelussa ${initiative.supportCount}. Tässä erässä on ${batchSize} uutta kannatusilmoitusta.</p>
        <p>Siirry käsittelemään aloitetta:<br/><@eu.link viewUrlFi /></p>
        
        <@eb.abstract "fi" "html" true/>
    </@eb.emailTemplate>
    
    <#-- SWEDISH -->      
    <@eb.emailTemplate "sv" titleSv>
        <@eb.initiativeDetails "sv" "html" />
        
        <p>Ansökan om bekräftelse av stödförklaringar.</p>        
        <p>Initiativet har sammanlagt ${initiative.totalSupportCount} stödröster, varav ${initiative.supportCount} på webbtjänsten medborgarinitiativ.fi.  I det här skedet finns det ${batchSize} nya stödförklaringar.</p>
        <p>Övergå till att behandla initiativet: <@eu.link viewUrlSv /></p>
    
        <@eb.abstract "sv" "html" true/>
    </@eb.emailTemplate>

</@el.emailHtml>

</#escape> 