<#import "/spring.ftl" as spring />
<#import "components/layout.ftl" as l />
<#import "components/utils.ftl" as u />
<#import "components/iframe.ftl" as i />

<#escape x as x?html>

<#--
 * Layout parameters for HTML-title and navigation.
 *
 * @param page is "page.iframeGenerator"
 * @param pageTitle can be assigned as custom HTML title
-->
<#assign page="page.iframeGenerator" />

<#--
 * Get default initiative id from URL
-->
<#assign initiativeId="" />
<#if RequestParameters['id']??>
	<#assign initiativeId=RequestParameters['id']?number />
</#if>


<#--
 * Set default data for iFrame
 *
 * Default to NO initiative
 *
 * [initiativeId, locale, limit, width, height, showTitle]
-->
<#assign iFrameDefaults = [initiativeId, locale, 600, 600, false]>

<#--
 * Set min and maximum values for the generated iFrame
 *
 * [min width, max width, min height, max height]
-->
<#assign iFrameBounds = [220, 1000, 300, 1500]>


<@l.main "page.iframeGenerator" pageTitle!"">


    <h1><@u.message "page.iframeGenerator" /></h1>

    <p><@u.message "iframeGenerator.instruction.description" /></p>
    <p><@u.message "iframeGenerator.instruction.links" /></p>

    <h3><@u.message "iframeGenerator.instruction.setup.title" /></h3>

    <p><@u.message "iframeGenerator.instruction.setup.description" /></p>
    <ul>
        <li><@u.message key="iframeGenerator.instruction.setup.initiative" /></li>
        <li><@u.message key="iframeGenerator.instruction.setup.lang" /></li>
        <li><@u.message key="iframeGenerator.instruction.setup.width" args=[iFrameBounds[0], iFrameBounds[1]] /></li>
        <li><@u.message key="iframeGenerator.instruction.setup.height" args=[iFrameBounds[2], iFrameBounds[3]] /></li>
    </ul>
    <p><@u.message "iframeGenerator.instruction.setup.refresh" /></p>

    <div class="view-block first">
        <@i.initiativeIframeGenerator iFrameDefaults iFrameBounds />
    </div>

</@l.main>

</#escape>