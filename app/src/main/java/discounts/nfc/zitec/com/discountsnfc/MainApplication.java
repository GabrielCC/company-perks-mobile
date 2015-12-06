package discounts.nfc.zitec.com.discountsnfc;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Vlad on 12/5/2015.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "zdY62t9B2BSHO2q4MIwsnkJLdYhTLTTQl2Pu1aMI", "Pesfa10QqGCAu8xW5qH1ylQFMEMZgtPq6hn786pj");
    }
}
