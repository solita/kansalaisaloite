<#import "components/layout.ftl" as l />
<#import "components/utils.ftl" as u />

<#escape x as x?html> 
<@l.main "page.frontpage">
 
    <div class="front-container">
    
        <div class="faux-columns cf">
            <div class="col-1">

                <div class="front-block block-1">
                    <h1><@u.message "index.block-1.title" /></h1>
        
                    <p><@u.message "index.block-1.p-1" /></p>
                    <p><@u.message "index.block-1.p-2" /></p>
                    
                    <a href="${urls.helpIndex()}" class="block-link"><@u.message "index.block-1.link" /></a>
                </div>
            
            </div>
            <div class="col-2">
            
                <div class="front-block block-2">
                    <h2><@u.message "index.block-2.title" /></h2>
        
                    <p><@u.message "index.block-2" /></p>
                    
                    <a href="${urls.search()}" class="block-link"><@u.message "index.block-2.link" /></a>
                </div>
                
                <div class="front-block block-3">
                    <h2><@u.message "index.block-3.title" /></h2>
                    
                    <#assign href1>${urls.helpIndex()}</#assign>
                    <#assign href2>${urls.help(HelpPage.CONTACT.getUri(locale))}</#assign>
                    <p><@u.messageHTML key="index.block-3.p-1" args=[href1, href2] /></p>
                    
                    <p><@u.messageHTML "index.block-3.p-2" /> <@u.scrambleEmail "kansalaisaloite.om@om.fi" /></p>
                </div>
            
            </div>
        
        </div>
    
    </div>



    
</@l.main>
</#escape> 

