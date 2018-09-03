package net.iGap.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanks.library.AnimateCheckBox;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;
import com.yalantis.ucrop.UCrop;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.fragments.filterImage.FragmentFilterImage;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.EmojiEditTextE;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.structs.StructBottomSheet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static net.iGap.module.AndroidUtils.closeKeyboard;
import static net.iGap.module.AndroidUtils.suitablePath;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEditImage extends BaseFragment {

    private final static String PATH = "PATH";
    private final static String ISCHAT = "ISCHAT";
    private final static String ISNICKNAMEPAGE = "ISNICKNAMEPAGE";
    private final static String SELECT_POSITION = "SLECT_POSITION";
    //    private String path;
    private int selectPosition = 0;
    //    private ImageView imgEditImage;
    private ViewPager viewPager;
    private AdapterViewPager mAdapter;
    private TextView txtEditImage;
    public static UpdateImage updateImage;
    private EmojiEditTextE edtChat;
    private TextView iconOk;
    private ViewGroup layoutCaption;
    private MaterialDesignTextView txtSet;
    private MaterialDesignTextView imvSendButton;
    private ViewGroup rootSend;
    private MaterialDesignTextView imvSmileButton;
    private boolean isEmojiSHow = false;
    private boolean initEmoji = false;
    private EmojiPopup emojiPopup;
    private String SAMPLE_CROPPED_IMAGE_NAME;
    private boolean isChatPage = true;
    private boolean isMultiItem = true;
    private boolean isNicknamePage = false;
    public static CompleteEditImage completeEditImage;
    private int num = 0;
    private TextView txtCountImage;
    private ArrayList<String> listPathString = new ArrayList<>();
    private AnimateCheckBox checkBox;
    public static HashMap<String, StructBottomSheet> textImageList = new HashMap<>();
    public static ArrayList<StructBottomSheet> itemGalleryList = new ArrayList<StructBottomSheet>();

    public FragmentEditImage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_image, container, false);
    }

    public static FragmentEditImage newInstance(String path, boolean isChatPage, boolean isNicknamePage, int selectPosition) {
        Bundle args = new Bundle();
        args.putString(PATH, path);
        args.putBoolean(ISCHAT, isChatPage);
        args.putBoolean(ISNICKNAMEPAGE, isNicknamePage);
        args.putInt(SELECT_POSITION, selectPosition);
        FragmentEditImage fragment = new FragmentEditImage();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        if (itemGalleryList == null || itemGalleryList.size() == 0) {
            if (G.fragmentManager != null) {
                G.fragmentManager.beginTransaction().remove(FragmentEditImage.this).commit();
            }
            return;
        }

        if (isChatPage) {

            layoutCaption.setVisibility(View.VISIBLE);
            imvSendButton.setVisibility(View.VISIBLE);
            txtSet.setVisibility(View.GONE);
            checkBox.setVisibility(View.VISIBLE);
            if (textImageList.size() > 0) {
                txtCountImage.setVisibility(View.VISIBLE);
                txtCountImage.setText(textImageList.size() + "");
            } else {
                txtCountImage.setVisibility(View.GONE);
            }
            if (itemGalleryList != null && itemGalleryList.size() == 1) {
                checkBox.setVisibility(View.GONE);
                txtCountImage.setVisibility(View.GONE);
                isMultiItem = false;

            }
        } else {
            txtSet.setVisibility(View.VISIBLE);
            layoutCaption.setVisibility(View.GONE);
            imvSendButton.setVisibility(View.GONE);
            checkBox.setVisibility(View.GONE);
            txtCountImage.setVisibility(View.GONE);
            isMultiItem = false;
        }

        /**
         *
         * check list size for show number of select item
         *
         */


        setViewPager();
        setCheckBoxItem();
        messageBox(view);


//        G.imageLoader.displayImage(suitablePath(path), imgEditImage);

        updateImage = new UpdateImage() {
            @Override
            public void result(String pathImageFilter) {

                serCropAndFilterImage(pathImageFilter);
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });


//                G.imageLoader.displayImage(suitablePath(path), imgEditImage);
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

                goToCropPage(v);
            }

        });


        txtSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (completeEditImage != null) {
                    completeEditImage.result(itemGalleryList.get(0).getPath(), "", null);
                }

                new HelperFragment(FragmentEditImage.this).remove();
                AndroidUtils.closeKeyboard(v);
            }
        });

        imvSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelperFragment(FragmentEditImage.this).remove();

                if (textImageList.size() == 0) {
                    setValueCheckBox(viewPager.getCurrentItem());
                }

                completeEditImage.result("", edtChat.getText().toString(), textImageList);
                AndroidUtils.closeKeyboard(v);
            }
        });

        isSoftKeyboardOpenOrNot(view);
    }

    private void isSoftKeyboardOpenOrNot(final View view) {

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = view.getRootView().getHeight() - view.getHeight();
                if (heightDiff > AndroidUtils.dpToPx(G.fragmentActivity, 200)) { // if more than 200 dp, it's probably a keyboard...
                    // ... do something here
                    rootSend.setVisibility(View.GONE);
                } else {
                    rootSend.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void messageBox(final View view) {
        if (textImageList.containsKey(itemGalleryList.get((itemGalleryList.size() - selectPosition - 1)).path)) {
            edtChat.setText(textImageList.get(itemGalleryList.get((itemGalleryList.size() - selectPosition - 1)).path).getText());
        } else {
            edtChat.setText("");
        }

        txtEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.closeKeyboard(v);
                if (!isNicknamePage) {
                    new HelperFragment(FragmentFilterImage.newInstance(itemGalleryList.get(viewPager.getCurrentItem()).path)).setReplace(false).load();
                } else {
                    FragmentFilterImage fragment = FragmentFilterImage.newInstance(itemGalleryList.get(viewPager.getCurrentItem()).path);
                    G.fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                }
            }
        });


        /**
         *
         * get message for each item
         *
         */

        iconOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String path = itemGalleryList.get(viewPager.getCurrentItem()).path;
                String message = edtChat.getText().toString();

                itemGalleryList.get(viewPager.getCurrentItem()).setSelected(false);
                checkBox.setChecked(true);
                checkBox.setUnCheckColor(G.context.getResources().getColor(R.color.green));

                StructBottomSheet item = new StructBottomSheet();
                item.setPath(path);
                item.setText(message);
                item.setId(itemGalleryList.get(viewPager.getCurrentItem()).getId());

                textImageList.put(path, item);
