package com.example.imooc_voice.view.friend.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.example.imooc_voice.R;
import com.example.imooc_voice.view.friend.model.FriendBodyValue;
import com.example.imooc_voice.view.login.LoginActivity;
import com.example.imooc_voice.view.login.manager.UserManager;
import com.example.lib_audio.app.AudioHelper;
import com.example.lib_common_ui.MultiImageViewLayout;
import com.example.lib_common_ui.recyclerview.MultiItemTypeAdapter;
import com.example.lib_common_ui.recyclerview.base.ItemViewDelegate;
import com.example.lib_common_ui.recyclerview.base.ViewHolder;
import com.example.lib_image_loader.app.ImageLoaderManager;

import java.util.List;

public class FriendRecyclerAdapter extends MultiItemTypeAdapter {
    public static final int MUSIC_TYPE = 0x01; // 音乐类型
    public static final int VIDEO_TYPE = 0x02; // 视频类型
    private Context mContext;
    public FriendRecyclerAdapter(Context context, List<FriendBodyValue> datas) {
        super(context, datas);
        mContext = context;
        addItemViewDelegate(MUSIC_TYPE, new MusicItemDelegate());
    }
    private class MusicItemDelegate implements ItemViewDelegate<FriendBodyValue> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_friend_list_picture_layout;
        }

        @Override
        public boolean isForViewType(FriendBodyValue item, int position) {
            return item.type == FriendRecyclerAdapter.MUSIC_TYPE;
        }

        @Override
        public void convert(ViewHolder holder,final FriendBodyValue recommendBodyValue, int position) {
            // 为view holder绑定数据
            holder.setText(R.id.name_view, recommendBodyValue.name + " 分享单曲: ");
            holder.setText(R.id.fansi_view, recommendBodyValue.fans +"粉丝");
            holder.setText(R.id.text_view, recommendBodyValue.text);
            holder.setText(R.id.zan_view, recommendBodyValue.zan);
            holder.setText(R.id.message_view, recommendBodyValue.msg);
            holder.setText(R.id.audio_name_view, recommendBodyValue.audioBean.name);
            holder.setText(R.id.audio_author_view, recommendBodyValue.audioBean.album);
            holder.setOnClickListener(R.id.album_layout, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AudioHelper.addAudio((Activity) mContext, recommendBodyValue.audioBean);
                }
            });
            holder.setOnClickListener(R.id.guanzhu_view, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!UserManager.getInstance().hasLogin()) {
                        LoginActivity.start(mContext);
                    }
                }
            });
            ImageView avatar = holder.getView(R.id.photo_view);
            ImageLoaderManager.getInstance().displayImageForCircle(avatar, recommendBodyValue.avatr);
            ImageView albumPicView = holder.getView(R.id.album_view);
            ImageLoaderManager.getInstance().displayImageForView(albumPicView, recommendBodyValue.audioBean.albumPic);

            // 多图布局
            MultiImageViewLayout imageViewLayout = holder.getView(R.id.image_layout);
            imageViewLayout.setList(recommendBodyValue.pics);
        }
    }
}