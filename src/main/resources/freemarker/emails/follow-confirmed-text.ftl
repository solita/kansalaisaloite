<#import "../components/email-blocks.ftl" as eb />

<#assign viewUrlFi>${urlsFi.view(initiative.id)}</#assign>
<#assign viewUrlSv>${urlsSv.view(initiative.id)}</#assign>

<@eb.initiativeDetails "fi" "text" />

Olet tilannut aloitteen sähköpostitiedotteet
Saat viestin sähköpostiisi, kun aloite etenee tai päättyy.

<@eb.abstract "sv" "text" />


---------------------------------------

<@eb.initiativeDetails "sv" "text" />


SV Olet tilannut aloitteen sähköpostitiedotteet
SV Saat viestin sähköpostiisi, kun aloite etenee tai päättyy.

<@eb.abstract "sv" "text" />
