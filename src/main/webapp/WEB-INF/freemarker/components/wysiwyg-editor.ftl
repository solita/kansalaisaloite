<#import "utils.ftl" as u />

<#escape x as x?html> 

<#--
 * Editor styles
 *
 * TODO: Resources could be optimized. On the other hand these are only form OM users in editing mode.
-->
<#macro styles>
    <link rel="stylesheet" type="text/css" href="${urls.baseUrl}/js/editor/lib/css/bootstrap-custom.css?version=${resourcesVersion}" />
    <link rel="stylesheet" type="text/css" href="${urls.baseUrl}/js/editor/lib/css/prettify.css?version=${resourcesVersion}" />
    <link rel="stylesheet" type="text/css" href="${urls.baseUrl}/js/editor/src/bootstrap-wysihtml5.css?version=${resourcesVersion}" />
</#macro>

<#--
 * Editor scripts
 *
 * TODO: Resources could be optimized. On the other hand these are only form OM users in editing mode.
-->
<#macro scripts>
    <script type="text/javascript" src="${urls.baseUrl}/js/editor/src/underscore-min.js?version=${resourcesVersion}"></script>
    <#-- wysihtml5-0.3.0.fix.min.js has a custom fix for image title-attribute to support scandinavian characters -->
    <script type="text/javascript" src="${urls.baseUrl}/js/editor/lib/js/wysihtml5-0.3.0.fix.min.js?version=${resourcesVersion}"></script>
    <script type="text/javascript" src="${urls.baseUrl}/js/editor/lib/js/prettify.js?version=${resourcesVersion}"></script>
    <script type="text/javascript" src="${urls.baseUrl}/js/editor/lib/js/bootstrap.min.js?version=${resourcesVersion}"></script>
    <script type="text/javascript" src="${urls.baseUrl}/js/editor/src/bootstrap-wysihtml5.js?version=${resourcesVersion}"></script>
    <script type="text/javascript" src="${urls.baseUrl}/js/editor/src/custom-wysihtml5.js?version=${resourcesVersion}"></script>
    <script type="text/javascript" src="${urls.baseUrl}/js/editor/src/locales/bootstrap-wysihtml5.fi-FI.js?version=${resourcesVersion}"></script>
    <script type="text/javascript" src="${urls.baseUrl}/js/editor/src/aloitepalvelu-wysihtml5.js?version=${resourcesVersion}"></script>
    <!--[if lt IE 9]>
        <script src='http://html5shiv.googlecode.com/svn/trunk/html5.js'></script>
    <![endif]-->
</#macro>

<#--
 * Editor buttons & Request messages
 *
 * Bootstrap buttons for actions: Edit / Save / Cancel / Publish
 * Bootstrap modal for confirmation dialogs
-->
<#macro buttons>
    <#if requestMessages?? && (requestMessages?size > 0)>
        <@requestMessage requestMessages />
    </#if>

    <div class="editor-buttons left view-mode bootstrap-icons hidden-nojs">
        <a class="btn" href="${urls.help(helpPage)}"><i class="icon-chevron-left"></i>&nbsp;&nbsp;<@u.message "editor.returnToView" /></a>
    </div>

    <div class="editor-buttons bootstrap-icons edit-mode hidden-nojs js-hide">
        <a class="btn btn-success js-wysihtml5-save js-submit" data-form="wysihtml5-form" href="#"><i class="icon-ok icon-white"></i>&nbsp;&nbsp;<@u.message "editor.save-draft" /></a>
        <a class="btn" href="${springMacroRequestContext.requestUri}"><@u.message "editor.cancel-edit" /></a>
    </div>

    <div class="editor-buttons bootstrap-icons view-mode hidden-nojs">
        <a class="btn btn-primary js-wysihtml5-edit" href="#"><i class="icon-pencil icon-white"></i>&nbsp;&nbsp;<@u.message "editor.edit" /></a>
        
        <span class="dropdown">
            <a href="#" data-toggle="dropdown" class="btn dropdown-toggle"><i class="icon-cog"></i>&nbsp;&nbsp;<@u.message "editor.actions" />&nbsp;&nbsp;<b class="caret"></b></a>
            <ul class="dropdown-menu">
                <li>
                    <a class="js-upload-image" href="#"><@u.message "editor.upload-image" /></a>
                </li>
                <li>
                    <a class="js-publish-draft" href="#"><@u.message "editor.publish-draft" /></a>
                </li>
                <li>
                    <a class="js-restore-published" href="#"><@u.message "editor.restore-published" /></a>
                </li>
                <li>
                    <a href="${urls.contentEditorHelp()}" target="_blank"><@u.message "editor.content-editor-help" /></a>
                </li>
            </ul>
        </span>

        <@confirmModal "upload-image" UrlConstants.ACTION_EDITOR_UPLOAD_IMAGE />
        <@confirmModal "publish-draft" UrlConstants.ACTION_EDITOR_PUBLISH_DRAFT />
        <@confirmModal "restore-published" UrlConstants.ACTION_EDITOR_RESTORE_PUBLISHED />
    </div>
