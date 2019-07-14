package org.eu.transfusion.spectrapitchdetector;

import android.os.AsyncTask;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

/**
 * https://stackoverflow.com/questions/9963691/android-asynctask-sending-callbacks-to-ui
 * Use a callback interface to send things back to the NS UI thread.
 */

public class PitchDetector {

    private static final int SAMPLE_RATE = 22050;
    private static final int BUFFER_SIZE = 1024 * 4;
    private static final int OVERLAP = 768 * 4;
    private static final int MIN_ITEMS_COUNT = 15;

    private static class ProgressUpdateInfo {
        public final float pitchHz;
        public final float probability;
        public final boolean isPitched;

        ProgressUpdateInfo(float pitchHz, float probability, boolean isPitch) {
            this.pitchHz = pitchHz;
            this.probability = probability;
            this.isPitched = isPitch;
        }
    }

    /**
     * doInBackground accepts no arguments, progress updates should be cents,
     * no return result (Void)
     */
    public static class PitchDetectorAsyncTask extends AsyncTask<Void, ProgressUpdateInfo, Void> {

        private AudioDispatcher audioDispatcher;

        // the interface meant to pass data back to the nativescript instance
        private OnPitchDetectionListener listener;
//        private boolean running;

        public PitchDetectorAsyncTask(OnPitchDetectionListener listener) {
            this.listener = listener;
//            this.running = true;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... voids) {

            PitchDetectionHandler pitchDetectionHandler = (pitchDetectionResult, audioEvent) -> {

                if (isCancelled()) {
                    stopAudioDispatcher();
                    return;
                }

                /*if (!IS_RECORDING) {
                    IS_RECORDING = true;
                    publishProgress();
                }*/

//                float pitch = pitchDetectionResult.getPitch();

                publishProgress(new ProgressUpdateInfo(pitchDetectionResult.getPitch(),
                        pitchDetectionResult.getProbability(), pitchDetectionResult.isPitched()));

            };

            PitchProcessor pitchProcessor = new PitchProcessor(PitchEstimationAlgorithm.FFT_YIN,
                    SAMPLE_RATE,
                    BUFFER_SIZE, pitchDetectionHandler);

            audioDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE,
                    BUFFER_SIZE, OVERLAP);

            audioDispatcher.addAudioProcessor(pitchProcessor);

            audioDispatcher.run();

            /*while(!isCancelled()) {
                try {
                    Thread.sleep(3000);
                    publishProgress(new ProgressUpdateInfo(220.4f, 0.8f, true));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
            return null;
        }

        // called by publishProgress(...) within doInBackground;
        @Override
        protected void onProgressUpdate(ProgressUpdateInfo... updateInfos) {
            ProgressUpdateInfo updateInfo = updateInfos[0];
            // void onPitchDetectionResult(float pitch, float probability, boolean isPitched);
            this.listener.onPitchDetectionResult(updateInfo.pitchHz, updateInfo.probability, updateInfo.isPitched);
        }

        @Override
        protected void onPostExecute(Void result) {
            // execution of result of Long time consuming operation
//            progressDialog.dismiss();
//            finalResult.setText(result);
        }

        @Override
        protected void onCancelled(Void result) {
            stopAudioDispatcher();
        }

        private void stopAudioDispatcher() {
            if (audioDispatcher != null && !audioDispatcher.isStopped()) {
                audioDispatcher.stop();
//                IS_RECORDING = false;
            }
        }

    }

    /*public void startPitchDetection() {

    }*/
}
