package br.com.silver.plock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import br.com.silver.plock.util.WebClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private CommandTask mCommTask = null;

    @BindView(R.id.pin) EditText mPinView;
    @BindView(R.id.command_layout) View mCommandView;
    @BindView(R.id.send_progress) View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.config_button)
    public void openConfig() {
        startActivity(new Intent(this, ConfigActivity.class));
    }

    @OnClick(R.id.send_button)
    public void attemptSend() {
        if (mCommTask != null) {
            return;
        }

        // Reset errors.
        mPinView.setError(null);

        String pin = mPinView.getText().toString();

        if (pin != "") {
            showProgress(true);
            mCommTask = new CommandTask(pin);
            mCommTask.execute((Void) null);
        } else {
            mPinView.setError(getString(R.string.error_incorrect_pin));
            mPinView.requestFocus();
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


    public class CommandTask extends AsyncTask<Void, Void, String> {

        private final String mPin;

        CommandTask(String pin) {
            mPin = pin;
        }

        @Override
        protected String doInBackground(Void... params) {
            WebClient wc = new WebClient(MainActivity.this);
            try {
                String response = wc.get();
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
                mPinView.setError(getString(R.string.error_incorrect_pin));
                mPinView.requestFocus();
            }

            Toast.makeText(MainActivity.this, success, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onCancelled() {
            mCommTask = null;
            showProgress(false);
        }
    }
}

