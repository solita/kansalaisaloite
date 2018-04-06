<#import "utils.ftl" as u />

<script type="text/javascript">
    window.showSuperSearch = false;
	<#if superSearchEnabled?? && superSearchEnabled>
		window.showSuperSearch = true;
		window.superSearchUrl = "${urls.getSuperSearchUrl()}";
	</#if>
</script>

<#if superSearchEnabled?? && superSearchEnabled>
<style>
    #searchIframe {
        width: 100%;
        border: none;
        height: 45px;
        z-index: 999;
        position:absolute;
        top :0px;
    }
    .super-search-placeholder {
        position:relative;
        width: 100%;
        height: 45px;
        display: none;
    }

</style>

<script type="text/javascript">
<#if urls.getSuperSearchBaseUrl()??>
    window.onmessage = function(e){
        if (e.origin === "${urls.getSuperSearchBaseUrl()}") {
            var newHeight = e.data;
            if (newHeight) {
                $("#searchIframe").height(newHeight);
            }
        }
    };
</#if>
</script>

<div class="super-search-placeholder"></div>

</#if>
<div class="om-header">
    <div id="headerNav" class="header-nav">
        <ul>
            <li>
                <a href="http://www.${(locale == "sv")?string("demokrati","demokratia")}.fi"><@u.message "otherServices.demokratia"/></a>

            <li>
                <a class="active"  href="https://www.kansalaisaloite.fi/${locale}"><@u.message "otherServices.initiative"/></a>

            <li>
                <a href="${urls.baseUrl}/${locale}"><@u.message "otherServices.municipalityinitiative"/></a>

            <li>
                <a href="http://www.lausuntopalvelu.fi/${(locale == "sv")?string("SV","FI")}"><@u.message "otherServices.lausuntopalvelu"/></a>

            <li>
                <a href="http://www.${(locale == "sv")?string("ungasideer","nuortenideat")}.fi"><@u.message "otherServices.nuortenIdeat"/></a>

            <li>
                <a href="http://www.otakantaa.fi/${locale}-FI"><@u.message "otherServices.otaKantaa"/></a>

            <li>
                <a href="http://www.vaalit.fi/${locale}"><@u.message "otherServices.vaalit"/></a>
        </ul>
    </div>

</div>
