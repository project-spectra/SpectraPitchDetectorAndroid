package org.eu.transfusion.spectrapitchdetector;

import android.os.AsyncTask;

/**
 * https://stackoverflow.com/questions/9963691/android-asynctask-sending-callbacks-to-ui
 * Use a callback interface to send things back to the NS UI thread.
 */

public class PitchDetector {

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
            while(!isCancelled()) {
                try {
                    Thread.sleep(3000);
                    publishProgress(new ProgressUpdateInfo(220.4f, 0.8f, true));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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

    }

    /*public void startPitchDetection() {

    }*/
}
