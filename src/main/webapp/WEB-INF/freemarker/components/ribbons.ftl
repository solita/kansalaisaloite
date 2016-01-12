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

    <#assign blueColor>ribbon-default</#assign> <#-- BLUE gradient -->
    <#assign redColor>ribbon-red</#assign> <#-- RED gradient -->
    <#assign turquoiseColor>ribbon-turquoise</#assign> <#-- TURQUOISE gradient for PROD SITE's infos -->

    <#assign ribbonColor>${(appEnvironment == "test")?string(redColor,blueColor)}</#assign>
</#macro>


<#macro topRibbon>
    <@assignSiteVars/>

    <#if (appEnvironment == "prod")>
        <#-- no ribbon in production -->

    <#else>
        <div class="debug-ribbon top fixed ${ribbonColor}">
            <div class="container">
                <#if (appEnvironment == "test")>
                    Tämä on oikeusministeriön kansalaisaloiteverkkopalvelun testisivusto, joka on kehityksen alla.
                    <br/>Huom! Testausten helpottamiseksi aikarajat on säädetty lyhyemmiksi. Kannatusilmoitusten keräyksen kesto 2 päivää ja muut 1 päivä.
                    <br/>Palveluun syötetyt tiedot voidaan tyhjentää ilman erillistä varoitusta.
                <#else>
                    Tämä on oikeusministeriön kansalaisaloiteverkkopalvelun kehitysversio.
                    <br/>Sovellus voi toimia arvaamattomasti ja sisältää päätöntä dataa.
                </#if>
            </div>
        </div>
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

    <#if (appEnvironment == "prod")>
        <#-- no ribbon in production -->

    <#else>
        <div class="debug-ribbon bottom ${ribbonColor}">
            <div class="container">
                Eri tiloissa olevia valmiita testialoitteita pääset luomaan täältä: <a href="${urls.testDataGeneration()}">Testidatan generointi</a>
            </div>
        </div>
    </#if>
</#macro>

</#escape> 