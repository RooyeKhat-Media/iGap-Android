/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.ActivityMediaPlayerBinding;
import net.iGap.databinding.ActivityMediaPlayerLandBinding;
import net.iGap.interfaces.OnComplete;
import net.iGap.module.MusicPlayer;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.viewmodel.FragmentMediaPlayerViewModel;

import java.util.List;

public class FragmentMediaPlayer extends BaseFragment {


    public static OnComplete onComplete;
    public static OnSetImage onSetImage;
    private SeekBar musicSeekbar;
    private RecyclerView rcvListMusicPlayer;
    public static FastItemAdapter fastItemAdapter;

    public static OnBackFragment onBackFragment;
    private FragmentMediaPlayerViewModel fragmentMediaPlayerViewModel;
    private ActivityMediaPlayerBinding fragmentMediaPlayerBinding;
    private ActivityMediaPlayerLandBinding activityMediaPlayerLandBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isNeedResume = true;

        if (G.twoPaneMode) {
            fragmentMediaPlayerBinding = DataBindingUtil.inflate(inflater, R.layout.activity_media_player, container, false);
            return fragmentMediaPlayerBinding.getRoot();
        } else {
            if (G.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activityMediaPlayerLandBinding = DataBindingUtil.inflate(inflater, R.layout.activity_media_player_land, container, false);
                return activityMediaPlayerLandBinding.getRoot();
            } else {
                fragmentMediaPlayerBinding = DataBindingUtil.inflate(inflater, R.layout.activity_media_player, container, false);
                return fragmentMediaPlayerBinding.getRoot();
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();

        MusicPlayer.isShowMediaPlayer = true;

        if (MusicPlayer.mp == null) {
            removeFromBaseFragment();
            return;
        }



        initComponent(view);
        MusicPlayer.onComplete = onComplete;
    }

    private void initDataBinding() {
        if (G.twoPaneMode) {

            fragmentMediaPlayerViewModel = new FragmentMediaPlayerViewModel(fragmentMediaPlayerBinding.getRoot());
            fragmentMediaPlayerBinding.setFragmentMediaPlayerViewModel(fragmentMediaPlayerViewModel);

        } else {
            if (G.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                fragmentMediaPlayerViewModel = new FragmentMediaPlayerViewModel(activityMediaPlayerLandBinding.getRoot());
                activityMediaPlayerLandBinding.setFragmentMediaPlayerViewModel(fragmentMediaPlayerViewModel);

            } else {
                fragmentMediaPlayerViewModel = new FragmentMediaPlayerViewModel(fragmentMediaPlayerBinding.getRoot());
                fragmentMediaPlayerBinding.setFragmentMediaPlayerViewModel(fragmentMediaPlayerViewModel);
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        MusicPlayer.isShowMediaPlayer = false;
        MusicPlayer.onComplete = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        fragmentMediaPlayerViewModel.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
        fragmentMediaPlayerViewModel.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        try {
            if (!G.twoPaneMode) {
                if (isAdded()) {
                    G.fragmentManager.beginTransaction().detach(this).attach(this).commit();
                }
            }
        } catch (Exception e) {
            Log.e("ddddd", "FragmentMediaPlayer  onConfigurationChanged  " + e.toString());
        }



        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MusicPlayer.onComplete = null;
    }

    //*****************************************************************************************


    private void initComponent(final View view) {

        final ImageView img_MusicImage = (ImageView) view.findViewById(R.id.ml_img_music_picture);
        onSetImage = new OnSetImage() {
            @Override
            public void setImage() {
                img_MusicImage.setImageBitmap(MusicPlayer.mediaThumpnail);
            }
        };

        musicSeekbar = view.findViewById(R.id.ml_seekBar1);
        musicSeekbar.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    MusicPlayer.setMusicProgress(musicSeekbar.getProgress());
                }
                return false;
            }
        });



        onBackFragment = new OnBackFragment() {
            @Override
            public void onBack() {
                removeFromBaseFragment();
            }
        };

        rcvListMusicPlayer = (RecyclerView) view.findViewById(R.id.rcvListMusicPlayer);
        final SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        fastItemAdapter = new FastItemAdapter();

