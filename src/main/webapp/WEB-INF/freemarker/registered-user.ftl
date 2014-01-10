<#import "components/layout.ftl" as l />
<#import "components/utils.ftl" as u />

<#escape x as x?html> 
<@l.main "page.registered-user">
<h2>${currentUser.firstNames!""} ${currentUser.lastName!""}</h2>

<#if currentUser.id??>
    User ID: ${currentUser.id}
<#else>
    <form action="${urls.myAccount()}" method="POST">
        <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
        <input type="submit" value="Rekisteröidy"/>
    </form>
</#if>
</@l.main>
</#escape> 
