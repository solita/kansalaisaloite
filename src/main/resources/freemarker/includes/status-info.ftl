<#import "../components/email-blocks.ftl" as eb />

<#--
 * Includes common status infos for generic emails.
 *
 * Includes both TEXT and HTML versions. TEXT version is used as a default value.
 *
 * Types:
 *  INVITATION_REJECTED
 *  INVITATION_ACCEPTED
 *  CONFIRM_ROLE
 *  AUTHOR_REMOVED
 *  AUTHOR_CONFIRMED
 *  SENT_TO_OM
 *  ACCEPTED_BY_OM
 *  REJECTED_BY_OM
 *  SENT_TO_VRK
 *  SENT_TO_PARLIAMENT - TODO
 *  VRK_RESOLUTION
-->

<#macro endlinesToHtml text="">${text?replace('\n','<br/>')}</#macro>

<#if emailMessageType == EmailMessageType.INVITATION_REJECTED>
    <#-- TEXT -->
    <#assign statusTitleFi>Kutsu vastuuhenkilöksi on hylätty</#assign>
    <#assign statusTitleSv>Den tilltänkta ansvarspersonen har avböjt inbjudan</#assign>
    <#assign statusInfoFi>
        Aloitteen vastuuhenkilöksi kutsuttu "${rejectedEmail!""}" on hylännyt kutsun.
        <@eb.initiativeStatus "fi" "text" />
    </#assign>
    <#assign statusInfoSv>
        "${rejectedEmail!""}" har avböjt inbjudan att bli ansvarsperson för initiativet.
        <@eb.initiativeStatus "sv" "text" />
    </#assign>
    
    <#-- HTML -->
    <#assign statusTitleHTMLFi>${statusTitleFi}</#assign>
    <#assign statusTitleHTMLSv>${statusTitleSv}</#assign>
    <#assign statusInfoHTMLFi>
        <p style="margin-top:0.5em;">Aloitteen vastuuhenkilöksi kutsuttu <@eu.link "mailto:"+rejectedEmail rejectedEmail /> on hylännyt kutsun.</p>
        
        <@eb.initiativeStatus "fi" "html" />
    </#assign>
    <#assign statusInfoHTMLSv>
        <p style="margin-top:0.5em;"><@eu.link "mailto:"+rejectedEmail rejectedEmail /> har avböjt inbjudan att bli ansvarsperson för initiativet.</p>
        
        <@eb.initiativeStatus "sv" "html" />
    </#assign>

<#elseif emailMessageType == EmailMessageType.INVITATION_ACCEPTED>
    <#-- TEXT -->
    <#assign statusTitleFi>Kutsu vastuuhenkilöksi on hyväksytty</#assign>
    <#assign statusTitleSv>Den tilltänkta ansvarspersonen har godkänt inbjudan</#assign>
    <#assign statusInfoFi>
        Aloitteen vastuuhenkilöksi kutsuttu "${currentAuthor.firstNames} ${currentAuthor.lastName}" on hyväksynyt kutsun.
        <@eb.initiativeStatus "fi" "text" />
    </#assign>
    <#assign statusInfoSv>
        "${currentAuthor.firstNames} ${currentAuthor.lastName}" har godkänt inbjudan att bli ansvarsperson för initiativet.
        <@eb.initiativeStatus "sv" "text" />
    </#assign>
    
    <#-- HTML -->
    <#assign statusTitleHTMLFi>${statusTitleFi}</#assign>
    <#assign statusTitleHTMLSv>${statusTitleSv}</#assign>
    <#assign statusInfoHTMLFi>
        <p style="margin-top:0.5em;">Aloitteen vastuuhenkilöksi kutsuttu <strong>${currentAuthor.firstNames} ${currentAuthor.lastName}</strong> on hyväksynyt kutsun.</p>
        
        <@eb.initiativeStatus "fi" "html" />
    </#assign>
    <#assign statusInfoHTMLSv>
        <p style="margin-top:0.5em;"><strong>${currentAuthor.firstNames} ${currentAuthor.lastName}</strong> har godkänt inbjudan att bli ansvarsperson för initiativet.</p>
        
        <@eb.initiativeStatus "sv" "html" />
    </#assign>
    
