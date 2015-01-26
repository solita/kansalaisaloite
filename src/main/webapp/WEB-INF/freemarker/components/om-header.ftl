<#import "utils.ftl" as u />

<div class="om-header">	
	<#if currentUser??>
		<div class="logged-in-info">
	        <#-- Authenticated = Logged in -->
	        <#if currentUser.authenticated>
	            <a href="#" class="user-name dropdown-toggle">${currentUser.firstNames} ${currentUser.lastName}<span class="icon-small settings"></span></a>
	            <ul id="user-menu" class="dropdown-menu user-menu">
	                <#-- OM search view - lists all initiatives -->
	                <#if currentUser.om><li><a href="${urls.searchOmView()}"><@u.message "user.omSearchView"/></a></li></#if>
	                <#-- Registered = Authenticated and has initiatives (except OM/VRK-users) -->
	                <#if currentUser.registered><li><a href="${urls.searchOwnOnly()}"><@u.message "user.myInitiatives"/></a></li></#if>
	                <li><a href="${urls.logout()}" ><@u.message "common.logout"/></a></li>
	            </ul>
	        <#else>
	            <a href="${urls.login(springMacroRequestContext.requestUri)}" title="<@u.message "common.login"/>" class="header-tool-link login"><@u.message "common.login"/></a>
	        </#if>
	    </div>
    </#if>
    
    <div id="headerNav" class="header-nav">
	    <ul>
		    <li>
		    	<a href="http://www.${(locale == "sv")?string("demokrati","demokratia")}.fi"><@u.message "otherServices.demokratia"/></a>
		    
		    <li>
		    	<a class="active" href="${urls.baseUrl}/${locale}"><@u.message "otherServices.initiative"/></a>

		    <li>
		    	<a href="https://www.kuntalaisaloite.fi/${locale}"><@u.message "otherServices.municipalityinitiative"/></a>
		    
		    <li>
		    	<a href="http://www.lausuntopalvelu.fi/"><@u.message "otherServices.lausuntopalvelu"/></a>
		    
		    <li>
		    	<a href="http://www.otakantaa.fi/${locale}-FI"><@u.message "otherServices.otaKantaa"/></a>
		    
		    <li>
		    	<a href="http://www.${(locale == "sv")?string("ungasideer","nuortenideat")}.fi"><@u.message "otherServices.nuortenIdeat"/></a>
		    
		    <li>
		    	<a href="http://www.vaalit.fi/${locale}"><@u.message "otherServices.vaalit"/></a>
		    
		    <li>
		    	<a href="http://www.yhdenvertaisuus.fi/${(locale == "sv")?string("vad_da_equality_fi","")}"><@u.message "otherServices.yhdenvertaisuus"/></a>
		</ul>
	</div>

</div>