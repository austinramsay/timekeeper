package com.austinramsay.events;

import com.austinramsay.model.Organization;

public interface OrganizationSelectionListener {
    void defineCurrentOrg(Organization selectedOrg);
}
