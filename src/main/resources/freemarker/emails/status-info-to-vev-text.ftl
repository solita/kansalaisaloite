<#import "../components/email-layout-html.ftl" as el />
<#import "../components/email-utils.ftl" as eu />
<#import "../components/email-blocks.ftl" as eb />

<#-- Use statusTitleFi and statusInfoFi for TEXT message -->
<#include "../includes/status-info.ftl" />

<#-- FINNISH -->
<@eb.initiativeDetails "fi" "text" />

${statusInfoFi!""}

<@eb.emailBottom "fi" "text" />

---------------------------------------

<#-- SWEDISH -->
${statusTitleSv!""}
      
<@eb.initiativeDetails "sv" "text" />

${statusInfoSv!""}

<@eb.emailBottom "sv" "text" />
