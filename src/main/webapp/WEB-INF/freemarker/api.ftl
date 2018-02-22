<#import "components/layout.ftl" as l /> 
<#import "components/utils.ftl"as u /> 
<#escape x as x?html> <@l.main "page.api">

<h1>Open Data API</h1>

<p>The Open Data API provides the same information about initiatives as the site's user interface does. This service contains two Open Data access points: one for listing public initiatives and
one for details of an individual initiative. Both interfaces support <a href="http://www.json.org/">JSON</a> 
and <a href="http://en.wikipedia.org/wiki/JSONP">JSONP</a> formats.</p>

<h3>List of Public Initiatives</h3>

<p><a href="${urls.initiatives()}">${urls.initiatives()}</a></p>

<p>Returns a <a href="${urls.search()}?show=all">list</a> of initiatives with <a href="#properties">Basic properties</a>.
Id of an initiative is an URI of initiative details in <a href="http://www.json.org/">JSON</a> format.</p>

<p>Parameters <tt>${UrlConstants.JSON_OFFSET}</tt> and <tt>${UrlConstants.JSON_LIMIT}</tt> may be used to restrict the results.
Maximum amount of initiatives to return is ${UrlConstants.MAX_INITIATIVE_JSON_RESULT_COUNT} and default is ${UrlConstants.DEFAULT_INITIATIVE_JSON_RESULT_COUNT}.<br/>
The list includes only initiatives with at least 50 support counts by default. This can be changed with <tt>${UrlConstants.JSON_MINSUPPORTCOUNT}</tt>.
</p>

<p>Results might be ordered with parameter <tt>${UrlConstants.JSON_ORDER_BY}</tt>. Possible values are
    <tt>
    <#list orderByValues as o>
    ${o}<#if orderByValues?size - 2 = o_index>  and<#elseif o_has_next>,<#elseif orderByValues?size - 1 = o_index>.</#if>
    </#list>
    </tt>
</p>

<p><a href="${urls.initiatives()}?${UrlConstants.JSON_OFFSET}=10&${UrlConstants.JSON_LIMIT}=10&${UrlConstants.JSON_MINSUPPORTCOUNT}=100&${UrlConstants.JSON_ORDER_BY}=mostTimeLeft">${urls.initiatives()}&${UrlConstants.JSON_OFFSET}=10&${UrlConstants.JSON_LIMIT}=10&${UrlConstants.JSON_MINSUPPORTCOUNT}=100${UrlConstants.JSON_ORDER_BY}=mostTimeLeft</a></p>

<h3>Initiative Details</h3>
<p>${urls.baseUrl}${UrlConstants.INITIATIVE}</p>

<p><a href="#properties">Details</a> of the initiative in <a href="http://www.json.org/">JSON</a> format.</p>

<h3>Initiative support counts per date</h3>
<p>${urls.baseUrl}${UrlConstants.SUPPORTS_BY_DATE}</p>
<p>List holding date-value pairs presenting the amounts of statements of support the initiative has gathered until yesterday. Is updated nightly.</p>

<h3>JSONP</h3>

<p>All interfaces support also <a href="http://en.wikipedia.org/wiki/JSONP">JSONP</a> format. Callback is given with <tt>${UrlConstants.JSONP_CALLBACK}</tt> parameter. E.g.<br/>
<a href="${urls.initiatives()}?${UrlConstants.JSONP_CALLBACK}=myCallback">${urls.initiatives()}?${UrlConstants.JSONP_CALLBACK}=myCallback</a>.</p>


<h3 id="properties">Initiative properties</h3>

