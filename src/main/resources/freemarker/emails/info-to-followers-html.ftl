<#import "../components/email-layout-html.ftl" as el />
<#import "../components/email-utils.ftl" as eu />
<#import "../components/email-blocks.ftl" as eb />

<#escape x as x?html>


    <#assign titleHTMLFi>Otsikko</#assign>
    <#assign titleHTMLSv>Otsikko SV</#assign>

    <@el.emailHtml "info-to-followers" statusTitleHTMLFi!"">

    <#-- FINNISH -->
        <@eb.emailTemplate "fi" titleHTMLFi>
            <@eb.initiativeDetails "fi" "html" />
            <#noescape>${titleHTMLFi}</#noescape>
            <@eb.emailBottom "fi" "html" />
        </@eb.emailTemplate>

    <#-- SWEDISH -->
        <@eb.emailTemplate "sv" titleHTMLSv>
            <@eb.initiativeDetails "sv" "html" />
            <#noescape>${titleHTMLSv}</#noescape>
            <@eb.emailBottom "sv" "html" />
        </@eb.emailTemplate>

    </@el.emailHtml>

</#escape>