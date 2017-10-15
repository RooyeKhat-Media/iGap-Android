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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import io.realm.Realm;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperString;
import net.iGap.interfaces.OnChannelCheckUsername;
import net.iGap.interfaces.OnChannelUpdateUsername;
import net.iGap.interfaces.OnClientGetRoomResponse;
import net.iGap.module.AppUtils;
import net.iGap.proto.ProtoChannelCheckUsername;
import net.iGap.proto.ProtoClientGetRoom;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestChannelCheckUsername;
import net.iGap.request.RequestChannelUpdateUsername;
import net.iGap.request.RequestClientGetRoom;

import static android.content.Context.CLIPBOARD_SERVICE;

public class FragmentCreateChannel extends BaseFragment implements OnChannelCheckUsername {

    private Long roomId;
    private String inviteLink;
    public static final int PRIVATE = 0;
    public static final int PUBLIC = 1;
    private EditText edtLink;
    private RadioButton raPublic;
    private RadioButton raPrivate;
    private TextInputLayout txtInputLayout;
    private TextView txtFinish;
    private String token;
    private boolean existAvatar;
    private ProgressBar prgWaiting;
    private String pathSaveImage;

    public FragmentCreateChannel() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_create_channel, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        G.onChannelCheckUsername = this;

        if (getArguments() != null) {
            roomId = getArguments().getLong("ROOMID");
            inviteLink = getArguments().getString("INVITE_LINK");
            token = getArguments().getString("TOKEN");
        }

