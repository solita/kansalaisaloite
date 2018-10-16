<#import "../components/layout.ftl" as l />
<#import "../components/utils.ftl" as u />

<#escape x as x?html>
    <@l.error "error.initiative-deleted.title">

    <#--
     *  We do not always know which 404-error page to show.
     *  User could eg. try something like 'www.kansalaisaloite.fi/svs'
    -->

    <h1><@u.message "error.initiative-deleted.title"/></h1>
    <p><@u.messageHTML key="error.initiative-deleted.description"/></p>

    <br/><br/>

    <h1><@u.message "error.initiative-deleted.title.sv"/></h1>
    <p><@u.messageHTML key="error.initiative-deleted.description.sv"/></p>

    </@l.error>
</#escape>