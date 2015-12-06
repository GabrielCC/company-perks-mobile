package discounts.nfc.zitec.com.discountsnfc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.Map;

public class RegisterUser2Activity extends BaseActivity {
    EditText firstName, lastName ;
    Button submitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        associateProgressBar();
        submitBtn = (Button) findViewById(R.id.submit_btn);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    public void submit()
    {
        UserData user = UserData.getInstance();
        user.firstName = firstName.getText().toString();
        user.lastName = lastName.getText().toString();
        new SaveUserTask().execute(user);
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    public class SaveUserTask extends AsyncTask<UserData, Void, Boolean> {
        public ParseException thrownException;

        @Override
        protected void onPreExecute() {
            showLoader();
        }

        @Override
        protected Boolean doInBackground(UserData... params) {
            UserData user = params[0];
            try {
                ParseObject company = new ParseObject("Company");
                company.setObjectId(user.companyId);

                ParseObject employee = new ParseObject("Employee");
                employee.put("firstName", user.firstName);
                employee.put("lastName", user.lastName);
                employee.put("companyId", company);
                employee.save();

                ParseObject card = new ParseObject("Card");
                card.put("employeeId", employee);
                card.put("qrCode", user.nfcId);
                card.save();
                return true;
            } catch (ParseException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            hideLoader();
            if (result)
            {
                UserData.getInstance().wasRegistered = true;
                finish();
            }
            else
            {
                displayErrorDialog("Could not save your card. Please try again later");
            }
        }
    }

}
