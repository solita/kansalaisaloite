<#import "email-utils.ftl" as eu />

<#escape x as x?html>

<#-- NOTE: These must be here for the text-templates OR find a better way. -->
<#assign viewUrlFi>${urlsFi.view(initiative.id)}</#assign>
<#assign viewUrlSv>${urlsSv.view(initiative.id)}</#assign>
<#assign viewUrlFiHash>${urlsFi.view(initiative.id, idHash)}</#assign>
<#assign viewUrlSvHash>${urlsSv.view(initiative.id, idHash)}</#assign>
<#assign defaultFi>Ei käännettyä sisältöä</#assign>
<#assign defaultSv>Detta innehåll har ingen översättning</#assign>

<#assign tableBg = "background:#fff;" />

<#macro emailTemplate lang="fi" title="">

    <table border="0" cellspacing="0" cellpadding="0" style="font-family:Arial, sans-serif;" width="100%" bgcolor="#f0f0f0">
    <tr>
        <td align="center">
            <@eu.spacer "15" />
            <table border="0" cellspacing="0" cellpadding="0" width="640" style="background:#fff; border-radius:5px; text-align:left; font-family:Arial, sans-serif;">        
                <tr style="color:#fff;">
                    <td width="20" style="${tableBg}"><@eu.spacer "0" /></td>
                    <td width="550" style="${tableBg} text-align:left;"><@eu.spacer "15" /><h4 style="font-size:20px; margin:0; color:#087480; font-weight:normal; font-family:'PT Sans','Trebuchet MS',Helvetica,sans-serif">${title}</h4><@eu.spacer "15" /></th>
                    <td width="20" style="${tableBg}"><@eu.spacer "0" /></td>
                </tr>
                <tr>
                    <td colspan="3" style="">
                    <table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
                        <tr>
                            <td width="20" style="${tableBg}"><@eu.spacer "0" /></td>
                            <td style="${tableBg} font-size:12px; font-family:'PT Sans','Trebuchet MS',Helvetica,sans-serif;">
                                <#--<@eu.spacer "5" />-->
    
                                <#-- Email content -->
                                <#nested />
                                        
                                <@eu.spacer "15" />
                            </td>
                            <td width="20" style="${tableBg}"><@eu.spacer "0" /></td>
                        </tr>
                    </table>
                    </td>
                </tr>
            </table>

            <#if lang == "fi">
                <p style="color:#686868; font-size:12px;">Epäiletkö että viesti tuli väärään osoitteeseen? Ole hyvä ja poista tämä viesti.</p>
            <#else>
                <p style="color:#686868; font-size:12px;">Misstänker du att meddelandet kom till fel adress? Var god och radera detta meddelandet.</p>
            </#if>
            <@eu.spacer "15" />
                    
        </td>
    </tr>
    </table>

</#macro>


<#--
 * initiativeDetails
 *
 * Common initiative details for top section of the email.
 * 
 * @param lang 'fi' or 'sv'
 * @param type 'text' or 'html'
 -->
<#macro initiativeDetails lang="" type="">
    <#if lang == "fi">
        <#if type == "html">
            <h4 style="font-size:12px; margin:1em 0 0 0;">${initiative.name.fi!initiative.name.sv!defaultFi}</h4>
            <p style="margin:0 0 1em 0;"><@eu.localDate initiative.startDate "fi" /> <#if initiative.proposalType == 'LAW'>Lakiehdotus<#else>Ehdotus lainvalmisteluun ryhtymisestä</#if>, <#if initiative.financialSupport>aloite saa taloudellista tukea<#else>ei taloudellista tukea</#if></p>
        <#else>
            Kansalaisaloite:
            "${initiative.name.fi!initiative.name.sv!defaultFi}"
            <@eu.localDate initiative.startDate "fi" /> <#if initiative.proposalType == 'LAW'>Lakiehdotus<#else>Ehdotus lainvalmisteluun ryhtymisestä</#if>, <#if initiative.financialSupport>aloite saa taloudellista tukea<#else>ei taloudellista tukea</#if>        
        </#if>
    <#elseif lang == "sv">
        <#if type == "html">
            <h4 style="font-size:12px; margin:1em 0 0 0;">${initiative.name.sv!initiative.name.fi!defaultSv}</h4>
            <p style="margin:0 0 1em 0;"><@eu.localDate initiative.startDate "sv" /> <#if initiative.proposalType == 'LAW'>Lagförslag<#else>Förslag för lagförberedelser</#if>, <#if initiative.financialSupport>ekonomiskt stöd<#else>inget ekonomiskt stöd</#if></p>
        <#else>
            Medborgarinitiativ:
            "${initiative.name.sv!initiative.name.fi!defaultSv}"
            <@eu.localDate initiative.startDate "sv" /> <#if initiative.proposalType == 'LAW'>Lagförslag<#else>Förslag för lagförberedelser</#if>, <#if initiative.financialSupport>ekonomiskt stöd<#else>inget ekonomiskt stöd</#if>
        </#if>    
    </#if>        
