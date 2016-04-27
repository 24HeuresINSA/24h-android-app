package com.insadelyon.les24heures.utils;

import com.insadelyon.les24heures.eventbus.RetrofitErrorEvent;

import de.greenrobot.event.EventBus;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created by remi on 14/04/15.
 */
public class RetrofitErrorHandler implements ErrorHandler {

    EventBus eventBus;

    public RetrofitErrorHandler() {
        eventBus = EventBus.getDefault();
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        RetrofitErrorEvent event = new RetrofitErrorEvent(cause);

        eventBus.postSticky(event);

        return cause;
    }
}
