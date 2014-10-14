<#import "/spring.ftl" as spring />
<#import "utils.ftl" as u />
<#import "forms.ftl" as f />

<#escape x as x?html>

<#--><#assign iframeUrl = urls.iframeBaseUrl+"/iframe" />-->
<#assign iframeUrl = urls.baseUrl+"/iframe" />

<#--
 * initiativeIframe (Leijuke)
 *
 * Generates iframe source code for displaying initiative's support vote details
 * which is use for NOSCRIPT users
 *
 * @param embed desides whether HTML is escaped for embed code or not espaced for pre-code.
 * @param id unique id for iframe
 * @param width sets iframe width in pixels. Use plain number.
 * @param height sets iframe height in pixels. Use plain number.
 * @param initiativeId sets the initiative
 * @param showTitle is the option for showing initiative title and date
-->
<#macro initiativeIframe initiativeId id="kansalaisaloite-leijuke" embed=true width="600" height="700" showTitle=true><@compress single_line=true>
    <#assign iframeHTML><iframe id="${id}"
            frameborder="0"
            scrolling="no"
            src="${iframeUrl}/${locale}/${initiativeId}?showTitle=${showTitle?string('true','false')}"
            width="${width}"
            height="${height}">
    </iframe></#assign>

    <#if embed>
        <#noescape>${iframeHTML}</#noescape>
    <#else>
        <pre>${iframeHTML}</pre>
    </#if>
</@compress></#macro>


<#--
 * initiativeIframeGenerator (Leijuke-generaattori)
 *
 * Interactive functionality for generating iFrames on the fly.
 *
 * @param defaults is an array for default values [width, height]
 * @param bounds is an array for min and max values [min limit, max limit, min width, max width, min height, max height]
-->
<#macro initiativeIframeGenerator defaults bounds>
	<#assign digit = "\\d+" />

    <h2><@u.message "iframeGenerator.settings.title" /></h2>
    <div class="hidden">
        <div class="column col-1of5">
            <label for="initiativeId" class="input-header">
                <@u.message "iframeGenerator.initiativeId" />
            </label>
            <input type="text" maxlength="6" class="x-small" value="${defaults[0]}" name="initiativeId" id="initiativeId" pattern="${digit}" />
        </div>

        <div class="column col-1of10 push-width">
            <label for="width" class="input-header">
                <@u.message "iframeGenerator.width" />
            </label>
            <input type="text" maxlength="4" class="x-small" value="${defaults[2]}" name="width" id="width" pattern="${digit}" />
        </div>
        <div class="column col-1of10">
            <label for="height" class="input-header">
                <@u.message "iframeGenerator.height" />
            </label>
            <input type="text" maxlength="4" class="x-small" value="${defaults[3]}" name="height" id="height" pattern="${digit}"  />
        </div>
        <div class="column col-1of3">
            <label for="height" class="input-header">
                <@u.message "iframeGenerator.showTitle" />
            </label>
            <div class="input-placeholder">
                <label class="inline">
            		<input type="checkbox" name="showTitle" <#if defaults[4]>checked="checked"</#if> id="showTitle" class="no-float" />
            	</label>
            </div>
        </div>
        <div class="column col-1of5">
            <div class="input-header">
                <@u.message "iframeGenerator.lang" />
            </div>

            <div class="input-placeholder">
                <label class="inline">
                    <input type="radio" name="language" value="${locale}" checked="checked" class="no-float" /><@u.message "iframeGenerator.lang.locale" />
                </label>
                <label class="inline push">
                    <input type="radio" name="language" value="${altLocale}" class="no-float" /><@u.message "iframeGenerator.lang.altLocale" />
                </label>
            </div>
        </div>

        <br class="clear" />

        <div class="input-header"><@u.message "initiative" /></div>
        <p id="initiative-name"><@u.message "iframeGenerator.chooseInitiative" /></p>

        <p><a href="#" class="js-reset-iframe"><@u.message "iframeGenerator.reset" /></a></p>

        <div id="iframe-container"></div>

        <script type="text/javascript">
        	(function(window){
	            window.hasIFrame = true;
	            window.defaultData = {
	                initiativeId:   "${defaults[0]}",
	                lang:           "${defaults[1]}",
	                width:          "${defaults[2]}",
	                height:         "${defaults[3]}",
	                showTitle:      ${defaults[4]?string('true','false')},
                    api:            "${urls.initiatives()}"
	            };

	            window.bounds = {
	                min : {
	                    width:  ${bounds[0]},
	                    height: ${bounds[2]}
	                },
	                max : {
	                    width:  ${bounds[1]?string("0")},
	                    height: ${bounds[3]?string("0")}
	                }
	            };

	            window.iFrameLoaded = function(id, loaderId){
	                document.getElementById(id).style.display="block";
	                document.getElementById(loaderId).style.display="none";
	            }
            }(window));
        </script>

        <script id="iframe-template" type="text/x-jsrender">
            <h2><@u.message "iframeGenerator.preview.title" /></h2>

            <div id="iframe-placeholder" style="width:{{:width}}px; height:{{:height}}px;"><span class="loader"></span></div>
            <iframe id="kansalaisaloite-leijuke"
                    frameborder="0"
                    scrolling="no"
                    src="${iframeUrl}/{{:lang}}/{{:initiativeId}}?showTitle={{:showTitle}}"
                    width="{{:width}}"
                    height="{{:height}}" onload="iFrameLoaded('kansalaisaloite-leijuke', 'iframe-placeholder')">
            </iframe>

            <#assign iFrameSrc>
            <@compress single_line=true>
                <iframe id="kansalaisaloite-leijuke"
                    frameborder="0"
                    scrolling="no"
                    src="${iframeUrl}/{{:lang}}/{{:initiativeId}}?showTitle={{:showTitle}}"
                    width="{{:width}}"
                    height="{{:height}}">
                </iframe>
            </@compress>
            </#assign>

            <h2><@u.message "iframeGenerator.source.title" /></h2>

            <pre id="iframe-source">${iFrameSrc}</pre>
        </script>
    </div>

    <noscript>
        <h3><@u.message "iframeGenerator.nojs.title" /></h3>

        <p><@u.message "iframeGenerator.nojs.description" /></p>

        <@i.initiativeIframe initiativeId="1" id="kansalaisaloite-leijuke" embed=true width=defaults[2] height=defaults[3] />

        <br/><br/>
        <h2><@u.message "iframeGenerator.source.title" /></h2>
        <p><@u.message "iframeGenerator.nojs.source" /></p>

        <@i.initiativeIframe initiativeId="1" id="kansalaisaloite-leijuke" embed=false width=defaults[2] height=defaults[3] />
    </noscript>
</#macro>


</#escape>