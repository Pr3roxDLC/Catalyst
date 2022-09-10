package com.krazzzzymonkey.catalyst.managers.accountManager.alt;

import com.krazzzzymonkey.catalyst.managers.accountManager.Config;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * @author mrebhan
 * @author The_Fireplace
 */
public class AltDatabase implements Serializable {

    public static final long serialVersionUID = 0xA17DA7AB;
    private static AltDatabase instance;

    private final ArrayList<AccountData> altList;

    private AltDatabase() {
        this.altList = new ArrayList<>();
    }

    private static void loadFromConfig() {
        if (instance == null)
            instance = (AltDatabase) Config.getInstance().getKey("altaccounts");
    }

    private static void saveToConfig() {
        Config.getInstance().setKey("altaccounts", instance);
    }

    public static AltDatabase getInstance() {
        loadFromConfig();
        if (instance == null) {
            instance = new AltDatabase();
            saveToConfig();
        }
        return instance;
    }

    public ArrayList<AccountData> getAlts() {
        return this.altList;
    }
}