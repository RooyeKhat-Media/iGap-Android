package net.iGap.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;
import com.yalantis.ucrop.UCrop;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.filterImage.FragmentFilterImage;
import net.iGap.helper.HelperFragment;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.EmojiEditTextE;
import net.iGap.module.MaterialDesignTextView;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static net.iGap.R.id.ac_ll_parent;
import static net.iGap.module.AndroidUtils.suitablePath;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEditImage extends BaseFragment {

    private final static String PATH = "PATH";
    private final static String ISCHAT = "ISCHAT";
    private final static String ISNICKNAMEPAGE = "ISNICKNAMEPAGE";
    private String path;
    private ImageView imgEditImage;
    public static UpdateImage updateImage;
    private EmojiEditTextE edtChat;
    private MaterialDesignTextView imvSmileButton;
    private boolean isEmojiSHow = false;
    private boolean initEmoji = false;
    private EmojiPopup emojiPopup;
    private String SAMPLE_CROPPED_IMAGE_NAME;
    private boolean isChatPage = true;
    private boolean isNicknamePage = false;
    public static CompleteEditImage completeEditImage;
    private int num = 0;

    public FragmentEditImage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_image, container, false);
    }

    public static FragmentEditImage newInstance(String path, boolean isChatPage, boolean isNicknamePage) {
        Bundle args = new Bundle();
        args.putString(PATH, path);
        args.putBoolean(ISCHAT, isChatPage);
        args.putBoolean(ISNICKNAMEPAGE, isNicknamePage);
        FragmentEditImage fragment = new FragmentEditImage();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            path = bundle.getString(PATH);
            isChatPage = bundle.getBoolean(ISCHAT);
            isNicknamePage = bundle.getBoolean(ISNICKNAMEPAGE);
        }

        if (path == null) {
            if (G.fragmentManager != null) {
                G.fragmentManager.beginTransaction().remove(FragmentEditImage.this).commit();
            }
            return;
        }

        imgEditImage = (ImageView) view.findViewById(R.id.imgEditImage);

        TextView txtEditImage = (TextView) view.findViewById(R.id.txtEditImage);
        txtEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.closeKeyboard(v);
                if (!isNicknamePage) {
                    new HelperFragment(FragmentFilterImage.newInstance(path)).setReplace(false).load();
                } else {
                    FragmentFilterImage fragment = FragmentFilterImage.newInstance(path);
                    G.fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                }
            }
        });
        G.imageLoader.displayImage(suitablePath(path), imgEditImage);

        updateImage = new UpdateImage() {
            @Override
            public void result(String pathImageFilter) {

                path = pathImageFilter;
                G.imageLoader.displayImage(suitablePath(path), imgEditImage);
            }
        };


        view.findViewById(R.id.pu_ripple_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.closeKeyboard(v);
                new HelperFragment(FragmentEditImage.this).remove();
                if (G.openBottomSheetItem != null && isChatPage)
                    G.openBottomSheetItem.openBottomSheet(false);
            }
        });

        view.findViewById(R.id.pu_txt_crop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.closeKeyboard(v);
                String newPath = "file://" + path;
                String fileNameWithOutExt = path.substring(path.lastIndexOf("/"));
                String extension = path.substring(path.lastIndexOf("."));
                SAMPLE_CROPPED_IMAGE_NAME = fileNameWithOutExt.substring(0, fileNameWithOutExt.lastIndexOf(".")) + num + extension;
                num++;
                Uri uri = Uri.parse(newPath);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    UCrop.Options options = new UCrop.Options();
                    options.setStatusBarColor(ContextCompat.getColor(G.context, R.color.black));
                    options.setToolbarColor(ContextCompat.getColor(G.context, R.color.black));
                    options.setCompressionQuality(80);
                    options.setFreeStyleCropEnabled(true);

                    UCrop.of(uri, Uri.fromFile(new File(G.DIR_IMAGES, SAMPLE_CROPPED_IMAGE_NAME)))
                            .withOptions(options)
                            .useSourceImageAspectRatio()
                            .start(G.context, FragmentEditImage.this);
                } else {
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMinCropResultSize(120, 120)
                            .setAutoZoomEnabled(false)
                            .setInitialCropWindowPaddingRatio(.08f) // padding window from all
                            .setBorderCornerLength(50)
                            .setBorderCornerOffset(0)
                            .setAllowCounterRotation(true)
                            .setBorderCornerThickness(8.0f)
                            .setShowCropOverlay(true)
                            .setAspectRatio(1, 1)
                            .setFixAspectRatio(false)
                            .setBorderCornerColor(getResources().getColor(R.color.whit_background))
                            .setBackgroundColor(getResources().getColor(R.color.ou_background_crop))
                            .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                            .start(G.fragmentActivity, FragmentEditImage.this);
                }

            }
        });

        imvSmileButton = (MaterialDesignTextView) view.findViewById(R.id.chl_imv_smile_button);

        edtChat = (EmojiEditTextE) view.findViewById(R.id.chl_edt_chat);
        edtChat.requestFocus();

        edtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmojiSHow) {

                    imvSmileButton.performClick();
                }
            }
        });


        imvSmileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!initEmoji) {
                    initEmoji = true;
                    setUpEmojiPopup(view);
                }

                emojiPopup.toggle();
            }
        });

        ViewGroup layoutCaption = view.findViewById(R.id.layout_caption);
        MaterialDesignTextView txtSet = view.findViewById(R.id.txtSet);
        MaterialDesignTextView imvSendButton = (MaterialDesignTextView) view.findViewById(R.id.pu_txt_sendImage);


        if (isChatPage) {
            layoutCaption.setVisibility(View.VISIBLE);
            imvSendButton.setVisibility(View.VISIBLE);
            txtSet.setVisibility(View.GONE);
        } else {
            txtSet.setVisibility(View.VISIBLE);
            layoutCaption.setVisibility(View.GONE);
            imvSendButton.setVisibility(View.GONE);
        }

        txtSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                completeEditImage.result(path, "");

                new HelperFragment(FragmentEditImage.this).remove();
                AndroidUtils.closeKeyboard(v);


            }
        });

        imvSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelperFragment(FragmentEditImage.this).remove();
                completeEditImage.result(path, edtChat.getText().toString());
                AndroidUtils.closeKeyboard(v);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            path = AttachFile.getFilePathFromUri(resultUri);
