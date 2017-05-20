package net.iGap.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import java.io.IOException;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.interfaces.ISignalingGetCallLog;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.proto.ProtoSignalingGetLog;
import net.iGap.realm.RealmCallLog;
import net.iGap.realm.RealmCallLogFields;
import net.iGap.request.RequestSignalingClearLog;
import net.iGap.request.RequestSignalingGetLog;


public class FragmentCall extends Fragment {

    private int mOffset = 0;
    private int mLimit = 50;
    private RecyclerView.OnScrollListener onScrollListener;
    boolean isSendRequestForLoading = false;
    boolean isThereAnyMoreItemToLoad = true;

    ProgressBar progressBar;

    private RealmRecyclerView mRecyclerView;

    public static FragmentCall newInstance() {
        return new FragmentCall();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_call, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.fc_layot_title).setBackgroundColor(Color.parseColor(G.appBarColor));  //set title bar color

        progressBar = (ProgressBar) view.findViewById(R.id.fc_progress_bar_waiting);
        AppUtils.setProgresColler(progressBar);

        RippleView rippleBack = (RippleView) view.findViewById(R.id.fc_call_ripple_txtBack);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        MaterialDesignTextView txtMenu = (MaterialDesignTextView) view.findViewById(R.id.fc_btn_menu);

