package net.iGap.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnMapUsersGet;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.realm.RealmGeoNearbyDistance;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestGeoGetComment;
import net.iGap.request.RequestGeoGetNearbyDistance;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

import static net.iGap.G.context;
import static net.iGap.G.inflater;
import static net.iGap.fragments.FragmentiGapMap.btnBack;
import static net.iGap.fragments.FragmentiGapMap.isBackPress;
import static net.iGap.fragments.FragmentiGapMap.pageUserList;

public class FragmentMapUsers extends BaseFragment implements ActivityMain.OnBackPressedListener, OnMapUsersGet {

    private final int DEFAULT_LOOP_TIME = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    private RecyclerView mRecyclerView;
    //private MapUserAdapterA mAdapter;
    private MapUserAdapter mAdapter;
    private HashMap<Long, CircleImageView> hashMapAvatar = new HashMap<>();
    private ImageView imvNothingFound;
    private TextView txtEmptyListComment;
    private RippleView rippleBack;
    private Realm realmMapUsers;

    public FragmentMapUsers() {
        // Required empty public constructor
    }

    public static FragmentMapUsers newInstance() {
        return new FragmentMapUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realmMapUsers = Realm.getDefaultInstance();
        return inflater.inflate(R.layout.fragment_map_users, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //G.onMapUsersGet = this;
        initComponent(view);
        if (FragmentiGapMap.location != null) {
            getDistanceLoop(0, false);
        }
    }

    private Realm getRealmMapUsers() {
        if (realmMapUsers == null || realmMapUsers.isClosed()) {
            realmMapUsers = Realm.getDefaultInstance();
        }
        return realmMapUsers;
    }

    private void initComponent(View view) {

        imvNothingFound = (ImageView) view.findViewById(R.id.sfl_imv_nothing_found);
        txtEmptyListComment = (TextView) view.findViewById(R.id.sfl_txt_empty_list_comment);
        rippleBack = (RippleView) view.findViewById(R.id.rippleBackMapUser);
        view.findViewById(R.id.toolbarMapUsers).setBackgroundColor(Color.parseColor(G.appBarColor));
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rcy_map_user);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(G.fragmentActivity));
        getRealmMapUsers().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(RealmGeoNearbyDistance.class).findAll().deleteAllFromRealm();
            }
        });

        mAdapter = new MapUserAdapter(getRealmMapUsers().where(RealmGeoNearbyDistance.class).findAll(), true);

        //fastAdapter
        //mAdapter = new MapUserAdapterA();
        mRecyclerView.setAdapter(mAdapter);
        ((ActivityMain) G.fragmentActivity).setOnBackPressedListener(FragmentMapUsers.this, false);
        rippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityMain.onBackPressedListener != null) {
                    ActivityMain.onBackPressedListener.doBack();
                }
                popBackStackFragment();
            }
        });

        if (mAdapter.getItemCount() > 0) {
            imvNothingFound.setVisibility(View.GONE);
            txtEmptyListComment.setVisibility(View.GONE);
        } else {
            imvNothingFound.setVisibility(View.VISIBLE);
            txtEmptyListComment.setVisibility(View.VISIBLE);
        }

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                if (mAdapter.getItemCount() > 0) {
                    imvNothingFound.setVisibility(View.GONE);
                    txtEmptyListComment.setVisibility(View.GONE);
                } else {
                    imvNothingFound.setVisibility(View.VISIBLE);
                    txtEmptyListComment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if (mAdapter.getItemCount() > 0) {
                    imvNothingFound.setVisibility(View.GONE);
                    txtEmptyListComment.setVisibility(View.GONE);
                } else {
                    imvNothingFound.setVisibility(View.VISIBLE);
                    txtEmptyListComment.setVisibility(View.VISIBLE);
                }
            }

        });

    }

    private void getDistanceLoop(final int delay, boolean loop) {
        if (loop && FragmentiGapMap.page == pageUserList) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new RequestGeoGetNearbyDistance().getNearbyDistance(FragmentiGapMap.location.getLatitude(), FragmentiGapMap.location.getLongitude());
                    getDistanceLoop(DEFAULT_LOOP_TIME, true);
                }
            }, delay);
        } else {
            new RequestGeoGetNearbyDistance().getNearbyDistance(FragmentiGapMap.location.getLatitude(), FragmentiGapMap.location.getLongitude());
        }
    }

    @Override
    public void doBack() {
        isBackPress = true;
        if (btnBack != null) btnBack.performClick();
    }

    @Override
    public void onMapUsersGet(final long userId) {
        //if (mAdapter != null) {
        //    G.handler.post(new Runnable() {
        //        @Override
        //        public void run() {
        //            RealmGeoNearbyDistance realmGeoNearbyDistance = getRealmMapUsers().where(RealmGeoNearbyDistance.class).equalTo(RealmGeoNearbyDistanceFields.USER_ID, userId).findFirst();
        //            if (realmGeoNearbyDistance != null) {
        //                mAdapter.add(new MapUserItem().setInfo(realmGeoNearbyDistance).withIdentifier(realmGeoNearbyDistance.getUserId()));
        //            }
        //        }
        //    });
        //}
    }

    /**
     * ***********************************************************************************
     * *********************************** FastAdapter ***********************************
     * ***********************************************************************************
     */

    //public class MapUserAdapterA<Item extends MapUserItem> extends FastItemAdapter<Item> {
    //    @Override
    //    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //        return super.onCreateViewHolder(parent, viewType);
    //    }
    //}
    //
    //class MapUserItem extends AbstractItem<MapUserItem, MapUserItem.ViewHolder> {
    //    RealmGeoNearbyDistance geoNearbyDistance;
    //
    //    public MapUserItem setInfo(RealmGeoNearbyDistance geoNearbyDistance) {
    //        this.geoNearbyDistance = geoNearbyDistance;
    //        return this;
    //    }
    //
    //    @Override
    //    public void bindView(final ViewHolder holder, List payloads) throws IllegalStateException {
    //        super.bindView(holder, payloads);
    //
    //        final RealmGeoNearbyDistance item = geoNearbyDistance;
    //        if (item == null) {
    //            return;
    //        }
    //        RealmRegisteredInfo registeredInfo = getRealmMapUsers().where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, item.getUserId()).findFirst();
    //        if (registeredInfo == null) {
    //            return;
    //        }
    //
    //        if (G.selectedLanguage.equals("en")) {
    //            holder.arrow.setText(G.fragmentActivity.getResources().getString(R.string.md_right_arrow));
    //        } else {
    //            holder.arrow.setText(G.fragmentActivity.getResources().getString(R.string.md_back_arrow));
    //        }
    //
    //        holder.arrow.setTextColor(Color.parseColor(G.appBarColor));
    //
    //        holder.layoutMap.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View view) {
    //                new HelperFragment(FragmentContactsProfile.newInstance(0, item.getUserId(), "Others")).setReplace(false).load();
    //            }
    //        });
    //
    //        holder.username.setText(registeredInfo.getDisplayName());
    //        if (item.isHasComment()) {
    //            if (item.getComment() == null || item.getComment().isEmpty()) {
    //                holder.comment.setText(context.getResources().getString(R.string.comment_waiting));
    //                new RequestGeoGetComment().getComment(item.getUserId());
    //            } else {
    //                holder.comment.setText(item.getComment());
    //            }
    //        } else {
    //            holder.comment.setText(context.getResources().getString(R.string.comment_no));
    //        }
    //
    //        holder.distance.setText(String.format(G.context.getString(R.string.distance), item.getDistance()));
    //        if (HelperCalander.isPersianUnicode) {
    //            holder.distance.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.distance.getText().toString()));
    //        }
    //
    //        hashMapAvatar.put(item.getUserId(), holder.avatar);
    //        HelperAvatar.getAvatar(item.getUserId(), HelperAvatar.AvatarType.USER, false, new OnAvatarGet() {
    //            @Override
    //            public void onAvatarGet(final String avatarPath, final long ownerId) {
    //                G.handler.post(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(ownerId));
    //                    }
    //                });
    //            }
    //
    //            @Override
    //            public void onShowInitials(final String initials, final String color) {
    //                G.handler.post(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        holder.avatar.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.avatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
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
    //        return R.layout.map_user_item;
    //    }
    //
    //    @Override
    //    public ViewHolder getViewHolder(View viewGroup) {
    //        return new ViewHolder(viewGroup);
    //    }
    //
    //    class ViewHolder extends RecyclerView.ViewHolder {
    //
    //        public LinearLayout layoutMap;
    //        public CircleImageView avatar;
    //        public TextView username;
    //        public TextView comment;
    //        public MaterialDesignTextView arrow;
    //        public CustomTextViewMedium distance;
    //
    //        public ViewHolder(View itemView) {
    //            super(itemView);
    //
    //            layoutMap = (LinearLayout) itemView.findViewById(R.id.lyt_map_user);
    //            avatar = (CircleImageView) itemView.findViewById(R.id.img_user_avatar_map);
    //            username = (TextView) itemView.findViewById(R.id.txt_user_name_map);
    //            comment = (TextView) itemView.findViewById(R.id.txt_user_comment_map);
    //            arrow = (MaterialDesignTextView) itemView.findViewById(R.id.txt_arrow_list_map);
    //            distance = (CustomTextViewMedium) itemView.findViewById(R.id.txt_user_distance_map);
    //
    //        }
    //    }
    //}
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realmMapUsers != null && !realmMapUsers.isClosed()) {
            realmMapUsers.close();
        }
        ((ActivityMain) G.fragmentActivity).setOnBackPressedListener(FragmentMapUsers.this, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentiGapMap.page = FragmentiGapMap.pageUserList;
        if (FragmentiGapMap.rippleMoreMap != null) {
            FragmentiGapMap.rippleMoreMap.setVisibility(View.GONE);
        }
        if (FragmentiGapMap.fabGps != null) {
            FragmentiGapMap.fabGps.setVisibility(View.GONE);
        }
    }

    /**
     * **********************************************************************************
     * ********************************** RealmAdapter **********************************
     * **********************************************************************************
     */

    private class MapUserAdapter extends RealmRecyclerViewAdapter<RealmGeoNearbyDistance, MapUserAdapter.ViewHolder> {
        MapUserAdapter(RealmResults<RealmGeoNearbyDistance> data, boolean autoUpdate) {
            super(data, autoUpdate);
        }

        @Override
        public MapUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MapUserAdapter.ViewHolder(inflater.inflate(R.layout.map_user_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final MapUserAdapter.ViewHolder holder, int i) {
            final RealmGeoNearbyDistance item = getItem(i);
            if (item == null) {
                return;
            }
            Realm realm = Realm.getDefaultInstance();
            RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, item.getUserId());
            if (registeredInfo == null) {
                realm.close();
                return;
            }

            if (G.selectedLanguage.equals("en")) {
                holder.arrow.setText(G.fragmentActivity.getResources().getString(R.string.md_right_arrow));
            } else {
                holder.arrow.setText(G.fragmentActivity.getResources().getString(R.string.md_back_arrow));
            }

            holder.arrow.setTextColor(Color.parseColor(G.appBarColor));

            holder.layoutMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //for close FragmentMapUsers
                    //if (btnBack != null){
                    //    btnBack.performClick();
                    //}
                    new HelperFragment(FragmentContactsProfile.newInstance(0, item.getUserId(), "Others")).setReplace(false).load();
                    //for close FragmentiGapMap
                    //if (G.onMapClose != null) {
                    //    G.onMapClose.onClose();
                    //}
                }
            });

            if (HelperCalander.isPersianUnicode) {
                holder.username.setGravity(Gravity.RIGHT);
            } else {
                holder.username.setGravity(Gravity.LEFT);
            }

            holder.username.setText(registeredInfo.getDisplayName());
            if (item.isHasComment()) {
                if (item.getComment() == null || item.getComment().isEmpty()) {
                    holder.comment.setText(context.getResources().getString(R.string.comment_waiting));
                    new RequestGeoGetComment().getComment(item.getUserId());
                } else {
                    holder.comment.setText(item.getComment());
                }
            } else {
                holder.comment.setText(context.getResources().getString(R.string.comment_no));
            }

            holder.distance.setText(String.format(G.context.getString(R.string.distance), item.getDistance()));
            if (HelperCalander.isPersianUnicode) {
                holder.distance.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.distance.getText().toString()));
            }

            hashMapAvatar.put(item.getUserId(), holder.avatar);
            HelperAvatar.getAvatar(item.getUserId(), HelperAvatar.AvatarType.USER, false, new OnAvatarGet() {
                @Override
                public void onAvatarGet(final String avatarPath, final long ownerId) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(ownerId));
                        }
                    });
                }

                @Override
                public void onShowInitials(final String initials, final String color) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.avatar.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.avatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                        }
                    });
                }
            });

            realm.close();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public LinearLayout layoutMap;
            public CircleImageView avatar;
            public TextView username;
            public TextView comment;
            public MaterialDesignTextView arrow;
            public CustomTextViewMedium distance;

            public ViewHolder(View itemView) {
                super(itemView);

                layoutMap = (LinearLayout) itemView.findViewById(R.id.lyt_map_user);
                avatar = (CircleImageView) itemView.findViewById(R.id.img_user_avatar_map);
                username = (TextView) itemView.findViewById(R.id.txt_user_name_map);
                comment = (TextView) itemView.findViewById(R.id.txt_user_comment_map);
                arrow = (MaterialDesignTextView) itemView.findViewById(R.id.txt_arrow_list_map);
                distance = (CustomTextViewMedium) itemView.findViewById(R.id.txt_user_distance_map);

            }
        }
    }
}
