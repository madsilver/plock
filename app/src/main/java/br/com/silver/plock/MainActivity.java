package br.com.silver.plock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import br.com.silver.plock.util.WebClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private CommandTask mCommTask = null;
    private String mUrl = null;
    private String mParam = null;
    private Boolean requireCode = false;

    @BindView(R.id.code) EditText mCodeView;
    @BindView(R.id.ssid_label) TextView mSSIDView;
    @BindView(R.id.command_layout) View mCommandView;
    @BindView(R.id.send_progress) View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mUrl = prefs.getString(getString(R.string.pref_url), "");
        mParam = prefs.getString(getString(R.string.pref_url_param), "");

        String ssid = WebClient.getSSID(this);
        mSSIDView.setText(ssid);
    }

    @OnClick(R.id.send_button)
    public void attemptSend() {
        if (mCommTask != null) {
            return;
        }

        if(mUrl.length() < 1 || mParam.length() < 1) {
            Toast.makeText(MainActivity.this, "Settings required", Toast.LENGTH_LONG).show();
            return;
        }

        mCodeView.setError(null);

        String code = mCodeView.getText().toString();

        if (code == null && requireCode ) {
            mCodeView.setError(getString(R.string.error_incorrect_code));
            mCodeView.requestFocus();
        } else {
            showProgress(true);
            mCommTask = new CommandTask(code, mUrl, mParam);
            mCommTask.execute((Void) null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_config:
                startActivity(new Intent(this, ConfigActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class CommandTask extends AsyncTask<Void, Void, String> {

        private final String mCode;
        private final String mUrl;
        private final String mParam;

        CommandTask(String code, String url, String param) {
            mCode = code;
            mUrl = url;
            mParam = param;
        }

        @Override
        protected String doInBackground(Void... params) {
            WebClient wc = new WebClient(mUrl, mParam);
            try {
                String response = wc.get(mCode);
                return response;
            } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(final String success) {
            mCommTask = null;
            showProgress(false);

            if (success != "ok") {
                mCodeView.setError(getString(R.string.error_incorrect_code));
                mCodeView.requestFocus();
            }

            Toast.makeText(MainActivity.this, success, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onCancelled() {
            mCommTask = null;
            showProgress(false);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCommandView.setVisibility(show ? View.GONE : View.VISIBLE);
            mCommandView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCommandView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mCommandView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

