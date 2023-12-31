package com.example.lib_audio.mediaplayer.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.media.tv.AitInfo;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.example.lib_audio.R;
import com.example.lib_audio.mediaplayer.core.AudioController;
import com.example.lib_audio.mediaplayer.core.CustomMediaPlayer;
import com.example.lib_audio.mediaplayer.db.RoomHelper;
import com.example.lib_audio.mediaplayer.events.AudioFavoriteEvent;
import com.example.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.example.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.example.lib_audio.mediaplayer.events.AudioProgressEvent;
import com.example.lib_audio.mediaplayer.events.AudioStartEvent;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_common_ui.base.BaseActivity;
import com.example.lib_image_loader.app.ImageLoaderManager;
import com.example.lib_audio.mediaplayer.utils.Utils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 播放音乐的Activity
 */
public class MusicPlayerActivity extends BaseActivity {

    private RelativeLayout mBgView;
    private TextView mInfoView;
    private TextView mAuthorView;

    private ImageView mFavouriteView;


    private SeekBar mProgressView;
    private TextView mStartTimeView;
    private TextView mTotalTimeView;


    private ImageView mPlayModeView;
    private ImageView mPlayView;
    private ImageView mNextView;
    private ImageView mPreviousView;

    private Animator animator;


    /**
     * data
     */
    private AudioBean mAudioBean; // 当前正在播放的歌曲
    private AudioController.PlayMode mPlayMode; // 当前播放模式



    public static void start(Activity context) {
        Intent intent = new Intent(context, MusicPlayerActivity.class);
        ActivityCompat.startActivity(context, intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(context).toBundle());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 为当前activity添加入场动画
            getWindow().setEnterTransition(
                    TransitionInflater.from(this)
                            .inflateTransition(
                                    R.transition.transition_bottom2top
                            )
            );
        }
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_music_service_layout);
        initData();
        initView();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    private void initData(){
        mAudioBean = AudioController.getInstance().getNowPlaying();
        mPlayMode = AudioController.getInstance().getPlayMode();
    }
    private void initView(){
        mBgView = findViewById(R.id.root_layout);
        // 给背景添加模糊效果
//        ImageLoaderManager.getInstance().displayImageForViewGroup(
//                mBgView, mAudioBean.albumPic,100
//        );
        findViewById(R.id.back_view).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.title_layout).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            }
        });
        findViewById(R.id.share_view).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                shareMusic(mAudioBean.mUrl, mAudioBean.name);
            }
        });

        findViewById(R.id.show_list_view).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // 弹出歌单列表dialog
                MusicListDialog dialog = new MusicListDialog(MusicPlayerActivity.this);
                dialog.show();
            }
        });
        mInfoView = findViewById(R.id.album_view);
        mInfoView.setText(mAudioBean.albumInfo);
        mInfoView.requestFocus(); // 跑马灯效果焦点获取
        mAuthorView = findViewById(R.id.author_view);
        mAuthorView.setText(mAudioBean.author);

        mFavouriteView = findViewById(R.id.favourite_view);
        mFavouriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioController.getInstance().changeFavourite();
            }
        });

        changeFavouriteStatus(false);
        mStartTimeView = findViewById(R.id.start_time_view);
        mTotalTimeView = findViewById(R.id.total_time_view);
        mProgressView = findViewById(R.id.progress_view);
        mProgressView.setProgress(0);
        mProgressView.setEnabled(false);

        mPlayModeView = findViewById(R.id.play_mode_view);
        mPlayModeView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // 切换播放模式
                switch (mPlayMode) {
                    case LOOP:
                        AudioController.getInstance().setPlayMode(AudioController.PlayMode.RANDOM);
                        break;
                    case RANDOM:
                        AudioController.getInstance().setPlayMode(AudioController.PlayMode.REPEAT);
                        break;
                    case REPEAT:
                        AudioController.getInstance().setPlayMode(AudioController.PlayMode.LOOP);
                        break;
                }
            }
        });
        updatePlayModeView();
        mPreviousView = findViewById(R.id.previous_view);
        mPreviousView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 上一首
                AudioController.getInstance().previous();
            }
        });
        mPlayView =findViewById(R.id.play_view);
        mPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 暂停或播放
                AudioController.getInstance().playOrPause();
            }
        });
        mNextView = findViewById(R.id.next_view);
        mNextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 下一首
                AudioController.getInstance().next();
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event){
        mAudioBean = event.mAudioBean;
//        ImageLoaderManager.getInstance().displayImageForViewGroup(mBgView, mAudioBean.albumPic,100);
        mInfoView.setText(mAudioBean.albumInfo);
        mAuthorView.setText(mAudioBean.author);
        changeFavouriteStatus(false);
        mProgressView.setProgress(0);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event){
        showPlayView();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        showPauseView();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioProgressEvent(AudioProgressEvent event){
        int totalTime = event.maxLength;
        int currentTime = event.progress;
        // 更新时间
        mStartTimeView.setText(Utils.formatTime(currentTime));
        mTotalTimeView.setText(Utils.formatTime(totalTime));
        mProgressView.setProgress(currentTime);
        mProgressView.setMax(totalTime);
        if(event.mStatus == CustomMediaPlayer.Status.PAUSED) {
            showPauseView();
        } else {
            showPlayView();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioFavouriteEvent(AudioFavoriteEvent event){
        // 更新收藏状态
        changeFavouriteStatus(true);
    }

    private void updatePlayModeView(){
        switch (mPlayMode){
            case LOOP:
                mPlayModeView.setImageResource(R.mipmap.player_loop);
                break;
            case RANDOM:
                mPlayModeView.setImageResource(R.mipmap.player_random);
                break;
            case REPEAT:
                mPlayModeView.setImageResource(R.mipmap.player_once);
                break;
        }
    }
    private void changeFavouriteStatus(boolean anim) {
        if(RoomHelper.selectFavourite(mAudioBean) != null){
            mFavouriteView.setImageResource(R.mipmap.audio_aeh);
        } else {
            mFavouriteView.setImageResource(R.mipmap.audio_aef);
        }
        if(anim){
            // 完成收藏效果动画
            if(animator != null) animator.end();
            PropertyValuesHolder animX = PropertyValuesHolder.ofFloat(View.SCALE_X.getName(),1.0f, 1.2f, 1.0f);
            PropertyValuesHolder animY = PropertyValuesHolder.ofFloat(View.SCALE_Y.getName(), 1.0f, 1.2f, 1.0f);
            animator = ObjectAnimator.ofPropertyValuesHolder(mFavouriteView, animX, animY);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(300);
            animator.start();
        }
    }
    private void showPlayView(){
        mPlayView.setImageResource(R.mipmap.audio_aj6);
    }
    private void showPauseView(){
        mPlayView.setImageResource(R.mipmap.audio_aj7);
    }
    /**
     * 分享给好友
     * @param url
     * @param name
     */
    private void shareMusic(String url, String name){

    }
}