<#elseif emailMessageType == EmailMessageType.CONFIRM_ROLE>
    <#-- TEXT -->
    <#assign statusTitleFi>Vahvista roolisi uudelleen</#assign>
    <#assign statusTitleSv>Bekräfta din roll på nytt</#assign>
    <#assign statusInfoFi>Aloitteen pysyviä tietoja on muutettu. Sinun tulee vahvistaa roolisi uudelleen tässä aloitteessa.</#assign>
    <#assign statusInfoSv>Den bestående informationen om initiativet har ändrats. Du ombeds därför bekräfta din roll i initiativet på nytt.</#assign>
    
    <#-- HTML -->
    <#assign statusTitleHTMLFi>${statusTitleFi}</#assign>
    <#assign statusTitleHTMLSv>${statusTitleSv}</#assign>
    <#assign statusInfoHTMLFi>${statusInfoFi}</#assign>
    <#assign statusInfoHTMLSv>${statusInfoSv}</#assign>

<#elseif emailMessageType == EmailMessageType.AUTHOR_REMOVED>
    <#-- TEXT -->
    <#assign statusTitleFi>Vastuuhenkilö on hylännyt roolinsa</#assign>
    <#assign statusTitleSv>Ansvarspersonen har avböjt sin roll</#assign>
    <#assign statusInfoFi>
        "${currentAuthor.firstNames} ${currentAuthor.lastName}" on hylännyt vastuuhenkilön roolinsa aloitteessa.
        <@eb.initiativeStatus "fi" "text" />
    </#assign>
    <#assign statusInfoSv>
        "${currentAuthor.firstNames} ${currentAuthor.lastName}" har avböjt sin roll som ansvarspersonen för initiativet.
        <@eb.initiativeStatus "sv" "text" />
    </#assign>
    
    <#-- HTML -->
    <#assign statusTitleHTMLFi>${statusTitleFi}</#assign>
    <#assign statusTitleHTMLSv>${statusTitleSv}</#assign>
    <#assign statusInfoHTMLFi>
        <p style="margin-top:0.5em;"><strong>${currentAuthor.firstNames} ${currentAuthor.lastName}</strong> on hylännyt vastuuhenkilön roolinsa aloitteessa.</p>
        
        <@eb.initiativeStatus "fi" "html" />
    </#assign>
    <#assign statusInfoHTMLSv>
        <p style="margin-top:0.5em;"><strong>${currentAuthor.firstNames} ${currentAuthor.lastName}</strong> har avböjt sin roll som ansvarspersonen för initiativet.</p>
        
        <@eb.initiativeStatus "sv" "html" />
    </#assign>
    
<#elseif emailMessageType == EmailMessageType.AUTHOR_CONFIRMED>
    <#-- TEXT -->
    <#assign statusTitleFi>Vastuuhenkilö on vahvistanut roolinsa</#assign>
    <#assign statusTitleSv>Ansvarspersonen har bekräftat sin roll</#assign>
    <#assign statusInfoFi>
        "${currentAuthor.firstNames} ${currentAuthor.lastName}" on vahvistanut vastuuhenkilön roolinsa aloitteessa.
        <@eb.initiativeStatus "fi" "text" />
    </#assign>
    <#assign statusInfoSv>
        "${currentAuthor.firstNames} ${currentAuthor.lastName}" har bekräftat sin roll som ansvarsperson för initiativet.
        <@eb.initiativeStatus "sv" "text" />
    </#assign>
    
    <#-- HTML -->
    <#assign statusTitleHTMLFi>${statusTitleFi}</#assign>
    <#assign statusTitleHTMLSv>${statusTitleSv}</#assign>
    <#assign statusInfoHTMLFi>
        <p style="margin-top:0.5em;"><strong>${currentAuthor.firstNames} ${currentAuthor.lastName}</strong> on vahvistanut vastuuhenkilön roolinsa aloitteessa.</p>
        
        <@eb.initiativeStatus "fi" "html" />
    </#assign>
    <#assign statusInfoHTMLSv>
        <p style="margin-top:0.5em;"><strong>${currentAuthor.firstNames} ${currentAuthor.lastName}</strong> har bekräftat sin roll som ansvarsperson för initiativet.</p>
        
        <@eb.initiativeStatus "sv" "html" />
    </#assign>

