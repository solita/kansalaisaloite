package fi.om.initiative.dto.initiative;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fi.om.initiative.dto.LocalizedString;
import fi.om.initiative.dto.author.Author;
import fi.om.initiative.validation.LocalizationRequired;
import fi.om.initiative.validation.group.Basic;

import javax.validation.Valid;

import java.util.List;

public abstract class InitiativeBase extends InitiativeInfo {
    
    @Valid
    private List<Link> links = Lists.newArrayList();

    @LocalizationRequired(groups=Basic.class)
    private LocalizedString proposal = new LocalizedString();
    
    @LocalizationRequired(groups=Basic.class)
    private LocalizedString rationale = new LocalizedString();
    
    public InitiativeBase() {
        super((Long) null);
    }
    
    public InitiativeBase(Long id) {
        super(id);
    }
    
    public InitiativeBase(InitiativeBase initiative) {
        super(initiative);
        setRationale(initiative.getRationale());
        setProposal(initiative.getProposal());
        // TODO: Deep copy? Links?
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
    
    public void addLink(Link link) {
        this.links.add(link);
    }
    
    public LocalizedString getProposal() {
        return proposal;
    }

    public void setProposal(LocalizedString proposal) {
        this.proposal = proposal;
    }

    public LocalizedString getRationale() {
        return rationale;
    }

    public void setRationale(LocalizedString rationale) {
        this.rationale = rationale;
    }

    public boolean hasTranslation(String lang) {
        return super.hasTranslation(lang) || proposal.hasTranslation(lang) || rationale.hasTranslation(lang);
    }

    public abstract void assignAuthors(List<Author> authors);
    
    public void cleanupAfterBinding() {
        links = Lists.newArrayList(Iterables.filter(links, Link.NOT_DELETED));
    }

}
