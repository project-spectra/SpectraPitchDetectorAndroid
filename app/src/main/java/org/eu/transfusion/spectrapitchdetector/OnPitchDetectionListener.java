package org.eu.transfusion.spectrapitchdetector;

public interface OnPitchDetectionListener {

    /**
     * https://0110.be/releases/TarsosDSP/TarsosDSP-2.1/TarsosDSP-2.1-Documentation/be/tarsos/dsp/pitch/PitchDetectionResult.html
     */
    void onPitchDetectionResult(float pitchHz, float probability, boolean isPitched);
}