</#macro>

<#--
 * initiativeStatus
 *
 * Display initiative's status for Status emails.
 * - Is allowed to send to OM?
 * - Is pending invitations?
 * 
 * @param lang 'fi' or 'sv'
 * @param type 'text' or 'html'
 -->
<#macro initiativeStatus lang="" type="">
    <#assign totalUnconfirmedCount = initiative.totalUnconfirmedCount />
    
    <#-- Generate plurals if needed. -->
    <#assign organizerFi>vastuuhenkilö${(totalUnconfirmedCount > 1)?string('ä','')}</#assign>
    <#assign organizerSv>ansvarsperson${(totalUnconfirmedCount > 1)?string('er','')}</#assign>
    
    <#if lang == "fi">
        <#if type == "html">
            <p style="margin-top:0.5em;">
                <#if enoughConfirmedAuthors>
                    <#if (totalUnconfirmedCount > 0)>
                        <strong>Osa vastuuhenkilöistä on vahvistamatta</strong><br/>
                        ${totalUnconfirmedCount} ${organizerFi} on vielä vahvistamatta. Aloitteella on kuitenkin jo nyt tarvittava määrä vastuuhenkilöitä ja se voidaan lähettää oikeusministeriön tarkastettavaksi.
                    <#else>
                        <strong>Aloitteen voi nyt lähettää oikeusministeriön tarkastettavaksi</strong><br/>
                        Kaikki vastuuhenkilöt on nyt vahvistettu.
                    </#if>
                <#else>
                    <strong>Aloite odottaa vastuuhenkilöitä</strong><br/>
                    <#if (totalUnconfirmedCount > 0)>${totalUnconfirmedCount} ${organizerFi} on vielä vahvistamatta. </#if>Aloitteella ei ole tarvittavia vastuuhenkilöitä, joten et voi vielä lähettää aloitetta oikeusministeriön tarkastettavaksi.
                </#if>
            </p>
        <#else>
            <#if enoughConfirmedAuthors>
                <#if (totalUnconfirmedCount > 0)>
                    Osa vastuuhenkilöistä on vahvistamatta
                    ${totalUnconfirmedCount} ${organizerFi} on vielä vahvistamatta. Aloitteella on kuitenkin jo nyt tarvittava määrä vastuuhenkilöitä ja se voidaan lähettää oikeusministeriön tarkastettavaksi.
                <#else>
                    Aloitteen voi nyt lähettää oikeusministeriön tarkastettavaksi
                    Kaikki vastuuhenkilöt on nyt vahvistettu.
                </#if>
            <#else>
                Aloite odottaa vastuuhenkilöitä
                <#if (totalUnconfirmedCount > 0)>${totalUnconfirmedCount} ${organizerFi} on vielä vahvistamatta. </#if>Aloitteella ei ole tarvittavia vastuuhenkilöitä, joten et voi vielä lähettää aloitetta oikeusministeriön tarkastettavaksi.
            </#if>
        </#if>
    <#elseif lang == "sv">
        <#if type == "html">
            <p style="margin-top:0.5em;">
                <#if enoughConfirmedAuthors>
                    <#if (totalUnconfirmedCount > 0)>
                        <strong>Alla ansvarspersoner har ännu inte bekräftat sina roller</strong><br/>
                        ${totalUnconfirmedCount} ${organizerSv} är ännu obekräftade. Initiativet har ändå tillräckligt många ansvarspersoner, och det kan således skickas till justitieministeriet för att granskas.
                    <#else>
                        <strong>Initiativet kan nu skickas till justitieministeriet för att granskas</strong><br/>
                        Alla ansvarspersoner har bekräftat sina roller.
                    </#if>
                <#else>
                    <strong>Initiativet inväntar ansvarspersoner</strong><br/>
                    <#if (totalUnconfirmedCount > 0)>${totalUnconfirmedCount} ${organizerSv} är ännu obekräftade. </#if>Initiativet har inte tillräckligt många ansvarspersoner, så du kan inte skicka det till justitieministeriet för att granskas än.
                </#if>
            </p>
        <#else>
            <#if enoughConfirmedAuthors>
                <#if (totalUnconfirmedCount > 0)>
                    Alla ansvarspersoner har ännu inte bekräftat sina roller
                    ${totalUnconfirmedCount} ${organizerSv} är ännu obekräftade. Initiativet har ändå tillräckligt många ansvarspersoner, och det kan således skickas till justitieministeriet för att granskas.
                <#else>
                    Initiativet kan nu skickas till justitieministeriet för att granskas
                    Alla ansvarspersoner har bekräftat sina roller.
                </#if>
            <#else>
                Initiativet inväntar ansvarspersoner
                <#if (totalUnconfirmedCount > 0)>${totalUnconfirmedCount} ${organizerSv} är ännu obekräftade. </#if>Initiativet har inte tillräckligt många ansvarspersoner, så du kan inte skicka det till justitieministeriet för att granskas än.
            </#if>
        </#if>
    </#if>
