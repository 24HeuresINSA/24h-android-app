package com.insadelyon.les24heures.eventbus;

import retrofit.RetrofitError;

/**
 * Created by remi on 14/04/15.
 */
public class RetrofitErrorEvent {
    String raison;
    RetrofitError retrofitError;

    public RetrofitErrorEvent(String raison, RetrofitError retrofitError) {
        this.raison = raison;
        this.retrofitError = retrofitError;
    }

    public RetrofitErrorEvent(RetrofitError retrofitError) {
        this.retrofitError = retrofitError;
    }

    public RetrofitErrorEvent() {

    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }

    public RetrofitError getRetrofitError() {
        return retrofitError;
    }

    public void setRetrofitError(RetrofitError retrofitError) {
        this.retrofitError = retrofitError;
    }
}
