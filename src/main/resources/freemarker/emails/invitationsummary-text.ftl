<#import "../components/email-utils.ftl" as eu />
<#import "../components/email-blocks.ftl" as eb />

<#-- TODO: Put these in one place, for example as globals or an include -->
<#assign viewUrlFi>${urlsFi.view(initiative.id)}</#assign>
<#assign viewUrlSv>${urlsSv.view(initiative.id)}</#assign>
<#assign helpUrlFi>${baseURL?trim}/fi/ohjeet</#assign>
<#assign helpUrlSv>${baseURL?trim}/sv/anvisningar</#assign>

<#-- FINNISH -->
Kansalaisaloitteesi “${(initiative.name['fi']!"")}” odottaa vastausta vastuuhenkilöiltä lähettämiisi sähköpostikutsuihin.

Nimetyille vastuuhenkilöille on lähetetty sähköpostikutsut. Asian etenemisestä lisätietoa alla:

Miten aloitteen käsittely etenee seuraavaksi?
1. Seuraavaksi kutsuttujen vastuuhenkilöiden täytyy hyväksyä (tai hylätä) saamansa kutsut. Kutsut ovat voimassa ${expirationDays} vuorokautta.
2. Kun aloitteella on vähintään yksi vireillepanija, edustaja sekä varaedustaja, vastuuhenkilö voi lähettää aloitteen oikeusministeriöön tarkastettavaksi.  Huomaa, että verkkopalvelu ei erikseen lähetä tietoa muiden vastuuhenkilöiden hyväksymästä tai hylkäämästä kutsusta.  Voit kirjautua palveluun ja seurata aloitteen tilaa aloitteen osoitteesta tai selaamalla “Selaa kansalaisaloitteita” => “Omat kansalaisaloitteeni”.
3. Oikeusministeriön tarkastuksen jälkeen kannatusilmoitusten kerääminen kansalaisaloite.fi-palvelussa alkaa automaattisesti. Huomioi, että kerääminen alkaa aikaisintaan antamanasi päivänä <@eu.localDate initiative.startDate "fi" />.

Näkyykö aloitteeni jo Kansalaisaloite.fi-palvelussa?
Aloite löytyy toistaiseksi suoran linkin kautta, mutta ei näy kansalaisaloite.fi-palvelun aloitehaussa. Kun oikeusministeriö on tarkastanut aloitteen JA kannatusilmoituksia on kertynyt vähintään 50 kappaletta, aloitteesi tulee näkyviin myös Kansalaisaloite.fi-palvelun hakuihin.

Mistä osoitteesta aloite nyt löytyy?
Aloitteesi sijaitsee osoitteessa ${viewUrlFi?trim}

Mistä saan lisätietoa?
Voit tutustua aloitteen tekemisen ohjeistuksiin Kansalaisaloite.fi-palvelun ohjeistossa:
${helpUrlFi}

Tarvittaessa ota yhteyttä kansalaisaloite.om@om.fi
    
---------------------------------------
    
<#-- SWEDISH -->      
Ditt medborgarinitiativ väntar på svar från ansvarspersonerna

Ditt medborgarinitiativ “${(initiative.name['sv']!"")}” väntar på svar från ansvarspersonerna på de e-postinbjudningar du skickat.

De namngivna ansvarspersonerna har fått e-postinbjudningar. Mer information om hur ärendet framskrider nedan: 

Hur framskrider behandlingen av initiativet nu?
1. Nu måste de inbjudna ansvarspersonerna godkänna (eller avböja) de inbjudningar de fått. Inbjudningarna är i kraft i ${expirationDays} dygn. 
2. Om initiativet har minst en initiativtagare, företrädare och ersättare, kan ansvarspersonen skicka initiativet till justitieministeriet för granskning. Observera att webbtjänsten inte separat meddelar om de övriga ansvarspersonerna har godkänt eller avböjt inbjudan. Du kan logga in i tjänsten och följa med hur initiativet framskrider på initiativets adress eller via ”Bläddra bland initiativ” => ”Mina medborgarinitiativ”. 
3. Efter justitieministeriets granskning börjar insamlingen av stödförklaringar automatiskt på webbtjänsten medborgarinitiativ.fi. Observera att insamlingen tidigast börjar på den dag du angivit <@eu.localDate initiative.startDate "sv" />.

Syns mitt initiativ redan på webbtjänsten medborgarinitiativ.fi?
Initiativet finns tillsvidare under direktlänken, men syns inte i initiativsökningen på medborgarinitiativ.fi. När justitieministeriet har granskat initiativet OCH minst 50 stödförklaringar har samlats in, syns ditt initiativ också bland sökningarna på tjänsten.

På vilken adress finns initiativet nu?
Ditt initiativ finns på adressen ${viewUrlSv?trim}

Var får jag mer information?
Du kan bekanta dig med anvisningarna för hur man gör ett initiativ på webbtjänsten Medborgarinitiativ.fi:
${helpUrlSv}

Ta vid behov kontakt på adressen kansalaisaloite.om@om.fi