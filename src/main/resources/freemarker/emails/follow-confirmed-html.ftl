<#import "../components/email-layout-html.ftl" as el />
<#import "../components/email-utils.ftl" as eu />
<#import "../components/email-blocks.ftl" as eb />

<#escape x as x?html>

    <#assign titleFi="Olet tilannut aloitteen sähköpostitiedotteet" />
    <#assign titleSv="SV Olet tilannut aloitteen sähköpostitiedotteet" />

    <@el.emailHtml "" titleFi>

    <#-- FINNISH -->
        <@eb.emailTemplate "fi" titleFi>
            <@eb.initiativeDetails "fi" "html"/>

        <p>Olet tilannut aloitteen sähköpostitiedotteet</p>
        <p>Saat viestin sähköpostiisi, kun aloite etenee tai päättyy.</p>

            <@eb.abstract "fi" "html" />
        </@eb.emailTemplate>

    <#-- SWEDISH -->
        <@eb.emailTemplate "sv" titleSv>
            <@eb.initiativeDetails "sv" "html" />

        <p>SV Olet tilannut aloitteen sähköpostitiedotteet</p>
        <p>SV Saat viestin sähköpostiisi, kun aloite etenee tai päättyy.</p>

            <@eb.abstract "sv" "html" />
        </@eb.emailTemplate>

    </@el.emailHtml>

</#escape>