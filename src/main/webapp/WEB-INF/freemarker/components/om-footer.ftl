<div class="om-footer">
    <div class="container row">
        <div class="block col-sm-4">
            <span class="footer-logo om"></span>
            <p>
                <@u.message "footer.ministerOfJustice"/><br/>
                <a href="${urls.help(HelpPage.KANSALAISALOITE_FI.getUri(locale))}"><@u.message "common.readMore" /> &gt;</a>
            </p>
        </div>
        <div class="block col-sm-4">
            <span class="footer-logo vivi"></span>
            <p>
                <@u.messageHTML "footer.ficora"/><br/>
                <a href="${urls.help(HelpPage.VIESTINTAVIRASTO.getUri(locale))}"><@u.message "common.readMore" /> &gt;</a>
            </p>
        </div>
        <div class="footer-links col-sm-4">
            <ul>
                <#list footerLinks as footerLink>
                    <li><span class="triangle-right"></span> <a href="${urls.help(footerLink.uri)}">${footerLink.subject}</a></li>
                </#list>
            </ul>
        </div>
      <a href="#header-tools" accesskey="3" id="back-to-top"><@u.message "accesskey.backToTop" /></a>
    </div>
</div>