<#import "../components/email-blocks.ftl" as eb />

<#assign viewUrlFi>${urlsFi.view(initiative.id)}</#assign>
<#assign viewUrlSv>${urlsSv.view(initiative.id)}</#assign>

<@eb.initiativeDetails "fi" "text" />

Olet tilannut aloitteen sähköpostitiedotteet
Saat tietoa aloitten etenemisestä sähköpostiisi siihen asti, kunnes se on toimitettu eduskunnan käsittelyyn.


<@eb.emailBottom "fi" "text" />

---------------------------------------

<@eb.initiativeDetails "sv" "text" />


Du har beställt e-postmeddelanden med information om initiativet
Du får information om hur initiativet framskrider tills det har skickats till riksdagen för behandling.

<@eb.emailBottom "sv" "text" />
