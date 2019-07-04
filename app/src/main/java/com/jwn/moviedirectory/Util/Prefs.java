package com.jwn.moviedirectory.Util;

import android.app.Activity;
import android.content.SharedPreferences;

// allows saving of search terms into sharedPreference
// so the last thing search for is in the search box, OR if no previous searches, show a default title
public class Prefs {
    SharedPreferences sharedPreferences;

    public Prefs(Activity activity) {
        sharedPreferences = activity.getPreferences(activity.MODE_PRIVATE);
    }

    // save the search term into sharedPreferences
    public void setSearch(String search) {
        sharedPreferences.edit().putString("search", search).commit();
    }

    //set the default search term
    public String getSearch() {
        return sharedPreferences.getString("search", "Batman");
    }
}
