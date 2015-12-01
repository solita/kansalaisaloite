<#escape x as x?html>

<#--
 * ribbons.ftl contains top and bottom ribbons to distinguish different test environments, 
 * normally there are no visible ribbons for the production environment 
 *
 * Assign ribbon colors by variables "blueColor" and "redColor"
 * class "ribbon-default" produces blue background
 * class "ribbon-red" produces red background
-->


<#macro assignSiteVars>
    <#assign prodSite>https://www.kansalaisaloite.fi</#assign>
    
    <#assign prodTestSite>https://testi.kansalaisaloite.fi</#assign>
    <#assign betaIpSite>https://80.69.172.30</#assign>
    <#assign testSite>https://80-69-172-30.fi-hel1.host.upcloud.com</#assign>

    <#assign devIpSite>https://80.69.174.88</#assign>
    <#assign devSite>https://80-69-174-88.fi-hel1.host.upcloud.com</#assign>

    <#assign localSite>http://localhost:8090</#assign>

    <#-- For testing ribbons locally:
    <#assign betaIpSite>${localSite}</#assign>
    <#assign localSite>https://localhost:8443x</#assign>
    -->
    
    <#assign blueColor>ribbon-default</#assign> <#-- BLUE gradient -->
    <#assign redColor>ribbon-red</#assign> <#-- RED gradient -->
    <#assign turquoiseColor>ribbon-turquoise</#assign> <#-- TURQUOISE gradient for PROD SITE's infos -->

    <#assign ribbonColor>${(urls.baseUrl?starts_with(betaIpSite) || urls.baseUrl?starts_with(prodTestSite) || urls.baseUrl?starts_with(testSite))?string(redColor,blueColor)}</#assign>
</#macro>


<#macro topRibbon>
    <@assignSiteVars/>

    <#if urls.baseUrl?starts_with(prodSite)>
        <#-- no ribbon in production -->
        
    <#elseif urls.baseUrl?starts_with(localSite)>
        <#-- no ribbon in localhost (should look like production) -->
        
    <#else>
        <div class="debug-ribbon top fixed ${ribbonColor}">
            <div class="container">
                <#if urls.baseUrl?starts_with(testSite) || urls.baseUrl?starts_with(prodTestSite)>
                    Tämä on oikeusministeriön kansalaisaloiteverkkopalvelun testisivusto, joka on kehityksen alla.
                    <br/>Huom! Testausten helpottamiseksi aikarajat on säädetty lyhyemmiksi. Kannatusilmoitusten keräyksen kesto 2 päivää ja muut 1 päivä.
                    <br/>Palveluun syötetyt tiedot voidaan tyhjentää ilman erillistä varoitusta.
                <#elseif urls.baseUrl?starts_with(devSite) >
                    Tämä on oikeusministeriön kansalaisaloiteverkkopalvelun kehitysversio.
                    <br/>Sovellus voi toimia arvaamattomasti ja sisältää päätöntä dataa.
                </#if>
            </div>
        </div>
        <div class="container relative"><div class="test-padge <#if infoRibbon??>has-info-ribbon</#if>"> </div></div>
    </#if>
    
    <#if infoRibbon??>
        <div class="info-ribbon top static ${turquoiseColor}">
            <div class="container">
                <#noescape>${infoRibbon}</#noescape>
            </div>
        </div>
    </#if>    
</#macro>


<#macro bottomRibbon>
    <@assignSiteVars/>

    <#if urls.baseUrl?starts_with(prodSite)>
        <#-- no ribbon in production -->
        
    <#elseif urls.baseUrl?starts_with(localSite)>
        <#-- no ribbon in localhost (should look like production) -->

    <#elseif urls.baseUrl?starts_with(devSite) || urls.baseUrl?starts_with(testSite)>
        <div class="debug-ribbon bottom ${ribbonColor}">
            <div class="container">
                Eri tiloissa olevia valmiita testialoitteita pääset luomaan täältä: <a href="${urls.testDataGeneration()}">Testidatan generointi</a>
            </div>
        </div>
    </#if>
</#macro>

</#escape> 