<table class="data">
  <thead>
    <tr>
        <th>Example
        </th>
        <th>Data Type
        </th>
        <th>
        B = Basic<br/>
        D = Detail
        </th>
        <th>Description
        </th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td class="apiExample">
{</td>
      <td>Object</td>
      <td></td>
      <td>Initiative</td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>id</b>":"${urls.initiative(1234)}",
      </td>
      <td>String (URI)</td>
      <td>B</td>
      <td>URI of this public initiative</td>
    </tr>

    <tr>
        <td class="apiExample">&nbsp;&nbsp;"<b>url</b>":{</td>
        <td>Object</td>
        <td>B</td>
        <td>URL:s to initiatives public page</td>
    </tr>
    <tr>
        <td class="apiExample">
            &nbsp;&nbsp;&nbsp;&nbsp;"<b>fi</b>":"https://www.kansalaisaloite.fi/fi/aloite/1234",
        <td>String</td>
        <td>B</td>
        <td>Finnish URL
        </td>
    </tr>
    <tr>
        <td class="apiExample">
            &nbsp;&nbsp;&nbsp;&nbsp;"<b>sv</b>":"https://www.kansalaisaloite.fi/sv/initiativ/1234",
        <td>String</td>
        <td>B</td>
        <td>Swedish URL
        </td>
    </tr>
    <tr>
        <td class="apiExample">&nbsp;&nbsp;},</td>
        <td></td>
        <td></td>
        <td>
        </td>
    </tr>

    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>modified</b>":"2012-11-01T13:50:30+02:00",
      </td>
      <td>String (xsd:dateTime)</td>
      <td>B</td>
      <td>Date and time of last modification
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>state</b>":"ACCEPTED",</td>
      <td>String (enum)</td>
      <td>B</td>
      <td>Initiative state:<br/>
        ACCEPTED - Initiative has been accepted by Ministry of Justice<br/>
        DONE - Initiative has been sent to parliament<br/>
        CANCELED - Initiative has been canceled
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>stateDate</b>":"2012-10-31T17:57:45+02:00",
      </td>
      <td>String (xsd:dateTime)</td>
      <td>B</td>
      <td>Date and time of last state change
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>supportCount</b>":0,</td>
      <td>Integer</td>
      <td>B</td>
      <td>Amount of statements of support in this service
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>externalSupportCount</b>":500,</td>
      <td>Integer</td>
      <td>B</td>
      <td>Amount of statements of support collected outside this service
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>totalSupportCount</b>":500,</td>
      <td>Integer</td>
      <td>B</td>
      <td>Total amount of statements of support (supportCount + externalSupportCount)
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>sentSupportCount</b>":0,</td>
      <td>Integer</td>
      <td>B</td>
      <td>Amount of statements of support sent to Population Register Center
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>verifiedSupportCount</b>":0,</td>
      <td>Integer</td>
      <td>B</td>
      <td>Amount of statements of support verified by Population Register Center
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>verified</b>":null,</td>
      <td>String (xsd:date)</td>
      <td>B</td>
      <td>Date of resolution by Population Register Center
      </td>
    </tr>
    <tr>
      <td class="apiExample">&nbsp;&nbsp;"<b>name</b>":{</td>
      <td>Object</td>
      <td>B</td>
      <td>Name of the initiative in Finnish and/or Swedish
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;&nbsp;&nbsp;"<b>fi</b>":"Nimi Suomeksi",</td>
      <td>String</td>
      <td>B</td>
      <td>Name in Finnish
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;&nbsp;&nbsp;"<b>sv</b>":null</td>
      <td>String</td>
      <td>B</td>
      <td>Name in Swedish
      </td>
    </tr>
    <tr>
      <td class="apiExample">&nbsp;&nbsp;},</td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>startDate</b>":"2012-10-31",</td>
      <td>String (xsd:date)</td>
      <td>B</td>
      <td>Date when the gathering of statements of support may begin
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>endDate</b>":"2012-11-02",</td>
      <td></td>
      <td></td>
      <td>Date when the gathering of statements of support ends (inclusive)
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>proposalType</b>":"LAW",</td>
      <td>String (enum)</td>
      <td>B</td>
      <td>Type of initiative:<br/>
      LAW - proposal for law<br/>
      PREPARATION - proposal to start preparing a law
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>primaryLanguage</b>":"fi",</td>
      <td>String (enum)</td>
      <td>B</td>
      <td>Primary language of the initiative
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>financialSupport</b>":false,</td>
      <td>Boolean</td>
      <td>B</td>
      <td>Does this initiative get financial support?
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>financialSupportURL</b>":null,</td>
      <td>String (URL)</td>
      <td>B</td>
      <td>URL of the financial support notification
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>supportStatementsOnPaper</b>":true,</td>
      <td>Boolean</td>
      <td>B</td>
      <td>Are statements of support collected on paper?
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>supportStatementsInWeb</b>":true,</td>
      <td>Boolean</td>
      <td>B</td>
      <td>Are statements of support collected in another web service?
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;"<b>supportStatementsRemoved</b>":null,</td>
      <td>String (xsd:dateTime)</td>
      <td>B</td>
      <td>Date and time of support notification removal.
      </td>
    </tr>
    <tr>
      <td class="apiExample">&nbsp;&nbsp;"<b>proposal</b>":{</td>
      <td>Object</td>
      <td>D</td>
      <td>Proposal text
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;&nbsp;&nbsp;"<b>fi</b>":"Ehdotus suomeksi.",</td>
      <td>String</td>
      <td>D</td>
      <td>Proposal text in Finnish
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;&nbsp;&nbsp;"<b>sv</b>":null</td>
      <td>String</td>
      <td>D</td>
      <td>Proposal text in Swedish
      </td>
    </tr>
    <tr>
      <td class="apiExample">&nbsp;&nbsp;},</td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
    <tr>
      <td class="apiExample">&nbsp;&nbsp;"<b>rationale</b>":{
      </td>
      <td>Object</td>
      <td>D</td>
      <td>Rationale of the proposal
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;&nbsp;&nbsp;"<b>fi</b>":"Perustelut suomeksi.",
      </td>
      <td>String</td>
      <td>D</td>
      <td>Rationale in Finnish
      </td>
    </tr>
    <tr>
      <td class="apiExample">
        &nbsp;&nbsp;&nbsp;&nbsp;"<b>sv</b>":null
      </td>
      <td>String</td>
      <td>D</td>
      <td>Rationale in Swedish
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;"<b>links</b>":[
      </td>
      <td>Array</td>
      <td>D</td>
      <td>Links for further information about this initiative
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;{
      </td>
      <td>Object</td>
      <td>D</td>
      <td>Link details
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>uri</b>":"http://www.om.fi",
      </td>
      <td>String (URL)</td>
      <td>D</td>
      <td>URL of the link
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>label</b>":"Oikeusministeriö"
      </td>
      <td>String</td>
      <td>D</td>
      <td>Label of the link in Finnish or Swedish
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;}
      </td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;],
      </td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;"<b>initiators</b>":[
      </td>
      <td>Array</td>
      <td>D</td>
      <td>Initiators of this initiative
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;{
      </td>
      <td>Object</td>
      <td>D</td>
      <td>Initiator details
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>firstNames</b>":"John",
      </td>
      <td>String</td>
      <td>D</td>
      <td>First names of this initiator
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>lastName</b>":"Doe",
      </td>
      <td>String</td>
      <td>D</td>
      <td>Last name of this initiator
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>homeMunicipality</b>":{
      </td>
      <td>Object</td>
      <td>D</td>
      <td>Home municipality of this initiator
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>fi</b>":"Helsinki",
      </td>
      <td>String</td>
      <td>D</td>
      <td>Municipality name in Finnish
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>sv</b>":"Helsingfors"
      </td>
      <td>String</td>
      <td>D</td>
      <td>Municipality name in Swedish
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
      </td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;}
      </td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;],
      </td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
    <tr>
      <td class="apiExample" id="representativeDetails">
&nbsp;&nbsp;"<b>representatives</b>":[
      </td>
      <td>Array</td>
      <td>D</td>
      <td>Representatives of this iniative
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;{
      </td>
      <td>Object</td>
      <td>D</td>
      <td>Representative details
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>firstNames</b>":"Jane",
      </td>
      <td>String</td>
      <td>D</td>
      <td>First names of the representative
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>lastName</b>":"Doe",
      </td>
      <td>String</td>
      <td>D</td>
      <td>Last name of the representative
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>homeMunicipality</b>":{
      </td>
      <td>Object</td>
      <td>D</td>
      <td>Home municipality of the initiator
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>fi</b>":"Helsinki",
      </td>
      <td>String</td>
      <td>D</td>
      <td>Municipality name in Finnish
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>sv</b>":"Helsingfors"
      </td>
      <td>String</td>
      <td>D</td>
      <td>Municipality name in Swedish
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
      </td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>contactInfo</b>":{
      </td>
      <td>Object</td>
      <td>D</td>
      <td>Contact information for the representative (one required)
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>email</b>":"firstname&nbsp;lastname&nbsp;&nbsp;domain&nbsp;fi",
      </td>
      <td>String</td>
      <td>D</td>
      <td>Email address of the representative. To make spammers lives even little more difficult, "." has been replaced with one and "@" with two whitespace characters</tt>
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>phone</b>":"09-123456",
      </td>
      <td>String</td>
      <td>D</td>
      <td>Phone number of the representative
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<b>address</b>":"PO BOX 25, FI-00023 Government"
      </td>
      <td>String</td>
      <td>D</td>
      <td>Contact address of the representative
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
      </td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;}
      </td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;],
      </td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;"<b>reserves</b>":[
      </td>
      <td>Object</td>
      <td>D</td>
      <td>Reserve representatives of this initiative
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;{
      </td>
      <td>Object</td>
      <td>D</td>
      <td>Reserve representative details
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...
      </td>
      <td></td>
      <td></td>
      <td>Same format as in <a href="#representativeDetails">representative details</a>
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;&nbsp;&nbsp;}
      </td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;],
      </td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
    <tr>
      <td class="apiExample">
&nbsp;&nbsp;},</td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
    <tr>
      <td class="apiExample">
}</td>
      <td></td>
      <td></td>
      <td>
      </td>
    </tr>
  </tbody>
</table>

</@l.main> </#escape>
