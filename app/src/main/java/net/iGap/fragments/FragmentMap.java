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

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperSetAction;
import net.iGap.helper.HelperString;
import net.iGap.proto.ProtoGlobal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static net.iGap.R.id.mf_fragment_map_view;

public class FragmentMap extends BaseFragment implements OnMapReadyCallback {

    public static String Latitude = "latitude";
    public static String Longitude = "longitude";
    public static String PosoitionMode = "positionMode";
    public static String flagFragmentMap = "FragmentMap";
    Marker marker;
    private GoogleMap mMap;
    private Double latitude;
    private Double longitude;
    private Mode mode;

    public static FragmentMap getInctance(Double latitude, Double longitude, Mode mode) {

        FragmentMap fragmentMap = new FragmentMap();

        Bundle bundle = new Bundle();
        bundle.putDouble(FragmentMap.Latitude, latitude);
        bundle.putDouble(FragmentMap.Longitude, longitude);
        bundle.putSerializable(PosoitionMode, mode);

        fragmentMap.setArguments(bundle);

        return fragmentMap;
    }

    public static String saveBitmapToFile(Bitmap bitmap) {

        String result = "";

        try {
            if (bitmap == null) return result;

            String fileName = "location_" + HelperString.getRandomFileName(3) + ".png";
            File file = new File(G.DIR_TEMP, fileName);

            OutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            result = file.getPath();
        } catch (FileNotFoundException e) {

        }

        return result;
    }

    public static void loadImageFromPosition(double latiude, double longitude, OnGetPicture listener) {

        String urlstr = "https://maps.googleapis.com/maps/api/staticmap?center=" + latiude + "," + longitude + "&zoom=16&size=480x240" + "&markers=color:red%7Clabel:S%7C" + latiude + "," + longitude + "&maptype=roadmap&key=" + G.context.getString(R.string.google_maps_key);

        new DownloadImageTask(listener).execute(urlstr);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.map_fragment, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle saveInctanceState) {
        super.onViewCreated(view, saveInctanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {

            latitude = bundle.getDouble(FragmentMap.Latitude);
            longitude = bundle.getDouble(FragmentMap.Longitude);
            mode = (Mode) bundle.getSerializable(PosoitionMode);
            if (G.onHelperSetAction != null) {
                G.onHelperSetAction.onAction(ProtoGlobal.ClientAction.SENDING_LOCATION);
            }

            initComponent(view);
        } else {
            close();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        HelperSetAction.sendCancel(FragmentChat.messageId);
    }

    private void close() {
        //  mActivity.getSupportFragmentManager().beginTransaction().remove(FragmentMap.this).commit();

        popBackStackFragment();
    }

    private void initComponent(View view) {

        SupportMapFragment mapFragment = new SupportMapFragment();

        //G.fragmentActivity.getSupportFragmentManager()
        //    .beginTransaction()
        //    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
        //    .replace(mf_fragment_map_view, mapFragment, null)
        //    .commit();

        new HelperFragment(mapFragment).setReplace(false).setAddToBackStack(false).setResourceContainer(mf_fragment_map_view).load();

        mapFragment.getMapAsync(FragmentMap.this);

        Button btnSendPosition = (Button) view.findViewById(R.id.mf_btn_send_position);
        btnSendPosition.setBackgroundColor(Color.parseColor(G.appBarColor));

        if (mode == Mode.sendPosition) {

            btnSendPosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (latitude == null || longitude == null) {

                        G.currentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.set_position), false);

                            }
                        });
                    } else {

                        try {
                            mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                                @Override
                                public void onSnapshotReady(Bitmap bitmap) {

                                    String path = saveBitmapToFile(bitmap);

                                    close();

                                    if (path.length() > 0) {
                                        //ActivityChat activity = (ActivityChat) mActivity;
                                        //activity.sendPosition(latitude, longitude, path);

                                        if (G.iSendPositionChat != null) {
                                            G.iSendPositionChat.send(latitude, longitude, path);
                                        }

                                    }
                                }
                            });
                        } catch (Exception e) {
                            close();
                            //ActivityChat activity = (ActivityChat) mActivity;
                            //activity.sendPosition(latitude, longitude, null);

                            if (G.iSendPositionChat != null) {
                                G.iSendPositionChat.send(latitude, longitude, null);
                            }
                        }
                    }
                }
            });
        } else if (mode == Mode.seePosition) {
            btnSendPosition.setVisibility(View.GONE);
        }
    }

    //****************************************************************************************************

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        final boolean[] updatePosition = {true};

        //if device has not gps permision in androi 6+ return form map
        if (ActivityCompat.checkSelfPermission(G.fragmentActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(G.fragmentActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mMap.setMyLocationEnabled(true);

        LatLng latLng = new LatLng(latitude, longitude);

        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("position"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        if (mode == Mode.sendPosition) {

            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {

                    updatePosition[0] = false;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    if (marker != null) {
                        marker.remove();
                    }

                    LatLng la = new LatLng(latitude, longitude);

                    marker = mMap.addMarker(new MarkerOptions().position(la).title("position"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(la, 16));

                    mMap.setOnMyLocationChangeListener(null);
                }
            });

            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {

                    if (updatePosition[0]) {
                        Display display = G.currentActivity.getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);

                        LatLng mapCenter = mMap.getProjection().fromScreenLocation(new Point(size.x / 2, size.y / 2));
                        latitude = mapCenter.latitude;
                        longitude = mapCenter.longitude;

                        if (marker != null) {
                            marker.remove();
                        }

                        marker = mMap.addMarker(new MarkerOptions().position(mapCenter).title("position"));
                    } else {
                        updatePosition[0] = true;
                    }
                }
            });

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    updatePosition[0] = false;
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;

                    if (marker != null) {
                        marker.remove();
                    }
                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title("position"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                }
            });

            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {

                    Location location = mMap.getMyLocation();

                    if (location == null) return false;

                    updatePosition[0] = false;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    if (marker != null) {
                        marker.remove();
                    }

                    LatLng la = new LatLng(latitude, longitude);

                    marker = mMap.addMarker(new MarkerOptions().position(la).title("position"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(la, 16));

                    return false;
                }
            });
        }
    }

    public enum Mode {
        sendPosition, seePosition;
    }

    public interface OnGetPicture {

        void getBitmap(Bitmap bitmap);
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        OnGetPicture listener;

        public DownloadImageTask(OnGetPicture listener) {
            this.listener = listener;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            if (listener != null) {
                listener.getBitmap(result);
            }
        }
    }
}
