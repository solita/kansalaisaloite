package fi.om.initiative.dto.initiative;

import com.google.common.collect.Lists;
import fi.om.initiative.dto.RepresentativeInfo;
import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.author.AuthorInfo;

import java.util.List;

public class InitiativePublic extends InitiativeBase {
    
    private List<AuthorInfo> initiators = Lists.newArrayList();
    
    private List<RepresentativeInfo> representatives = Lists.newArrayList();
    
    private List<RepresentativeInfo> reserves = Lists.newArrayList();


    public InitiativePublic(Long id) {
        super(id);
    }

    public InitiativePublic(InitiativeManagement initiative) {
        super(initiative);
        //TODO: copy links and authors
    }
    
    public List<AuthorInfo> getInitiators() {
        return initiators;
    }

    @Override
    public void assignAuthors(List<Author> authors) {
        for (Author author : authors) {
            if (author.isInitiator()) {
                this.initiators.add(new AuthorInfo(author));
            }
            if (author.isRepresentative()) {
                this.representatives.add(new RepresentativeInfo(author));
            } else if (author.isReserve()) {
                this.reserves.add(new RepresentativeInfo(author));
            }
        }
    }

    public List<RepresentativeInfo> getRepresentatives() {
        return representatives;
    }

    public List<RepresentativeInfo> getReserves() {
        return reserves;
    }
}