<#elseif emailMessageType == EmailMessageType.SENT_TO_OM>
    <#-- TEXT -->
    <#assign statusTitleFi>Kansalaisloite on lähetetty tarkastettavaksi oikeusministeriöön</#assign>
    <#assign statusTitleSv>Medborgarinitiativet har skickats till justitieministeriet för granskning</#assign>
    <#assign statusInfoFi>
        Kansalaisloite on lähetetty oikeusministeriölle tarkastettavaksi.
        
        Oikeusministeriö tarkastaa, että kansalaisaloite.fi-palveluun tehdyissä aloitteissa on tarvittavat tiedot ja ettei aloite sisällä verkossa julkaistavaksi sopimatonta materiaalia. Tämän jälkeen aloitteelle voi kerätä kannatusilmoituksia. Aloitteita käsitellään oikeusministeriössä arkisin virka-aikana ja tarkastus kestää muutamia päiviä. Saat tietoa tarkastuksen etenemisestä sähköpostitse.
    </#assign>
    <#assign statusInfoSv>
        Medborgarinitiativet har skickats till justitieministeriet för granskning.
    
        Justitieministeriet granskar att de initiativ som gjorts på webbtjänsten medborgarinitiativ.fi har den information som krävs och att initiativet inte innehåller material som är olämpligt att publicera på internet. Därefter kan insamlingen av stödförklaringar för initiativet påbörjas. Justitieministeriet behandlar initiativ på vardagar under tjänstetid och granskningen tar några dagar. Du får information om hur granskningen framskrider per e-post.
    </#assign>
    
    <#-- HTML -->
    <#assign statusTitleHTMLFi>${statusTitleFi}</#assign>
    <#assign statusTitleHTMLSv>${statusTitleSv}</#assign>
    <#assign statusInfoHTMLFi>
        <p>Kansalaisloite on lähetetty oikeusministeriölle tarkastettavaksi.</p>
        <p>Oikeusministeriö tarkastaa, että kansalaisaloite.fi-palveluun tehdyissä aloitteissa on tarvittavat tiedot ja ettei aloite sisällä verkossa julkaistavaksi sopimatonta materiaalia. Tämän jälkeen aloitteelle voi kerätä kannatusilmoituksia. Aloitteita käsitellään oikeusministeriössä arkisin virka-aikana ja tarkastus kestää muutamia päiviä. Saat tietoa tarkastuksen etenemisestä sähköpostitse.</p>    
    </#assign>
    <#assign statusInfoHTMLSv>
        <p>Medborgarinitiativet har skickats till justitieministeriet för granskning.</p>
        <p>Justitieministeriet granskar att de initiativ som gjorts på webbtjänsten medborgarinitiativ.fi har den information som krävs och att initiativet inte innehåller material som är olämpligt att publicera på internet. Därefter kan insamlingen av stödförklaringar för initiativet påbörjas. Justitieministeriet behandlar initiativ på vardagar under tjänstetid och granskningen tar några dagar. Du får information om hur granskningen framskrider per e-post.</p>
    </#assign>
    
