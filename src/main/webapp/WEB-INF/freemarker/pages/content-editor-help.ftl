<#import "../components/layout.ftl" as l />
<#import "../components/utils.ftl" as u />

<#escape x as x?html>

<#assign pageTitle>Sisällönhallinnan ohje</#assign>

<#--
 * Navigation for subpages (public view)
 *
 * @param map is the hashMap for navigation items
 * @param titleKey is for navigation block title
-->
<#macro navigation map titleKey="">
    <#if titleKey?has_content><h3 class="navi-title"><@u.message titleKey /></h3></#if>
    <ul class="navi block-style">
        <#list map as link>
            <li ><a href="${urls.help(link.uri)}" <#if link.uri == helpPage>class="active"</#if>>${link.subject}</a></li>
        </#list>
    </ul>
</#macro>

<#--
 * Layout parameters for HTML-title
 *
 * @param page is for example "page.help.general.title"
 * @param pageTitle used in HTML title.
-->
<@l.main "page.help" pageTitle!"">

    <div class="columns cf">
        <#--<div class="column col-1of4 navigation">
            <p> </p>
        </div>-->


        <div class="column col-3of4 last">
            <h1>${pageTitle}</h1>
            
            <div class="view-block">
                <h3>Sisällysluettelo</h3>
                <ol>
                    <li><a href="#h1">Sisällönhallinta</a>
                        <ol>
                            <li><a href="#h1-1">Katselunäkymä</a></li>
                            <li><a href="#h1-2">Muokkausnäkymä</a></li>
                        </ol>
                    </li>
                    <li><a href="#h2">Sisällön muokkaaminen</a>
                        <ol>
                            <li><a href="#h2-1">Otsikon muokkaaminen</a></li>
                            <li><a href="#h2-2">Sisältötekstin muokkaaminen</a></li>
                            <li><a href="#h2-3">Väliotsikon valinta</a></li>
                            <li><a href="#h2-4">Kappalelainaus</a></li>
                            <li><a href="#h2-5">Kuvan lataaminen palvelimelle</a></li>
                            <li><a href="#h2-6">Kuvan valitseminen ja lisääminen sisältöön</a></li>
                            <li><a href="#h2-7">Linkin lisääminen</a></li>
                            <li><a href="#h2-8">HTML-koodin muokkaaminen</a></li>
                        </ol>
                    </li>
                    <li><a href="#h3">Tallentaminen ja julkaisu</a>
                        <ol>
                            <li><a href="#h3-1">Luonnoksen tallentaminen</a></li>
                            <li><a href="#h3-2">Luonnoksen julkaisu</a></li>
                            <li><a href="#h3-3">Julkisen version palauttaminen luonnokseksi</a></li>
                            <li><a href="#h3-4">Sivun näkyvyys vasemmassa navigaatiossa</a></li>
                        </ol>
                    </li>
                </ol>
            </div>
            
            <h2 id="h1">1 Sisällönhallinta</h2>
            
            <p>Sisällönhallinta vaatii oikeusministeriön (OM) käyttäjien oikeudet. Kun olet kirjautunut OM-oikeuksilla sisään, näet "Siirry muokkausnäkymään"-painikkeen Ohjeet- ja Uutiset-sivulla.</p>
            
            <p><strong>HUOM!</strong> Kaikkea muokattavaa sisältöä hallitaan päävalikon Ohjeet-linkin alta. Uutis-sivun julkinen näkymä sijaitsee päävalikon Uutiset-kohdan alla, mutta sen muokkausnäkymä sijaitsee Ohjeet-kohdan alla omana osionaan.</p>
            
            <h3 id="h1-1">1.1 Katselunäkymä</h3>
            
            <p>Klikkaa "Siirry muokkausnäkymään"-painiketta siirtyäksesi muokkausnäkymään. Muokkausnäkymästä voit palata takaisin julkiseen katselunäkymään. Muokkausnäkymä näyttää aina sisällön luonnosta. Julkinen katselunäkymää näyttää aina julkaistua sisältöä.</p>
            
            <p><@u.image src="content-editor-help/katselunakyma.png" alt="Katselunäkymä" cssClass="borders" /></p>
            
            <h3 id="h1-2">1.2 Muokkausnäkymä</h3>
            
            <p>Muokkausnäkymässä näet "Muokkaa"-painikkeen sekä toiminnot: Lataa kuva, julkaise luonnos, palauta julkinen versio sekä linkin tähän ohjeeseen.</p>
            
            <p><@u.image src="content-editor-help/toiminnot.png" alt="Muokkausnäkymä" cssClass="borders" /></p>
            
            <h2 id="h2">2. Sisällön muokkaaminen</h2>
            
            <p>Avaa sisältöeditori klikkaamalla "Muokkaa"-painiketta.</p>
            
            <h3 id="h2-1">2.1 Otsikon muokkaaminen</h3>
            
            <p>Ylin muokattava kenttä on sisällön pääotsikko, joka on tason 1 otsikko. Otsikko-kenttään voi syöttää vain muotoilematonta tekstiä. Otsikko tulee näkyviin vasemman palstan navigaation linkin tekstiksi sekä HTML-dokumentin otsikoksi.</p>
            
            <p>Käytä otsikkona lyhyttä, ytimekästä ja kuvaava tekstiä. Otsikon maksimipituudeksi on asetettu 100 merkkiä.</p>
            
            <p><@u.image src="content-editor-help/otsikon-muokkaus.png" alt="Pääotsikon muokkaus" cssClass="borders" /></p>
            
            <h3 id="h2-2">2.2 Sisältötekstin muokkaaminen</h3>
            
            <p>Alempaan muokattavaan kenttään syötetään sivun varsinainen sisältö. Sisältö-kentän yläpuolella on työkalu-valikko, jossa on toiminnot:</p>
            
            <ul>
                <li>Tekstityylin valinta: Ei mitään, Kappale, Otsikko 2-4, Päivämäärä (jota voidaan käyttää uutisissa)</li>
                <li>Tekstin korostukset: Lihavointi, Kursivointi sekä Alleviivaus</li>
                <li>Listat: järjestämätön (numeroimaton) ja järjestetty (numeroitu) lista</li>
                <li>Kappalelainaus: sisennys ja ulonnus. Toiminto sisentää valitun kappaleen tai poistaa sisennyksen.</li>
                <li>Sisällön HTML-koodin muokkaus</li>
                <li>Lisää linkki -toiminto</li>
                <li>Lisää kuva -toiminto</li>
            </ul>
            
            <p>Muista aina valita leipätekstille tyyli "Kappale". Tällöin kappaleen jälkeen tulee automaattisesti marginaali ennen seuraavaa kappaletta. Jos valitset "Ei mitään", tekstillä ei ole vielä mitään tyyliä ja seuraava rivi alkaa heti perään ilman marginaalia.</p>
            
            <p>Oletus valinta on aina aluksi "Ei mitään", ellei sisällössä satu olemaan valmiiksi "Kappaletta".</p>
            
            <p><@u.image src="content-editor-help/kappaleen-valinta.png" alt="Kappaleen valinta" cssClass="borders" /></p>
            
            <h3 id="h2-3">2.3 Väliotsikon valinta</h3>
            
            <p>Valitse hiirellä otsikon teksti tai vie kursori samalle riville, jossa otsikko on. Tai valitse uudelle riville otsikkotyyli ja kirjoita otsikon teksti. Valitse tekstityylin pudotusvalikosta sopiva otsikkotaso. Sisällössä on käytössä otsikko tasot 2-4. Otsikko 1 on valittu jo sisällön pääotsikoksi, joten sitä ei voi käyttää sisällössä.</p>
            
            <p><@u.image src="content-editor-help/otsikon-valinta.png" alt="Otsikon valinta" cssClass="borders" /></p>
            
            <h3 id="h2-4">2.4 Kappalelainaus</h3>
            
            <p>Valitse sisältö jonka haluat muuttaa lainaukseksi. Voit valita useamman teksti-kappaleen sekä otsikon ja sisentää valitun sisällön. Voit tarvittaessa poistaa sisennyksen valitsemalla sisennetyn sisällön ja valitsemalla sen jälkeen ulonna.</p>
            
            <p><@u.image src="content-editor-help/sisennys.png" alt="Sisennys" cssClass="borders" /></p>
            
            <h3 id="h2-5">2.5 Kuvan lataaminen palvelimelle</h3>
            
            <p>Kuvan voi ladata palvelimelle muokkausnäkymässä silloin kun sisältöeditori ei ole aktiivisena. Jos sinulla on muokkaus kesken, tallenna ensin luonnos ja lataa kuva vasta sen jälkeen.</p>
            
            <p>Voit ladata tietokoneeltasi JPG- ja PNG-muotoisia kuvia. Valokuvat kannattaa olla JPG-muodossa. Isoja selkeitä väripintoja sisältävät kuvat tai piirroskuvat kannattaa olla PNG-muodossa. Kuvan maksimileveys on 710 pikseliä. Kuvan on suositeltavaa olla valmiiksi oikeassa koossa palvelimelle ladattaessa, koska lataaja ei skaalaa sitä mitenkään.</p>
            
            <p>Valitse Toiminnot-valikon alta Lataa kuva. Sivulle aukeaa modal-ikkuna, jolla voit valita kuvan tietokoneeltasi ja ladata sen palvelimelle. Latauksen onnistuttua sivulle ilmestyy vihreäpohjainen viesti onnistumisesta.</p>

            <p>Jos kuvan tiedostonnimellä on muu pääte kuin .jpg tai .png, tulee kuvanlatauksessa virhe. Virhe tulee myös, jos yritetään ladata tiedostoa, joka ei ole kuva.</p>
            
            <p><@u.image src="content-editor-help/toiminnot.png" alt="Toiminnot" cssClass="borders" /></p>
            
            <p><@u.image src="content-editor-help/lataa-kuva.png" alt="Lataa kuva" cssClass="borders" /></p>
            
            <h3 id="h2-6">2.6 Kuvan valitseminen ja lisääminen sisältöön</h3>
            
            <p>Kun kuva on ladattu palvelimelle, se voidaan lisätä sisältöön. Avaa sisältö muokkaukseen klikkaamalla "Muokkaa"-painiketta. Mene hiiren kursorilla kohtaan, johon haluat lisätä kuvan. Klikkaa Lisää kuva -painiketta. Valitse haluamasi kuva valinta-ikkunasta. <strong>HUOM!</strong> Muista kirjoittaa kuvalle aina kuvaava kuvausteksti.</p>
            
            <p>Voit siirtää kuvaa paikasta toiseen sisällössä leikkaa/liimaa-menetelmällä tai raahaamalla.</p>
            
            <p><@u.image src="content-editor-help/kuvan-valinta.png" alt="Kuvan valinta" cssClass="borders" /></p>
            
            <h3 id="h2-7">2.7 Linkin lisääminen</h3>
            
            <p>Voit luoda linkkejä kahdella tavalla. Joko niin, että linkin osoite toimii tekstinä tai niin, että linkillä on kuvaava teksti.</p>
            
            <h4>Linkin osoite tekstinä</h4>
            
            <p>Linkin muoto on esimerkiksi: "https://www.kansalaisaloite.fi"</p>
            
            <p>Vie hiiren kursori kohtaan, johon haluat luoda linkin. Klikkaa Lisää linkki -painiketta. Kirjoita linkin teksti sekä valitse haluatko sen aukeavan uuteen ikkunaan (ulkoinen linkki) vai et (sisäinen linkki). Jos valitset, että linkki aukeaa uuteen ikkunaan, linkin perään tulee pieni ikoni. Klikkaa sen jälkeen vielä Lisää linkki -painiketta.</p>
           
            <h4>Linkillä kuvaava teksti</h4>
            
            <p>Linkin muoto on esimerkiksi: "Kansalaisaloite"</p> 
            
            <p>Valitse sisällöstä teksti, josta haluat tehdä linkin. Valinnan jälkeen toimi kuten edellä, eli klikkaa Lisää linkki -painiketta ja jatka edellä mainitun ohjeen mukaan.</p>
            
            <p><@u.image src="content-editor-help/linkin-lisaaminen.png" alt="Linkin lisääminen" cssClass="borders" /></p>
            
            <h3 id="h2-8">2.8 HTML-koodin muokkaaminen</h3>
            
            <p>Voit halutessasi muokata suoraan HTML-koodia. Tämä vaatii tuntemusta HTML-koodista.</p>
            
            <p>Tällä toiminnolla näet sisällön suoraan HTML-kooditasolla, jolloin voit siivota tarvittaessa koodia tai korjata virheellisesti syötettyä sisältöä. <strong>HUOM!</strong> Et voi syöttää mitä tahansa HTML-koodia vaan ainoastaan niitä sallittuja elementtejä, jotka ovat valittavissa editorin työkalu-valikosta.</p>
            
            <p><@u.image src="content-editor-help/html-muokkaus.png" alt="HTML-muokkaus" cssClass="borders" /></p>
            
            <h2 id="h3">3. Tallentaminen ja julkaisu</h2>
            
            <h3 id="h3-1">3.1 Luonnoksen tallentaminen</h3>
            
            <p>Tallenna luonnos klikkaamalla "Tallenna"-painiketta. Luonnoksen tallentaminen ei vielä julkaise sisältöä. Voit siis rauhassa muokata luonnosta ja tallentaa sen seuraavaa muokkausta tai esikatselua varten.</p>
            
            <h3 id="h3-2">3.2 Luonnoksen julkaiseminen</h3>
            
            <p>Kun luonnos valmis julkaistavaksi, valitse Toiminnot-valikosta "Julkaise luonnos". Tämän jälkeen sivulle ilmestyy vielä varmistus-ikkuna. Klikkaa ikkunasta "Julkaise luonnos"-painiketta. Jos sisällön julkaiseminen onnistuu, siitä ilmoitetaan vihreäpohjaisella ilmoituksella. Jos tapahtuu jokin virhe, yritä uudelleen.</p>
            
            <p><@u.image src="content-editor-help/julkaise-luonnos.png" alt="Julkaise luonnos" cssClass="borders" /></p>
            
            <h3 id="h3-3">3.3 Julkisen version palauttaminen luonnokseksi</h3>
            
            <p>Julkinen sisältö voidaan tarvittaessa palauttaa takaisin luonnokseksi. Huomaa, että silloin nykyinen luonnos menetetään.</p>
            
            <p>Palauta julkinen versio luonnokseksi valitsemalla toiminnot-valikosta "Palauta julkinen versio". Tämän jälkeen sivulle ilmestyy vielä varmistus-ikkuna. Klikkaa ikkunasta "Palauta julkinen versio"-painiketta. Jos sisällön palauttaminen onnistuu, siitä ilmoitetaan vihreäpohjaisella ilmoituksella. Jos tapahtuu jokin virhe, yritä uudelleen.</p>
            
            <p><@u.image src="content-editor-help/palauta-julkinen-versio.png" alt="Palauta julkinen versio" cssClass="borders" /></p>
            
            <h3 id="h3-4">3.4 Sivun näkyvyys vasemmassa navigaatiossa</h3>
            
            <p>Sivu näytetään katselunäkymässä eli julkisena vasemmassa valikossa ainoastaan silloin kun sisällön julkinen pääotsikko ei ole tyhjä. Sivulle on joka tapauksessa julkinen pääsy, jos tietää suoran osoitteen sivulle.</p>
            
            <p>Jos vasemmassa navigaatiossa on linkki, jonka teksti on "[TYHJÄ]", ei linkki näy julkisessa katselunäkymässä lainkaan.</p>
            
            <p>Luonnoksen otsikkoa ei näytetä vasemmassa navigaatiossa, vaan siinä näytetään aina julkisen version otsikko. Sivu voidaan poistaa vasemmasta navigaatiosta julkaisemalla sisältö tyhjällä otsikolla. Luonnokseen voidaan tallentaa otsikko, kunhan sitä ei julkaista.</p>
            
            <p><@u.image src="content-editor-help/vasen-navigaatio.png" alt="Sivun näkyvyys vasemmassa navigaatiossa" cssClass="borders" /></p>
        </div>
    </div>

</@l.main>
</#escape>

