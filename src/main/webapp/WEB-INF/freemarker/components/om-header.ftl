<#import "utils.ftl" as u />

<div class="om-header">

	<ul class="header-links">
	    <li>
	    	<a href="http://www.${(locale == "sv")?string("demokrati","demokratia")}.fi"><@u.message "otherServices.demokratia"/></a>
	    </li>
	    <li>
	    	<a class="active" href="${urls.baseUrl}/${locale}"><@u.message "otherServices.initiative"/></a>
	    </li>
	    <li>
	    	<a href="https://www.kuntalaisaloite.fi/${locale}"><@u.message "otherServices.municipalityinitiative"/></a>
	    </li>
	    <li>
	    	<a href="http://www.lausuntopalvelu.fi/"><@u.message "otherServices.lausuntopalvelu"/></a>
	    </li>
	    <li>
	    	<a href="http://www.otakantaa.fi/${locale}-FI"><@u.message "otherServices.otaKantaa"/></a>
	    </li>
	</ul>

</div>