        txtMenu.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).items(R.array.pop_up_call_log_menu).contentColor(Color.BLACK).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {

                            new RequestSignalingClearLog().signalingClearLog(G.userId);
                        }
                    }
                }).show();

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = (int) getResources().getDimension(R.dimen.dp200);
                layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
                dialog.getWindow().setAttributes(layoutParams);
            }
        });

        mRecyclerView = (RealmRecyclerView) view.findViewById(R.id.fc_recycler_view_call);
        mRecyclerView.setItemViewCacheSize(500);
        mRecyclerView.setDrawingCacheEnabled(true);

        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmCallLog> results = realm.where(RealmCallLog.class).findAllSorted(RealmCallLogFields.ID);
        CallAdapter callAdapter = new CallAdapter(getActivity(), results);
        mRecyclerView.setAdapter(callAdapter);

        onScrollListener = new RecyclerView.OnScrollListener() {

            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isThereAnyMoreItemToLoad) {
                    if (!isSendRequestForLoading) {

                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                        if (lastVisiblePosition + 10 >= mOffset) {
                            getLogListWithOfset();
                        }
                    }
                }
            }
        };

        mRecyclerView.getRecycleView().addOnScrollListener(onScrollListener);

        G.iSignalingGetCallLog = new ISignalingGetCallLog() {
            @Override public void onGetList(int size) {

                G.handler.post(new Runnable() {
                    @Override public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });

                if (size == 0) {
                    isThereAnyMoreItemToLoad = false;
                    mRecyclerView.getRecycleView().removeOnScrollListener(onScrollListener);
                } else {
                    isSendRequestForLoading = false;
                    mOffset += size;
                }
            }
        };

        realm.close();

        FloatingActionButton fabContactList = (FloatingActionButton) view.findViewById(R.id.fc_fab_contact_list);
        fabContactList.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));

        fabContactList.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                final Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "Contacts");
                bundle.putBoolean("ACTION", true);
                fragment.setArguments(bundle);

                try {
                    getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .addToBackStack(null)
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });

        getLogListWithOfset();

    }

    private void getLogListWithOfset() {
        isSendRequestForLoading = true;

        new RequestSignalingGetLog().signalingGetLog(mOffset, mLimit);

        progressBar.setVisibility(View.VISIBLE);
    }

    //*************************************************************************************************************

    public static void call(long userID, boolean isIncomingCall) {

        Intent intent = new Intent(G.context, ActivityCall.class);
        intent.putExtra(ActivityCall.UserIdStr, userID);
        intent.putExtra(ActivityCall.INCOMONGCALL_STR, isIncomingCall);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        G.context.startActivity(intent);
    }

    //*************************************************************************************************************

    enum CallMode {
        call_made, call_received, call_missed, call_missed_outgoing
    }

    //***************************************** adapater call ***************************************************

    public class CallAdapter extends RealmBasedRecyclerViewAdapter<RealmCallLog, CallAdapter.ViewHolder> {

        public CallAdapter(Context context, RealmResults<RealmCallLog> realmResults) {
            super(context, realmResults, true, false, false, "");
        }

        public class ViewHolder extends RealmViewHolder {

            protected CircleImageView image;
            protected TextView name;
            protected MaterialDesignTextView icon;
            protected MaterialDesignTextView call_type_icon;
            protected TextView timeAndInfo;
            protected RippleView rippleCall;
            protected TextView timeDureation;

            public ViewHolder(View view) {
                super(view);

                timeDureation = (TextView) itemView.findViewById(R.id.fcsl_txt_dureation_time);
                call_type_icon = (MaterialDesignTextView) itemView.findViewById(R.id.fcsl_call_type_icon);
                image = (CircleImageView) itemView.findViewById(R.id.fcsl_imv_picture);
                name = (TextView) itemView.findViewById(R.id.fcsl_txt_name);
                icon = (MaterialDesignTextView) itemView.findViewById(R.id.fcsl_txt_icon);
                timeAndInfo = (TextView) itemView.findViewById(R.id.fcsl_txt_time_info);
                rippleCall = (RippleView) itemView.findViewById(R.id.fcsl_ripple_call);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {

                        HelperPublicMethod.goToChatRoom(realmResults.get(getPosition()).getlogProto().getPeer().getId(), null, null);
                    }
                });

                rippleCall.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override public void onComplete(RippleView rippleView) throws IOException {
                        call(realmResults.get(getPosition()).getlogProto().getPeer().getId(), false);
                    }
                });
            }
        }

        @Override public CallAdapter.ViewHolder onCreateRealmViewHolder(ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_call_sub_layout, null);
            ViewHolder callHolder = new ViewHolder(view);

            return callHolder;
        }

        @Override public void onBindRealmViewHolder(final CallAdapter.ViewHolder viewHolder, int i) {

            ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog item = realmResults.get(i).getlogProto();

            // set icon and icon color
            switch (item.getStatus()) {
                case OUTGOING:
                    viewHolder.icon.setText(R.string.md_call_made);
                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.green));
                    break;
                case MISSED:
                    viewHolder.icon.setText(R.string.md_call_missed);
                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.red));
                    break;
                case CANCELED:
                    viewHolder.icon.setText(R.string.md_call_missed_outgoing);
                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.red));
                    break;
                case INCOMING:
                    viewHolder.icon.setText(R.string.md_call_received);
                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.green));
                    break;
            }

            switch (item.getType()) {

                case SCREEN_SHARING:
                    viewHolder.call_type_icon.setText(R.string.md_stay_current_portrait);
                    break;
                case VIDEO_CALLING:
                    viewHolder.call_type_icon.setText(R.string.md_video_cam);
                    break;
                case VOICE_CALLING:
                    viewHolder.call_type_icon.setText(R.string.md_phone);
                    break;
            }

            viewHolder.timeAndInfo.setText(HelperCalander.checkHijriAndReturnTime(item.getOfferTime()));
            viewHolder.timeDureation.setText(DateUtils.formatElapsedTime(item.getDuration()));

            if (HelperCalander.isLanguagePersian) {
                viewHolder.timeAndInfo.setText(HelperCalander.convertToUnicodeFarsiNumber(viewHolder.timeAndInfo.getText().toString()));
                viewHolder.timeDureation.setText(HelperCalander.convertToUnicodeFarsiNumber(viewHolder.timeDureation.getText().toString()));
            }


            viewHolder.name.setText(item.getPeer().getDisplayName());

            HelperAvatar.getAvatar(item.getPeer().getId(), HelperAvatar.AvatarType.USER, new OnAvatarGet() {
                @Override public void onAvatarGet(final String avatarPath, long ownerId) {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), viewHolder.image);
                }

                @Override public void onShowInitials(final String initials, final String color) {
                    viewHolder.image.setImageBitmap(
                        net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) viewHolder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                }
            });
        }
    }
}
