package com.example.lib_audio.mediaplayer.db;

import android.content.Context;

import androidx.room.Room;

import com.example.lib_audio.app.AudioHelper;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_audio.mediaplayer.model.Favourite;
import com.example.lib_audio.mediaplayer.model.room.AudioBeanDao;
import com.example.lib_audio.mediaplayer.model.room.FavouriteDao;

import java.util.List;

public class RoomHelper {
    // 数据库的名字
    private static final String DB_NAME = "music_db";
    private static AppDatabase mAppDatabase;
    public static void initDatabase() {
        Context context = AudioHelper.getContext();
        mAppDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                // 允许在主线程访问数据库
                .allowMainThreadQueries()
                // 将旧的删除，创建新的
                .fallbackToDestructiveMigration()
                .build();
    }

    public static void selectAudioBeans(List<AudioBean> audioBeans) {
        AudioBeanDao dao = mAppDatabase.audioBeanDao();
        for(AudioBean audioBean : audioBeans) {
            if(null == dao.findByAudioId(audioBean.id)){
                dao.insert(audioBean);
            }
        }
    }
    public static void addFavourite(AudioBean audioBean) {
        FavouriteDao dao = mAppDatabase.favouriteDao();
        Favourite favourite = new Favourite();
        favourite.setAudioId(audioBean.id);
//        favourite.setAudioBean(audioBean);
        dao.insert(favourite);
    }
    public static void removeFavourite(AudioBean audioBean) {
        FavouriteDao dao = mAppDatabase.favouriteDao();
        dao.deleteByAudioId(audioBean.id);
    }
    public static Favourite selectFavourite(AudioBean audioBean) {
        FavouriteDao dao = mAppDatabase.favouriteDao();
        return dao.findByAudioId(audioBean.id);
    }
}