        view.findViewById(R.id.fch_backgroundToolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        prgWaiting = (ProgressBar) view.findViewById(R.id.fch_prgWaiting_addContact);
        AppUtils.setProgresColler(prgWaiting);

        prgWaiting.setVisibility(View.GONE);

        ViewGroup vgRoot = (ViewGroup) view.findViewById(R.id.fch_root);
        vgRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        TextView txtBack = (TextView) view.findViewById(R.id.fch_txt_back);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromBaseFragment(FragmentCreateChannel.this);
            }
        });

        TextView txtCancel = (TextView) view.findViewById(R.id.fch_txt_cancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(FragmentCreateChannel.this).commit();

                removeFromBaseFragment(FragmentCreateChannel.this);
            }
        });

        txtFinish = (TextView) view.findViewById(R.id.fch_txt_finish);
        txtFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                G.onChannelUpdateUsername = new OnChannelUpdateUsername() {
                    @Override
                    public void onChannelUpdateUsername(final long roomId, final String username) {

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                        realmRoom.getChannelRoom().setUsername(username);
                                        realmRoom.getChannelRoom().setPrivate(false);
                                    }
                                });
                                realm.close();
                                getRoom(roomId, ProtoGlobal.Room.Type.CHANNEL);
                            }
                        });
                    }

                    @Override
                    public void onError(int majorCode, int minorCode, int time) {
                        hideProgressBar();
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                final Snackbar snack = Snackbar.make(G.fragmentActivity.findViewById(android.R.id.content), G.fragmentActivity.getResources().getString(R.string.normal_error), Snackbar.LENGTH_LONG);

                                snack.setAction(G.fragmentActivity.getResources().getString(R.string.cancel), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        snack.dismiss();
                                    }
                                });
                                snack.show();
                            }
                        });
                    }

                    @Override
                    public void onTimeOut() {
                        hideProgressBar();
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                final Snackbar snack = Snackbar.make(G.fragmentActivity.findViewById(android.R.id.content), G.fragmentActivity.getResources().getString(R.string.time_out), Snackbar.LENGTH_LONG);

                                snack.setAction(G.fragmentActivity.getResources().getString(R.string.cancel), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        snack.dismiss();
                                    }
                                });
                                snack.show();
                            }
                        });
                    }
                };

                if ((raPrivate.isChecked() || edtLink.getText().toString().length() > 0) && roomId > 0) {

                    showProgressBar();

                    if (raPrivate.isChecked()) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                realmRoom.getChannelRoom().setPrivate(true);
                            }
                        });
                        realm.close();
                        getRoom(roomId, ProtoGlobal.Room.Type.CHANNEL);
                    } else {

                        String userName = edtLink.getText().toString().replace("iGap.net/", "");
                        new RequestChannelUpdateUsername().channelUpdateUsername(roomId, userName);
                    }
                }
            }
        });

        txtInputLayout = (TextInputLayout) view.findViewById(R.id.fch_txtInput_nikeName);
        txtInputLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (raPrivate.isChecked()) {
                    final PopupMenu popup = new PopupMenu(G.fragmentActivity, view);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.menu_item_copy, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_link_copy:
                                    String copy;
                                    copy = edtLink.getText().toString();
                                    ClipboardManager clipboard = (ClipboardManager) G.context.getSystemService(CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                                    clipboard.setPrimaryClip(clip);

                                    break;
                            }
                            return true;
                        }
                    });

                    popup.show(); //
                }
            }
        });
        edtLink = (EditText) view.findViewById(R.id.fch_edt_link);
        edtLink.setText("iGap.net/");
        Selection.setSelection(edtLink.getText(), edtLink.getText().length());
        edtLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!raPrivate.isChecked()) {

                    if (!editable.toString().contains("iGap.net/")) {
                        edtLink.setText("iGap.net/");
                        Selection.setSelection(edtLink.getText(), edtLink.getText().length());
                    }
                    if (HelperString.regexCheckUsername(editable.toString().replace("iGap.net/", ""))) {
                        String userName = edtLink.getText().toString().replace("iGap.net/", "");
                        new RequestChannelCheckUsername().channelCheckUsername(roomId, userName);
                    } else {
                        txtFinish.setEnabled(false);
                        txtFinish.setTextColor(G.context.getResources().getColor(R.color.gray_6c));
                        txtInputLayout.setErrorEnabled(true);
                        txtInputLayout.setError("" + G.fragmentActivity.getResources().getString(R.string.INVALID));
                    }
                }
            }
        });

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.fch_radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setInviteLink();
            }
        });

        raPublic = (RadioButton) view.findViewById(R.id.fch_radioButton_Public);
        raPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        raPrivate = (RadioButton) view.findViewById(R.id.fch_radioButton_private);
        raPrivate.setChecked(true);
        raPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        setInviteLink();
    }

    private void getRoom(final Long roomId, final ProtoGlobal.Room.Type type) {
        G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
            @Override
            public void onClientGetRoomResponse(final ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder, RequestClientGetRoom.IdentityClientGetRoom identity) {

                if (identity.createRoomMode != RequestClientGetRoom.CreateRoomMode.requestFromOwner) {
                    return;
                }

                try {
                    if (G.fragmentActivity != null) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {

                                hideProgressBar();
                                Fragment fragment = ContactGroupFragment.newInstance();
                                Bundle bundle = new Bundle();
                                bundle.putLong("RoomId", roomId);
                                bundle.putString("LIMIT", room.getGroupRoomExtra().getParticipantsCountLimitLabel());
                                bundle.putString("TYPE", type.toString());
                                bundle.putBoolean("NewRoom", true);
                                fragment.setArguments(bundle);

                                popBackStackFragment();
                                new HelperFragment(fragment).load();
                            }
                        });
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                hideProgressBar();
            }

            @Override
            public void onTimeOut() {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }
        };

        new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.requestFromOwner);
    }

    private void setInviteLink() {

        if (raPrivate.isChecked()) {
            edtLink.setText(inviteLink);
            edtLink.setEnabled(false);
            txtFinish.setEnabled(true);
            txtFinish.setTextColor(G.context.getResources().getColor(R.color.toolbar_background));
            txtInputLayout.setErrorEnabled(true);
            txtInputLayout.setError("");
        } else if (raPublic.isChecked()) {
            edtLink.setEnabled(true);
            edtLink.setText("");
        }
    }

    @Override
    public void onChannelCheckUsername(final ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status status) {

        if (G.fragmentActivity != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.AVAILABLE) {

                        txtFinish.setEnabled(true);
                        txtFinish.setTextColor(G.context.getResources().getColor(R.color.toolbar_background));
                        txtInputLayout.setErrorEnabled(true);
                        txtInputLayout.setError("");
                    } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.INVALID) {
                        txtFinish.setEnabled(false);
                        txtFinish.setTextColor(G.context.getResources().getColor(R.color.gray_6c));
                        txtInputLayout.setErrorEnabled(true);
                        txtInputLayout.setError("" + G.fragmentActivity.getResources().getString(R.string.INVALID));
                    } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.TAKEN) {
                        txtFinish.setEnabled(false);
                        txtFinish.setTextColor(G.context.getResources().getColor(R.color.gray_6c));
                        txtInputLayout.setErrorEnabled(true);
                        txtInputLayout.setError("" + G.fragmentActivity.getResources().getString(R.string.TAKEN));
                    } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.OCCUPYING_LIMIT_EXCEEDED) {
                        txtFinish.setEnabled(false);
                        txtFinish.setTextColor(G.context.getResources().getColor(R.color.gray_6c));
                        txtInputLayout.setErrorEnabled(true);
                        txtInputLayout.setError("" + G.fragmentActivity.getResources().getString(R.string.OCCUPYING_LIMIT_EXCEEDED));
                    }
                }
            });
        }
    }

    @Override
    public void onError(int majorCode, int minorCode) {

        if (G.fragmentActivity != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack = Snackbar.make(G.fragmentActivity.findViewById(android.R.id.content), G.fragmentActivity.getResources().getString(R.string.normal_error), Snackbar.LENGTH_LONG);
                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        }
    }

    @Override
    public void onTimeOut() {

        if (G.fragmentActivity != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack = Snackbar.make(G.fragmentActivity.findViewById(android.R.id.content), G.fragmentActivity.getResources().getString(R.string.time_out), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        }
    }

    private void showProgressBar() {

        if (G.fragmentActivity != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {

                    prgWaiting.setVisibility(View.VISIBLE);
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
        }
    }

    private void hideProgressBar() {

        if (G.fragmentActivity != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {

                    prgWaiting.setVisibility(View.GONE);
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
        }
    }

    @Override
    public void onDetach() {

        if (prgWaiting.getVisibility() == View.VISIBLE) {
            hideProgressBar();
        }
        super.onDetach();
    }
}
