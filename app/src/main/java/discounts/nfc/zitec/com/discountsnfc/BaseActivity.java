package discounts.nfc.zitec.com.discountsnfc;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static final String MERCHANT_ID = "YFF4QV33J4PDW";
    public static final int DIALOG_CLOSE_DELAY_TIME = 8000;
    public static final String CARD_NOT_REGISTERED_ERROR_CODE = "404.1";
    public static final String PREF_KEY_MERCHANT_ID = "merchantId";
    public static final String PREF_KEY_DEVICE_ID = "deviceId";
    protected ProgressBar progressBar;
    protected SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        /*
        if ((!prefs.contains(PREF_KEY_MERCHANT_ID) || !prefs.contains(PREF_KEY_DEVICE_ID)) && !(this instanceof SetupActivity))
        {
            startActivity(new Intent(this,SetupActivity.class));
            finish();
        }
        */
    }


    protected void associateProgressBar()
    {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    public void showLoader()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoader()
    {
        progressBar.setVisibility(View.GONE);
    }

    protected void displayErrorDialog(String error)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_error);
        dialog.setTitle("Ooops...");

        // set the custom dialog components - text, image and button
        TextView error_text = (TextView) dialog.findViewById(R.id.error_text);

        error_text.setText(error);

        dialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    dialog.cancel();
                }
                catch (Exception e){}
            }
        }, DIALOG_CLOSE_DELAY_TIME);
    }
}
