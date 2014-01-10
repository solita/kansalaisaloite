package fi.om.initiative.service;

import fi.om.initiative.dto.InfoTextFooterLink;

import java.util.List;
import java.util.Locale;

public interface FooterLinkProvider {
    List<InfoTextFooterLink> getFooterLinks(Locale locale);
}
