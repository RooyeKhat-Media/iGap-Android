/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperSaveFile;
import net.iGap.helper.HelperString;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnColorChangedListenerSelect;
import net.iGap.module.AttachFile;
import net.iGap.module.ColorPiker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * for draw a paint and send to other user or save in device folder
 */

public class ActivityPaint extends ActivityEnhanced {

    AttachFile attachFile;
    private int minBrushSize = 12;
    private int brushSize = minBrushSize;
    private int paintColor = Color.BLACK;
    private boolean captureimage = false;
    private Bitmap mBitmap;
    private Dialog dialogBrush;
    private DrawingView drawingView;
    private Paint paint;
    private FrameLayout frameLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_paint);

        attachFile = new AttachFile(this);

        init();
    }

    void init() {

        drawingView = new DrawingView(ActivityPaint.this, false, null);
        frameLayout = (FrameLayout) findViewById(R.id.frame);
        frameLayout.addView(drawingView);

        setPaintColor();

        TextView tvclose = (TextView) findViewById(R.id.textView_close);
        tvclose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showAlertDialogExit();
            }
        });

        TextView tvRefesh = (TextView) findViewById(R.id.textView_new);
        tvRefesh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                drawingView = new DrawingView(ActivityPaint.this, false, null);
                frameLayout.removeAllViews();
                frameLayout.addView(drawingView);
            }
        });

        TextView tvSave = (TextView) findViewById(R.id.textView_save);
        tvSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                savePicToFile(false);
            }
        });

        TextView tvSend = (TextView) findViewById(R.id.textView_send);
        tvSend.setTextColor(Color.parseColor(G.attachmentColor));
        tvSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                savePicToFile(true);
            }
        });

        TextView tvEraser = (TextView) findViewById(R.id.textView_erase);
        tvEraser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setPaintClear();
            }
        });
        Typeface typefaceEraser = G.typeface_Fontico;
        tvEraser.setTypeface(typefaceEraser);

        TextView tvPaint = (TextView) findViewById(R.id.textView_paintsize);
        tvPaint.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                initDialogBrush();
                setPaintColor();
            }
        });

        TextView tvColor = (TextView) findViewById(R.id.textView_color);
        tvColor.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showDialogSelectColor();
            }
        });

        TextView tvSience = (TextView) findViewById(R.id.textView_sience);
        tvSience.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                captureimage = false;
                try {
                    attachFile.requestOpenGalleryForImageSingleSelect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        TextView tvCamera = (TextView) findViewById(R.id.textView_camera);
        tvCamera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                captureimage = true;
                try {
                    attachFile.requestTakePicture();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDialogSelectColor() {
        ColorPiker d = new ColorPiker(ActivityPaint.this, paintColor, new OnColorChangedListenerSelect() {

            @Override
            public void colorChanged(String key, int color) {

                if (key.equals("ok")) {
                    paintColor = color;
                    setPaintColor();
                }
            }

            @Override
            public void Confirmation(Boolean result) {

            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        @SuppressWarnings("deprecation") int screenWidth = display.getWidth();

        d.getWindow().setLayout(screenWidth, WindowManager.LayoutParams.WRAP_CONTENT);
        d.setCancelable(true);

        d.show();
    }

    void initDialogBrush() {

        dialogBrush = new Dialog(ActivityPaint.this);
        dialogBrush.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBrush.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBrush.setContentView(R.layout.dialog_brush_paint);
        dialogBrush.setCancelable(true);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialogBrush.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.BOTTOM;
        // lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialogBrush.show();
        dialogBrush.getWindow().setAttributes(layoutParams);

        final TextView tvBrushSize = (TextView) dialogBrush.findViewById(R.id.textView_brush_size);

        SeekBar skBrushSize = (SeekBar) dialogBrush.findViewById(R.id.seekBar_brush_size);
        skBrushSize.setProgress(brushSize - minBrushSize);
        skBrushSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setPaintColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tvBrushSize.setText(brushSize + "");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brushSize = progress + minBrushSize;
                tvBrushSize.setText(brushSize + "");
            }
        });
    }

    void setPaintColor() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(paintColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(brushSize);
    }

    void setPaintClear() {

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.parseColor("#ffffff"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(50);
    }

    void savePicToFile(Boolean send) {

        String fileName = "paint_" + HelperString.getRandomFileName(3) + ".png";

        File dir = new File(G.DIR_IMAGES);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File f = new File(dir, fileName);

        int x = 0;
        while (f.exists()) {
            f = new File(G.DIR_IMAGES, fileName);
            x++;
        }

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (send) {
            Intent data = new Intent();
            data.setData(Uri.parse(f.getAbsolutePath()));
            setResult(Activity.RESULT_OK, data);
            finish();
        } else {
            HelperSaveFile.savePicToGallery(f.getAbsolutePath(), true);
            //Toast.makeText(getApplicationContext(), getString(R.string.picture_is_saved_en), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case AttachFile.request_code_image_from_gallery_single_select:

                    try {
                        setImageToBitmap(data);
                    } catch (Exception e) {
                        Toast.makeText(ActivityPaint.this, getString(R.string.cannot_load_data_en), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    break;
                case AttachFile.request_code_TAKE_PICTURE:
                    ImageHelper.correctRotateImage(AttachFile.imagePath, false);

                    try {
                        setImageToBitmap1();
                    } catch (Exception e) {
                        Toast.makeText(ActivityPaint.this, getString(R.string.cannot_load_data_en), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void showAlertDialogExit() {

        new MaterialDialog.Builder(ActivityPaint.this).title(getString(R.string.do_you_want_exit)).positiveText(R.string.yes).negativeText(R.string.no).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);

                ActivityPaint.this.finish();
            }
        }).show();
    }

    private void setImageToBitmap(Intent data) {
        Uri selectedImageUri = data.getData();
        drawingView = new DrawingView(ActivityPaint.this, true, selectedImageUri);
        frameLayout.removeAllViews();
        frameLayout.addView(drawingView);
    }

    private void setImageToBitmap1() {
        drawingView = new DrawingView(ActivityPaint.this, true, Uri.parse(AttachFile.imagePath));
        frameLayout.removeAllViews();
        frameLayout.addView(drawingView);
    }

    public class DrawingView extends View {

        private static final float TOUCH_TOLERANCE = 4;
        public int width;
        public int height;
        Context context;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        private Paint circlePaint;
        private Path circlePath;
        private Boolean fromGallery;
        private Uri PicAddress;
        private float mX, mY;

        public DrawingView(Context c, boolean FromGallery, Uri picAddress) {
            super(c);
            context = c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
            this.fromGallery = FromGallery;
            this.PicAddress = picAddress;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mBitmap.eraseColor(Color.WHITE);
            mCanvas = new Canvas(mBitmap);

            if (fromGallery) {

                if (!captureimage) {
                    String[] projection = {MediaColumns.DATA};
                    Cursor cursor = getContentResolver().query(PicAddress, projection, null, null, null);
                    if (cursor != null) {
                        try {
                            int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
                            cursor.moveToFirst();
                            String selectedImagePath = cursor.getString(column_index);

                            File imgFile = new File(selectedImagePath);
                            Bitmap b = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            if (b != null) {
                                b = Bitmap.createScaledBitmap(b, w, h, false);
                                Paint paint = new Paint();
                                mCanvas.drawBitmap(b, 0, 0, paint);
                            }
                        } finally {
                            cursor.close();
                        }
                    }
                } else {
                    String filePath = AttachFile.imagePath;
                    File imgFile = new File(filePath);
                    Bitmap b = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if (b != null) {
                        b = Bitmap.createScaledBitmap(b, w, h, false);
                        Paint paint = new Paint();
                        mCanvas.drawBitmap(b, 0, 0, paint);
                    }
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, paint);
            canvas.drawPath(circlePath, circlePaint);
        }

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {

            mPath.lineTo(mX, mY);

            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, paint);
            // kill this so we don't double draw
            mPath.reset();

            mCanvas.drawPoint(mX, mY, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
}
