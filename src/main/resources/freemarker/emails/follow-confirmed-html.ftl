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

        <p>Saat tietoa aloitten etenemisestä sähköpostiisi siihen asti, kunnes se on toimitettu eduskunnan käsittelyyn.</p>

            <@eb.emailBottom "fi" "html" />
        </@eb.emailTemplate>

    <#-- SWEDISH -->
        <@eb.emailTemplate "sv" titleSv>
            <@eb.initiativeDetails "sv" "html" />

        <p>SV Saat tietoa aloitten etenemisestä sähköpostiisi siihen asti, kunnes se on toimitettu eduskunnan käsittelyyn.</p>

            <@eb.emailBottom "sv" "html" />
        </@eb.emailTemplate>

    </@el.emailHtml>

</#escape>