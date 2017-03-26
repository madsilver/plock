package br.com.silver.plock.util;

import android.os.AsyncTask;

/**
 * Created by silver on 25/03/17.
 */

public class CommandTask extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
        return null;
    }

    /**
     * Interface
     */
    public interface OnDone {
        void onDone(String done);
    }
}
