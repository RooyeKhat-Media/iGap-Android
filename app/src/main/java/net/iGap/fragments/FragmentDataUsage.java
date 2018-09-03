package net.iGap.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.DataUsageAdapter;
import net.iGap.helper.HelperDataUsage;
import net.iGap.interfaces.DataUsageListener;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.structs.DataUsageStruct;
import net.iGap.realm.RealmDataUsage;

import java.util.ArrayList;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

public class FragmentDataUsage extends Fragment implements DataUsageListener {

    private ArrayList<DataUsageStruct> usageArrayList = new ArrayList<>();
    private long totalSendByte;
    private long totalReceivedByte;
    private boolean type;
    private DataUsageAdapter adapter;
    private TextView txtDataUsageHeader;
    private RippleView rippleDataUsage;
    private RelativeLayout rvMainDataUsage;

    public static FragmentDataUsage newInstance() {
        return new FragmentDataUsage();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_data_usage, container, false);
        txtDataUsageHeader = (TextView) view.findViewById(R.id.txtDataUsageHeader);

        rvMainDataUsage = (RelativeLayout) view.findViewById(R.id.rvMainDataUsage);
        rvMainDataUsage.setBackgroundColor(Color.parseColor(G.backgroundTheme_2));


        AppBarLayout appBarDataUsage = (AppBarLayout) view.findViewById(R.id.appBarDataUsage);
        appBarDataUsage.setBackgroundColor(Color.parseColor(G.appBarColor));

        rippleDataUsage = (RippleView) view.findViewById(R.id.dataUsage_ripple_back);

        type = getArguments().getBoolean("TYPE", false);

        initData(type);

        usageArrayList.add(new DataUsageStruct(1, 0, 0, 0, 0, "Total"));
        usageArrayList.add(new DataUsageStruct(2, 0, 0, 0, 0, "ClearData"));


        return view;
    }

    private void initData(boolean type) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmDataUsage> wifiRealmDataUsages;
        RealmResults<RealmDataUsage> dataRealmDataUsages;
        if (type) {
            txtDataUsageHeader.setText(getResources().getString(R.string.wifi_data_usage));
            totalReceivedByte = 0;
            totalSendByte = 0;
            wifiRealmDataUsages = realm.where(RealmDataUsage.class).equalTo("connectivityType", true).findAll();
            if (wifiRealmDataUsages.size() == 0)
                wifiRealmDataUsages = realm.where(RealmDataUsage.class).findAll();

            for (RealmDataUsage usage : wifiRealmDataUsages) {
                usageArrayList.add(new DataUsageStruct(0, usage.getDownloadSize(), usage.getUploadSize(), usage.getNumUploadedFiles(), usage.getNumDownloadedFile(), usage.getType()));
                totalReceivedByte += usage.getDownloadSize();
                totalSendByte += usage.getUploadSize();
            }

        } else {
            txtDataUsageHeader.setText(getResources().getString(R.string.mobile_data_usage));
            totalReceivedByte = 0;
            totalSendByte = 0;
            dataRealmDataUsages = realm.where(RealmDataUsage.class).equalTo("connectivityType", false).findAll();
            if (dataRealmDataUsages.size() == 0)
                dataRealmDataUsages = realm.where(RealmDataUsage.class).findAll();
            for (RealmDataUsage usage : dataRealmDataUsages) {
                usageArrayList.add(new DataUsageStruct(0, usage.getDownloadSize(), usage.getUploadSize(), usage.getNumUploadedFiles(), usage.getNumDownloadedFile(), usage.getType()));
                totalReceivedByte += usage.getDownloadSize();
                totalSendByte += usage.getUploadSize();
            }
        }
        realm.close();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rcDataUsage = view.findViewById(R.id.rcDataUsage);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(G.currentActivity);
        adapter = new DataUsageAdapter(G.currentActivity, usageArrayList, totalReceivedByte, totalSendByte, type, this);
        rcDataUsage.setAdapter(adapter);
        rcDataUsage.setLayoutManager(layoutManager);

        rippleDataUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

    }

    @Override
    public void doClearDB(boolean type) {
        HelperDataUsage.clearUsageRealm(type);
        usageArrayList.clear();
        initData(type);
        adapter.notifyDataSetChanged();
    }
}
