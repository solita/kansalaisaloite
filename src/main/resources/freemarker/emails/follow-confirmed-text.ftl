<#import "../components/email-blocks.ftl" as eb />

<#assign viewUrlFi>${urlsFi.view(initiative.id)}</#assign>
<#assign viewUrlSv>${urlsSv.view(initiative.id)}</#assign>

<@eb.initiativeDetails "fi" "text" />

Olet tilannut aloitteen sähköpostitiedotteet
Saat tietoa aloitten etenemisestä sähköpostiisi siihen asti, kunnes se on toimitettu eduskunnan käsittelyyn.


<@eb.emailBottom "fi" "text" />

---------------------------------------

<@eb.initiativeDetails "sv" "text" />


SV Olet tilannut aloitteen sähköpostitiedotteet
SV Saat tietoa aloitten etenemisestä sähköpostiisi siihen asti, kunnes se on toimitettu eduskunnan käsittelyyn.

<@eb.emailBottom "sv" "text" />
