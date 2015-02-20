<#import "utils.ftl" as u />

<div class="om-header">
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
		    	<a href="http://www.${(locale == "sv")?string("ungasideer","nuortenideat")}.fi"><@u.message "otherServices.nuortenIdeat"/></a>

		    <li>
		    	<a href="http://www.otakantaa.fi/${locale}-FI"><@u.message "otherServices.otaKantaa"/></a>

		    <li>
		    	<a href="http://www.vaalit.fi/${locale}"><@u.message "otherServices.vaalit"/></a>

		    <li>
		    	<a href="http://www.yhdenvertaisuus.fi/${(locale == "sv")?string("vad_da_equality_fi","")}"><@u.message "otherServices.yhdenvertaisuus"/></a>
		</ul>
	</div>

</div>