package fi.om.initiative.dto;


import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.dto.initiative.ManagementSettingsBuilder;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.Test;

import static fi.om.initiative.dto.initiative.ManagementSettingsBuilder.ConfirmStatus.CONFIRMED;
import static fi.om.initiative.dto.initiative.ManagementSettingsBuilder.ConfirmStatus.UNCONFIRMED;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class ManagementSettingsTest  {

    private static final Long ID = -5L;

    @Test
    public void no_initiative_id() {
        ManagementSettings settings =
                ManagementSettingsBuilder
                        .withInitiativeId(null)
                        .toSettings();

        assertThat(settings.getEditMode(), is(EditMode.FULL));
        assertThat(settings.isAllowEditBasic(), is(true));
        assertThat(settings.isAllowEditCurrentAuthor(), is(true));
        assertThat(settings.isAllowEditExtra(), is(true));
        assertThat(settings.isAllowEditOrganizers(), is(true));

        assertThat(settings.isAllowSendToOM(), is(false));
        assertThat(settings.isAllowRespondByOM(), is(false));
        assertThat(settings.isAllowSendToVRK(), is(false));
        assertThat(settings.isAllowRespondByVRK(), is(false));

        assertThat(settings.isAllowAcceptInvitation(), is(false));
        assertThat(settings.isAllowConfirmCurrentAuthor(), is(false));
        assertThat(settings.isAllowDeleteCurrentAuthor(), is(false));
        assertThat(settings.isAllowSendInvitations(), is(false));
        assertThat(settings.isAllowRemoveSupportVotes(), is(false));
        assertThat(settings.isExtraWarningforRemoveSupportVotes(), is(false));

    }

    // - - - - - - - - - -
    // allowConfirmedCurrentAuthor
    // - - - - - - - - - -

    @Test
    public void allowConfirmCurrentAuthor_true_if_author_uncorfirmed_and_state_is_proposal() {
        ManagementSettings settings = ManagementSettingsBuilder.withInitiativeId(ID)
                .withInitiativeState(InitiativeState.PROPOSAL)
                .withIsAuthor(UNCONFIRMED)
                .withIsDefaultUser()
                .toSettings();
        assertThat(settings.isAllowConfirmCurrentAuthor(), is(true));
    }

    @Test
    public void allowConfirmCurrentAuthor_false_if_no_author() {

        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withInitiativeState(InitiativeState.PROPOSAL)
                .withIsAuthor(UNCONFIRMED)
                .withIsDefaultUser();
        assertPrecondition(builder.toSettings().isAllowConfirmCurrentAuthor(), is(true));

        builder.withIsNotAuthor();
        assertThat(builder.toSettings().isAllowConfirmCurrentAuthor(), is(false));
    }

    @Test
    public void allowConfirmCurrentAuthor_false_if_author_is_confirmed() {

        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withInitiativeState(InitiativeState.PROPOSAL)
                .withIsAuthor(UNCONFIRMED)
                .withIsDefaultUser();
        assertPrecondition(builder.toSettings().isAllowConfirmCurrentAuthor(), is(true));

        builder.withIsAuthor(CONFIRMED);
        assertThat(builder.toSettings().isAllowConfirmCurrentAuthor(), is(false));
    }

    @Test
    public void allowConfirmCurrentAuthor_true_only_if_initiative_state_proposal() {

        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsAuthor(UNCONFIRMED)
                .withIsDefaultUser();

        for (InitiativeState initiativeState : InitiativeState.values()) {
            builder.withInitiativeState(initiativeState);
            if (initiativeState == InitiativeState.PROPOSAL) {
                assertThat(builder.toSettings().isAllowConfirmCurrentAuthor(), is(true));
            }
            else {
                assertThat(builder.toSettings().isAllowConfirmCurrentAuthor(), is(false));
            }
        }
    }

    // - - - - - - - - - -
    //  allowDeleteCurrentAuthor
    // - - - - - - - - - -

    @Test
    public void allowDeleteCurrentAuthor_is_true_if_current_author_not_null_and_initiative_state_not_done_or_canceled() {

        for (InitiativeState state : InitiativeState.values()) {
            ManagementSettings settings = ManagementSettingsBuilder.withInitiativeId(ID)
                    .withInitiativeState(state)
                    .withIsAuthor(UNCONFIRMED)
                    .withIsDefaultUser()
                    .toSettings();

            if (state == InitiativeState.DONE || state == InitiativeState.CANCELED) {
                assertThat(settings.isAllowDeleteCurrentAuthor(), is(false));
            } else {
                assertThat(settings.isAllowDeleteCurrentAuthor(), is(true));
            }
        }
    }

    @Test
    public void allowDeleteCurrentAuthor_is_false_if_current_author_is_null() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withInitiativeState(InitiativeState.ACCEPTED)
                .withIsAuthor(UNCONFIRMED)
                .withIsDefaultUser();

        assertThat(builder.toSettings().isAllowDeleteCurrentAuthor(), is(true));

        builder.withIsNotAuthor();
        assertThat(builder.toSettings().isAllowDeleteCurrentAuthor(), is(false));

    }

    // - - - - - - - - - -
    //  editMode
    // - - - - - - - - - -

    @Test
    public void editMode_is_given_editMode_if_not_null_and_author_is_confirmed() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withEditMode(EditMode.BASIC)
                .withIsAuthor(CONFIRMED)
                .withIsDefaultUser();
        assertThat(builder.toSettings().getEditMode(), is(EditMode.BASIC));
    }

    @Test
    public void editMode_is_none_if_no_editmode_is_given() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withEditMode(null)
                .withIsAuthor(CONFIRMED)
                .withIsDefaultUser();

        assertThat(builder.toSettings().getEditMode(), is(EditMode.NONE));
    }

    @Test
    public void editMode_is_none_if_author_unconfirmed() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withEditMode(EditMode.BASIC)
                .withIsAuthor(CONFIRMED)
                .withIsDefaultUser();
        assertPrecondition(builder.toSettings().getEditMode(), is(EditMode.BASIC));

        builder.withIsAuthor(UNCONFIRMED);
        assertThat(builder.toSettings().getEditMode(), is(EditMode.NONE));

    }

    // - - - - - - - - - -
    //  allowRespondBy
    // - - - - - - - - - -

    @Test
    public void allowRespondByOM_is_true_if_not_current_author_and_is_OM_and_state_is_review() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsNotAuthor()
                .withIsOmUser()
                .withInitiativeState(InitiativeState.REVIEW);

        assertThat(builder.toSettings().isAllowRespondByOM(), is(true));
    }

    @Test
    public void allowRespondByVRK_is_true_if_not_current_author_and_is_VRK_user_and_state_is_accepted_and_has_sent_supports() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsNotAuthor()
                .withIsVRKUser()
                .withInitiativeState(InitiativeState.ACCEPTED)
                .withSentSupportCount(100);

        assertThat(builder.toSettings().isAllowRespondByVRK(), is(true));
    }

    @Test
    public void allowRespondByOM_is_false_if_is_author_even_if_om_user() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsNotAuthor()
                .withInitiativeState(InitiativeState.REVIEW)
                .withIsOmUser()
                .withSentSupportCount(100);
        assertPrecondition(builder.toSettings().isAllowRespondByOM(), is(true));

        builder.withIsAuthor(CONFIRMED);
        assertThat(builder.toSettings().isAllowRespondByOM(), is(false));
        builder.withIsAuthor(UNCONFIRMED);
        assertThat(builder.toSettings().isAllowRespondByOM(), is(false));
    }

    @Test
    public void allowRespondByVRK_is_false_if_is_author_even_if_vr_user() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsNotAuthor()
                .withInitiativeState(InitiativeState.ACCEPTED)
                .withIsVRKUser()
                .withSentSupportCount(100);
        assertPrecondition(builder.toSettings().isAllowRespondByVRK(), is(true));

        builder.withIsAuthor(CONFIRMED);
        assertThat(builder.toSettings().isAllowRespondByVRK(), is(false));
        builder.withIsAuthor(UNCONFIRMED);
        assertThat(builder.toSettings().isAllowRespondByVRK(), is(false));
    }

    @Test
    public void allowRespondByOM_is_false_if_state_is_not_review() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsNotAuthor()
                .withIsOmUser()
                .withInitiativeState(InitiativeState.REVIEW);
        assertPrecondition(builder.toSettings().isAllowRespondByOM(), is(true));

        builder.withInitiativeState(InitiativeState.ACCEPTED);
        assertThat(builder.toSettings().isAllowRespondByOM(), is(false));
    }

    @Test
    public void allowRespondByOM_is_false_if_user_is_not_OM() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsNotAuthor()
                .withIsOmUser()
                .withInitiativeState(InitiativeState.REVIEW);
        assertPrecondition(builder.toSettings().isAllowRespondByOM(), is(true));

        builder.withIsDefaultUser();
        assertThat(builder.toSettings().isAllowRespondByOM(), is(false));
    }

    @Test
    public void allowRespondByVRK_is_false_if_state_not_accepted() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsNotAuthor()
                .withIsVRKUser()
                .withSentSupportCount(100);

        for (InitiativeState initiativeState : InitiativeState.values()) {
            builder.withInitiativeState(initiativeState);
            if (initiativeState == InitiativeState.ACCEPTED) {
                assertThat(builder.toSettings().isAllowRespondByVRK(), is(true));
            }
            else {
                assertThat(builder.toSettings().isAllowRespondByVRK(), is(false));
            }
        }
    }

    @Test
    public void allowRespondByVRK_is_false_if_has_not_sent_supports() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsNotAuthor()
                .withIsVRKUser()
                .withInitiativeState(InitiativeState.ACCEPTED)
                .withSentSupportCount(100);

        assertPrecondition(builder.toSettings().isAllowRespondByVRK(), is(true));

        builder.withSentSupportCount(0);
        assertThat(builder.toSettings().isAllowRespondByVRK(), is(false));
    }

    @Test
    public void allowRespondByVRK_is_false_if_not_vrk_user() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsNotAuthor()
                .withIsVRKUser()
                .withInitiativeState(InitiativeState.ACCEPTED)
                .withSentSupportCount(1);
        assertPrecondition(builder.toSettings().isAllowRespondByVRK(), is(true));

        builder.withIsNotVrkUser();
        assertThat(builder.toSettings().isAllowRespondByVRK(), is(false));
    }

    @Test
    public void allowEditBasic_and_Organizers_is_true_if_author_confirmed_and_state_is_proposal_or_draft() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder
                .withInitiativeId(ID)
                .withIsDefaultUser()
                .withIsAuthor(CONFIRMED);

        for (InitiativeState initiativeState : new InitiativeState[]{ InitiativeState.PROPOSAL, InitiativeState.DRAFT}) {
            builder.withInitiativeState(initiativeState);
            assertThat(builder.toSettings().isAllowEditBasic(), is(true));
            assertThat(builder.toSettings().isAllowEditOrganizers(), is(true));
        }
    }

    // - - - - - - - - - -
    //  allowEdit
    // - - - - - - - - - -

    @Test
    public void allowEditBasic_and_Organizers_is_false_if_author_confirmed_and_state_is_not_proposal_nor_draft() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder
                .withInitiativeId(ID)
                .withIsDefaultUser()
                .withIsAuthor(CONFIRMED);

        for (InitiativeState initiativeState : InitiativeState.values()) {
            builder.withInitiativeState(initiativeState);

            if (initiativeState != InitiativeState.PROPOSAL && initiativeState != InitiativeState.DRAFT) {
                assertThat(builder.toSettings().isAllowEditBasic(), is(false));
                assertThat(builder.toSettings().isAllowEditOrganizers(), is(false));
            }
        }
    }

    @Test
    public void allowEditBasic_and_Organizers_is_false_if_author_not_confirmed_or_null_not_matter_what_the_state_is() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder
                .withInitiativeId(ID)
                .withIsDefaultUser();

        for (InitiativeState initiativeState : InitiativeState.values()) {
            builder.withInitiativeState(initiativeState);

            builder.withIsAuthor(UNCONFIRMED);
            assertThat(builder.toSettings().isAllowEditBasic(), is(false));
            assertThat(builder.toSettings().isAllowEditOrganizers(), is(false));

            builder.withIsNotAuthor();
            assertThat(builder.toSettings().isAllowEditBasic(), is(false));
            assertThat(builder.toSettings().isAllowEditOrganizers(), is(false));
        }
    }

    @Test
    public void allowEditCurrentAuthor_and_allowEditExtra_is_true_if_confirmed_author_and_status_in_draft_proposal_review_accepted_otherwise_false() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsAuthor(CONFIRMED)
                .withIsDefaultUser();

        for (InitiativeState initiativeState : InitiativeState.values()) {
            builder.withInitiativeState(initiativeState);

            if (initiativeState == InitiativeState.DRAFT
                    || initiativeState == InitiativeState.PROPOSAL
                    || initiativeState == InitiativeState.REVIEW
                    || initiativeState == InitiativeState.ACCEPTED)    {

                assertThat(initiativeState.toString(), builder.toSettings().isAllowEditCurrentAuthor(), is(true));
                assertThat(builder.toSettings().isAllowEditExtra(), is(true));
            }
            else {
                assertThat(initiativeState.toString(), builder.toSettings().isAllowEditCurrentAuthor(), is(false));
                assertThat(builder.toSettings().isAllowEditExtra(), is(false));
            }
        }
    }

    @Test
    public void allowEditCurrentAuthor_and_allowEditExtra_is_false_if_not_current_author_or_not_confirmed_no_matter_what_state_is() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsDefaultUser();

        for (InitiativeState initiativeState : InitiativeState.values()) {
            builder.withInitiativeState(initiativeState);

            builder.withIsNotAuthor();
            assertThat(builder.toSettings().isAllowEditCurrentAuthor(), is(false));
            assertThat(builder.toSettings().isAllowEditExtra(), is(false));

            builder.withIsAuthor(UNCONFIRMED);
            assertThat(builder.toSettings().isAllowEditCurrentAuthor(), is(false));
            assertThat(builder.toSettings().isAllowEditExtra(), is(false));

        }

    }

    // - - - - - - - - - -
    //  allowSendToOm / VRK
    // - - - - - - - - - -

    @Test
    public void allowSendToOm_is_true_if_enough_confirmed_authors_and_only_if_status_is_proposal_or_draft() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsAuthor(CONFIRMED)
                .withIsDefaultUser()
                .withIsEnoughConfirmedAuthors(true);

        for (InitiativeState initiativeState : InitiativeState.values()) {
            builder.withInitiativeState(initiativeState);
            if (initiativeState == InitiativeState.PROPOSAL || initiativeState == InitiativeState.DRAFT) {
                assertThat(builder.toSettings().isAllowSendToOM(), is(true));
            }
            else {
                assertThat(builder.toSettings().isAllowSendToOM(), is(false));
            }
        }

    }

    @Test
    public void allowSendToOm_is_false_if_not_author_or_not_confirmed_author() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsAuthor(CONFIRMED)
                .withIsDefaultUser()
                .withInitiativeState(InitiativeState.PROPOSAL)
                .withIsEnoughConfirmedAuthors(true);
        assertPrecondition(builder.toSettings().isAllowSendToOM(), is(true));

        builder.withIsAuthor(UNCONFIRMED);
        assertThat(builder.toSettings().isAllowSendToOM(), is(false));

        builder.withIsNotAuthor();
        assertThat(builder.toSettings().isAllowSendToOM(), is(false));
    }

    @Test
    public void allowSendToOm_is_false_if_not_enough_confirmed_authors() {
        ManagementSettingsBuilder builder = ManagementSettingsBuilder.withInitiativeId(ID)
                .withIsAuthor(CONFIRMED)
                .withIsDefaultUser()
                .withInitiativeState(InitiativeState.PROPOSAL)
                .withIsEnoughConfirmedAuthors(true);
        assertPrecondition(builder.toSettings().isAllowSendToOM(), is(true));

        builder.withIsEnoughConfirmedAuthors(false);
        assertThat(builder.toSettings().isAllowSendToOM(), is(false));
    }

    private static DateTime anyDate() {
        return new DateTime(1900,1,1,0,0);
    }

    private static <T> void assertPrecondition(T actual, Matcher<T> matcher) {
        assertThat("Precondition failed: object at incorrect state", actual, matcher);
    }

}
