<#import "utils.ftl" as u />

<div id="aloite-links">
    <a href="http://www.${(locale == "sv")?string("demokrati","demokratia")}.fi"><@u.message "otherServices.demokratia"/></a><span class="hide"> | </span>
    <a class="active" href="${urls.baseUrl}/${locale}"><@u.message "otherServices.initiative"/></a><span class="hide"> | </span>
    <a href="https://www.kuntalaisaloite.fi/${locale}"><@u.message "otherServices.municipalityinitiative"/></a>
    <a href="http://www.lausuntopalvelu.fi/"><@u.message "otherServices.lausuntopalvelu"/></a><span class="hide"> | </span>
    <a href="http://www.otakantaa.fi/${locale}-FI"><@u.message "otherServices.otaKantaa"/></a>
</div>