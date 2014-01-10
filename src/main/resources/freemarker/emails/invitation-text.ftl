<#import "../components/email-utils.ftl" as eu />
<#import "../components/email-blocks.ftl" as eb />

<#assign invitationUrlFi>${urlsFi.invitation(initiative.id,idHash,invitation.invitationCode)}</#assign>
<#assign invitationUrlSv>${urlsSv.invitation(initiative.id,idHash,invitation.invitationCode)}</#assign>
<#assign helpUrlFi>${baseURL?trim}/fi/ohjeet</#assign>
<#assign helpUrlSv>${baseURL?trim}/sv/anvisningar</#assign>

<#assign ingressLength=500 />
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

<#-- FINNISH -->
${currentAuthor.firstNames} ${currentAuthor.lastName} on kutsunut sinut ${invitationRoleFi?trim}ksi kansalaisaloitteeseen. Alla näet tiivistelmän kansalaisaloitteen sisällöstä.

<@eb.initiativeDetails "fi" "text" />

Tutustu ${invitationRoleFi?trim}n velvollisuuksiin ja oikeuksiin Kansalaisaloite.fi:n ohjeissa:
${helpUrlFi?trim}

Jos haluat hyväksyä kutsun ${invitationRoleFi?trim}ksi, siirry aloitteen sivulle alla olevalla linkillä ja valitse "Hyväksy". Tämän jälkeen sinut ohjataan tunnistautumaan. Tunnistautumiseen tarvitaan verkkopankkitunnuksia, mobiilivarmenne tai sirullinen henkilökortti (HST-kortti).

Jos haluat hylätä kutsun, se tapahtuu myös aloitteen sivulla valitsemalla "Hylkää". Hylkääminen ei vaadi tunnistautumista.
${invitationUrlFi?trim}

Tämä kutsu on voimassa ${expirationDays} vuorokautta, jonka jälkeen et voi enää vastata kutsuun.
   
-----

<@eb.abstract "fi" "text" />

<#-- SWEDISH -->
---------------------------------------

Du har bjudits in som ${invitationRoleSv?trim} för ett medborgarinitiativ.
            
${currentAuthor.firstNames} ${currentAuthor.lastName} har bjudit in dig som ${invitationRoleSv?trim} för ett medborgarinitiativ. Nedan ser du ett sammandrag av medborgarinitiativets innehåll. 

<@eb.initiativeDetails "sv" "text" />

Bekanta dig med ersättarens förpliktelser i anvisningarna på Medborgarinitiativ.fi: 
${helpUrlSv?trim}

Om du vill godkänna inbjudan som ${invitationRoleSv?trim}, gå till initiativets sida via länken nedan och välj ”Godkänn”. Efter det tas du vidare för att identifiera dig. För identifiering krävs nätbankskoder, mobilcertifikat eller ett chipförsett personkort (HST-kort). 

Om du vill avböja inbjudan gör du även det via initiativets sida genom att välja ”Avböj”. Du behöver inte identifiera dig för att avböja.
${invitationUrlSv?trim}

Denna inbjudan är i kraft i ${expirationDays} dygn. Efter det kan du inte längre svara på inbjudan.
            
-----

<@eb.abstract "sv" "text" />

