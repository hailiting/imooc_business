package com.example.lib_network.okhttp;

import com.example.lib_network.okhttp.listener.DisposeDataHandle;
import com.example.lib_network.okhttp.response.CommonFileCallback;
import com.example.lib_network.okhttp.response.CommonJsonCallback;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommonOkHttpClient {
    private static final int TIME_OUT = 30000;
    private static OkHttpClient mOkHttpClient;

    // 完成对okhttpClient初始化
    static {
        OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();
        okhttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession sslSession) {
                // hostname 黑名单 白名单
                return true;
            }
        });
        okhttpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("User-Agent", "Imooc-Mobile").build();
                return chain.proceed(request);
            }
        });
        okhttpClientBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        okhttpClientBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        okhttpClientBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
        okhttpClientBuilder.followRedirects(true);
        mOkHttpClient = okhttpClientBuilder.connectionSpecs(Collections.singletonList(ConnectionSpec.CLEARTEXT)).build();
    }
    public static Call get(Request request, DisposeDataHandle handle){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(handle));
        return call;
    }
    public static Call post(Request request, DisposeDataHandle handle){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(handle));
        return call;
    }

    /**
     * 下载请求
     * @return
     */
    public static Call downloadFile(Request request, DisposeDataHandle handle){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonFileCallback(handle));
        return call;
    }
}
