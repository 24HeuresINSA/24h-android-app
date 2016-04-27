package com.insadelyon.les24heures.socialSharing;

import android.content.Intent;
import android.widget.ShareActionProvider;


public class OnShareTargetSelectedListener implements ShareActionProvider.OnShareTargetSelectedListener {

        @Override
        public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
            final String packageName = intent.getComponent().getPackageName();
            if (packageName.equals("com.facebook.katana")) {
                // Do something for Facebook
            } else if (packageName.equals("com.twitter.android")) {
                // Do something for Twitter
            }
            return false;
        }


}
