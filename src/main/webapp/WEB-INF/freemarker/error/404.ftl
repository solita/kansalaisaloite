<#import "../components/layout.ftl" as l />
<#import "../components/utils.ftl" as u />

<#escape x as x?html> 
<@l.error "error.404.title">

    <h1><@u.message "error.404.title"/></h1>
    <p><@u.messageHTML key="error.404.description" args=[urls.baseUrl] /></p>
 
</@l.error>
</#escape>