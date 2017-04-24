/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperCalander;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.interfaces.OnSelectedList;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.CircleImageView;
import com.iGap.module.Contacts;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.module.structs.StructContactInfo;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.request.RequestUserContactsBlock;
import com.iGap.request.RequestUserContactsUnblock;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import java.util.ArrayList;
import java.util.List;

public class FragmentBlockedUser extends Fragment {

    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blocked_user, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (realm != null) realm.close();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.fbu_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        RippleView rippleBack = (RippleView) view.findViewById(R.id.fbu_ripple_back_Button);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentBlockedUser.this).commit();

                if (realm != null) realm.close();
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
                //  bundle.putBoolean("SINGLE_SELECT", true);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.parentPrivacySecurity, fragment).commit();
            }
        });

        realm = Realm.getDefaultInstance();
        RealmRecyclerView realmRecyclerView = (RealmRecyclerView) view.findViewById(R.id.fbu_realm_recycler_view);

        RealmResults<RealmRegisteredInfo> results = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.BLOCK_USER, true).findAll();

        BlockListAdapter blockListAdapter = new BlockListAdapter(getActivity(), results);
        realmRecyclerView.setAdapter(blockListAdapter);
    }

    private void openDialogToggleBlock(final long userId) {

        new MaterialDialog.Builder(G.currentActivity).content(R.string.un_block_user).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new RequestUserContactsUnblock().userContactsUnblock(userId);
            }
        }).negativeText(R.string.B_cancel).show();
    }

    public class BlockListAdapter extends RealmBasedRecyclerViewAdapter<RealmRegisteredInfo, BlockListAdapter.ViewHolder> {

        public BlockListAdapter(Context context, RealmResults<RealmRegisteredInfo> realmResults) {
            super(context, realmResults, true, true, false, "");
        }

        public class ViewHolder extends RealmViewHolder {

            protected CircleImageView image;
            protected CustomTextViewMedium title;
            protected CustomTextViewMedium subtitle;
            protected View bottomLine;

            public ViewHolder(View view) {
                super(view);

                image = (CircleImageView) view.findViewById(R.id.imageView);
                title = (CustomTextViewMedium) view.findViewById(R.id.title);
                subtitle = (CustomTextViewMedium) view.findViewById(R.id.subtitle);
                bottomLine = (View) view.findViewById(R.id.bottomLine);
                bottomLine.setVisibility(View.VISIBLE);
                view.findViewById(R.id.topLine).setVisibility(View.GONE);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        openDialogToggleBlock(realmResults.get(getPosition()).getId());

                        return true;
                    }
                });
            }
        }

        @Override
        public BlockListAdapter.ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.contact_item, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindRealmViewHolder(final BlockListAdapter.ViewHolder viewHolder, int i) {

            viewHolder.title.setText(realmResults.get(i).getDisplayName());
            viewHolder.subtitle.setText(realmResults.get(i).getPhoneNumber());
            if (HelperCalander.isLanguagePersian) {
                viewHolder.subtitle.setText(HelperCalander.convertToUnicodeFarsiNumber(viewHolder.subtitle.getText().toString()));
            }

            HelperAvatar.getAvatar(realmResults.get(i).getId(), HelperAvatar.AvatarType.USER, new OnAvatarGet() {
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
                            viewHolder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) viewHolder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                        }
                    });
                }
            });
        }
    }
}
