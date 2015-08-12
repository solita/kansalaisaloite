package fi.om.initiative.dto.initiative;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.om.initiative.dto.*;
import fi.om.initiative.json.DateTimeJsonSerializer;
import fi.om.initiative.json.InitURISerializer;
import fi.om.initiative.json.JsonId;
import fi.om.initiative.json.LocalDateJsonSerializer;
import fi.om.initiative.validation.LocalizationRequired;
import fi.om.initiative.validation.group.Basic;
import fi.om.initiative.validation.group.Extra;
import fi.om.initiative.validation.group.OM;
import fi.om.initiative.validation.group.VRK;
import fi.om.initiative.web.Urls;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.ReadablePeriod;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class InitiativeInfo {

    private Long id;
    
    private DateTime modified;
    
    private InitiativeState state = InitiativeState.DRAFT;
    
    private DateTime stateDate; 
    
    private String acceptanceIdentifier;
    
    private int supportCount;
    
    private int sentSupportCount;
    
    @Min(value=0, groups=Extra.class)
    private int externalSupportCount;

    @Min(value=0, groups=VRK.class)
    private int verifiedSupportCount;

    @NotNull(groups=VRK.class)
    private LocalDate verified; 
    
    @LocalizationRequired(groups=Basic.class, maxLength = InitiativeConstants.INITIATIVE_NAME_MAX)
    private LocalizedString name = new LocalizedString();
    
    @NotNull(groups=Basic.class)
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @NotNull(groups=Basic.class)
    private ProposalType proposalType = ProposalType.PREPARATION;
    
    private LanguageCode primaryLanguage;
    
    private boolean financialSupport;
    
    private InitURI financialSupportURL;

    private boolean supportStatementsOnPaper; 

    private boolean supportStatementsInWeb;

    private boolean supportStatementPdf;

    private String supportStatementAddress;

    private DateTime supportStatementsRemoved;

    @NotNull(groups = OM.class)
    private String parliamentIdentifier;

    @NotNull(groups = OM.class)
    private String parliamentURL;

    @NotNull(groups = OM.class)
    private LocalDate parliamentSentTime;

    public InitiativeInfo(Long id) {
        this.id = id;
    }

    public InitiativeInfo(InitiativeInfo initiative) {
        assignId(initiative.getId());
        setName(initiative.getName());
        setPrimaryLanguage(initiative.getPrimaryLanguage());
        setExternalSupportCount(initiative.getExternalSupportCount());
        assignModified(initiative.getModified());
        assignState(initiative.getState());
        assignStateDate(initiative.getStateDate());
        assignSupportCount(initiative.getSupportCount());

        setStartDate(initiative.getStartDate());
        assignEndDate(initiative.getEndDate());
        setProposalType(initiative.getProposalType());
        setFinancialSupport(initiative.isFinancialSupport());
        setFinancialSupportURL(initiative.getFinancialSupportURL());
        setSupportStatementsOnPaper(initiative.isSupportStatementsOnPaper());
        setSupportStatementsInWeb(initiative.isSupportStatementsInWeb());
    }

    @JsonId(path=Urls.INITIATIVE)
    public Long getId() {
        return id;
    }
    
    public void assignId(Long id) {
        this.id = id;
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(LocalizedString name) {
        this.name = name;
    }

    @JsonSerialize(using=LocalDateJsonSerializer.class)
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate date) {
        this.startDate = date;
    }

    @JsonSerialize(using=LocalDateJsonSerializer.class)
    public LocalDate getEndDate() {
        return endDate;
    }

    public void assignEndDate(LocalDate date) {
        this.endDate = date;
    }

    public void assignEndDate(LocalDate startDate, ReadablePeriod votingDuration) {
        this.endDate = getEndDateForPeriod(startDate, votingDuration);
    }
    
    public LanguageCode getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(LanguageCode primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }
    
    public ProposalType getProposalType() {
        return proposalType;
    }

    public void setProposalType(ProposalType proposalType) {
        this.proposalType = proposalType;
    }

    @JsonSerialize(using=DateTimeJsonSerializer.class)
    public DateTime getModified() {
        return modified;
    }

    public void assignModified(DateTime modified) {
        this.modified = modified;
    }

    public InitiativeState getState() {
        return state;
    }

    public void assignState(InitiativeState state) {
        this.state = state;
    }

    @JsonSerialize(using=DateTimeJsonSerializer.class)
    public DateTime getStateDate() {
        return stateDate;
    }

    public void assignStateDate(DateTime stateDate) {
        this.stateDate = stateDate;
    }

    public String getAcceptanceIdentifier() {
        return acceptanceIdentifier;
    }

    public void setAcceptanceIdentifier(String acceptanceIdentifier) {
        this.acceptanceIdentifier = acceptanceIdentifier;
    }

    public int getSupportCount() {
        return supportCount;
    }
    
    public int getTotalSupportCount() {
        int total = supportCount + externalSupportCount;

        // This is because it's possible for the user to decrease
        // the amount of out-of-system collected supports after they have been verified.
        if (total < verifiedSupportCount) {
            return verifiedSupportCount;
        } else {
            return total;
        }
    }

    public LocalDate getEndDateForSendToParliament(ReadablePeriod sendToParliamentDuration) {
        return getEndDateForPeriod(verified, sendToParliamentDuration);
    }
    
    public LocalDate getEndDateForVotesRemoval(ReadablePeriod votesRemovalDuration) {
        LocalDate endDateOrVerified = getMaxEndDateAndVerified();
        return getEndDateForPeriod(endDateOrVerified, votesRemovalDuration);
    }

    private LocalDate getDateBeforeEndDateForVotesRemoval(ReadablePeriod votesRemovalDuration, ReadablePeriod beforeDeadLine) {
        LocalDate endDateForVotesRemoval = getEndDateForVotesRemoval(votesRemovalDuration);
        return endDateForVotesRemoval != null ? endDateForVotesRemoval.minus(beforeDeadLine) : null;
    }
    
    public boolean isVotesRemovalEndDateNear(LocalDate now, ReadablePeriod votesRemovalDuration, ReadablePeriod beforeDeadLine) {
        LocalDate dateBeforeEndDateForVotesRemoval = getDateBeforeEndDateForVotesRemoval(votesRemovalDuration, beforeDeadLine);
        if (dateBeforeEndDateForVotesRemoval == null) {
            return false;
        } else {
            return !now.isBefore(dateBeforeEndDateForVotesRemoval);
        }
    }
    
    private LocalDate getMaxEndDateAndVerified() {
        if (this.endDate == null) {
            return null;
        }
        else {
            LocalDate endDateOrVerified = this.endDate;
            if (this.verified != null && this.verified.isAfter(endDateOrVerified)) {
                endDateOrVerified = verified;
            }
            return endDateOrVerified;
        }
    }
    
    public void assignSupportCount(int supportCount) {
        this.supportCount = supportCount;
    }

    public boolean isSupportStatementsOnPaper() {
        return supportStatementsOnPaper;
    }

    public void setSupportStatementsOnPaper(boolean supportStatementsOnPaper) {
        this.supportStatementsOnPaper = supportStatementsOnPaper;
    }

    public boolean isSupportStatementsInWeb() {
        return supportStatementsInWeb;
    }

    public void setSupportStatementsInWeb(boolean supportStatementsInWeb) {
        this.supportStatementsInWeb = supportStatementsInWeb;
    }

    @JsonIgnore
    public boolean isSupportStatementPdf() {
        return supportStatementPdf;
    }

    @JsonIgnore
    public int getVotingDaysLeft() {
        return Days.daysBetween(LocalDate.now(), endDate).getDays() +1;
    }

    @JsonIgnore
    public int getTotalVotingDays() {
        return Days.daysBetween(startDate, endDate).getDays() +1;
    }

    public void setSupportStatementPdf(boolean supportStatementPdf) {
        this.supportStatementPdf = supportStatementPdf;
    }

    @JsonIgnore
    public String getSupportStatementAddress() {
        return supportStatementAddress;
    }

    public void setSupportStatementAddress(String supportStatementAddress) {
        this.supportStatementAddress = supportStatementAddress;
    }

    public boolean isFinancialSupport() {
        return financialSupport;
    }

    public void setFinancialSupport(boolean financialSupport) {
        this.financialSupport = financialSupport;
    }

    @JsonSerialize(using = InitURISerializer.class)
    public InitURI getFinancialSupportURL() {
        return financialSupportURL;
    }

    public void setFinancialSupportURL(InitURI financialSupportURL) {
        this.financialSupportURL = financialSupportURL;
    }
    
    public boolean hasTranslation(String lang) {
        return name.hasTranslation(lang);
    }

    public int getExternalSupportCount() {
        if (isSupportStatementsInWeb() || isSupportStatementsOnPaper()) {
            return externalSupportCount;
        } else {
            return 0; // if there is no external support channel selected, external support should always be 0
        }
    }

    public void setExternalSupportCount(int externalSupportCount) {
        this.externalSupportCount = externalSupportCount;
    }

    public int getSentSupportCount() {
        return sentSupportCount;
    }
    
    @JsonIgnore
    public int getUnsentSupportCount() {
        return supportCount - sentSupportCount;
    }

    public void assignSentSupportCount(int sentSupportCount) {
        this.sentSupportCount = sentSupportCount;
    }

    public int getVerifiedSupportCount() {
        return verifiedSupportCount;
    }

    public void setVerifiedSupportCount(int verifiedSupportCount) {
        this.verifiedSupportCount = verifiedSupportCount;
    }

    @JsonSerialize(using=LocalDateJsonSerializer.class)
    public LocalDate getVerified() {
        return verified;
    }

    public void setVerified(LocalDate verified) {
        this.verified = verified;
    }

    @JsonSerialize(using=DateTimeJsonSerializer.class)
    public DateTime getSupportStatementsRemoved() {
        return supportStatementsRemoved;
    }

    public void assignSupportStatementsRemoved(DateTime supportStatementsRemoved) {
        this.supportStatementsRemoved = supportStatementsRemoved;
    }

    public boolean isVotingStarted(LocalDate now) {
        if (startDate == null) {
            return false;
        } else {
            return !now.isBefore(startDate);
        }
    }

    public boolean isVotingEnded(LocalDate now) {
        if (endDate == null) {
            return false;
        } else {
            return now.isAfter(getEndDate());
        }
    }

    boolean isSendToParliamentEnded(ReadablePeriod sendToParliamentDuration, LocalDate now) {
        LocalDate endDateForSendToParliament = getEndDateForSendToParliament(sendToParliamentDuration);
        return isInclusivePeriodEnded(endDateForSendToParliament, now);
    }

    public boolean isVotingInProggress() {
        return isVotingInProggress(LocalDate.now());
    }
    public boolean isVotingInProggress(LocalDate now) {
        return isVotingStarted(now) && !isVotingEnded(now);
    }
    
    public boolean isVotingSuspended(int minSupportCountForSearch, ReadablePeriod requiredMinSupportCountDuration, LocalDate now) {
        if (startDate == null) {
            return false;
        } else {
            return isVotingInProggress(now)
                    && !hasTotalSupportCountAtLeast(minSupportCountForSearch)
                    && isMinSupportCountDurationEnded(requiredMinSupportCountDuration, now);
        }
    }

    public LocalDate getEndDateForSendToVrk(ReadablePeriod sendToVrkDuration) {
        return getEndDateForPeriod(this.endDate, sendToVrkDuration);
    }
    
    public boolean isSendToVrkEnded(ReadablePeriod sendToVrkDuration, LocalDate now) {
        return isInclusivePeriodEnded(getEndDateForSendToVrk(sendToVrkDuration), now);
    }

    public boolean hasTotalSupportCountAtLeast(int minSupportCountForSearch) {
        return getTotalSupportCount() >= minSupportCountForSearch;
    }

    boolean isMinSupportCountDurationEnded(ReadablePeriod requiredMinSupportCountDuration, LocalDate now) {
        return isInclusivePeriodEnded(startDate, requiredMinSupportCountDuration, now);
    }


    /*
     * Generic helpers for date handling
     */
    
    private static boolean isInclusivePeriodEnded(LocalDate periodStartDate, ReadablePeriod period, LocalDate now) {
        LocalDate periodEndDate = getEndDateForPeriod(periodStartDate, period);
        return isInclusivePeriodEnded(periodEndDate, now);
    }
    
    private static boolean isInclusivePeriodEnded(LocalDate periodEndDate, LocalDate now) {
        if (periodEndDate == null) {
            return false;
        } else {
            return now.isAfter(periodEndDate);
        }
    }

    private static LocalDate getEndDateForPeriod(LocalDate periodStartDate, ReadablePeriod period) {
        return periodStartDate != null ? periodStartDate.plus(period) : null;
    }

    @JsonIgnore
    public String getParliamentIdentifier() {
        return parliamentIdentifier;
    }

    @JsonIgnore
    public String getParliamentURL() {
        return parliamentURL;
    }

    @JsonIgnore
    public LocalDate getParliamentSentTime() {
        return parliamentSentTime;
    }

    public void setParliamentIdentifier(String parliamentIdentifier) {
        this.parliamentIdentifier = parliamentIdentifier;
    }

    public void setParliamentURL(String parliamentURL) {
        this.parliamentURL = parliamentURL;
    }

    public void setParliamentSentTime(LocalDate parliamentSentTime) {
        this.parliamentSentTime = parliamentSentTime;
    }
}
