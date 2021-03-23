package com.example.licenta.asyncTask;

public interface Callback<R> {
    void runResultOnUiThread(R result);
}