//                FragmentChat.listPathString.add(itemGalleryList.get(((itemGalleryList.size() - selectPosition) - 1)).path);
                if (textImageList.size() > 0 && isMultiItem) {
                    txtCountImage.setVisibility(View.VISIBLE);
                    txtCountImage.setText(textImageList.size() + "");
                } else {
                    txtCountImage.setVisibility(View.GONE);
                }
                closeKeyboard(v);
                v.setVisibility(View.GONE);

            }
        });


        edtChat.requestFocus();

        edtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmojiSHow) {

                    imvSmileButton.performClick();
                }
            }
        });

        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String oldPath = "";
                if (textImageList.containsKey(itemGalleryList.get(viewPager.getCurrentItem()).path)) {
                    oldPath = textImageList.get(itemGalleryList.get(viewPager.getCurrentItem()).path).getText();
                }
                if (!oldPath.equals(s.toString())) {
                    iconOk.setVisibility(View.VISIBLE);
                } else {
                    iconOk.setVisibility(View.GONE);
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


    }

    private void setCheckBoxItem() {
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMultiItem) setValueCheckBox(viewPager.getCurrentItem());
            }
        });

        if (itemGalleryList.get(viewPager.getCurrentItem()).isSelected) {
            checkBox.setChecked(false);
            checkBox.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));
        } else {
            checkBox.setChecked(true);
            checkBox.setUnCheckColor(G.context.getResources().getColor(R.color.green));
        }

    }

    private void setViewPager() {

        mAdapter = new AdapterViewPager(itemGalleryList);
        viewPager.setAdapter(mAdapter);

        viewPager.setCurrentItem((itemGalleryList.size() - selectPosition) - 1);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (itemGalleryList.get(position).isSelected) {
                    checkBox.setChecked(false);
                    checkBox.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));
                } else {
                    checkBox.setChecked(true);
                    checkBox.setUnCheckColor(G.context.getResources().getColor(R.color.green));
                }

                if (textImageList.containsKey(itemGalleryList.get(position).path)) {
                    edtChat.setText(textImageList.get(itemGalleryList.get(position).path).getText());
                } else {
                    edtChat.setText("");
                }
                iconOk.setVisibility(View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String path;
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            path = AttachFile.getFilePathFromUri(resultUri);

            serCropAndFilterImage(path);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) { // result for crop
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                path = result.getUri().getPath();
                serCropAndFilterImage(path);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
            }
        }

    }

    public interface UpdateImage {
        void result(String path);
    }






    private void setUpEmojiPopup(View view) {
        switch (G.themeColor) {
            case Theme.BLUE_GREY_COMPLETE:
            case Theme.INDIGO_COMPLETE:
            case Theme.BROWN_COMPLETE:
            case Theme.GREY_COMPLETE:
            case Theme.TEAL_COMPLETE:
            case Theme.DARK:

                setEmojiColor(view,G.backgroundTheme_2, G.textTitleTheme, G.textTitleTheme);
                break;
            default:
                setEmojiColor(view,"#eceff1", "#61000000", "#61000000");


        }

    }

    private void setEmojiColor(View view,String BackgroundColor, String iconColor, String dividerColor) {

        emojiPopup = EmojiPopup.Builder.fromRootView(view.findViewById(R.id.ac_ll_parent))
                .setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {

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
                })
                .setBackgroundColor(Color.parseColor(BackgroundColor))
                .setIconColor(Color.parseColor(iconColor))
                .setDividerColor(Color.parseColor(dividerColor))
                .build(edtChat);

    }




    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        imvSmileButton.setText(drawableResourceId);
    }

    public interface CompleteEditImage {
        void result(String path, String message, HashMap<String, StructBottomSheet> textImageList);
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

    private class AdapterViewPager extends PagerAdapter {

        ArrayList<StructBottomSheet> itemGalleryList;

        public AdapterViewPager(ArrayList<StructBottomSheet> itemGalleryList) {
            this.itemGalleryList = itemGalleryList;
        }

        @Override
        public int getCount() {
            return itemGalleryList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            LayoutInflater inflater = LayoutInflater.from(G.fragmentActivity);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.adapter_viewpager_edittext, (ViewGroup) container, false);
            final ImageView imgPlay = (ImageView) layout.findViewById(R.id.img_editImage);
            if (itemGalleryList.get(position).path != null) {
                G.imageLoader.displayImage(suitablePath(itemGalleryList.get(position).path), imgPlay);
            }

            imgPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isMultiItem) setValueCheckBox(position);
                }
            });
            ((ViewGroup) container).addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    private void setValueCheckBox(int position) {

        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
            checkBox.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));
            itemGalleryList.get(position).setSelected(true);
            textImageList.remove(itemGalleryList.get(position).path);

        } else {
            checkBox.setChecked(true);
            StructBottomSheet item = new StructBottomSheet();
            item.setText(edtChat.getText().toString());
            item.setPath(itemGalleryList.get(position).path);
            item.setId(itemGalleryList.get(position).getId());
            textImageList.put(itemGalleryList.get(position).path, item);
            checkBox.setUnCheckColor(G.context.getResources().getColor(R.color.green));
            itemGalleryList.get(position).setSelected(false);
        }
        if (textImageList.size() > 0 && isChatPage) {
            txtCountImage.setVisibility(View.VISIBLE);
            txtCountImage.setText(textImageList.size() + "");
        } else {
            txtCountImage.setVisibility(View.GONE);
        }
    }


    private void initView(View view) {

        G.fragmentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Bundle bundle = getArguments();
        if (bundle != null) {
//            path = bundle.getString(PATH);
            isChatPage = bundle.getBoolean(ISCHAT);
            isNicknamePage = bundle.getBoolean(ISNICKNAMEPAGE);
            selectPosition = bundle.getInt(SELECT_POSITION);
        }


        layoutCaption = view.findViewById(R.id.layout_caption);
        txtSet = view.findViewById(R.id.txtSet);
        imvSendButton = (MaterialDesignTextView) view.findViewById(R.id.pu_txt_sendImage);

        //imgEditImage = (ImageView) view.findViewById(R.id.imgEditImage);
        iconOk = (TextView) view.findViewById(R.id.chl_imv_ok_message);
        rootSend = (ViewGroup) view.findViewById(R.id.pu_layout_cancel_crop);
        txtEditImage = (TextView) view.findViewById(R.id.txtEditImage);
        edtChat = (EmojiEditTextE) view.findViewById(R.id.chl_edt_chat);
        edtChat.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Config.MAX_TEXT_ATTACHMENT_LENGTH)});
        txtCountImage = view.findViewById(R.id.stfaq_txt_countImageEditText);
        viewPager = view.findViewById(R.id.viewPagerEditText);
        checkBox = (AnimateCheckBox) view.findViewById(R.id.checkBox_editImage);
        imvSmileButton = (MaterialDesignTextView) view.findViewById(R.id.chl_imv_smile_button);
    }

    private void goToCropPage(View v) {
        AndroidUtils.closeKeyboard(v);
        String newPath = "file://" + itemGalleryList.get(viewPager.getCurrentItem()).path;
        String fileNameWithOutExt = newPath.substring(newPath.lastIndexOf("/"));
        String extension = newPath.substring(newPath.lastIndexOf("."));
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

    public static ArrayList<StructBottomSheet> insertItemList(String path, boolean isSelected) {

        if (itemGalleryList == null) {
            itemGalleryList = new ArrayList<>();
        }

        if (!HelperPermission.grantedUseStorage()) {
            return itemGalleryList;
        }
        StructBottomSheet item = new StructBottomSheet();
        item.setId(itemGalleryList.size());
        item.setPath(path);
        item.setText("");
        item.isSelected = isSelected;
        itemGalleryList.add(0, item);
        textImageList.put(path, item);

        return itemGalleryList;
    }

    private void serCropAndFilterImage(String path) {

        int po = (viewPager.getCurrentItem());

        if (textImageList.containsKey(itemGalleryList.get(po).getPath())) {
            textImageList.get(itemGalleryList.get(po).getPath()).setPath(path);
        }
        itemGalleryList.get(viewPager.getCurrentItem()).setPath(path);


        mAdapter.notifyDataSetChanged();


    }

}
