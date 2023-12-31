package com.example.imooc_voice.view.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.imooc_voice.R;
import com.example.imooc_voice.view.common.widget.ViewPager2Helper;
import com.example.imooc_voice.view.home.adpater.HomePagerAdapter;
import com.example.imooc_voice.view.home.model.CHANNEL;
import com.example.imooc_voice.view.login.LoginActivity;
import com.example.imooc_voice.view.login.manager.UserManager;
import com.example.imooc_voice.view.login.user.LoginEvent;
import com.example.lib_audio.app.AudioHelper;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_common_ui.base.BaseActivity;
import com.example.lib_image_loader.app.ImageLoaderManager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private static final CHANNEL[] CHANNELS = new CHANNEL[]{CHANNEL.MY, CHANNEL.DISCORY,CHANNEL.FRIEND};

    /**
     * View
     */
    private DrawerLayout mDrawerLayout;
    private View mToggleView;
    private View mSearchView;
    private ViewPager2 mViewPager;

    // ViewPage 初始化
    private HomePagerAdapter mAdapter;

    private View unLoginLayout;
    private ImageView mPhotoView;

    /**
     * data
     */
    private ArrayList<AudioBean> mLists = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册eventbus
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_home);
        initView();
        initData();
    }
    private void initData() {
        mLists.add(new AudioBean("100001", "https://storage.googleapis.com/automotive-media/Keys_To_The_Kingdom.mp3",
                "以你的名字喊我", "周杰伦", "七里香", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                "https://gimg3.baidu.com/search/src=http%3A%2F%2Fpics4.baidu.com%2Ffeed%2F6609c93d70cf3bc7b2637bcef64e53adcd112a0f.jpeg%40f_auto%3Ftoken%3D90252c7f8fb58194a1e8f80123a2f572&refer=http%3A%2F%2Fwww.baidu.com&app=2021&size=f360,240&n=0&g=0n&q=75&fmt=auto?sec=1696957200&t=2bb4f19440d8df697af286ee17d30df0",
                "4:30"));
        mLists.add(
                new AudioBean("100002", "https://oss.nbs.cn/M00/22/E4/wKhkDmPZ1uSAJWFwAlYRLUW4gK0892.mp3", "勇气",
                        "梁静茹", "勇气", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://img0.baidu.com/it/u=3874461902,3428516308&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500",
                        "4:40"));
        mLists.add(
                new AudioBean("100003", "https://m10.music.126.net/20231031145729/1c65826d274d730292d89e844aa92d6d/ymusic/3b60/d457/d751/f335941028faecfb3d11a3afa9d14783.mp3", "灿烂如你",
                        "汪峰", "春天里", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://img2.baidu.com/it/u=872836838,2141302734&fm=253&fmt=auto&app=138&f=JPEG?w=281&h=499",
                        "3:20"));
        AudioHelper.startMusicService(mLists);
    }
    private void initView() {
        // 找出id
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggleView = findViewById(R.id.toggle_view);
        mToggleView.setOnClickListener(this);
        mSearchView=findViewById(R.id.search_view);
        mViewPager=findViewById(R.id.view_pager);
        mAdapter = new HomePagerAdapter(this, CHANNELS);
        mViewPager.setAdapter(mAdapter);
        initMagicIndicator();
        // 登录相关UI
        unLoginLayout = findViewById(R.id.unloggin_layout);
        unLoginLayout.setOnClickListener(this);
        mPhotoView = findViewById(R.id.avatr_view);
    }
    // 初始化指示器
    private void initMagicIndicator(){
        MagicIndicator magicIndicator = findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        // 初始化
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return CHANNELS == null ? 0 : CHANNELS.length;
            }
            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(CHANNELS[index].getKey());
                simplePagerTitleView.setTextSize(19);
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                simplePagerTitleView.setNormalColor(Color.parseColor("#999999"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#333333"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });
        // 绑定
        magicIndicator.setNavigator(commonNavigator);
        ViewPager2Helper.bind(magicIndicator, mViewPager);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        // 侧边栏显示隐藏
        if (v.getId() == R.id.toggle_view) {
            if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        }
        // 登录方法
        if (v.getId() == R.id.unloggin_layout) {
            if (!UserManager.getInstance().hasLogin()) {
                LoginActivity.start(this);
            } else {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        }
    }
    // 方法执行在主线程中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        // 将为注册的隐藏掉
        unLoginLayout.setVisibility(View.GONE);
        mPhotoView.setVisibility(View.VISIBLE);
        // 加载头像
        // ImageLoaderManager 图片加载组件
        ImageLoaderManager.getInstance().displayImageForCircle(mPhotoView,UserManager.getInstance().getUser().data.photoUrl);
    }
}