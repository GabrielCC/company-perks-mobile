package discounts.nfc.zitec.com.discountsnfc;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterUser1Activity extends BaseActivity {

    List<Company> companyList;
    CharSequence[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
        associateProgressBar();
    }

    public void selectedCompanyId(String id)
    {
        UserData.getInstance().companyId = id;
        startActivity(new Intent(this,RegisterUser2Activity.class));
        finish();
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        Map<String, String> params = new HashMap<String, String>();
        params.put("merchantId", MERCHANT_ID);
        new GetCompaniesTask().execute(params);
    }

    public class GetCompaniesTask extends AsyncTask<Map<String, String>, Void, List<Map<String, String>>> {
        public ParseException thrownException;

        @Override
        protected void onPreExecute() {
            showLoader();
        }

        @Override
        protected List<Map<String, String>> doInBackground(Map<String, String>... params) {
            try {
                return ParseCloud.callFunction("getCompanies", params[0]);
            } catch (ParseException e) {
                thrownException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Map<String, String>> result) {
            hideLoader();
            if (result == null || result.isEmpty()) {

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUser1Activity.this);
                builder.setTitle("Select your company");
                //builder.setCancelable(false);
                items = new CharSequence[result.size()];
                int i = 0;
                companyList = new ArrayList<>();
                for (Map<String, String> item : result)
                {
                    companyList.add(new Company(item.get("companyId"),item.get("name")));
                    items[i] = item.get("name");
                    i++;
                }
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedCompany = items[which].toString();
                        for (Company c : companyList) {
                            if (c.name.equals(selectedCompany)) {
                                selectedCompanyId(c.companyId);
                                break;
                            }
                        }
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        finish();
                    }
                });
                try {
                    builder.show();
                }
                catch (Exception e)
                {

                }
            }
        }
    }

}
