<#import "../components/email-layout-html.ftl" as el />
<#import "../components/email-utils.ftl" as eu />
<#import "../components/email-blocks.ftl" as eb />

<#-- TODO: Put these in one place, for example as globals or an include -->
<#assign viewUrlFi>${urlsFi.view(initiative.id)}</#assign>
<#assign viewUrlSv>${urlsSv.view(initiative.id)}</#assign>

<#-- FINNISH -->
<@eb.initiativeDetails "fi" "text" />

Pyyntö kannatusilmoitusten vahvistukseksi.

Aloitteelle on kerätty kannatusilmoituksia yhteensä ${initiative.totalSupportCount} kpl, joista kansalaisaloite.fi-palvelussa ${initiative.supportCount}. Tässä erässä on ${batchSize} uutta kannatusilmoitusta.

Siirry käsittelemään aloitetta:
${viewUrlFi}

<@eb.abstract "fi" "text" />

---------------------------------------

<#-- SWEDISH -->      
Ansökan om bekräftelse av antalet stödförklaringar

<@eb.initiativeDetails "sv" "text" />

Ansökan om bekräftelse av stödförklaringar.

Initiativet har sammanlagt ${initiative.totalSupportCount} stödröster, varav ${initiative.supportCount} på webbtjänsten medborgarinitiativ.fi.  I det här skedet finns det ${batchSize} nya stödförklaringar. 

Övergå till att behandla initiativet:
${viewUrlSv}

<@eb.abstract "sv" "text" />

