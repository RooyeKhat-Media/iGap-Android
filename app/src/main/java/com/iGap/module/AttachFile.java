/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


package com.iGap.module;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityExplorer;
import com.iGap.activities.ActivityPaint;
import com.iGap.helper.HelperPermision;
import com.iGap.helper.HelperString;
import com.iGap.helper.ImageHelper;
import com.iGap.interfaces.OnComplete;
import com.iGap.interfaces.OnGetPermission;
import com.iGap.proto.ProtoGlobal;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.iGap.G.onHelperSetAction;

public class AttachFile {

    public static final int request_code_TAKE_PICTURE = 10;
    public static final int request_code_image_from_gallery_single_select = 11;
    public static final int request_code_VIDEO_CAPTURED = 12;
    public static final int request_code_pic_audi = 13;
    public static final int request_code_pic_file = 14;
    public static final int request_code_contact_phone = 15;
    public static final int request_code_position = 16;
    public static final int request_code_paint = 17;
    public static final int MEDIA_TYPE_IMAGE = 18;
    public static final int requestOpenGalleryForImageMultipleSelect = 19;
    public static final int requestOpenGalleryForVideoMultipleSelect = 20;
    public static final int request_code_open_document = 21;
    public static final int request_code_trim_video = 22;

    private PopupWindow popupWindow;


