<#import "../components/email-layout-html.ftl" as el />
<#import "../components/email-utils.ftl" as eu />
<#import "../components/email-blocks.ftl" as eb />

<#escape x as x?html> 

<#assign titleFi="Kutsu kansalaisaloitteen vastuuhenkilöksi" />
<#assign titleSv="Inbjudan som ansvarsperson för ett medborgarinitiativ" />

<#assign invitationUrlFi>${urlsFi.invitation(initiative.id,idHash,invitation.invitationCode)}</#assign>
<#assign invitationUrlSv>${urlsSv.invitation(initiative.id,idHash,invitation.invitationCode)}</#assign>
<#assign helpUrlFi>${baseURL?trim}/fi/ohjeet</#assign>
<#assign helpUrlSv>${baseURL?trim}/sv/anvisningar</#assign>
<#assign invitationRoleFi>
    <#if invitation.initiator>vireillepanija
    <#elseif invitation.representative>edustaja
    <#elseif invitation.reserve>varaedustaja</#if>
</#assign>
<#assign invitationRoleSv>
    <#if invitation.initiator>initiativtagare
    <#elseif invitation.representative>företrädare
    <#elseif invitation.reserve>ersättare</#if>
</#assign>


<@el.emailHtml "invitation" titleFi>

    <#-- FINNISH -->
    <@eb.emailTemplate "fi" titleFi>
        <p style="margin:0.5em 0;">${currentAuthor.firstNames} ${currentAuthor.lastName} on kutsunut sinut ${invitationRoleFi?trim}ksi kansalaisaloitteeseen. Alla näet tiivistelmän kansalaisaloitteen sisällöstä.</p>
    
        <@eb.initiativeDetails "fi" "html" />
        
        <p style="margin:0.5em 0;">Tutustu ${invitationRoleFi?trim}n velvollisuuksiin ja oikeuksiin Kansalaisaloite.fi:n <@eu.link helpUrlFi "ohjeissa" />.</p>
        
        <p style="margin:0.5em 0;">Jos haluat hyväksyä kutsun ${invitationRoleFi?trim}ksi, siirry aloitteen sivulle alla olevalla linkillä ja valitse "Hyväksy". Tämän jälkeen sinut ohjataan tunnistautumaan. Tunnistautumiseen tarvitaan verkkopankkitunnuksia, mobiilivarmenne tai sirullinen henkilökortti (HST-kortti).</p>
        
        <p style="margin:0.5em 0;">Jos haluat hylätä kutsun, se tapahtuu myös aloitteen sivulla valitsemalla "Hylkää". Hylkääminen ei vaadi tunnistautumista.</p>
        
        <p><@eu.button "Siirry hyväksymään kutsu" "${invitationUrlFi}" "green" /></p>
        
        <p style="margin:0.5em 0;">Tämä kutsu on voimassa ${expirationDays} vuorokautta, jonka jälkeen et voi enää vastata kutsuun.</p>

        <@eu.spacer "0" "border-bottom:1px solid #ccc;" />
        <@eb.abstract "fi" "html" />
    </@eb.emailTemplate>
    
    <#-- SWEDISH -->    
    <@eb.emailTemplate "sv" titleSv>                
        <p style="margin:1em 0 0.5em 0;">${currentAuthor.firstNames} ${currentAuthor.lastName} har bjudit in dig som ${invitationRoleSv?trim} för ett medborgarinitiativ. Nedan ser du ett sammandrag av medborgarinitiativets innehåll.</p>
        
        <@eb.initiativeDetails "sv" "html" />
    
        <p style="margin:0.5em 0;">Bekanta dig med ersättarens förpliktelser i <@eu.link helpUrlSv "anvisningarna" /> på Medborgarinitiativ.fi.</p>
        <p style="margin:0.5em 0;">Om du vill godkänna inbjudan som ${invitationRoleSv?trim}, gå till initiativets sida via länken nedan och välj ”Godkänn”. Efter det tas du vidare för att identifiera dig. För identifiering krävs nätbankskoder, mobilcertifikat eller ett chipförsett personkort (HST-kort).</p>
        
        <p><@eu.button "Gå till godkännande av inbjudan" "${invitationUrlSv}" "green" /></p>
        
        <p style="margin:0.5em 0;">Denna inbjudan är i kraft i ${expirationDays} dygn. Efter det kan du inte längre svara på inbjudan.</p>
                    
        <@eu.spacer "0" "border-bottom:1px solid #ccc;" />
        
        <@eb.abstract "sv" "html" />
    </@eb.emailTemplate>
    
</@el.emailHtml>

</#escape> 