<#elseif emailMessageType == EmailMessageType.ACCEPTED_BY_OM>
    <#-- TEXT -->
    <#assign statusTitleFi>Aloite on tarkastettu</#assign>
    <#assign statusTitleSv>Initiativet har granskats</#assign>
    <#assign statusInfoFi>
        Oikeusministeriö on tarkastanut kansalaisaloitteen ja hyväksynyt sen julkaistavaksi kansalaisaloite.fi -palveluun. Aloitteen kannatusilmoitusten kerääminen alkaa yllä mainitusta päivämäärästä alkaen.
        
        Oikeusministeriön saate:
        ${stateComment!"Ei saatetta"}
        
        Oikeusministeriön asianumero:
        ${acceptanceIdentifier!"Ei asianumeroa"}
    </#assign>
    <#assign statusInfoSv>
        Justitieministeriet har granskat medborgarinitiativet och godkänt det för publicering på webbtjänsten medborgarinitiativ.fi. Insamlingen av stödförklaringar för initiativet börjar från och med det datum som nämns ovan.
        
        Justitieministeriets följebrev:
        ${stateComment!"Ingen följebrev"}
        
        Justitieministeriets ärendenummer:
        ${stateComment!"Ingen ärendenummer"}
    </#assign>
    
    <#-- HTML -->
    <#assign statusTitleHTMLFi>${statusTitleFi}</#assign>
    <#assign statusTitleHTMLSv>${statusTitleSv}</#assign>
    <#assign stateCommentHtml><@endlinesToHtml stateComment/></#assign>
    <#assign statusInfoHTMLFi>
        <p>Oikeusministeriö on tarkastanut kansalaisaloitteen ja hyväksynyt sen julkaistavaksi kansalaisaloite.fi -palveluun. Aloitteen kannatusilmoitusten kerääminen alkaa yllä mainitusta päivämäärästä alkaen.</p>
        <p><strong>Oikeusministeriön saate:</strong><br>${stateCommentHtml!"Ei saatetta"}</p>
        <p><strong>Oikeusministeriön asianumero:</strong><br>${acceptanceIdentifier!"Ei asianumeroa"}</p>
    </#assign>
    <#assign statusInfoHTMLSv>
        <p>Justitieministeriet har granskat medborgarinitiativet och godkänt det för publicering på webbtjänsten medborgarinitiativ.fi. Insamlingen av stödförklaringar för initiativet börjar från och med det datum som nämns ovan.</p>
        <p><strong>Justitieministeriets följebrev:</strong><br>${stateCommentHtml!"Ingen följebrev"}</p>
        <p><strong>Justitieministeriets ärendenummer:</strong><br>${acceptanceIdentifier!"Ingen ärendenummer"}</p>
    </#assign>
    
<#elseif emailMessageType == EmailMessageType.REJECTED_BY_OM>
    <#-- TEXT -->
    <#assign statusTitleFi>Aloite on palautettu korjattavaksi</#assign>
    <#assign statusTitleSv>Initiativet har skickats tillbaka för korrigering</#assign>
    <#assign statusInfoFi>
        Oikeusministeriö on tarkastanut kansalaisaloitteen ja palauttanut sen täydennettäväksi.
        
        Oikeusministeriön saate:
        ${stateComment!"Ei saatetta"}
    </#assign>
    <#assign statusInfoSv>
        Justitieministeriet har granskat medborgarinitiativet  och skickat tillbaka det för komplettering.
        
        Justitieministeriets följebrev:
         ${stateComment!"Ingen följebrev"}
    </#assign>
    
    <#-- HTML -->
    <#assign stateCommentHtml><@endlinesToHtml stateComment/></#assign>
    <#assign statusTitleHTMLFi>${statusTitleFi}</#assign>
    <#assign statusTitleHTMLSv>${statusTitleSv}</#assign>
    <#assign statusInfoHTMLFi>
        <p>Oikeusministeriö on tarkastanut kansalaisaloitteen ja palauttanut sen täydennettäväksi.</p>
        <p><strong>Oikeusministeriön saate:</strong><br>${stateCommentHtml!"Ei saatetta"}</p>
    </#assign>
    <#assign statusInfoHTMLSv>
        <p>Justitieministeriet har granskat medborgarinitiativet  och skickat tillbaka det för komplettering.</p>
        <p><strong>Justitieministeriets följebrev:</strong><br> ${stateCommentHtml!"Ingen följebrev"}</p>
    </#assign>
    
