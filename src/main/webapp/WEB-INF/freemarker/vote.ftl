<#import "/spring.ftl" as spring />
<#import "components/layout.ftl" as l />
<#import "components/utils.ftl" as u />

<#escape x as x?html> 
<#assign page="page.vote" />

<@l.main page>

    <h1> <@u.message page /> </h1>
    
    <#assign notYetVotedHTML><p><@u.message "vote.notYetVoted" /></p></#assign>
    <div class="system-msg msg-summary">
        <@u.systemMessageHTML html=notYetVotedHTML type="info" />
    </div>
    
    <div class="vote input-block">
        <div class="table full">
            <div class="row">
                <div class="cell cell-1of4 title"><@u.message "initiative.name."+locale /></div>
                <div class="cell cell-3of4"><@u.text initiative.name /></div>
            </div>
            <div class="row">
                <div class="cell cell-1of4 title"><@u.message "vote.authenticatedUser" /></div>
                <div class="cell cell-3of4">${currentUser.firstNames!""} ${currentUser.lastName!""}</div>
            </div>
            <div class="row">
                <div class="cell cell-1of4 title"><@u.message "initiative.currentAuthor.homeMunicipality" /></div>
                <div class="cell cell-3of4"><@u.text currentUser.homeMunicipality /></div>
            </div>
            <div class="row">
                <div class="cell cell-1of4 title"><@u.message "vote.date" /></div>
                <div class="cell cell-3of4"><@u.localDate .now /></div>
            </div>
        </div>
        
        <form method="POST" action="${urls.voteAction(initiative.id)}" id="vote-form">
            <input type="hidden" name="CSRFToken" value="${CSRFToken}"/>
            <input type="hidden" name="action" value="vote"/>

            <br/>          
            <p><label><input type="checkbox" name="confirm" value="true" class="binder" /> <@u.messageHTML "vote.confirm" /></label></p>
          
            <button class="small-button green bind" value="Tallenna kannatusilmoitus" name="${UrlConstants.ACTION_VOTE}" type="submit"><span class="small-icon save-and-send"><@u.message "vote.saveVote.btn" /></span></button>
            <a href="${urls.view(initiative.id)}" class="push"><@u.message "action.cancel" /></a>
        </form>
    </div>
    
</@l.main>
</#escape> 
