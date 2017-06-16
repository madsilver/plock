package br.com.silver.plock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    @BindView(R.id.url_label) TextView mUrlLabel;
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
        mUrlLabel.setText(mUrl);

//        String ssid = WebClient.getSSID(this);
//        mSSIDView.setText(ssid.replace("\"",""));
    }

    private Boolean validate() {
        if (mCommTask != null) {
            return false;
        }

        if(mUrl.length() < 1) {
            Toast.makeText(MainActivity.this, "Settings required", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @OnClick(R.id.button_action_1)
    public void action1() {
        if (validate()) {
            showProgress(true);
            String cmd = String.format("%s/%s", mUrl, "dev1");
            mCommTask = new CommandTask(cmd);
            mCommTask.execute((Void) null);
        }
    }

    @OnClick(R.id.button_action_2_on)
    public void action2on() {
        if (validate()) {
            showProgress(true);
            String cmd = String.format("%s/%s", mUrl, "dev2?cmd=1");
            mCommTask = new CommandTask(cmd);
            mCommTask.execute((Void) null);
        }
    }

    @OnClick(R.id.button_action_2_off)
    public void action2off() {
        if (validate()) {
            showProgress(true);
            String cmd = String.format("%s/%s", mUrl, "dev2?cmd=0");
            mCommTask = new CommandTask(cmd);
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

    public class CommandTask extends AsyncTask<Void, Void, Integer> {

        private final String mUrl;

        CommandTask(String url) {
            mUrl = url;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            WebClient wc = new WebClient(mUrl);
            try {
                int response = wc.request();
                return response;
            } catch (IOException e) {
                e.printStackTrace();
                return 412;
            }
        }

        @Override
        protected void onPostExecute(final Integer status) {
            mCommTask = null;
            showProgress(false);
            String resp;
            switch(status) {
                case 200: resp = "OK"; break;
                case 400: resp = "Bad Request"; break;
                case 404: resp = "Not Found"; break;
                case 412: resp = "Connection Failure"; break;
                case 500: resp = "Internal Server Error"; break;
                default: resp = "Unidentified Error";
            }
            Toast.makeText(MainActivity.this, resp, Toast.LENGTH_LONG).show();
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