<#elseif emailMessageType == EmailMessageType.SENT_TO_VRK>
    <#-- TEXT -->
    <#assign statusTitleFi>Kannatusilmoitusten määrän vahvistuspyyntö lähetetty</#assign>
    <#assign statusTitleSv>Ansökan om bekräftelse av antalet stödförklaringar har skickats</#assign>
    <#assign statusInfoFi>Kannatusilmoitusten määrän vahvistuspyyntö on lähetetty Väestörekisterikeskukseen</#assign>
    <#assign statusInfoSv>Ansökan om att granska stödförklaringarna har skickats till Befolkningsregistercentralen.</#assign>
    
    <#-- HTML -->
    <#assign statusTitleHTMLFi>${statusTitleFi}</#assign>
    <#assign statusTitleHTMLSv>${statusTitleSv}</#assign>
    <#assign statusInfoHTMLFi>
        <p>Kannatusilmoitusten määrän vahvistuspyyntö on lähetetty Väestörekisterikeskukseen.</p>
    </#assign>
    <#assign statusInfoHTMLSv>
        <p>Ansökan om att granska stödförklaringarna har skickats till Befolkningsregistercentralen.</p>
    </#assign>
        
<#elseif emailMessageType == EmailMessageType.SENT_TO_PARLIAMENT>
    <#-- TEXT -->
    <#assign statusTitleFi>Aloite on toimitettu eduskuntaan</#assign>
    <#assign statusTitleSv>Initiativet har överlämnats till riksdagen</#assign>
    <#assign statusInfoFi>Aloite on toimitettu eduskuntaan</#assign>
    <#assign statusInfoSv>Initiativet har överlämnats till riksdagen</#assign>
    
    <#-- HTML -->
    <#assign statusTitleHTMLFi>${statusTitleFi}</#assign>
    <#assign statusTitleHTMLSv>${statusTitleSv}</#assign>
    <#assign statusInfoHTMLFi>
        Aloite on toimitettu eduskuntaan <@eu.localDate initiative.parliamentSentTime "fi" /><br/>
        Linkki eduskunnan sivulle: <a href="${initiative.parliamentURL}">${initiative.parliamentIdentifier}</a>
    </#assign>
    <#assign statusInfoHTMLSv>
        Initiativet har överlämnats till riksdagen <@eu.localDate initiative.parliamentSentTime "fi" /><br/>
        Länk till riksdagens webbplats: <a href="${initiative.parliamentURL}">${initiative.parliamentIdentifier}</a>
    </#assign>
    
