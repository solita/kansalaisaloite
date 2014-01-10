<#escape x as x?html>
<!DOCTYPE HTML>
<html>
<head></head>
<body>

<form action="${vetumaRequest.RETURL}" method="post">

        <input type="hidden" name="RCVID" value="${vetumaRequest.RCVID}"/>
        <input type="hidden" name="APPID" value="${vetumaRequest.APPID!'kansalaisaloite.fi'}"/>
        <input type="hidden" name="TIMESTMP" value="${vetumaRequest.TIMESTMP}"/>
        <input type="hidden" name="SO" value="${vetumaRequest.SO}"/>
        <input type="hidden" name="LG" value="${vetumaRequest.LG}"/>
        <input type="hidden" name="RETURL" value="${vetumaRequest.RETURL}"/>
        <input type="hidden" name="CANURL" value="${vetumaRequest.CANURL}"/>
        <input type="hidden" name="ERRURL" value="${vetumaRequest.ERRURL}"/>
        <input type="hidden" name="EXTRADATA" value="${vetumaRequest.EXTRADATA}"/>
        <input type="hidden" name="TRID" value="${vetumaRequest.TRID!''}"/>
        <input type="hidden" name="STATUS" value="${vetumaRequest.STATUS}"/>
        <input type="hidden" name="MAC" value="${vetumaRequestMAC}"/>
        <input type="hidden" name="VTJDATA" value="${vetumaRequest.VTJDATA}"/>
        <input type="submit" value="Palaa palveluun"/>

</form>

</body>
</html>

</#escape>