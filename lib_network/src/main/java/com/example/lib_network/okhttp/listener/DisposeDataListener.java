package com.example.lib_network.okhttp.listener;

public interface DisposeDataListener {
    void onSuccess(Object responseObj);

    void onFailure(Object reasonObj);
}