<#elseif emailMessageType == EmailMessageType.VRK_RESOLUTION>
    <#-- TEXT -->
    <#assign statusTitleFi>Väestörekisterikeskus on antanut päätöksen tarkistetuista kannatusilmoituksista</#assign>
    <#assign statusTitleSv>Befolkningsregistercentralen har gett sitt beslut om de granskade stödförklaringarna</#assign>
    <#assign statusInfoFi>
        Väestörekisterikeskus on vahvistanut kannatusilmoitusten määräksi ${initiative.verifiedSupportCount} kpl <@eu.localDate initiative.verified "fi" />. 
        Päätöksen diaarinumero on ${initiative.verificationIdentifier}. 
    </#assign>
    <#assign statusInfoSv>
        Befolkningsregistercentralen har bekräftat att stödförklaringarna är ${initiative.verifiedSupportCount} till antalet <@eu.localDate initiative.verified "sv" />
        Beslutets diarienummer är ${initiative.verificationIdentifier}. 
    </#assign>
    
    <#-- HTML -->
    <#assign statusTitleHTMLFi>${statusTitleFi}</#assign>
    <#assign statusTitleHTMLSv>${statusTitleSv}</#assign>
    <#assign statusInfoHTMLFi>
        <p>Väestörekisterikeskus on vahvistanut kannatusilmoitusten määräksi <strong>${initiative.verifiedSupportCount}</strong> kpl <@eu.localDate initiative.verified "fi" />.</p> 
        <p>Päätöksen diaarinumero on <strong>${initiative.verificationIdentifier}</strong>.</p>
    </#assign>
    <#assign statusInfoHTMLSv>
        <p>Befolkningsregistercentralen har bekräftat att stödförklaringarna är <strong>${initiative.verifiedSupportCount}</strong> till antalet <@eu.localDate initiative.verified "sv" />.</p>
        <p>Beslutets diarienummer är <strong>${initiative.verificationIdentifier}</strong>.</p>
    </#assign>

<#elseif emailMessageType == EmailMessageType.VOTING_ENDED>

    <#-- TEXT -->
    <#assign statusTitleFi>Kannatusilmoitusten kerääminen on päättynyt</#assign>
    <#assign statusTitleSv>Insamlingen av stödförklaringar har avslutats</#assign>

    <#if initiative.votingDaysLeft gt 0>
        <#assign statusInfoFi>
        ${statusTitleFi}. Aloite ei kerännyt vaadittua ${initiativeSettings.minSupportCountForSearch} kannatusilmoitusta yhden kuukauden aikana.
        Aloite keräsi ${initiative.totalSupportCount} kannatusilmoitusta, joista ${initiative.supportCount} kpl kansalaisaloite.fi-palvelussa ja ${initiative.externalSupportCount} kpl muilla menetelmillä.
        </#assign>
        <#assign statusInfoSv>
        ${statusTitleSv}. Initiativet uppnådde inte det förutsatta kravet på ${initiativeSettings.minSupportCountForSearch} stödförklaringar inom en månad.
        Initiativet samlade in ${initiative.totalSupportCount} stödförklaringar, av vilka ${initiative.supportCount} undertecknades på medborgarinitiativ.fi och ${initiative.externalSupportCount} på annat sätt.
        </#assign>


    <#elseif initiative.totalSupportCount gte initiativeSettings.requiredVoteCount>
        <#assign statusInfoFi>
        ${statusTitleFi}. Aloite keräsi ${initiative.totalSupportCount} kannatusilmoitusta, joista ${initiative.supportCount} kpl kansalaisaloite.fi-palvelussa ja ${initiative.externalSupportCount} kpl muilla menetelmillä.
        Aloitteen vastuuhenkilöiden tulee toimittaa kannatusilmoitukset Väestörekisterikeskuksen tarkastettaviksi kuuden kuukauden sisällä keräyksen päättymisestä, jotta aloite voi edetä eduskunnan käsiteltäväksi.
        </#assign>
        <#assign statusInfoSv>
        ${statusTitleSv}. Initiativet samlade in ${initiative.totalSupportCount} stödförklaringar, av vilka ${initiative.supportCount} undertecknades på medborgarinitiativ.fi och ${initiative.externalSupportCount} på annat sätt.
        Ansvarspersonerna ska skicka stödförklaringarna till Befolkningsregistercentralen för kontroll inom sex månader efter att insamlingen avslutades för att initiativet ska kunna överlämnas till riksdagen för behandling.
        </#assign>

    <#else >
        <#assign statusInfoFi>
        ${statusTitleFi}. Aloite keräsi ${initiative.totalSupportCount} kannatusilmoitusta, joista ${initiative.supportCount} kpl kansalaisaloite.fi-palvelussa ja ${initiative.externalSupportCount} kpl muilla menetelmillä.
        Aloitteen olisi pitänyt kerätä vähintään ${initiativeSettings.requiredVoteCount} kannatusilmoitusta edetäkseen eduskunnan käsittelyyn.
        </#assign>
        <#assign statusInfoSv>
        ${statusTitleSv}. Initiativet samlade in ${initiative.totalSupportCount} stödförklaringar, av vilka ${initiative.supportCount} undertecknades på medborgarinitiativ.fi och ${initiative.externalSupportCount} på annat sätt.
        Initiativet skulle ha behövt minst ${initiativeSettings.requiredVoteCount} stödförklaringar för att kunna överlämnas till riksdagen för behandling.
        </#assign>

    </#if>

    <#-- HTML -->
    <#assign statusTitleHTMLFi>${statusTitleFi}</#assign>
    <#assign statusTitleHTMLSv>${statusTitleSv}</#assign>
    <#assign statusInfoHTMLFi>
        <p>${statusInfoFi}</p>
    </#assign>
    <#assign statusInfoHTMLSv>
        <p>${statusInfoSv}</p>
    </#assign>
    
