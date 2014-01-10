<#escape x as x?html> 
<!DOCTYPE HTML>
<html>
<head>

<script type="text/javascript" src="${urls.baseUrl}/js/jquery-1.7.2.min.js"></script>

<script type="text/javascript">

$(document).ready(function() {

    $('.user-select a').click(function(){
    
        $('input[name="firstName"]').val( $(this).data('first-name') );
        $('input[name="lastName"]').val( $(this).data('last-name') );
        $('input[name="ssn"]').val( $(this).data('ssn') );
        
        $('form').submit();
    });
});


</script>

<#-- For generating random SSN for random supporter. -->
<#function rand min max>
  <#local now = .now?long?c />
  <#local randomNum = _rand +
    ("0." + now?substring(now?length-1) + now?substring(now?length-2))?number />
  <#if (randomNum > 1)>
    <#assign _rand = randomNum % 1 />
  <#else>
    <#assign _rand = randomNum />
  </#if>
  <#return (min + ((max - min) * _rand))?round />
</#function>
<#assign _rand = 0.36 />
<#assign randomSSN = rand(1000, 9999)?c />


</head>
<body>
    <form action="${urls.login()}" method="post">
        <b>HUOM: Tämä sivu on käytössä vain testauksen aikana eikä tule olemaan osa lopullista sovellusta!</b><br/>
        <br/>
        Oikeasti tämän sivun tilalle tulee VETUMA-tunnistus (tunnistus pankkitunnuksilla / sähköisellä henkilökortilla). <br/>
        Testipuolella VETUMA-tunnistuksen korvaa toistaiseksi tämä sivu, koska näin saa helposti käyttöön testauksen kannalta riittävän määrän erilaisia testihenkilöllisyyksiä.
        

        <h4>Kirjaudu käyttäjänä</h4>
        <div class="user-select">
            <a href="#" data-first-name="Anna" data-last-name="Testi" data-ssn="081181-9984" data-home-municipality="Helsinki">Anna Testi, Helsinki - 081181-9984</a><br />
            <a href="#" data-first-name="Maija" data-last-name="Meikäläinen" data-ssn="010170-960F" data-home-municipality="Vantaa">Maija Meikäläinen, Vantaa - 010170-960F</a><br />
            <a href="#" data-first-name="Teemu" data-last-name="Testaaja" data-ssn="010101-123N" data-home-municipality="Pori">Teemu Testaaja, Pori - 010101-123N</a><br />
            <a href="#" data-first-name="Kalle" data-last-name="Kannattaja" data-ssn="210281-9988" data-home-municipality="Pietarsaari">Kalle Kannattaja, Pietarsaari - 210281-9988</a><br />
            <a href="#" data-first-name="Vanja" data-last-name="Varhonen" data-ssn="050505-0005" data-home-municipality="Helsinki">Vanja Varhonen, Helsinki - 050505-0005</a> Generoitujen testialoitteiden varaedustaja<br />
            <br/>
            <a href="#" data-first-name="Satunnainen" data-last-name="Kannattaja" data-ssn="210281-${randomSSN}" data-home-municipality="Pietarsaari">Satunnainen Kannattaja, Lohja - 210281-${randomSSN}</a> - satunnainen HETU (samoja arvoja esiintyy jonkin verran)<br />
            <a href="#" data-first-name="Testaa" data-last-name="Portaalia" data-ssn="210202A9989" data-home-municipality="Tampere">Testaa Portaalia, Tampere - 210202A9989</a> - alaikäinen, ei äänioikeutta<br />
            <br/>
            <a href="#" data-first-name="Oili" data-last-name="Oikkonen" data-ssn="010101-0001" data-home-municipality="Helsinki">Oili Oikkonen, Helsinki - 010101-0001</a> - OM:n virkailija (luotava ensin testidatasivun kautta!)<br />
            <a href="#" data-first-name="Veikko" data-last-name="Verkkonen" data-ssn="020202-0002" data-home-municipality="Helsinki">Veikko Verkkonen, Helsinki - 020202-0002</a> - VRK:n virkailija (luotava testidatasivun kautta!)<br />
        </div>
    
        <h4>Tai täytä tiedot itse</h4>
        <input type="hidden" name="target" value="${target!''?url}">
        <div>Etunimi: <input type="text" name="firstName"/></div>
        <div>Sukunimi: <input type="text" name="lastName"/></div>
        <div>Hetu: <input type="text" name="ssn"/></div>
        <div>Kotikunta: <input type="text" name="homeMunicipality" value="Helsinki"/></div>
        <div>Suomen kansalainen: <input type="checkbox" value="true" name="finnishCitizen" checked="checked"/> </div>
        <input type="submit" name="Login" value="Login"/>
    </form>
</body>
</html>
</#escape> 
