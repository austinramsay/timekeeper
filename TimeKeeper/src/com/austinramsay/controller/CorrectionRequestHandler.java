package com.austinramsay.controller;

import com.austinramsay.timekeeper.CorrectionRequest;

public interface CorrectionRequestHandler {
    void sendCorrection(CorrectionRequest correction);
}
