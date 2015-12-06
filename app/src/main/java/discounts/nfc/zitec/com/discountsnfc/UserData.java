package discounts.nfc.zitec.com.discountsnfc;

/**
 * Created by Vlad on 12/5/2015.
 */
public class UserData
{
    private static UserData instance;
    public static UserData getNewInstance()
    {
        instance = new UserData();
        return instance;
    }

    public static UserData getInstance()
    {
        return instance;
    }

    public String firstName, companyId, lastName, nfcId;
    public boolean wasRegistered = false;
}