</#macro>

<#--
 * WYSIHTML5 form
 *
 * TODO: Finalize height - could be adjusted automatically. Test and Finalize placeholder.
 *
 * @param content is a hashmap containing content, subject, modifier and modified date
-->
<#macro form content>
    <form action="${springMacroRequestContext.requestUri}" method="POST" id="wysihtml5-form">
        <#if CSRFToken??><input type="hidden" name="CSRFToken" value="${CSRFToken}"/></#if>
        <input type="hidden" name="${UrlConstants.ACTION_EDITOR_SAVE_DRAFT}" value="true" />
        
        <input type="text" id="subject" name="subject" value="${content.subject!""}" maxlength="100" placeholder="<@u.message "editor.addTitlePlaceholder" />">

        <#assign placeHolder><@u.message "editor.addContentPlaceholder" /></#assign>
        <textarea id="wysihtml5-editor" spellcheck="false" wrap="off" autofocus placeholder="${placeHolder}" style="height:1000px;" name="content">
            <#noescape>${content.content!""}</#noescape>
        </textarea>
    </form>
</#macro>

<#--
 * confirmModal
 *
 * Uses Bootstrap's modal for confirmation dialogs 
 *
 * @param name binds the form with the controller 
 * @param id is the id of the confirmation form
-->
<#macro confirmModal id name>

    <div class="bootstrap-${id} bootstrap-icons modal fade">
        <div class="modal-header"> 
            <a class="close" data-dismiss="modal">&times;</a> 
            <h4><@u.message "editor."+id /></h4> 
        </div> 
        <div class="modal-body"> 
            <p><@u.messageHTML "editor."+id+".description" /></p>
            
            <#if name == UrlConstants.ACTION_EDITOR_UPLOAD_IMAGE>
                <form id="form-upload-image" enctype="multipart/form-data" action="${urls.images()}" method="POST">
                    <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
                    <input type="file" name="image">
                </form>
            <#else>
                <form action="${springMacroRequestContext.requestUri}" method="post" id="form-${id}">
                    <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
                    <input type="hidden" name="${name}" value="true" />
                </form>
            </#if>
        </div> 
        <div class="modal-footer">
            <a class="btn btn-primary js-submit" data-form="form-${id}"  href="#"><i class="icon-ok icon-white"></i>&nbsp;&nbsp;<@u.message "editor."+id /></a>
            <a href="#" class="btn" data-dismiss="modal"><@u.message "editor.cancel" /></a>
        </div>
    </div>

</#macro>

<#--
 * requestMessage
 *
 * Editor's request message is separated from service's default message
 * for better customization.
 *
 * Request message uses systemMessage macro to show messages.
 * Message types are SUCCESS and WARNING (INFO and ERROR might be implemented later on).
 *
 * @param messageList
-->
<#macro requestMessage messageList>
    <#list messageList as requestMessage>
        <#if requestMessage.type == RequestMessageType.SUCCESS>
            <#--
                Use cssClass 'auto-hide' if message is removed automatically with a delay.
                Requires function to be enabled from JavaScript.
            -->
            <@u.systemMessage path=requestMessage type="success" cssClass="msg-editor" />
        <#else>
            <@u.systemMessage path=requestMessage type=requestMessage.type?lower_case />
        </#if>
    </#list>
</#macro>

<#--
 * modifier
 *
 * @param name modifier name
 * @param time modified date and time
-->
<#macro modifier name time>
    <div class="modifier-details">
        <@u.message "editor.draft" /><span class="bull">&bull;</span><@u.message "editor.modifier" /> <@u.dateTime time /><span class="bull">&bull;</span>${name}
    </div>
</#macro>

</#escape> 