</#macro>


<#--
 * emailBottom
 *
 * Common initiative details for bottom section of the email.
 * 
 * @param lang 'fi' or 'sv'
 * @param type 'text' or 'html'
 * @param sentTo 'show' shows additional info
 -->
<#macro emailBottom lang="" type="" sentTo="">
    <#if lang == "fi">
        <#if type == "html">
            <#if sentTo == "show"><p>Tämä viesti on lähetetty kaikille yllä mainitun aloitteen vireillepanijoille, edustajille ja varaedustajille.</p></#if>
            <p style="margin:1em 0 0.5em 0;">Aloitteesi sijaitsee osoitteessa: <@eu.link viewUrlFi /></p>
        <#else>
            <#if sentTo == "show">Tämä viesti on lähetetty kaikille yllä mainitun aloitteen vireillepanijoille, edustajille ja varaedustajille.
            </#if>
            Aloitteesi sijaitsee osoitteessa:
            ${viewUrlFi}        
        </#if>
    <#elseif lang == "sv">
        <#if type == "html">
            <#if sentTo == "show"><p>Detta meddelande har skickats till alla initiativtagare, företrädare och ersättare för initiativet ovan.</p></#if>
            <p style="margin:1em 0 0.5em 0;">Ditt initiativ finns på adressen: <@eu.link viewUrlSv /></p>
        <#else>
            <#if sentTo == "show">Detta meddelande har skickats till alla initiativtagare, företrädare och ersättare för initiativet ovan.
            </#if>
            Ditt initiativ finns på adressen:
            ${viewUrlSv}
        </#if>    
    </#if>        
</#macro>

<#--
 * abstract
 *
 * First chapter of the initiative proposal.
 * 
 * @param lang 'fi' or 'sv'
 * @param type 'text' or 'html'
 * @param omOrVrk boolean
 -->
<#macro abstract lang="" type="" omOrVrk=false>
    <#if lang == "fi">
        <#if type == "html">
            <h4 style="font-size:12px; margin:1em 0 0.5em 0;">Tiivistelmä kansalaisaloitteesta</h4>
            <p style="margin:0.5em 0;"><@eu.shortenText initiative.proposal "fi" "html" /></p>
            <p style="margin:0.5em 0;">
            <#if omOrVrk>
                <@eu.link viewUrlFi "Näytä aloitteen koko sisältö &rarr;" />
            <#else>
                <@eu.link viewUrlFiHash "Näytä aloitteen koko sisältö &rarr;" />
            </#if>
            </p>
        <#else>
            Tiivistelmä kansalaisaloitteesta:
            <@eu.shortenText initiative.proposal "fi" "text" />


            Näytä aloitteen koko sisältö:
            <#if omOrVrk>
                ${viewUrlFi}
            <#else>
                ${viewUrlFiHash}
            </#if>
        </#if>
    <#elseif lang == "sv">
        <#if type == "html">
            <h4 style="font-size:12px; margin:1em 0 0.5em 0;">Initiativets sammanfattning</h4>
            <p style="margin:0.5em 0;"><@eu.shortenText initiative.proposal "sv" "html" /></p>
            <p style="margin:0.5em 0;">
                <#if omOrVrk>
                    <@eu.link viewUrlSv "Visa initiativets innehåll &rarr;" />
                <#else>
                    <@eu.link viewUrlSvHash "Visa initiativets innehåll &rarr;" />
                </#if>
            </p>
        <#else>
            Sammandrag av medborgarinitiativet:
            <@eu.shortenText initiative.proposal "sv" "text" />
            
            
            Visa initiativets innehåll:
            <#if omOrVrk>
                ${viewUrlSv}
            <#else>
                ${viewUrlSvHash}
            </#if>
        </#if>
    </#if>
</#macro>

</#escape>