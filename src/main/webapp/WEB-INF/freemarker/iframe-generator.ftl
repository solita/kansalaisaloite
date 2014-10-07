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
<#assign page="page.find" />


<#--
 * Set default data for iFrmae
 *
 * [initiativeId, locale, limit, width, height, showTitle]
-->
<#assign iFrameDefaults = [1, locale, 600, 600, false]>

<#--
 * Set min and maximum values for the generated iFrame
 *
 * [min width, max width, min height, max height]
-->
<#assign iFrameBounds = [220, 890, 300, 1000]>


<@l.main "page.iframeGenerator" pageTitle!"">

    <h1><@u.message "page.iframeGenerator" /></h1>

    <p><@u.message "iframeGenerator.instruction.description" /></p>
    <p><@u.message "iframeGenerator.instruction.links" /></p>
    <p><@u.message "iframeGenerator.instruction.update" /></p>

    <h3><@u.message "iframeGenerator.instruction.setup.title" /></h3>

    <p><@u.message "iframeGenerator.instruction.setup.description" /></p>
    <ul>
        <li><@u.message key="iframeGenerator.instruction.setup.municipality" /></li>
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