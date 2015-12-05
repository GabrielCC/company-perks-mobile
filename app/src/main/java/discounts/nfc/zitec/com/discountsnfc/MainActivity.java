package discounts.nfc.zitec.com.discountsnfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "ZTC-Discounts-NFC";

    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView_explanation);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText("NFC is disabled.");
        } else {
            mTextView.setText("Nothing yet");
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        intentFiltersArray = new IntentFilter[]{ndef, tech};
        techListsArray = new String[][]{new String[]{android.nfc.tech.IsoDep.class.getName()},
                new String[]{android.nfc.tech.NfcA.class.getName()},
                new String[]{android.nfc.tech.NfcB.class.getName()},
                new String[]{android.nfc.tech.NfcF.class.getName()},
                new String[]{android.nfc.tech.NfcV.class.getName()},
                new String[]{android.nfc.tech.Ndef.class.getName()},
                new String[]{android.nfc.tech.NdefFormatable.class.getName()},
                new String[]{android.nfc.tech.MifareClassic.class.getName()},
                new String[]{android.nfc.tech.MifareUltralight.class.getName()}};
    }

    public void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }

    public void onNewIntent(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //do something with tagFromIntent
        parseTag(tagFromIntent);
    }

    protected void parseTag(Tag tag)
    {
        for (String tech : tag.getTechList())
        {
            if (tech.equals(android.nfc.tech.Ndef.class.getName()))
            {

            }
            else if (tech.equals(android.nfc.tech.NfcA.class.getName()))
            {
                
            }
        }
    }
}