        for (RealmRoomMessage r : MusicPlayer.mediaList) {
            fastItemAdapter.add(new AdapterListMusicPlayer().setItem(r).withIdentifier(r.getMessageId()));
        }
        rcvListMusicPlayer.setAdapter(fastItemAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(_mActivity);
        rcvListMusicPlayer.setLayoutManager(linearLayoutManager);
        rcvListMusicPlayer.setHasFixedSize(true);

        /*linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rcvListMusicPlayer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (rcvListMusicPlayer.computeVerticalScrollOffset() > 0) {
                    slidingUpPanelLayout.setEnabled(false);
                } else {
                    slidingUpPanelLayout.setEnabled(true);
                }
            }
        });*/

        fastItemAdapter.withSelectable(true);
        fastItemAdapter.withOnClickListener(new OnClickListener() {
            @Override
            public boolean onClick(View v, IAdapter adapter, IItem item, int position) {

                if (MusicPlayer.musicName.equals(MusicPlayer.mediaList.get(position).getAttachment().getName())) {
                    MusicPlayer.playAndPause();
                } else {
                    MusicPlayer.startPlayer(MusicPlayer.mediaList.get(position).getAttachment().getName(), MusicPlayer.mediaList.get(position).getAttachment().getLocalFilePath(), FragmentChat.titleStatic, FragmentChat.mRoomIdStatic, false, MusicPlayer.mediaList.get(position).getMessageId() + "");
                }

                return false;
            }
        });

        rcvListMusicPlayer.scrollToPosition(fastItemAdapter.getPosition(Long.parseLong(MusicPlayer.messageId)));
    }

    public class AdapterListMusicPlayer extends AbstractItem<AdapterListMusicPlayer, AdapterListMusicPlayer.ViewHolder> {

        private RealmRoomMessage realmRoomMessagesList;

        public AdapterListMusicPlayer() {

        }

        public AdapterListMusicPlayer setItem(RealmRoomMessage realmRoomMessages) {
            realmRoomMessagesList = realmRoomMessages;
            return this;
        }

        //The unique ID for this type of item
        @Override
        public int getType() {
            return R.id.rootListMusicPlayer;
        }

        //The layout to be used for this type of item
        @Override
        public int getLayoutRes() {
            return R.layout.adapter_list_music_player;
        }

        //The logic to bind your data to the view

        @Override
        public void bindView(ViewHolder holder, List payloads) {
            super.bindView(holder, payloads);

            if (MusicPlayer.mp != null && MusicPlayer.mp.isPlaying() && Long.parseLong(MusicPlayer.messageId) == (realmRoomMessagesList.getMessageId())) {
                holder.iconPlay.setText(R.string.md_round_pause_button);
            } else {
                holder.iconPlay.setText(R.string.md_play_rounded_button);
            }
            holder.txtNameMusic.setText(realmRoomMessagesList.getAttachment().getName());
            MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();

            String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if (artist != null) {
                holder.txtMusicplace.setText(artist);
            } else {
                holder.txtMusicplace.setText(G.context.getString(R.string.unknown_artist));
            }

        }

        //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
        protected class ViewHolder extends RecyclerView.ViewHolder {

            private TextView txtNameMusic, txtMusicplace, iconPlay;

            public ViewHolder(View view) {
                super(view);
                txtNameMusic = itemView.findViewById(R.id.txtListMusicPlayer);
                txtMusicplace = itemView.findViewById(R.id.ml_txt_music_place);
                iconPlay = itemView.findViewById(R.id.ml_btn_play_music);

                iconPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MusicPlayer.musicName.equals(MusicPlayer.mediaList.get(getAdapterPosition()).getAttachment().getName())) {
                            MusicPlayer.playAndPause();
                        } else {
                            MusicPlayer.startPlayer(MusicPlayer.mediaList.get(getAdapterPosition()).getAttachment().getName(), MusicPlayer.mediaList.get(getAdapterPosition()).getAttachment().getLocalFilePath(), FragmentChat.titleStatic, FragmentChat.mRoomIdStatic, false, MusicPlayer.mediaList.get(getAdapterPosition()).getMessageId() + "");
                        }

                    }
                });
            }
        }

        @Override
        public ViewHolder getViewHolder(View v) {
            return new ViewHolder(v);
        }

    }




    public interface OnBackFragment {
        void onBack();
    }

    public interface OnSetImage {
        void setImage();
    }

}
