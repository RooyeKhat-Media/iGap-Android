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
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnSelectedList;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.Contacts;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.request.RequestUserContactsBlock;
import net.iGap.request.RequestUserContactsUnblock;

import static net.iGap.G.inflater;

public class FragmentBlockedUser extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blocked_user, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.fbu_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        RippleView rippleBack = (RippleView) view.findViewById(R.id.fbu_ripple_back_Button);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                removeFromBaseFragment(FragmentBlockedUser.this);
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


        RecyclerView realmRecyclerView = (RecyclerView) view.findViewById(R.id.fbu_realm_recycler_view);
        realmRecyclerView.setItemViewCacheSize(100);
        realmRecyclerView.setItemAnimator(null);
        realmRecyclerView.setLayoutManager(new LinearLayoutManager(G.fragmentActivity));

        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmRegisteredInfo> results = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.BLOCK_USER, true).findAll();

        BlockListAdapter blockListAdapter = new BlockListAdapter(results);
        realmRecyclerView.setAdapter(blockListAdapter);

        realm.close();
    }

    private void openDialogToggleBlock(final long userId) {

        new MaterialDialog.Builder(G.currentActivity).content(R.string.un_block_user).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new RequestUserContactsUnblock().userContactsUnblock(userId);
            }
        }).negativeText(R.string.B_cancel).show();
    }

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

            public ViewHolder(View view) {
                super(view);

                image = (CircleImageView) view.findViewById(R.id.imageView);
                title = (CustomTextViewMedium) view.findViewById(R.id.title);
                subtitle = (CustomTextViewMedium) view.findViewById(R.id.subtitle);
                bottomLine = view.findViewById(R.id.bottomLine);
                bottomLine.setVisibility(View.VISIBLE);
                view.findViewById(R.id.topLine).setVisibility(View.GONE);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        openDialogToggleBlock(realmRegisteredInfo.getId());
                        return true;
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

            RealmRegisteredInfo registeredInfo = viewHolder.realmRegisteredInfo = getItem(i);
            if (registeredInfo == null) {
                return;
            }

            viewHolder.title.setText(registeredInfo.getDisplayName());
            viewHolder.subtitle.setText(registeredInfo.getPhoneNumber());
            if (HelperCalander.isLanguagePersian) {
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
        }
    }
}
