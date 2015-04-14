package com.insalyon.les24heures.service.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.insalyon.les24heures.DTO.ApplicationVersionDTO;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.ApplicationVersionEvent;
import com.insalyon.les24heures.service.ApplicationVersionService;
import com.insalyon.les24heures.service.RetrofitService;
import com.insalyon.les24heures.utils.ApplicationVersionState;
import com.insalyon.les24heures.utils.RetrofitErrorHandler;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by remi on 13/04/15.
 */
public class ApplicationVersionServiceImpl implements ApplicationVersionService {

    static ApplicationVersionServiceImpl applicationVersionService;




    public static ApplicationVersionServiceImpl getInstance() {
        if (applicationVersionService == null) {
            //synchronized (resourceService) {
            applicationVersionService = new ApplicationVersionServiceImpl();
            //}
        }
        return applicationVersionService;
    }


    @Override
    public void checkApplicationVersion(final Context context) {
        final EventBus eventBus = EventBus.getDefault();

        //each time we check for app version, we ensure that applicationVersionState is null while we did'nt get the answer
        final SharedPreferences[] settings = {context.getSharedPreferences(context.getResources().getString(R.string.SHARED_PREF_APP_VERSION), 0)};
        final SharedPreferences.Editor[] editor = {settings[0].edit()};
        editor[0].putString("applicationVersionState", null);
        editor[0].commit();

        new RestAdapter.Builder()
                .setEndpoint(context.getResources().getString(R.string.backend_url_mobile))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new RetrofitErrorHandler())
                .build().create(RetrofitService.class).getApplicationVersion(new Callback<ApplicationVersionDTO>() {
            @Override
            public void success(ApplicationVersionDTO applicationVersionDTO, Response response)  {
                String appVersion = applicationVersionDTO.getAndroid();




                ApplicationVersionEvent event = new ApplicationVersionEvent();

                if (appVersion != null && !appVersion.isEmpty()) {
                    PackageInfo pInfo = null;
                    try {
                        pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        //TODO piwik error
                        event.setState(ApplicationVersionState.TODATE);
                    }
                    String currentVersion = pInfo.versionName;

                    if (appVersion.split("\\.").length == 2 && currentVersion.split("\\.").length == 2)
                        if (appVersion.split("\\.")[0].equals(currentVersion.split("\\.")[0]))
                            if (appVersion.split("\\.")[1].equals(currentVersion.split("\\.")[1]))
                                event.setState(ApplicationVersionState.TODATE);
                            else
                                event.setState(ApplicationVersionState.MINOR);
                        else
                            event.setState(ApplicationVersionState.MAJOR);
                    else
                        event.setState(ApplicationVersionState.TODATE);                        //TODO piwik error
                } else
                    event.setState(ApplicationVersionState.TODATE);                        //TODO piwik error


                 settings[0] = context.getSharedPreferences(context.getResources().getString(R.string.SHARED_PREF_APP_VERSION), 0);
                editor[0] = settings[0].edit();
                editor[0].putString("applicationVersionState", event.getState().toString());
                editor[0].commit();
                eventBus.post(event);
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

    }
}
