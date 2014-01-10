<#import "../components/layout.ftl" as l />
<#import "../components/utils.ftl" as u />

<#escape x as x?html>
<@l.error "error.404.title">

    <#--
     *  We do not always know which 404-error page to show.
     *  User could eg. try something like 'www.kansalaisaloite.fi/svs'
    -->

    <h1><@u.message "error.404.title"/></h1>
    <p><@u.messageHTML key="error.404.description" args=[urls.baseUrl] /></p>

    <br/><br/>

    <h2><@u.message "error.404.title.sv"/></h2>
    <p><@u.messageHTML key="error.404.description.sv" args=[urls.baseUrl] /></p>

</@l.error>
</#escape>