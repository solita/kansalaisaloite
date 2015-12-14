<#import "components/layout.ftl" as l />
<#import "components/utils.ftl" as u />

<#escape x as x?html> 
<@l.main "page.createNew">

    <h1><@u.message "page.beforeCreate" /></h1>

    <p><@u.message "beforeCreate.intro" /></p>

    <div class="container-fluid beforeCreate">
      <div class="row">
          <div class="col-sm-1"></div>
          <div class="col-sm-10 bg-white">
              <div class="col-sm-6">
                <h2><@u.message "beforeCreate.authenticateAndCreate.title" /></h2>

                <p><@u.message "beforeCreate.authenticateAndCreate.description" /></p>

                <ol>
                  <li><@u.message "beforeCreate.authenticateAndCreate.step-1" /></li>
                  <li><@u.message "beforeCreate.authenticateAndCreate.step-2" /></li>
                  <li><@u.message "beforeCreate.authenticateAndCreate.step-3" /></li>
                </ol>
                <a href="${urls.login(urls.createNew())}" class="block-link"><@u.message "beforeCreate.authenticateAndCreate.btn" /></a>
              </div>
              <div class="col-sm-6">
                <h2><@u.message "beforeCreate.howToCreate.title" /></h2>

                <p><@u.message "beforeCreate.howToCreate.description" /></p>
                <a href="${urls.help(HelpPage.INITIATIVE_STEPS.getUri(locale))}" class="block-link"><@u.message "beforeCreate.howToCreate.btn" /></a>
              </div>
          </div>
          <div class="col-sm-1"></div>
      </div>
    </div>
</@l.main>
</#escape> 
