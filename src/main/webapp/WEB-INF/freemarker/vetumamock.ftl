<#escape x as x?html>
<!DOCTYPE HTML>
<html>
<head></head>
<body>

<p>Tämä sivu korvaa vetuman. Muu järjestelmä luulee oikeasti keskustelevansa vetuman kanssa.</p>

<form action="/vetumamockreturn" method="post">

    <input type="hidden" name="RCVID" value="${vetumaRequest.RCVID}"/>
    <input type="hidden" name="APPID" value="${vetumaRequest.APPID!'kansalaisaloite.fi'}"/>
    <input type="hidden" name="TIMESTMP" value="${vetumaRequest.TIMESTMP}"/>
    <input type="hidden" name="SO" value="${vetumaRequest.SO}"/>
    <input type="hidden" name="LG" value="${vetumaRequest.LG}"/>
    <input type="hidden" name="RETURL" value="${vetumaRequest.RETURL}"/>
    <input type="hidden" name="CANURL" value="${vetumaRequest.CANURL}"/>
    <input type="hidden" name="ERRURL" value="${vetumaRequest.ERRURL}"/>
    <!--<input type="hidden" name="EXTRADATA" value="${vetumaRequest.EXTRADATA}"/>-->
    <input type="hidden" name="TRID" value="${vetumaRequest.TRID!''}"/>

    <input type="text" name="first_name" value="Matti Petteri"/><br>
    <input type="text" name="last_name" value="Meikalainen"/><br/>
    <input type="text" name="municipality_fi" value="Helsinki"/>
    <input type="text" name="municipality_sv" value="Helsingfors"/><br/>
    <input id="hetu" type="text" name="EXTRADATA" value="HETU=010190-0000"/>

    <a href="#" onclick="document.getElementById('hetu').value = 'HETU=010101-0001';">om</a> /
    <a href="#" onclick="document.getElementById('hetu').value = 'HETU=020202-0002';">vrk</a>

    <br/><label><input type="checkbox" checked="checked" name="fi" value="1"/> Suomen kansalainen</label><br>
    <br/>
    Kirjautumiskoodi:
    <select name="STATUS"/>
        <option value="SUCCESSFUL">SUCCESSFUL</option>
        <option value="CANCELLED">CANCELLED</option>
        <option value="REJECTED">REJECTED</option>
        <option value="ERROR">ERROR</option>
        <option value="FAILURE">FAILURE</option>
    </select>
    <br/>
    <input type="submit" value="Leiki vetumaa"/>

</form>

</body>
</html>

</#escape>