    public static boolean isInAttach = false;
    OnComplete complete;
    private Context context;
    private LocationManager locationManager;
    private ProgressDialog pd;
    private Boolean sendPosition = false;
    public static String imagePath = "";
    public static Uri imageUri;

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {

            if (sendPosition) {
                sendPosition = false;

                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }

                location.getLatitude();
                location.getLongitude();

                String position = String.valueOf(location.getLatitude()) +
                        "," + String.valueOf(location.getLongitude());

                if (complete != null) complete.complete(true, position, "");
            }

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(locationListener);
        }
    };

    //*************************************************************************************************************

    public AttachFile(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
    }

    public static String getFilePathFromUri(Uri uri) {

        if (uri == null) {
            return null;
        }

        String path;

        if (uri.getScheme() != null && uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            path = FileUtils.getPath(G.context, uri);
        } else {
            path = uri.getPath();
        }

        return path;
    }

    public void requestPaint() throws IOException {

        HelperPermision.getStoragePermision(context, new OnGetPermission() {
            @Override
            public void Allow() {
                Intent intent = new Intent(context, ActivityPaint.class);
                ((Activity) context).startActivityForResult(intent, request_code_paint);
                onHelperSetAction.onAction(ProtoGlobal.ClientAction.PAINTING);
            }

            @Override
            public void deny() {

            }
        });
    }

    public void requestTakePicture() throws IOException {

        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
            Toast.makeText(context, context.getString(R.string.device_dosenot_camera_en), Toast.LENGTH_SHORT).show();
            return;
        }


        HelperPermision.getCameraPermission(context, new OnGetPermission() {
            @Override
            public void Allow() throws IOException {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    dispatchTakePictureIntent();
                } else {
                    Uri outPath = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, 0);

                    if (outPath != null) {
                        imagePath = outPath.getPath();
                        imageUri = outPath;
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPath);
                        ((Activity) context).startActivityForResult(intent, request_code_TAKE_PICTURE);
                        isInAttach = true;
                    }
                }
            }

            @Override
            public void deny() {

            }
        });
    }

    private Uri getOutputMediaFileUri(int type, int camera) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && camera == 0) {
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", getOutputMediaFile(type));
        } else {
            return Uri.fromFile(getOutputMediaFile(type));
        }
    }

    //=================================== Start Android 7
    public static String mCurrentPhotoPath;

    public void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(G.context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                ((Activity) context).startActivityForResult(takePictureIntent, request_code_TAKE_PICTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void onClickGallery() {
        List<Intent> targets = new ArrayList<>();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        List<ResolveInfo> candidates = context.getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo candidate : candidates) {
            String packageName = candidate.activityInfo.packageName;
            if (!packageName.equals("com.google.android.apps.photos") && !packageName.equals("com.google.android.apps.plus") && !packageName.equals("com.android.documentsui")) {
                Intent iWantThis = new Intent();
                iWantThis.setType("image/*");
                iWantThis.setAction(Intent.ACTION_PICK);
                iWantThis.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                iWantThis.setPackage(packageName);
                targets.add(iWantThis);
            }
        }
        if (targets.size() > 0) {
            Intent chooser = Intent.createChooser(targets.remove(0), "Select Picture");
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[targets.size()]));
            ((Activity) context).startActivityForResult(chooser, request_code_image_from_gallery_single_select);
        } else {
            Intent intent1 = new Intent(Intent.ACTION_PICK);
            intent1.setType("image/*");
            ((Activity) context).startActivityForResult(Intent.createChooser(intent1, "Select Picture"), request_code_image_from_gallery_single_select);
        }
    }

    //=================================== End Android 7

    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(G.DIR_IMAGES);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();

            //if (!mediaStorageDir.mkdirs()) {
            //    return null;
            //}
        }

        File mediaFile;

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "image_" + HelperString.getRandomFileName(3) + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    //*************************************************************************************************************

    //*************************************************************************************************************
    public void requestOpenGalleryForImageMultipleSelect() throws IOException {

        //this code use for open galary for image and video together
        //Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://media/internal/images/media"));
        //((Activity) context).startActivityForResult(intent, request_code_media_from_gallery);


        HelperPermision.getStoragePermision(context, new OnGetPermission() {
            @Override
            public void Allow() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                ((Activity) context).startActivityForResult(intent, requestOpenGalleryForImageMultipleSelect);
                onHelperSetAction.onAction(ProtoGlobal.ClientAction.SENDING_IMAGE);
                isInAttach = true;
            }

            @Override
            public void deny() {

            }
        });
    }

    //*************************************************************************************************************
    public void requestOpenGalleryForVideoMultipleSelect() throws IOException {

        HelperPermision.getStoragePermision(context, new OnGetPermission() {
            @Override
            public void Allow() {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                ((Activity) context).startActivityForResult(intent, requestOpenGalleryForVideoMultipleSelect);
                onHelperSetAction.onAction(ProtoGlobal.ClientAction.SENDING_VIDEO);
                isInAttach = true;
            }

            @Override
            public void deny() {

            }
        });


    }

    //*************************************************************************************************************
    public void requestOpenGalleryForImageSingleSelect() throws IOException {

        HelperPermision.getStoragePermision(context, new OnGetPermission() {
            @Override
            public void Allow() {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                ((Activity) context).startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_picture_en)), request_code_image_from_gallery_single_select);
                isInAttach = true;
            }

            @Override
            public void deny() {

            }
        });
    }

    //*************************************************************************************************************

    //*************************************************************************************************************
    public void requestVideoCapture() throws IOException {

        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
            Toast.makeText(context, context.getString(R.string.device_dosenot_camera_en), Toast.LENGTH_SHORT).show();
            return;
        }

        HelperPermision.getCameraPermission(context, new OnGetPermission() {
            @Override
            public void Allow() {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                ((Activity) context).startActivityForResult(intent, request_code_VIDEO_CAPTURED);
                isInAttach = true;
            }

            @Override
            public void deny() {

            }
        });


    }

    public void showDialogOpenCamera(View view, final ProgressBar prgWaiting) {

        new MaterialDialog.Builder(context)
                .items(R.array.capture)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:

                                try {
                                    requestTakePicture();
                                    onHelperSetAction.onAction(ProtoGlobal.ClientAction.CAPTURING_IMAGE);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();

                                if (prgWaiting != null) {
                                    prgWaiting.setVisibility(View.VISIBLE);
                                }

                                break;
                            case 1:
                                try {
                                    requestVideoCapture();
                                    onHelperSetAction.onAction(ProtoGlobal.ClientAction.CAPTURING_VIDEO);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .negativeText(R.string.B_cancel)
                .show();
    }

    //*************************************************************************************************************

    public void requestPickAudio() throws IOException {
        //Intent intent = new Intent();
        //intent.setActionTyping(Intent.ACTION_PICK);
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //intent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        //((Activity) context).startActivityForResult(intent, request_code_pic_audi);


        HelperPermision.getStoragePermision(context, new OnGetPermission() {
            @Override
            public void Allow() {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                ((Activity) context).startActivityForResult(intent, request_code_pic_audi);
                onHelperSetAction.onAction(ProtoGlobal.ClientAction.SENDING_AUDIO);

                isInAttach = true;
            }

            @Override
            public void deny() {

            }
        });
    }

    public void requestPickFile() throws IOException {
        HelperPermision.getStoragePermision(context, new OnGetPermission() {
            @Override
            public void Allow() {
                Intent intent = new Intent(context, ActivityExplorer.class);
                ((Activity) context).startActivityForResult(intent, request_code_pic_file);
                onHelperSetAction.onAction(ProtoGlobal.ClientAction.SENDING_FILE);
            }

            @Override
            public void deny() {

            }
        });
    }

    public void requestPickContact() throws IOException {

        HelperPermision.getContactPermision(context, new OnGetPermission() {
            @Override
            public void Allow() {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                ((Activity) context).startActivityForResult(intent, request_code_contact_phone);
                onHelperSetAction.onAction(ProtoGlobal.ClientAction.CHOOSING_CONTACT);
                isInAttach = true;
            }

            @Override
            public void deny() {

            }
        });

    }

    public void requestGetPosition(OnComplete complete) throws IOException {

        this.complete = complete;

        HelperPermision.getLocationPermission(context, new OnGetPermission() {
            @Override
            public void Allow() {
                getPosition();

            }

            @Override
            public void deny() {

            }
        });
    }

    private void getPosition() {

        try {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showSettingsAlert();
            } else {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                //G.onHelperSetAction.onAction(ProtoGlobal.ClientAction.SENDING_LOCATION);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (location != null) {
                    location.getLatitude();
                    location.getLongitude();

                    String position = String.valueOf(location.getLatitude()) +
                            "," + String.valueOf(location.getLongitude());

                    if (complete != null) complete.complete(true, position, "");
                } else {
                    sendPosition = true;
                    pd = new ProgressDialog(context);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage(context.getString(R.string.just_wait_en));
                    pd.setIndeterminate(false);
                    pd.setCancelable(true);
                    pd.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestOpenDocumentFolder() throws IOException {


        HelperPermision.getStoragePermision(context, new OnGetPermission() {
            @Override
            public void Allow() {
                Intent intent = new Intent(context, ActivityExplorer.class);
                intent.putExtra("Mode", "documnet");
                ((Activity) context).startActivityForResult(intent, request_code_open_document);
                onHelperSetAction.onAction(ProtoGlobal.ClientAction.SENDING_DOCUMENT);
            }

            @Override
            public void deny() {

            }
        });

    }

    //*************************************************************************************************************

    void showSettingsAlert() {

        new MaterialDialog.Builder(context).title(context.getString(R.string.do_you_want_to_turn_on_gps))
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        turnOnGps();
                    }
                })
                .show();
    }

    private void turnOnGps() {
        ((Activity) context).startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), request_code_position);
        isInAttach = true;
    }

    //*************************************************************************************************************

    public static ArrayList<String> getClipData(ClipData clipData) {

        if (clipData != null) {
            ArrayList<String> list = new ArrayList<>();

            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                if (item.getUri() == null) {
                    continue;
                }
                String path = getFilePathFromUri(item.getUri());
                list.add(path);
            }

            if (list.size() < 1) return null;

            return list;
        }

        return null;
    }

    public String getFileName(String path) {

        if (path == null) return "";
        if (path.length() < 1) return "";

        String filename = path.substring(path.lastIndexOf("/") + 1);

        return filename;
    }

    public String saveGalleryPicToLocal(String galleryPath) {

            String result = "";
            if (galleryPath == null) return "";

        if (ImageHelper.isNeedToCompress(new File(galleryPath))) {

            Bitmap bitmap = ImageHelper.decodeFile(new File(galleryPath));
            bitmap = ImageHelper.correctRotate(galleryPath, bitmap);

            if (bitmap != null) {
                result = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, 1).getPath();
                ImageHelper.SaveBitmapToFile(result, bitmap);
            }

            return result;
        } else {

            return galleryPath;
        }


    }
}
