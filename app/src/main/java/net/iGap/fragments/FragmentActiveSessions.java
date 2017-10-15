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
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.AdapterActiveSessions;
import net.iGap.adapter.items.chat.AdapterActiveSessionsHeader;
import net.iGap.interfaces.OnUserSessionGetActiveList;
import net.iGap.interfaces.OnUserSessionLogout;
import net.iGap.interfaces.OnUserSessionTerminate;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AppUtils;
import net.iGap.module.SUID;
import net.iGap.module.structs.StructSessions;
import net.iGap.proto.ProtoUserSessionGetActiveList;
import net.iGap.request.RequestUserSessionGetActiveList;
import net.iGap.request.RequestUserSessionLogout;
import net.iGap.request.RequestUserSessionTerminate;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentActiveSessions extends BaseFragment {

    private FastAdapter fastAdapter;
    private RecyclerView rcvContent;
    private List<StructSessions> structItems = new ArrayList<>();
    private List<IItem> items = new ArrayList<>();
    private ProgressBar prgWaiting;
    private FastItemAdapter fastItemAdapter;
    private boolean isClearAdapter = true;
    private List<StructSessions> list = new ArrayList<>();

    public FragmentActiveSessions() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_active_sessions, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.acs_toolbar_session).setBackgroundColor(Color.parseColor(G.appBarColor));

        prgWaiting = (ProgressBar) view.findViewById(R.id.stas_prgWaiting);
        AppUtils.setProgresColler(prgWaiting);

        prgWaiting.setVisibility(View.VISIBLE);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.stas_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                removeFromBaseFragment(FragmentActiveSessions.this);
            }
        });

        fastItemAdapter = new FastItemAdapter();
        rcvContent = (RecyclerView) view.findViewById(R.id.stas_rcvContent);
        rcvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvContent.setItemAnimator(new DefaultItemAnimator());
        rcvContent.setAdapter(fastItemAdapter);

        G.onUserSessionGetActiveList = new OnUserSessionGetActiveList() {
            @Override
            public void onUserSessionGetActiveList(final List<ProtoUserSessionGetActiveList.UserSessionGetActiveListResponse.Session> session) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < session.size(); i++) {

                            StructSessions item = new StructSessions();
                            item.setSessionId(session.get(i).getSessionId());
                            item.setName(session.get(i).getAppName());
                            item.setAppId(session.get(i).getAppId());
                            item.setBuildVersion(session.get(i).getAppBuildVersion());
                            item.setAppVersion(session.get(i).getAppVersion());
                            item.setPlatform(session.get(i).getPlatform());
                            item.setPlatformVersion(session.get(i).getPlatformVersion());
                            item.setDevice(session.get(i).getDevice());
                            item.setDeviceName(session.get(i).getDeviceName());
                            item.setLanguage(session.get(i).getLanguage());
                            item.setCountry(session.get(i).getCountry());
                            item.setCurrent(session.get(i).getCurrent());
                            item.setCreateTime(session.get(i).getCreateTime());
                            item.setActiveTime(session.get(i).getActiveTime());
                            item.setIp(session.get(i).getIp());

                            if (item.isCurrent()) {
                                structItems.add(0, item);
                            } else {
                                structItems.add(item);
                            }

                            list.add(item);
                        }

                        itemAdapter();
                    }
                });
            }
        };
        new RequestUserSessionGetActiveList().userSessionGetActiveList();

        G.onUserSessionTerminate = new OnUserSessionTerminate() {

            @Override
            public void onUserSessionTerminate(final Long messageId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        prgWaiting.setVisibility(View.GONE);

                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getSessionId() == messageId) {
                                int j = fastItemAdapter.getPosition(list.get(i).getSessionId());
                                if (j >= 0) {
                                    fastItemAdapter.remove(j);
                                    list.remove(i);
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        prgWaiting.setVisibility(View.GONE);
                        final Snackbar snack = Snackbar.make(G.fragmentActivity.findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
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
            public void onError() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        prgWaiting.setVisibility(View.GONE);
                        final Snackbar snack = Snackbar.make(G.fragmentActivity.findViewById(android.R.id.content), G.fragmentActivity.getResources().getString(R.string.error), Snackbar.LENGTH_LONG);
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

        fastItemAdapter.withSelectable(true);
        fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener() {
            @Override
            public boolean onClick(final View v, IAdapter adapter, final IItem item, final int position) {

                if (item instanceof AdapterActiveSessions) {
                    if (((AdapterActiveSessions) item).getItem().isCurrent()) {
                        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.active_session_title).content(R.string.active_session_content).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                prgWaiting.setVisibility(View.VISIBLE);
                                G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                G.onUserSessionLogout = new OnUserSessionLogout() {
                                    @Override
                                    public void onUserSessionLogout() {

                                        G.handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                prgWaiting.setVisibility(View.GONE);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError() {
                                        G.handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                prgWaiting.setVisibility(View.GONE);
                                                final Snackbar snack = Snackbar.make(G.fragmentActivity.findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
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
                                        G.handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                prgWaiting.setVisibility(View.GONE);
                                                final Snackbar snack = Snackbar.make(G.fragmentActivity.findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
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

                                new RequestUserSessionLogout().userSessionLogout();
                            }
                        }).show();
                    } else {
                        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.active_session_title).content(R.string.active_session_content).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                prgWaiting.setVisibility(View.VISIBLE);
                                G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                new RequestUserSessionTerminate().userSessionTerminate(((AdapterActiveSessions) item).getItem().getSessionId());
                            }
                        }).show();
                    }
                } else {
                    final int size = list.size();
                    if (size > 1) {
                        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.active_session_all_title).content(R.string.active_session_all_content).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                v.setVisibility(View.GONE); // click on AdapterActiveSessionsHeader

                                for (int i = 0; i < size; i++) {
                                    if (!list.get(i).isCurrent()) {
                                        new RequestUserSessionTerminate().userSessionTerminate(list.get(i).getSessionId());
                                    }
                                }
                            }
                        }).show();
                    }
                }
                return false;
            }
        });
    }

    public void itemAdapter() {
        boolean b = false;

        for (StructSessions s : structItems) {

            if (s.isCurrent()) {
                fastItemAdapter.add(new AdapterActiveSessions(s).withIdentifier(s.getSessionId()));
            } else if (!b) {
                fastItemAdapter.add(new AdapterActiveSessionsHeader(structItems).withIdentifier(SUID.id().get()));
                fastItemAdapter.add(new AdapterActiveSessions(s).withIdentifier(s.getSessionId()));
                b = true;
            } else if (b) {
                fastItemAdapter.add(new AdapterActiveSessions(s).withIdentifier(s.getSessionId()));
            }
        }

        prgWaiting.setVisibility(View.GONE);
    }
}
