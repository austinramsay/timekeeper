package com.austinramsay.events;

import com.austinramsay.timekeeper.CorrectionRequest;
import com.austinramsay.types.Marked;

public interface CorrectionViewerButtonListener {
    void fireRemoveCorrection();
    void fireMarkup(Marked marked);
    void closeWindow();
}
