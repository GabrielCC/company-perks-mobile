package discounts.nfc.zitec.com.discountsnfc;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseCloud;
import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private NfcAdapter mNfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;
    private String currentNfcId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        associateProgressBar();


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            //alert NFC disabled
        } else {

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
        String id = parseTag(tagFromIntent);
        if (id == null) {
            handleGeneralError();
        }
        currentNfcId = id;
        Map<String, String> params = new HashMap<String, String>();
        params.put("merchantId", MERCHANT_ID);
        params.put("nfcId", id);
        new DiscountApplyTask().execute(params);
    }

    public class DiscountApplyTask extends AsyncTask<Map<String, String>, Void, Map<String, String>> {
        public ParseException thrownException;

        @Override
        protected void onPreExecute() {
            showLoader();
        }

        @Override
        protected Map<String, String> doInBackground(Map<String, String>... params) {
            try {
                return ParseCloud.callFunction("cloverDiscount", params[0]);
            } catch (ParseException e) {
                thrownException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            hideLoader();
            if (result == null || result.isEmpty()) {
                if (result == null && thrownException != null) {
                    handleParseError(thrownException);
                } else {
                    handleGeneralError();
                }
            } else {
                displayUserInfo(result);
            }
        }
    }

    protected String parseTag(Tag tag) {
        String id = "";
        byte[] bytes = tag.getId();
        if (bytes.length == 0) {
            return null;
        } else {
            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        }
    }

    protected void handleParseError(ParseException e) {
        try {
            JSONObject error = new JSONObject(e.getMessage());
            if (error.has("code"))
            {
                String code = error.getString("code");
                if (code.equals(CARD_NOT_REGISTERED_ERROR_CODE))
                {
                    UserData.getNewInstance().nfcId = currentNfcId;
                    startActivity(new Intent(this,RegisterUser1Activity.class));
                }
                else
                {
                    displayErrorDialog(error.getString("message"));
                }
            }
        } catch (JSONException e1) {
            displayErrorDialog(e.getMessage());
        }

    }

    protected void handleGeneralError() {
        displayErrorDialog("Could not read NFC data");
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
                dialog.cancel();
            }
        }, DIALOG_CLOSE_DELAY_TIME);
    }

    protected void displayUserInfo (Map<String, String> info)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_results);
        dialog.setTitle("Discount applied");

        // set the custom dialog components - text, image and button
        TextView firstName = (TextView) dialog.findViewById(R.id.firstName);
        TextView lastName = (TextView) dialog.findViewById(R.id.lastName);
        TextView companyName = (TextView) dialog.findViewById(R.id.companyName);
        TextView discount = (TextView) dialog.findViewById(R.id.discount);

        firstName.setText(info.get("firstName"));
        lastName.setText(info.get("lastName"));
        companyName.setText(info.get("companyName"));
        discount.setText(info.get("discount")+"%");

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
