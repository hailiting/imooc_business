package com.example.lib_image_loader.app;


import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.lib_image_loader.R;
import com.example.lib_image_loader.image.Utils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * 图片加载类，与外界的唯一通信类，
 * 支持为各种view，notification,appwidget, viewgroup加载图片
 */
public class ImageLoaderManager {

    private ImageLoaderManager() {

    }

    private static class SingletonHolder {
        private static ImageLoaderManager instance = new ImageLoaderManager();
    }

    public static ImageLoaderManager getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 为ImageView加载图片
     */
    public void displayImageForView(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(imageView);
    }

    /**
     * 为imageview加载圆形图片
     *
     * @param imageView
     * @param url
     */
    public void displayImageForCircle(final ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new BitmapImageViewTarget(imageView) {
                    //将imageview包装成target
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory
                                .create(imageView.getResources(), resource);
                        drawable.setCircular(true);
                        imageView.setImageDrawable(drawable);
                    }
                });
    }


    /**
     * 完成为viewgroup设置背景并模糊处理
     *
     * @param group
     * @param url
     */

    public void displayImageForViewGroup(final ViewGroup group, String url, final int radius) {
        Glide.with(group.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new SimpleTarget<Bitmap>() {//设置宽高
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        final Bitmap res = resource;
                        Disposable subscribe = Observable.just(resource)
                                .map(new Function<Bitmap, Drawable>() {
                                    @Override
                                    public Drawable apply(Bitmap bitmap) {
                                        return new BitmapDrawable(
                                                //毛玻璃效果
                                                Utils.doBlur(res, radius, true)
                                        );
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Drawable>() {
                                    @Override
                                    public void accept(Drawable drawable) {
                                        group.setBackground(drawable);
                                    }
                                });
                    }
                });
    }
    /**
     * 为notification中的id控件加载图片
     *
     * @param context
     * @param rv
     * @param id
     * @param notification
     * @param NOTIFICATION_ID
     * @param url
     */
    public void displayImageForNotification(Context context, RemoteViews rv,
                                            int id, Notification notification,
                                            int NOTIFICATION_ID, String url) {
        this.displayImageForTarget(context, initNotificationTaret(context, rv, id, notification, NOTIFICATION_ID), url);
    }

    //构造一个notification target
    private NotificationTarget initNotificationTaret(Context context, RemoteViews rv,
                                                     int id, Notification notification,
                                                     int NOTIFICATION_ID) {
        NotificationTarget target =
                new NotificationTarget
                        (context, id, rv, notification,
                                NOTIFICATION_ID);
        return target;
    }

    /**
     * 为非view加载图片
     */
    private void displayImageForTarget(Context context, Target target, String url) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(BitmapTransitionOptions.withCrossFade())
                .fitCenter()
                .into(target);
    }


    private RequestOptions initCommonRequestOption() {
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.b4y)
                .error(R.mipmap.b4y)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .priority(Priority.NORMAL);
        return options;
    }
}
