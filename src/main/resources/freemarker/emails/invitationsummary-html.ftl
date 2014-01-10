<#import "../components/email-layout-html.ftl" as el />
<#import "../components/email-utils.ftl" as eu />
<#import "../components/email-blocks.ftl" as eb />

<#escape x as x?html> 

<#assign titleFi="Kansalaisaloitteesi odottaa vastausta vastuuhenkilöiltä" />
<#assign titleSv="Ditt medborgarinitiativ väntar på svar från ansvarspersonerna" />

<#assign helpUrlFi>${baseURL?trim}/fi/ohjeet</#assign>
<#assign helpUrlSv>${baseURL?trim}/sv/anvisningar</#assign>

<@el.emailHtml "invitation-summary" titleFi>

    <#-- FINNISH -->
    <@eb.emailTemplate "fi" titleFi>
        <p style="margin-bottom:0.5em;">Kansalaisaloitteesi <i>${(initiative.name['fi']!initiative.name['sv']!"")}</i> odottaa vastausta vastuuhenkilöiltä lähettämiisi sähköpostikutsuihin.</p>
        
        <p style="margin-top:0.5em;">Nimetyille vastuuhenkilöille on lähetetty sähköpostikutsut. Asian etenemisestä lisätietoa alla:</p>
        
        <h4 style="font-size:12px; margin:1em 0 0.5em 0;">Miten aloitteen käsittely etenee seuraavaksi?</h4>
        <ol style="margin-top:0.5em;">
            <li style="margin-top:0.5em; margin-bottom:0.5em;">Seuraavaksi kutsuttujen vastuuhenkilöiden täytyy hyväksyä (tai hylätä) saamansa kutsut. Kutsut ovat voimassa ${expirationDays} vuorokautta.</li>
            <li style="margin-bottom:0.5em;">Kun aloitteella on vähintään yksi vireillepanija, edustaja sekä varaedustaja, vastuuhenkilö voi lähettää aloitteen oikeusministeriöön tarkastettavaksi.  Huomaa, että verkkopalvelu ei erikseen lähetä tietoa muiden vastuuhenkilöiden hyväksymästä tai hylkäämästä kutsusta.  Voit kirjautua palveluun ja seurata aloitteen tilaa aloitteen osoitteesta tai selaamalla “Selaa kansalaisaloitteita” => “Omat kansalaisaloitteeni”.</li>
            <li style="margin-bottom:0.5em;">Oikeusministeriön tarkastuksen jälkeen kannatusilmoitusten kerääminen kansalaisaloite.fi-palvelussa alkaa automaattisesti. Huomioi, että kerääminen alkaa aikaisintaan antamanasi päivänä <@eu.localDate initiative.startDate "fi" /></li>
        </ol>
    
        <h4 style="font-size:12px; margin:1em 0 0.5em 0;">Näkyykö aloitteeni jokansalaisaloite.fi-palvelussa?</h4>
        <p style="margin-top:0.5em;">Aloite löytyy toistaiseksi vain linkin kautta, eli jos tiedät sen osoitteen. Kun oikeusministeriö on tarkastanut aloitteen tulee se näkyviin myös Kansalaisaloite.fi-palvelun hakuihin.</p>
        
        <h4 style="font-size:12px; margin:1em 0 0.5em 0;">Mistä osoitteesta aloite nyt löytyy?</h4>
        <p style="margin-top:0.5em;">Aloitteesi sijaitsee osoitteessa <@eu.link viewUrlFi /></p>
        
        <h4 style="font-size:12px; margin:1em 0 0.5em 0;">Mistä saan lisätietoa?</h4>
        <p style="margin-top:0.5em;">Voit tutustua aloitteen tekemisen ohjeistuksiin Kansalaisaloite.fi-palvelun <@eu.link helpUrlFi "ohjeistossa" />.</p>
        <p style="margin-top:0.5em;">Tarvittaessa ota yhteyttä <@eu.link "mailto:kansalaisaloite.om@om.fi" "kansalaisaloite.om@om.fi" /></p>
    
    </@eb.emailTemplate>
        
    <#-- SWEDISH -->
    <@eb.emailTemplate "sv" titleSv>      
          
        <p style="margin-bottom:0.5em;">Ditt medborgarinitiativ <i>${(initiative.name['sv']!initiative.name['fi']!"")}</i> väntar på svar från ansvarspersonerna på de e-postinbjudningar du skickat.</p>
        <p style="margin-top:0.5em;">De namngivna ansvarspersonerna har fått e-postinbjudningar. Mer information om hur ärendet framskrider nedan:</p>
        
        <h4 style="font-size:12px; margin:1em 0 0.5em 0;">Hur framskrider behandlingen av initiativet nu?</h4>
        <ol style="margin-top:0.5em;">
            <li style="margin-top:0.5em; margin-bottom:0.5em;">Nu måste de inbjudna ansvarspersonerna godkänna (eller avböja) de inbjudningar de fått. Inbjudningarna är i kraft i ${expirationDays} dygn.</li>
            <li style="margin-bottom:0.5em;">Om initiativet har minst en initiativtagare, företrädare och ersättare, kan ansvarspersonen skicka initiativet till justitieministeriet för granskning. Observera att webbtjänsten inte separat meddelar om de övriga ansvarspersonerna har godkänt eller avböjt inbjudan. Du kan logga in i tjänsten och följa med hur initiativet framskrider på initiativets adress eller via ”Bläddra bland initiativ” => ”Mina medborgarinitiativ”.</li>
            <li style="margin-bottom:0.5em;">Efter justitieministeriets granskning börjar insamlingen av stödförklaringar automatiskt på webbtjänsten medborgarinitiativ.fi. Observera att insamlingen tidigast börjar på den dag du angivit <@eu.localDate initiative.startDate "sv" />.</li>
        </ol>
    
        <h4 style="font-size:12px; margin:1em 0 0.5em 0;">Syns mitt initiativ redan på webbtjänsten medborgarinitiativ.fi?</h4>
        <p style="margin-top:0.5em;">Tills vidare kommer du endast åt initiativet via en länk, alltså endast om du känner till adressen. När justitieministeriet har granskat initiativet kommer det att synas även i sökningar på medborgarinitiativ.fi.</p>
        
        <h4 style="font-size:12px; margin:1em 0 0.5em 0;">På vilken adress finns initiativet nu?</h4>
        <p style="margin-top:0.5em;">Ditt initiativ finns på adressen <@eu.link viewUrlSv /></p>
        
        <h4 style="font-size:12px; margin:1em 0 0.5em 0;">Var får jag mer information?</h4>
        <p style="margin-top:0.5em;">Du kan bekanta dig med <@eu.link helpUrlSv "anvisningarna" /> för hur man gör ett initiativ på webbtjänsten Medborgarinitiativ.fi.</p>
        <p style="margin-top:0.5em;">Ta vid behov kontakt på adressen <@eu.link "mailto:kansalaisaloite.om@om.fi" "kansalaisaloite.om@om.fi" /></p>
    
    </@eb.emailTemplate>

</@el.emailHtml>

</#escape> 