<#import "components/utils.ftl" as u />

<#escape x as x?html> 
<!DOCTYPE HTML>
<html>
<head>
<script type="text/javascript">
window.onload = function () {
    var form = document.getElementById("VetumaLogin");
    form.submit();
};
</script>
</head>
<body>
    <form id="VetumaLogin" action="${vetumaURL}" method="POST">
        <input type="hidden" name="RCVID" value="${vetumaRequest.RCVID}"/>
        <input type="hidden" name="APPID" value="${vetumaRequest.APPID!'kansalaisaloite.fi'}"/>
        <input type="hidden" name="TIMESTMP" value="${vetumaRequest.TIMESTMP}"/>
        <input type="hidden" name="SO" value="${vetumaRequest.SO}"/>
        <input type="hidden" name="SOLIST" value="${vetumaRequest.SOLIST}"/>
        <input type="hidden" name="TYPE" value="${vetumaRequest.TYPE}"/>
        <input type="hidden" name="AU" value="${vetumaRequest.AU}"/>
        <input type="hidden" name="LG" value="${vetumaRequest.LG}"/>
        <input type="hidden" name="RETURL" value="${vetumaRequest.RETURL}"/>
        <input type="hidden" name="CANURL" value="${vetumaRequest.CANURL}"/>
        <input type="hidden" name="ERRURL" value="${vetumaRequest.ERRURL}"/>
        <input type="hidden" name="AP" value="${vetumaRequest.AP}"/>
        <input type="hidden" name="MAC" value="${vetumaRequest.MAC}"/>
        <input type="hidden" name="EXTRADATA" value="${vetumaRequest.EXTRADATA}"/>
        <input type="hidden" name="APPNAME" value="${vetumaRequest.APPNAME}"/>
        <input type="hidden" name="TRID" value="${vetumaRequest.TRID!''}"/>
        <input type="submit" value="<@u.message "common.continueToVetuma" />"/>
    </form>
</body>
</html>
</#escape> 
