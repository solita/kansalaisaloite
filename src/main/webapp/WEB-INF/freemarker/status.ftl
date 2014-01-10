<#import "components/layout.ftl" as l /> 

<#escape x as x?html> 
<@l.main "page.status">

    <h1>Status page</h1>

    <h3>Application</h3>
    <table class="data status">
        <tbody>
            <#list applicationInfoRows as infoRow>
            <tr>
                <td>${infoRow.key}</td>
                <td>${infoRow.value}</td>
            </tr>
            </#list>
        </tbody>
    </table>

    <h3>SchemaVersion</h3>
    <table class="data status">
        <tbody>
            <#list schemaVersionInfoRows as infoRow>
            <tr>
                <td>${infoRow.key}</td>
                <td>${infoRow.value}</td>
            </tr>
            </#list>
        </tbody>
    </table>

    <h3>System</h3>
    <table class="data status">
        <tbody>
            <#list systemInfoRows as infoRow>
            <tr>
                <td>${infoRow.key}</td>
                <td>${infoRow.value}</td>
            </tr>
            </#list>
        </tbody>
    </table>

    <h3>Hard coded URL:s</h3>
        <table class="data status">
            <tbody>
                <#list hardCodedUris as hardCodedUri>
                <tr>
                    <td>${hardCodedUri.key}</td>
                    <td>${hardCodedUri.value}</td>
                </tr>
                </#list>
            </tbody>
        </table>


    <h3>Configuration</h3>
    <table class="data status">
        <tbody>
            <#list configurationInfoRows as infoRow>
            <tr>
                <td>${infoRow.key}</td>
                <td>${infoRow.value}</td>
            </tr>
            </#list>
        </tbody>
    </table>

    <h3>Configuration Test</h3>
    <table class="data status">
        <tbody>
            <#list configurationTestInfoRows as infoRow>
            <tr>
                <td>${infoRow.key}</td>
                <td>${infoRow.value}</td>
            </tr>
            </#list>
        </tbody>
    </table>

</@l.main>
</#escape> 
