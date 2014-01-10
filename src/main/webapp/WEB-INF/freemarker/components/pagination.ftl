<#import "utils.ftl" as u />

<#escape x as x?html> 

<#--
 * pages
 *
 * Print link to pages
 * Current page is displayed as a span
 *
 * @param totalItems is the total amount of filtered initiatives
 * @param limit is the current limit
 * @param offset is the current offset
-->
<#macro pages totalItems limit offset>
    <#assign totalPages = (totalItems / limit)?ceiling />
    <#assign currOffset= (offset / limit)?floor + 1 />
    
    <span class="page-numbers">
        <@u.message "pagination.page" /> <span>${currOffset} / ${totalPages}</span>
    </span>
    
</#macro>

<#--
 * numbers
 *
 * Print link to pages
 * Current page is displayed as a span
 *
 * @param totalItems is the total amount of filtered initiatives
 * @param limit is the current limit
 * @param offset is the current offset
-->
<#macro numbers totalItems limit offset>
    <#assign totalPages = (totalItems / limit)?ceiling />
    
    <#list 1..totalPages as page>
        <#if page_index == 0><span class="pagination-numbers"></#if>
        
        <#assign currOffset=(page-1)*limit />
        
        <#if currOffset == offset>
            <span>${page}</span>
        <#else>
            <a href="${urls.search()}${searchParameters.withOffset(currOffset)}">${page}</a>
        </#if>
        
        <#if !page_has_next></span></#if>
    </#list>  
</#macro>

<#--
 * previousPage
 *
 * Print previous page link
 * Do not display link if the first page is active
 *
 * @param limit is the current limit
 * @param offset is the current offset
-->
<#macro previousPage limit offset>
    <#assign prev = offset - limit />
    <#if (prev >= 0)>
        <a href="${urls.search()}${searchParameters.withOffset(prev)}" class="prev"><span class="icon-small arrow-left"></span> <@u.message "pagination.prev" /></a>
    <#else>
        <span class="prev"><span class="icon-small arrow-left"></span> <@u.message "pagination.prev" /></span>
    </#if>
</#macro>

<#--
 * nextPage
 *
 * Print next page link
 * Do not display link if there is no more pages
 *
 * @param totalItems is the total amount of filtered initiatives
 * @param limit is the current limit
 * @param offset is the current offset
-->
<#macro nextPage totalItems limit offset>
    <#assign totalPages = (totalItems / limit)?ceiling />
    <#assign next = offset + limit />
    
    <#if (next < totalPages * limit)>
        <a href="${urls.search()}${searchParameters.withOffset(offset + limit)}" class="next"><@u.message "pagination.next" /> <span class="icon-small arrow-right"></span></a>
    <#else>
        <span class="next"><@u.message "pagination.next" /> <span class="icon-small arrow-right"></span></span>
    </#if>
</#macro>

<#--
 * limiters
 *
 * Print limiter links
 * Compress to remove whitespaces
 *
 * @param limits the set of all limits
 * @param limit is the current limit
 * @param is size of the maximum limit
-->
<#macro limiters limits limit maxLimit>
<@compress single_line=true>
    <span class="pagination-limiter">
        <@u.message "pagination.limiter" />
        <#list limits as l>
            <#if l != limit>
                <a href="${urls.search()}${searchParameters.withLimit(l)}">${l}</a>
            <#else>
                <span class="active">${l}</span>
            </#if>

            <#if !l_has_next>
            <span class="separator hide">|</span>
                <#if limit != maxLimit>
                    <a href="${urls.search()}${searchParameters.withMaxLimit}"><@u.message "pagination.withMaxLimit" /></a>
                <#else>
                    <span class="active"><@u.message "pagination.withMaxLimit" /></span>
                </#if>
            </#if>
        </#list>
    </span>
</@compress>
</#macro>

<#--
 * pagination
 *
 * Print pagination if more than 1 page
 * Show always limiters
 *
 * @param limit is the current limit
 * @param offset is the current offset
-->
<#macro pagination limit offset cssClass="">
    <#assign limits = [20, 100]>
    <#assign totalInitiatives = totalCount />
    <#assign totalPages = (totalInitiatives / limit)?ceiling />
    <#assign showPagination = currentSearch.limit ?? && (totalPages > 1) />
    <#assign showLimits = (totalInitiatives > limits[0]) />

    <#if showPagination || showLimits>
        <div class="pagination cf ${cssClass}">
            
                <#if showPagination>
                    <div class="pagination-links">
                        <@previousPage limit offset />
            
                        <@pages totalInitiatives limit offset />
                        <#--<@numbers totalInitiatives limit offset />-->
                        
                        <@nextPage totalInitiatives limit offset />
                    </div>
                </#if>
            
                <#if showLimits>
                    <@limiters limits limit 500 />
                </#if>
            
         </div>
     </#if>
 </#macro>
 
 </#escape>