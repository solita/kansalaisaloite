<#import "../components/email-layout-html.ftl" as el />
<#import "../components/email-utils.ftl" as eu />
<#import "../components/email-blocks.ftl" as eb />

<#-- TODO: Put these in one place, for example as globals or an include -->
<#assign viewUrlFi>${urlsFi.view(initiative.id)}</#assign>
<#assign viewUrlSv>${urlsSv.view(initiative.id)}</#assign>


<#-- FINNISH -->
<@eb.initiativeDetails "fi" "text" />

Siirry käsittelemään aloitetta:
${viewUrlFi}

<@eb.abstract "fi" "text" true/>

---------------------------------------

<#-- SWEDISH -->      
Ett medborgarinitiativ för granskning

<@eb.initiativeDetails "sv" "text" />

Övergå till att behandla initiativet:
${viewUrlSv}

<@eb.abstract "sv" "text" true/>

