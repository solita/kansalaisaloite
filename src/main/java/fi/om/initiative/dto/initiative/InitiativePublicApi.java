package fi.om.initiative.dto.initiative;

import fi.om.initiative.dto.author.Author;

import java.util.List;

public class InitiativePublicApi extends InitiativeBase {

    public InitiativePublicApi(Long id) {
        super(id);
    }

    @Override
    public void assignAuthors(List<Author> authors) {}
}