//            G.imageLoader.displayImage(path, imgEditImage);
            imgEditImage.setImageURI(Uri.parse(path));
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) { // result for crop
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                path = result.getUri().getPath();
                imgEditImage.setImageURI(Uri.parse(path));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
            }
        }

    }

    public interface UpdateImage {
        void result(String path);
    }

    private void setUpEmojiPopup(View view) {
        emojiPopup = EmojiPopup.Builder.fromRootView(view.findViewById(ac_ll_parent)).setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {

            @Override
            public void onEmojiBackspaceClick(View v) {

            }
        }).setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
            @Override
            public void onEmojiPopupShown() {
                changeEmojiButtonImageResource(R.string.md_black_keyboard_with_white_keys);
                isEmojiSHow = true;
            }
        }).setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
            @Override
            public void onKeyboardOpen(final int keyBoardHeight) {

            }
        }).setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
            @Override
            public void onEmojiPopupDismiss() {
                changeEmojiButtonImageResource(R.string.md_emoticon_with_happy_face);
                isEmojiSHow = false;
            }
        }).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                emojiPopup.dismiss();
            }
        }).build(edtChat);
    }

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        imvSmileButton.setText(drawableResourceId);
    }

    public interface CompleteEditImage {
        void result(String path, String message);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    AndroidUtils.closeKeyboard(v);
                    new HelperFragment(FragmentEditImage.this).remove();
                    if (G.openBottomSheetItem != null && isChatPage)
                        G.openBottomSheetItem.openBottomSheet(false);
                    return true;
                }
                return false;
            }
        });
    }
}
