package com.insalyon.les24heures.socialSharing;

import android.content.Context;
import android.content.Intent;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;

/**
 * Created by lbillon on 5/3/2015.
 */
public class ShareIntentFactory {
    static String BASE_DAY_URL = "http://24heures.org/animations#";
    static String BASE_NIGHT_URL = "http://24heures.org/concerts#";

    public static Intent getResourceSharingIntent(Context context, DayResource dayResource) {
        String append_message = getAppendMessage(context);
        Intent sharingIntent = getBaseIntent();
        sharingIntent.putExtra(Intent.EXTRA_TEXT, dayResource.getTitle() + ' ' + append_message + '\n' + BASE_DAY_URL + dayResource.get_id());

        return sharingIntent;
    }

    public static Intent getResourceSharingIntent(Context context, NightResource nightResource) {
        String append_message = getAppendMessage(context);
        Intent sharingIntent = getBaseIntent();
        sharingIntent.putExtra(Intent.EXTRA_TEXT, nightResource.getTitle() + ' ' + append_message + '\n' + BASE_NIGHT_URL + nightResource.get_id());

        return sharingIntent;
    }

    private static Intent getBaseIntent() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        return sharingIntent;
    }

    private static String getAppendMessage(Context context) {
        return context.getResources().getString(R.string.sharing_append_message);
    }
}