<#elseif emailMessageType == EmailMessageType.REMOVED_SUPPORT_VOTES>
    <#-- TEXT -->
    <#assign statusTitleFi>Aloitteen kannatusilmoitukset on hävitetty</#assign>
    <#assign statusTitleSv>Initiativets stödförklaringar har raderats</#assign>
    <#assign statusInfoFi>Aloitteen kannatusilmoitukset on hävitetty.</#assign>
    <#assign statusInfoSv>Initiativets stödförklaringar har raderats.</#assign>
    
    <#-- HTML -->
    <#assign statusTitleHTMLFi>${statusTitleFi}</#assign>
    <#assign statusTitleHTMLSv>${statusTitleSv}</#assign>
    <#assign statusInfoHTMLFi>${statusInfoFi}</#assign>
    <#assign statusInfoHTMLSv>${statusInfoSv}</#assign>
    
<#elseif emailMessageType == EmailMessageType.VOTING_HALFWAY>

    <#assign statusTitleFi>Kannatusilmoitusten kerääminen on puolivälissä</#assign>
    <#assign statusTitleSv>Insamling av stödförklarigar är halvvägs</#assign>
    <#assign statusTitleHTMLFi>${statusTitleFi}</#assign>
    <#assign statusTitleHTMLSv>${statusTitleSv}</#assign>

    <#assign statusInfoFi>
        Aloite on kerännyt ${initiative.totalSupportCount} kannatusilmoitusta, joista ${initiative.supportCount} kpl kansalaisaloite.fi-palvelussa ja ${initiative.externalSupportCount} kpl muilla menetelmillä.
        Keruuaika päättyy <@eu.localDate initiative.endDate "fi" />. Edetäkseen eduskunnan käsittelyyn aloitteen tulee kerätä vähintään ${initiativeSettings.requiredVoteCount} kannatusilmoitusta kuuden kuukauden aikana.
        <#--<p>Tämä on ${percentFromGoal?string["0.##"]} prosenttia kokonaistavoitteesta.</p>-->
    </#assign>
    <#assign statusInfoSv>
        Initiativet har samlat in ${initiative.totalSupportCount} stödförklaringar, av vilka ${initiative.supportCount} har undertecknats på medborgarinitiativ.fi och ${initiative.externalSupportCount} på annat sätt.
        Insamlingstiden går ut <@eu.localDate initiative.endDate "fi" />. För att initiativet ska kunna överlämnas till riskdagen för behandling ska det samla in minst ${initiativeSettings.requiredVoteCount} stödförklaringar inom sex månader.
    </#assign>

    <#assign statusInfoHTMLFi>${statusInfoFi}</#assign>
    <#assign statusInfoHTMLSv>${statusInfoSv}</#assign>

</#if>



