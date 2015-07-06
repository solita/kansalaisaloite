package fi.om.initiative.dto.initiative;

public enum FlowState {
    /**
     * @see InitiativeState#DRAFT
     */
    DRAFT,
    /**
     * @see InitiativeState#PROPOSAL
     */
    PROPOSAL,
    /**
     * @see InitiativeState#REVIEW
     */
    REVIEW,
    /**
     * Accepted, mutta keräyspvm tulevaisuudessa
     * 
     * @see InitiativeState#ACCEPTED
     */
    ACCEPTED_NOT_STARTED,
    /**
     * Keräys käynnissä, ensimmäinen kuukausi menossa 
     * 
     * @see InitiativeState#ACCEPTED
     */
    ACCEPTED_FIRST_MONTH,
    /**
     * Keräys käynnissä, 1kk ohitettu, ei saatu yli 50
     * 
     * @see InitiativeState#ACCEPTED
     */
    ACCEPTED_FIRST_MONTH_FAILED,
    /**
     * Keräys käynnissä, 1kk ohitettu, saatiin yli 50
     * 
     * @see InitiativeState#ACCEPTED
     */
    ACCEPTED_RUNNING,
    /**
     * Keräys päättynyt, vahvistamattomia yli 50 000 JA ei vahvistusta yli 50 000
     * 
     * @see InitiativeState#ACCEPTED
     */
    ACCEPTED_UNCONFIRMED,
    /**
     * Keräys päättynyt, jäätiin alle 50 000
     * 
     * @see InitiativeState#ACCEPTED
     */
    ACCEPTED_FAILED,
    /**
     * VRK vahvistanut yli 50 000, aikaa yhä jäljellä
     * 
     * @see InitiativeState#ACCEPTED
     */
    ACCEPTED_CONFIRMED_RUNNING,
    /**
     * VRK vahvistanut yli 50 000, keräysaika päättynyt
     * 
     * @see InitiativeState#ACCEPTED
     */
    ACCEPTED_CONFIRMED,
    /**
     * VRK vahvistanut yli 50 000, mutta aloitetetta ei ole lähetetty eduskuntaan  
     * määräajassa "pvm" + 6 kk (missä "pvm" = max(keräyksenpäättymispvm, VRK:n viimeisin hyväksymispvm) 
     * 
     * @see InitiativeState#ACCEPTED
     */
    ACCEPTED_CONFIRMED_FAILED,
    /**
     * Aloitteen keräys on päättynyt sekä saanut yli 50000 kannatusilmoitusta ja kannatusilmoitukset on lähetetty VRK:lle tarkistettavaksi.
     */
    ACCEPTED_SENT_TO_VRK,

    /**
     * Aloite on kerännyt alustavasti yli 50000 kannatusilmoitusta, lähetetty VRK:lle tarkistettavaksi mutta VRK:n tarkistuksen jälkeen jää alle 50 000
     */
    ACCEPTED_CONFIRMATION_FAILED,
    /**
     * @see InitiativeState#DONE
     */
    DONE,
    /**
     * @see InitiativeState#CANCELED
     */
    CANCELED
}
