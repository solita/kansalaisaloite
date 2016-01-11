<#import "/spring.ftl" as spring />
<#import "layout.ftl" as l />
<#import "utils.ftl" as u />
<#import "forms.ftl" as f />

<#macro mobileFrontPageImageContainer imageNumber>
<div class="image-container-mobile hidden-md hidden-lg">
  <div class="mobile-image image-${imageNumber}-1" ></div>
  <div class="mobile-image image-${imageNumber}-2" ></div>
  <div class="mobile-image image-${imageNumber}-3" ></div>
  <a href="${urls.createNew()}" class="hero-holder-mobile noprint">
    <span class="hero"><@u.messageHTML "index.hero" /><i class="icon-front i-arrow-right"></i></span>
  </a>
  <#if requestMessages?? && (requestMessages?size > 0)>
      <@u.frontpageRequestMessage requestMessages />
  </#if>
</div>
</#macro>