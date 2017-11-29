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

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.swipe.SwipeLayout;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnBlockStateChanged;
import net.iGap.interfaces.OnSelectedList;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.Contacts;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.request.RequestUserContactsBlock;
import net.iGap.request.RequestUserContactsUnblock;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

import static net.iGap.G.inflater;

public class FragmentBlockedUser extends BaseFragment implements OnBlockStateChanged {

    //private BlockListAdapter mAdapter;
    private Realm realmBlockedUser;
    private StickyRecyclerHeadersDecoration decoration;
    private Realm getRealmBlockedUser() {
        if (realmBlockedUser == null || realmBlockedUser.isClosed()) {
            realmBlockedUser = Realm.getDefaultInstance();
        }
        return realmBlockedUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realmBlockedUser = Realm.getDefaultInstance();
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_blocked_user, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //G.onBlockStateChanged = this;

        view.findViewById(R.id.fbu_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        RippleView rippleBack = (RippleView) view.findViewById(R.id.fbu_ripple_back_Button);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                popBackStackFragment();
            }
        });

        RippleView rippleAdd = (RippleView) view.findViewById(R.id.fbu_ripple_add);
        rippleAdd.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                List<StructContactInfo> userList = Contacts.retrieve(null);

                Fragment fragment = ShowCustomList.newInstance(userList, new OnSelectedList() {
                    @Override
                    public void getSelectedList(boolean result, String message, int countForShowLastMessage, final ArrayList<StructContactInfo> list) {

                        for (int i = 0; i < list.size(); i++) {

                            new RequestUserContactsBlock().userContactsBlock(list.get(i).peerId);
                        }
                    }
                });

                Bundle bundle = new Bundle();
                // if you want to have  single select in select list
                fragment.setArguments(bundle);
                new HelperFragment(fragment).setReplace(false).load();
            }
        });

        //+ manually update
        //mAdapter = new BlockListAdapter(); // (+ avoid use from realm-adapter) BlockListAdapter blockListAdapter = new BlockListAdapter(results);

        RecyclerView realmRecyclerView = (RecyclerView) view.findViewById(R.id.fbu_realm_recycler_view);
        realmRecyclerView.setItemViewCacheSize(100);
        realmRecyclerView.setItemAnimator(null);
        realmRecyclerView.setLayoutManager(new LinearLayoutManager(G.fragmentActivity));

        //+ manually update
        //RealmResults<RealmRegisteredInfo> results = getRealmBlockedUser().where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.BLOCK_USER, true).findAll();
        //for (RealmRegisteredInfo registeredInfo : results) {
        //    mAdapter.add(new BlockedUser().setInfo(registeredInfo).withIdentifier(registeredInfo.getId()));
        //}
        //realmRecyclerView.setAdapter(mAdapter);// (+ avoid use from realm-adapter) realmRecyclerView.setAdapter(blockListAdapter);
        //mAdapter.withOnLongClickListener(new FastAdapter.OnLongClickListener() {
        //    @Override
        //    public boolean onLongClick(View v, IAdapter adapter, IItem item, int position) {
        //        openDialogToggleBlock(((BlockedUser) item).registeredInfo.getId());
        //        return true;
        //    }
        //});

        RealmResults<RealmRegisteredInfo> results = getRealmBlockedUser().where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.BLOCK_USER, true).findAll();
        BlockListAdapter blockListAdapter = new BlockListAdapter(results.sort(RealmRegisteredInfoFields.DISPLAY_NAME));
        realmRecyclerView.setAdapter(blockListAdapter);
        decoration = new StickyRecyclerHeadersDecoration(new StickyHeader(results.sort(RealmRegisteredInfoFields.DISPLAY_NAME)));
        realmRecyclerView.addItemDecoration(decoration);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realmBlockedUser != null && !realmBlockedUser.isClosed()) {
            realmBlockedUser.close();
        }
        G.onBlockStateChanged = null;
    }

    @Override
    public void onBlockStateChanged(final boolean blocked, final long userId) {
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        if (blocked) {
        //            RealmRegisteredInfo realmRegisteredInfo = getRealmBlockedUser().where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();
        //            if (realmRegisteredInfo != null) {
        //                mAdapter.add(new BlockedUser().setInfo(realmRegisteredInfo).withIdentifier(realmRegisteredInfo.getId()));
        //            }
        //        } else {
        //            mAdapter.remove(mAdapter.getPosition(userId));
        //        }
        //    }
        //});
    }


    private void openDialogToggleBlock(final long userId) {

    }


    /**
     * ***********************************************************************************
     * *********************************** FastAdapter ***********************************
     * ***********************************************************************************
     */

    //+ manually update
    //public class BlockListAdapter<Item extends BlockedUser> extends FastItemAdapter<Item> {
    //
    //    @Override
    //    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //        return super.onCreateViewHolder(parent, viewType);
    //    }
    //}

    //+ manually update
    //public class BlockedUser extends AbstractItem<BlockedUser, BlockedUser.ViewHolder> {
    //
    //    RealmRegisteredInfo registeredInfo;
    //
    //    public BlockedUser setInfo(RealmRegisteredInfo registeredInfo) {
    //        this.registeredInfo = registeredInfo;
    //        return this;
    //    }
    //
    //    @Override
    //    public void bindView(final ViewHolder viewHolder, List payloads) throws IllegalStateException {
    //        super.bindView(viewHolder, payloads);
    //
    //        if (registeredInfo == null) {
    //            return;
    //        }
    //
    //        viewHolder.title.setText(registeredInfo.getDisplayName());
    //        viewHolder.subtitle.setText(registeredInfo.getPhoneNumber());
    //        if (HelperCalander.isPersianUnicode) {
    //            viewHolder.subtitle.setText(viewHolder.subtitle.getText().toString());
    //        }
    //
    //        HelperAvatar.getAvatar(registeredInfo.getId(), HelperAvatar.AvatarType.USER, false, new OnAvatarGet() {
    //            @Override
    //            public void onAvatarGet(final String avatarPath, long ownerId) {
    //                G.handler.post(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), viewHolder.image);
    //                    }
    //                });
    //            }
    //
    //            @Override
    //            public void onShowInitials(final String initials, final String color) {
    //                G.handler.post(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        viewHolder.image.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) viewHolder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
    //                    }
    //                });
    //            }
    //        });
    //    }
    //
    //    @Override
    //    public int getType() {
    //        return 0;
    //    }
    //
    //    @Override
    //    public int getLayoutRes() {
    //        return R.layout.contact_item;
    //    }
    //
    //    @Override
    //    public ViewHolder getViewHolder(View v) {
    //        return new ViewHolder(v);
    //    }
    //
    //    public class ViewHolder extends RecyclerView.ViewHolder {
    //
    //        RealmRegisteredInfo realmRegisteredInfo;
    //        protected CircleImageView image;
    //        protected CustomTextViewMedium title;
    //        protected CustomTextViewMedium subtitle;
    //        View bottomLine;
    //
    //        public ViewHolder(View view) {
    //            super(view);
    //
    //            image = (CircleImageView) view.findViewById(R.id.imageView);
    //            title = (CustomTextViewMedium) view.findViewById(R.id.title);
    //            subtitle = (CustomTextViewMedium) view.findViewById(R.id.subtitle);
    //            bottomLine = view.findViewById(R.id.bottomLine);
    //            bottomLine.setVisibility(View.VISIBLE);
    //            view.findViewById(R.id.topLine).setVisibility(View.GONE);
    //        }
    //    }
    //}

    /**
     * **********************************************************************************
     * ********************************** RealmAdapter **********************************
     * **********************************************************************************
     */

    public class BlockListAdapter extends RealmRecyclerViewAdapter<RealmRegisteredInfo, BlockListAdapter.ViewHolder> {

        BlockListAdapter(RealmResults<RealmRegisteredInfo> realmResults) {
            super(realmResults, true);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            RealmRegisteredInfo realmRegisteredInfo;
            protected CircleImageView image;
            protected CustomTextViewMedium title;
            protected CustomTextViewMedium subtitle;
            View bottomLine;
            private SwipeLayout swipeLayout;


            public ViewHolder(View view) {
                super(view);

                image = (CircleImageView) view.findViewById(R.id.imageView);
                title = (CustomTextViewMedium) view.findViewById(R.id.title);
                subtitle = (CustomTextViewMedium) view.findViewById(R.id.subtitle);
                bottomLine = view.findViewById(R.id.bottomLine);
                bottomLine.setVisibility(View.VISIBLE);
                view.findViewById(R.id.topLine).setVisibility(View.GONE);

                //itemView.setOnLongClickListener(new View.OnLongClickListener() {
                //    @Override
                //    public boolean onLongClick(View v) {
                //        openDialogToggleBlock(realmRegisteredInfo.getId());
                //        return true;
                //    }
                //});
                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipeRevealLayout);
                swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        openDialogToggleBlock(realmRegisteredInfo.getId());
                    }
                });
            }
        }

        @Override
        public BlockListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.contact_item, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final BlockListAdapter.ViewHolder viewHolder, int i) {

            final RealmRegisteredInfo registeredInfo = viewHolder.realmRegisteredInfo = getItem(i);
            if (registeredInfo == null) {
                return;
            }

            viewHolder.title.setText(registeredInfo.getDisplayName());
            viewHolder.subtitle.setText(LastSeenTimeUtil.computeTime(registeredInfo.getId(), registeredInfo.getLastSeen(), false));
            if (HelperCalander.isPersianUnicode) {
                viewHolder.subtitle.setText(viewHolder.subtitle.getText().toString());
            }

            HelperAvatar.getAvatar(registeredInfo.getId(), HelperAvatar.AvatarType.USER, false, new OnAvatarGet() {
                @Override
                public void onAvatarGet(final String avatarPath, long ownerId) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), viewHolder.image);
                        }
                    });
                }

                @Override
                public void onShowInitials(final String initials, final String color) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            viewHolder.image.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) viewHolder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                        }
                    });
                }
            });

            viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    MaterialDialog dialog = new MaterialDialog.Builder(G.currentActivity).content(R.string.un_block_user).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new RequestUserContactsUnblock().userContactsUnblock(registeredInfo.getId());
                        }
                    }).negativeText(R.string.B_cancel).build();

                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            viewHolder.swipeLayout.close();
                        }
                    });
                    dialog.show();
                }

                @Override
                public void onStartClose(SwipeLayout layout) {


                }

                @Override
                public void onClose(SwipeLayout layout) {
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

                }
            });
        }
    }

    private class StickyHeader implements StickyRecyclerHeadersAdapter {

        RealmResults<RealmRegisteredInfo> realmResults;

        StickyHeader(RealmResults<RealmRegisteredInfo> realmResults) {
            this.realmResults = realmResults;
        }

        @Override
        public long getHeaderId(int position) {
            return realmResults.get(position).getDisplayName().toUpperCase().charAt(0);
        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_header_item, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

            CustomTextViewMedium textView = (CustomTextViewMedium) holder.itemView;
            textView.setText(realmResults.get(position).getDisplayName().toUpperCase().substring(0, 1));
        }

        @Override
        public int getItemCount() {
            return realmResults.size();
        }
    }
}
