package net.iGap.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.HashMap;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.interfaces.ISignalingGetCallLog;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnCallLogClear;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.DialogAnimation;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.PreCachingLayoutManager;
import net.iGap.module.TimeUtils;
import net.iGap.proto.ProtoSignalingGetLog;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmCallLog;
import net.iGap.realm.RealmCallLogFields;
import net.iGap.request.RequestSignalingClearLog;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestSignalingGetLog;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN) public class FragmentCall extends BaseFragment implements OnCallLogClear {

    public static final String OPEN_IN_FRAGMENT_MAIN = "OPEN_IN_FRAGMENT_MAIN";
    boolean openInMain = false;

    private int mOffset = 0;
    private int mLimit = 50;
    private RecyclerView.OnScrollListener onScrollListener;
    boolean isSendRequestForLoading = false;
    boolean isThereAnyMoreItemToLoad = true;
    private AppCompatImageView imgCallEmpty;
    private TextView empty_call;
    ProgressBar progressBar;
    private int attampOnError = 0;
    boolean canclick = false;
    int move = 0;
    public FloatingActionButton fabContactList;
    private RecyclerView mRecyclerView;
    private HashMap<Long, CircleImageView> hashMapAvatar = new HashMap<>();
    //private CallAdapterA mAdapter;

    public static FragmentCall newInstance(boolean openInFragmentMain) {

        FragmentCall fragmentCall = new FragmentCall();

        Bundle bundle = new Bundle();
        bundle.putBoolean(OPEN_IN_FRAGMENT_MAIN, openInFragmentMain);
        fragmentCall.setArguments(bundle);

        return fragmentCall;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        openInMain = getArguments().getBoolean(OPEN_IN_FRAGMENT_MAIN);
        if (openInMain) {
            return inflater.inflate(R.layout.fragment_call, container, false);
        }
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_call, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //G.onCallLogClear = this;
        //openInMain = getArguments().getBoolean(OPEN_IN_FRAGMENT_MAIN);

        view.findViewById(R.id.fc_layot_title).setBackgroundColor(Color.parseColor(G.appBarColor));  //set title bar color


        imgCallEmpty = (AppCompatImageView) view.findViewById(R.id.img_icCall);
        empty_call = (TextView) view.findViewById(R.id.textEmptyCal);
        progressBar = (ProgressBar) view.findViewById(R.id.fc_progress_bar_waiting);


        RippleView rippleBack = (RippleView) view.findViewById(R.id.fc_call_ripple_txtBack);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                G.fragmentActivity.onBackPressed();
            }
        });

        MaterialDesignTextView txtMenu = (MaterialDesignTextView) view.findViewById(R.id.fc_btn_menu);

        txtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogMenu();
            }
        });


        mRecyclerView = (RecyclerView) view.findViewById(R.id.fc_recycler_view_call);
        mRecyclerView.setItemViewCacheSize(1000);
        mRecyclerView.setItemAnimator(null);

        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(G.fragmentActivity, 6000);

        mRecyclerView.setLayoutManager(layoutManager);

        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmCallLog> results = realm.where(RealmCallLog.class).findAllSorted(RealmCallLogFields.TIME, Sort.DESCENDING);

        if (results.size() > 0) {
            imgCallEmpty.setVisibility(View.GONE);
            empty_call.setVisibility(View.GONE);

        } else {
            imgCallEmpty.setVisibility(View.VISIBLE);
            empty_call.setVisibility(View.VISIBLE);
        }

        CallAdapter callAdapter = new CallAdapter(results);
        mRecyclerView.setAdapter(callAdapter);

        //fastAdapter
        //mAdapter = new CallAdapterA();
        //for (RealmCallLog callLog : results) {
        //    mAdapter.add(new CallItem().setInfo(callLog).withIdentifier(callLog.getId()));
        //}
        //mRecyclerView.setAdapter(mAdapter);
        //mAdapter.withOnClickListener(new FastAdapter.OnClickListener() {
        //    @Override
        //    public boolean onClick(View v, IAdapter adapter, IItem item, int position) {
        //        long userId = ((CallItem) item).callLog.getLogProto().getPeer().getId();
        //        if (userId != 134 && G.userId != userId) {
        //            call(userId, false);
        //        }
        //        return true;
        //    }
        //});

        onScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isThereAnyMoreItemToLoad) {
                    if (!isSendRequestForLoading) {

                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                        if (lastVisiblePosition + 15 >= mOffset) {
                            getLogListWithOffset();
                        }
                    }
                }
            }
        };

        mRecyclerView.addOnScrollListener(onScrollListener);

        G.iSignalingGetCallLog = new ISignalingGetCallLog() {
            @Override
            public void onGetList(final int size, final List<ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog> signalingLogList) {

                if (signalingLogList != null) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Realm realm = Realm.getDefaultInstance();
                            //realm.executeTransaction(new Realm.Transaction() {
                            //    @Override
                            //    public void execute(Realm realm) {
                            //        for (ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog callLog : signalingLogList) {
                            //            RealmCallLog realmCallLog = realm.where(RealmCallLog.class).equalTo(RealmCallLogFields.ID, callLog.getId()).findFirst();
                            //            if (realmCallLog != null && mAdapter.getPosition(callLog.getId()) == -1) {
                            //                if (imgCallEmpty != null && imgCallEmpty.getVisibility() == View.VISIBLE) {
                            //                    imgCallEmpty.setVisibility(View.GONE);
                            //                    empty_call.setVisibility(View.GONE);
                            //                }
                            //                mAdapter.add(0, new CallItem().setInfo(realmCallLog).withIdentifier(callLog.getId()));
                            //            }
                            //        }
                            //    }
                            //});
                            //realm.close();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

                if (size == -1) {

                    if (attampOnError < 2) {
                        isSendRequestForLoading = false;
                        attampOnError++;
                    } else {
                        isThereAnyMoreItemToLoad = false;
                        mRecyclerView.removeOnScrollListener(onScrollListener);
                    }
                } else if (size == 0) {
                    isThereAnyMoreItemToLoad = false;
                    mRecyclerView.removeOnScrollListener(onScrollListener);
                } else {
                    isSendRequestForLoading = false;
                    mOffset += size;
                }
            }
        };

        realm.close();

        fabContactList = (FloatingActionButton) view.findViewById(R.id.fc_fab_contact_list);
        fabContactList.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));

        fabContactList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showContactListForCall();
            }
        });

        getLogListWithOffset();

        if (openInMain) {

            fabContactList.setVisibility(View.GONE);

            view.findViewById(R.id.fc_layot_title).setVisibility(View.GONE);

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) throws ClassCastException {
                    super.onScrolled(recyclerView, dx, dy);

                    if (((ActivityMain) G.fragmentActivity).arcMenu.isMenuOpened()) {
                        ((ActivityMain) G.fragmentActivity).arcMenu.toggleMenu();
                    }

                    if (dy > 0) {
                        // Scroll Down
                        if (((ActivityMain) G.fragmentActivity).arcMenu.fabMenu.isShown()) {
                            ((ActivityMain) G.fragmentActivity).arcMenu.fabMenu.hide();
                        }
                    } else if (dy < 0) {
                        // Scroll Up
                        if (!((ActivityMain) G.fragmentActivity).arcMenu.fabMenu.isShown()) {
                            ((ActivityMain) G.fragmentActivity).arcMenu.fabMenu.show();
                        }
                    }
                }
            });

        }
    }

    public void showContactListForCall() {
        final Fragment fragment = RegisteredContactsFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", "call");
        bundle.putBoolean("ACTION", true);
        fragment.setArguments(bundle);

        try {
            //G.fragmentActivity.getSupportFragmentManager()
            //    .beginTransaction()
            //    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
            //    .addToBackStack(null)
            //    .replace(R.id.fragmentContainer, fragment)
            //    .commit();

            new HelperFragment(fragment).setReplace(false).load();

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void getLogListWithOffset() {

        if (G.isSecure && G.userLogin) {
            isSendRequestForLoading = true;
            new RequestSignalingGetLog().signalingGetLog(mOffset, mLimit);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getLogListWithOffset();
                }
            }, 1000);
        }

    }

    public void openDialogMenu() {
        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
        View view = dialog.getCustomView();

        DialogAnimation.animationUp(dialog);
        dialog.show();

        final TextView txtClear = (TextView) view.findViewById(R.id.dialog_text_item1_notification);
        txtClear.setText(G.fragmentActivity.getResources().getString(R.string.clean_log));

        TextView iconClear = (TextView) view.findViewById(R.id.dialog_icon_item1_notification);
        iconClear.setText(G.fragmentActivity.getResources().getString(R.string.md_rubbish_delete_file));

        ViewGroup root1 = (ViewGroup) view.findViewById(R.id.dialog_root_item1_notification);
        root1.setVisibility(View.VISIBLE);

        root1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                if (G.userLogin) {
                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.clean_log).content(R.string.are_you_sure_clear_call_logs).
                            positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Realm realm = Realm.getDefaultInstance();
                            try {
                                RealmCallLog realmCallLog = realm.where(RealmCallLog.class).findAllSorted(RealmCallLogFields.TIME, Sort.DESCENDING).first();
                                new RequestSignalingClearLog().signalingClearLog(realmCallLog.getId());
                                imgCallEmpty.setVisibility(View.VISIBLE);
                                empty_call.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                realm.close();
                            }
                        }
                    }).negativeText(R.string.B_cancel).show();
                } else {
                    HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server));
                }
            }
        });
    }

    //*************************************************************************************************************

    public static void call(long userID, boolean isIncomingCall) {

        if (G.userLogin) {

            if (!G.isInCall) {
                Realm realm = Realm.getDefaultInstance();
                RealmCallConfig realmCallConfig = realm.where(RealmCallConfig.class).findFirst();

                if (realmCallConfig == null) {
                    new RequestSignalingGetConfiguration().signalingGetConfiguration();
                    HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server));
                } else {

                    if (G.currentActivity != null) {

                        Intent intent = new Intent(G.currentActivity, ActivityCall.class);
                        intent.putExtra(ActivityCall.USER_ID_STR, userID);
                        intent.putExtra(ActivityCall.INCOMING_CALL_STR, isIncomingCall);
                        ActivityCall.isGoingfromApp = true;
                        G.currentActivity.startActivity(intent);
                    } else {
                        Intent intent = new Intent(G.context, ActivityCall.class);
                        intent.putExtra(ActivityCall.USER_ID_STR, userID);
                        intent.putExtra(ActivityCall.INCOMING_CALL_STR, isIncomingCall);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        ActivityCall.isGoingfromApp = true;
                        G.context.startActivity(intent);
                    }


                }

                realm.close();
            }


        } else {

            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server));
        }
    }

    @Override
    public void onCallLogClear() {
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        if (mAdapter != null) {
        //            mAdapter.clear();
        //        }
        //    }
        //});
    }

    //*************************************************************************************************************

    /**
     * ***********************************************************************************
     * *********************************** FastAdapter ***********************************
     * ***********************************************************************************
     */
    //+ manually update
    //public class CallAdapterA<Item extends CallItem> extends FastItemAdapter<Item> {
    //
    //    @Override
    //    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //        return super.onCreateViewHolder(parent, viewType);
    //    }
    //}
    //
    //public class CallItem extends AbstractItem<CallItem, CallItem.ViewHolder> {
    //    String lastHeader = "";
    //    RealmCallLog callLog;
    //
    //    public CallItem setInfo(RealmCallLog callLog) {
    //        this.callLog = callLog;
    //        return this;
    //    }
    //
    //    @Override
    //    public void bindView(final ViewHolder viewHolder, List payloads) throws IllegalStateException {
    //        super.bindView(viewHolder, payloads);
    //
    //        if (callLog == null || !callLog.isValid()) {
    //            return;
    //        }
    //
    //        if (viewHolder.itemView.findViewById(R.id.mainContainer) == null) {
    //            ((ViewGroup) viewHolder.itemView).addView(ViewMaker.getViewItemCall());
    //        }
    //
    //        viewHolder.timeDuration = (TextView) viewHolder.itemView.findViewById(R.id.fcsl_txt_dureation_time);
    //        viewHolder.image = (CircleImageView) viewHolder.itemView.findViewById(R.id.fcsl_imv_picture);
    //        viewHolder.name = (EmojiTextViewE) viewHolder.itemView.findViewById(R.id.fcsl_txt_name);
    //        viewHolder.icon = (MaterialDesignTextView) viewHolder.itemView.findViewById(R.id.fcsl_txt_icon);
    //        viewHolder.timeAndInfo = (TextView) viewHolder.itemView.findViewById(R.id.fcsl_txt_time_info);
    //
    //        final ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog item = viewHolder.callLog = callLog.getLogProto();
    //
    //        // set icon and icon color
    //        switch (item.getStatus()) {
    //            case OUTGOING:
    //                viewHolder.icon.setText(R.string.md_call_made);
    //                viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.green));
    //                viewHolder.timeDuration.setTextColor(G.context.getResources().getColor(R.color.green));
    //                break;
    //            case MISSED:
    //                viewHolder.icon.setText(R.string.md_call_missed);
    //                viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.red));
    //                viewHolder.timeDuration.setTextColor(G.context.getResources().getColor(R.color.red));
    //                viewHolder.timeDuration.setText(R.string.miss);
    //                break;
    //            case CANCELED:
    //
    //                viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.green));
    //                viewHolder.timeDuration.setTextColor(G.context.getResources().getColor(R.color.green));
    //                viewHolder.timeDuration.setText(R.string.not_answer);
    //                break;
    //            case INCOMING:
    //                viewHolder.icon.setText(R.string.md_call_received);
    //                viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.colorPrimary));
    //                viewHolder.timeDuration.setTextColor(G.context.getResources().getColor(R.color.colorPrimary));
    //                break;
    //        }
    //
    //        if (HelperCalander.isLanguagePersian) {
    //            viewHolder.timeAndInfo.setText(TimeUtils.toLocal(item.getOfferTime() * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME + " " + HelperCalander.checkHijriAndReturnTime(item.getOfferTime())));
    //        } else {
    //            viewHolder.timeAndInfo.setText(HelperCalander.checkHijriAndReturnTime(item.getOfferTime()) + " " + TimeUtils.toLocal(item.getOfferTime() * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME));
    //        }
    //
    //        if (item.getDuration() > 0) {
    //            viewHolder.timeDuration.setText(DateUtils.formatElapsedTime(item.getDuration()));
    //        }
    //
    //        if (HelperCalander.isLanguagePersian) {
    //            viewHolder.timeAndInfo.setText(HelperCalander.convertToUnicodeFarsiNumber(viewHolder.timeAndInfo.getText().toString()));
    //            viewHolder.timeDuration.setText(HelperCalander.convertToUnicodeFarsiNumber(viewHolder.timeDuration.getText().toString()));
    //        }
    //
    //        viewHolder.name.setText(item.getPeer().getDisplayName());
    //
    //        hashMapAvatar.put(item.getId(), viewHolder.image);
    //
    //        HelperAvatar.getAvatarCall(item.getPeer(), item.getPeer().getId(), HelperAvatar.AvatarType.USER, false, new OnAvatarGet() {
    //            @Override
    //            public void onAvatarGet(final String avatarPath, long ownerId) {
    //                G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(item.getId()));
    //            }
    //
    //            @Override
    //            public void onShowInitials(final String initials, final String color) {
    //                hashMapAvatar.get(item.getId()).setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) viewHolder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
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
    //        return R.layout.fragment_call_sub_layout_code;
    //    }
    //
    //    @Override
    //    public ViewHolder getViewHolder(View viewGroup) {
    //        return new ViewHolder(viewGroup);
    //    }
    //
    //    public class ViewHolder extends RecyclerView.ViewHolder {
    //
    //        private ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog callLog;
    //        private CircleImageView image;
    //        private EmojiTextViewE name;
    //        private MaterialDesignTextView icon;
    //        private TextView timeAndInfo;
    //        private TextView timeDuration;
    //
    //        public ViewHolder(View view) {
    //            super(view);
    //            //imgCallEmpty.setVisibility(View.GONE);
    //            //empty_call.setVisibility(View.GONE);
    //            //
    //            //timeDuration = (TextView) itemView.findViewById(R.id.fcsl_txt_dureation_time);
    //            //image = (CircleImageView) itemView.findViewById(R.id.fcsl_imv_picture);
    //            //name = (EmojiTextViewE) itemView.findViewById(R.id.fcsl_txt_name);
    //            //icon = (MaterialDesignTextView) itemView.findViewById(R.id.fcsl_txt_icon);
    //            //timeAndInfo = (TextView) itemView.findViewById(R.id.fcsl_txt_time_info);
    //            //
    //            //itemView.setOnClickListener(new View.OnClickListener() {
    //            //    @Override
    //            //    public void onClick(View v) {
    //            //
    //            //        // HelperPublicMethod.goToChatRoom(realmResults.get(getPosition()).getLogProto().getPeer().getId(), null, null);
    //            //
    //            //        if (canclick) {
    //            //            long userId = callLog.getPeer().getId();
    //            //
    //            //            if (userId != 134 && G.userId != userId) {
    //            //                call(userId, false);
    //            //            }
    //            //        }
    //            //    }
    //            //});
    //            //
    //            //itemView.setOnTouchListener(new View.OnTouchListener() {
    //            //    @Override
    //            //    public boolean onTouch(View v, MotionEvent event) {
    //            //
    //            //        if (event.getAction() == MotionEvent.ACTION_DOWN) {
    //            //            move = (int) event.getX();
    //            //        } else if (event.getAction() == MotionEvent.ACTION_UP) {
    //            //
    //            //            int i = Math.abs((int) (move - event.getX()));
    //            //
    //            //            if (i < 10) {
    //            //                canclick = true;
    //            //            } else {
    //            //                canclick = false;
    //            //            }
    //            //        }
    //            //
    //            //        return false;
    //            //    }
    //            //});
    //        }
    //    }
    //}


    /**
     * **********************************************************************************
     * ********************************** RealmAdapter **********************************
     * **********************************************************************************
     */

    public class CallAdapter extends RealmRecyclerViewAdapter<RealmCallLog, CallAdapter.ViewHolder> {

        public CallAdapter(RealmResults<RealmCallLog> realmResults) {
            super(realmResults, true);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog callLog;
            private CircleImageView image;
            private EmojiTextViewE name;
            private MaterialDesignTextView icon;
            private TextView timeAndInfo;
            private TextView timeDuration;

            public ViewHolder(View view) {
                super(view);

                imgCallEmpty.setVisibility(View.GONE);
                empty_call.setVisibility(View.GONE);

                timeDuration = (TextView) itemView.findViewById(R.id.fcsl_txt_dureation_time);
                image = (CircleImageView) itemView.findViewById(R.id.fcsl_imv_picture);
                name = (EmojiTextViewE) itemView.findViewById(R.id.fcsl_txt_name);
                icon = (MaterialDesignTextView) itemView.findViewById(R.id.fcsl_txt_icon);
                timeAndInfo = (TextView) itemView.findViewById(R.id.fcsl_txt_time_info);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // HelperPublicMethod.goToChatRoom(realmResults.get(getPosition()).getLogProto().getPeer().getId(), null, null);

                        if (canclick) {
                            long userId = callLog.getPeer().getId();

                            if (userId != 134 && G.userId != userId) {
                                call(userId, false);
                            }
                        }
                    }
                });

                itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            move = (int) event.getX();
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {

                            int i = Math.abs((int) (move - event.getX()));

                            if (i < 10) {
                                canclick = true;
                            } else {
                                canclick = false;
                            }
                        }

                        return false;
                    }
                });
            }
        }

        @Override
        public CallAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            //  new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_call_sub_layout, null));

            return new ViewHolder(ViewMaker.getViewItemCall());
        }

        @Override
        public void onBindViewHolder(final CallAdapter.ViewHolder viewHolder, int i) {

            final ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog item = viewHolder.callLog = getItem(i).getLogProto();

            // set icon and icon color
            switch (item.getStatus()) {
                case OUTGOING:
                    viewHolder.icon.setText(R.string.md_call_made);
                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.green));
                    viewHolder.timeDuration.setTextColor(G.context.getResources().getColor(R.color.green));
                    break;
                case MISSED:
                    viewHolder.icon.setText(R.string.md_call_missed);
                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.red));
                    viewHolder.timeDuration.setTextColor(G.context.getResources().getColor(R.color.red));
                    viewHolder.timeDuration.setText(R.string.miss);
                    break;
                case CANCELED:

                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.green));
                    viewHolder.timeDuration.setTextColor(G.context.getResources().getColor(R.color.green));
                    viewHolder.timeDuration.setText(R.string.not_answer);
                    break;
                case INCOMING:
                    viewHolder.icon.setText(R.string.md_call_received);
                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.colorPrimary));
                    viewHolder.timeDuration.setTextColor(G.context.getResources().getColor(R.color.colorPrimary));
                    break;
            }

            //switch (item.getType()) {
            //
            //    case SCREEN_SHARING:
            //        viewHolder.call_type_icon.setText(R.string.md_stay_current_portrait);
            //        break;
            //    case VIDEO_CALLING:
            //        viewHolder.call_type_icon.setText(R.string.md_video_cam);
            //        break;
            //    case VOICE_CALLING:
            //        viewHolder.call_type_icon.setText(R.string.md_phone);
            //        break;
            //}

            if (HelperCalander.isLanguagePersian) {
                viewHolder.timeAndInfo.setText(TimeUtils.toLocal(item.getOfferTime() * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME + " " + HelperCalander.checkHijriAndReturnTime(item.getOfferTime())));
            } else {
                viewHolder.timeAndInfo.setText(HelperCalander.checkHijriAndReturnTime(item.getOfferTime()) + " " + TimeUtils.toLocal(item.getOfferTime() * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME));
            }

            if (item.getDuration() > 0) {
                viewHolder.timeDuration.setText(DateUtils.formatElapsedTime(item.getDuration()));
            }

            if (HelperCalander.isLanguagePersian) {
                viewHolder.timeAndInfo.setText(HelperCalander.convertToUnicodeFarsiNumber(viewHolder.timeAndInfo.getText().toString()));
                viewHolder.timeDuration.setText(HelperCalander.convertToUnicodeFarsiNumber(viewHolder.timeDuration.getText().toString()));
            }

            viewHolder.name.setText(item.getPeer().getDisplayName());

            hashMapAvatar.put(item.getId(), viewHolder.image);

            HelperAvatar.getAvatarCall(item.getPeer(), item.getPeer().getId(), HelperAvatar.AvatarType.USER, false, new OnAvatarGet() {
                @Override
                public void onAvatarGet(final String avatarPath, long ownerId) {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(item.getId()));
                }

                @Override
                public void onShowInitials(final String initials, final String color) {
                    hashMapAvatar.get(item.getId()).setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) viewHolder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (progressBar != null) {
            AppUtils.setProgresColler(progressBar);
        }

        if (G.isUpdateNotificaionCall) {
            G.isUpdateNotificaionCall = false;

            if (mRecyclerView != null) {
                if (mRecyclerView.getAdapter() != null) {
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }


    }
}
