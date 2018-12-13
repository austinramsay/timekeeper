package com.austinramsay.events;

public interface OrgCreatorListener {
    void submitNewOrganization(NewOrganizationEvent noe);
    void fireVerifiedFields(boolean verified);
}
