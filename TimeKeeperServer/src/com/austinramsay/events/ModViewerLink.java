package com.austinramsay.events;

import com.austinramsay.timekeeper.CorrectionRequest;

public interface ModViewerLink {
    void fireRemoveCorrection(CorrectionRequest correction);
    void refreshModel();
}
