package net.iGap.fragments.filterImage;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.helper.HelperFragment;
import net.iGap.module.AttachFile;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import it.chengdazhi.styleimageview.StyleImageView;
import it.chengdazhi.styleimageview.Styler;

import static net.iGap.module.AndroidUtils.suitablePath;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFilterImage extends Fragment {

    private RecyclerView rcvEditImage;
    private List<StructFilterImage> options;
    private List<String> optionTexts;

    private StyleImageView image;
    private View lastChosenOptionView;
    private CheckBox enableAnimationCheckBox;
    private EditText animationDurationEditText;
    private SeekBar brightnessBar;
    private SeekBar contrastBar;
    private SeekBar saturationBar;
    private final static String PATH = "PATH";
    private String path;


    public FragmentFilterImage() {
        // Required empty public constructor
    }

    public static FragmentFilterImage newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(PATH, path);
        FragmentFilterImage fragment = new FragmentFilterImage();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter_image, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        image = (StyleImageView) view.findViewById(R.id.image);
        initOptions();

        rcvEditImage = (RecyclerView) view.findViewById(R.id.rcvEditImage);
        rcvEditImage.setAdapter(new AdapterFilterImage());
        LinearLayoutManager layoutManager = new LinearLayoutManager(G.context, LinearLayoutManager.HORIZONTAL, false);
        rcvEditImage.setLayoutManager(layoutManager);
        rcvEditImage.setHasFixedSize(true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            path = bundle.getString(PATH);
        }

        G.imageLoader.displayImage(suitablePath(path), image);

        view.findViewById(R.id.pu_ripple_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelperFragment(FragmentFilterImage.this).remove();
            }
        });

        view.findViewById(R.id.pu_txt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentEditImage.updateImage.result(AttachFile.getFilePathFromUri(getImageUri(G.context, image.getBitmap())));
                new HelperFragment(FragmentFilterImage.this).remove();
            }
        });

        view.findViewById(R.id.pu_txt_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(G.fragmentActivity)
                        .title("Clear")
                        .content("Are you sure")
                        .positiveText("ok")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                image.clearStyle();
                                if (saturationBar != null) {
                                    saturationBar.setProgress(100);
                                }
                            }
                        })
                        .negativeText("cancel")
                        .show();
            }
        });

        enableAnimationCheckBox = (CheckBox) view.findViewById(R.id.animation_checkbox);
        animationDurationEditText = (EditText) view.findViewById(R.id.duration_edittext);
        enableAnimationCheckBox.setChecked(image.isAnimationEnabled());
        animationDurationEditText.setText(String.valueOf(image.getAnimationDuration()));
        if (image.isAnimationEnabled()) {
            animationDurationEditText.setEnabled(true);
            animationDurationEditText.setTextColor(Color.BLACK);
        } else {
            animationDurationEditText.setEnabled(false);
            animationDurationEditText.setTextColor(Color.GRAY);
        }
        enableAnimationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    animationDurationEditText.setText("0");
                    animationDurationEditText.setEnabled(false);
                    animationDurationEditText.setTextColor(Color.GRAY);
                } else {
                    animationDurationEditText.setEnabled(true);
                    animationDurationEditText.setTextColor(Color.BLACK);
                }
                if (b) {
                    image.enableAnimation(Long.parseLong(animationDurationEditText.getText().toString()));
                } else {
                    image.disableAnimation();
                }
            }
        });
        animationDurationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    image.enableAnimation(Long.parseLong(charSequence.toString()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        brightnessBar = (SeekBar) view.findViewById(R.id.seekbar_brightness);
        contrastBar = (SeekBar) view.findViewById(R.id.seekbar_contrast);
        brightnessBar.setProgress(image.getBrightness() + 255);
        contrastBar.setProgress((int) (image.getContrast() * 100));
        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                image.setBrightness(i - 255).updateStyle();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        contrastBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                image.setContrast(i / 100F).updateStyle();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    public void initOptions() {

        options = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            StructFilterImage structFilterImage = new StructFilterImage();
            structFilterImage.setCreate(false);
            switch (i) {
                case 0: {

                    structFilterImage.setName("Grey Scale");
                    structFilterImage.setStyle(Styler.Mode.GREY_SCALE);
                }
                break;
                case 1: {
                    structFilterImage.setName("Invert");
                    structFilterImage.setStyle(Styler.Mode.INVERT);

                }
                break;
                case 2: {
                    structFilterImage.setName("RGB to BGR");
                    structFilterImage.setStyle(Styler.Mode.RGB_TO_BGR);
                }
                break;
                case 3: {
                    structFilterImage.setName("Sepia");
                    structFilterImage.setStyle(Styler.Mode.SEPIA);
                }
                break;
                case 4: {
                    structFilterImage.setName("Black & White");
                    structFilterImage.setStyle(Styler.Mode.BLACK_AND_WHITE);
                }
                break;
                case 5: {
                    structFilterImage.setName("Bright");
                    structFilterImage.setStyle(Styler.Mode.BRIGHT);
                }
                break;
                case 6: {
                    structFilterImage.setName("Vintage Pinhole");
                    structFilterImage.setStyle(Styler.Mode.VINTAGE_PINHOLE);
                }
                break;
                case 7: {
                    structFilterImage.setName("Kodachrome");
                    structFilterImage.setStyle(Styler.Mode.KODACHROME);
                }
                break;
                case 8: {
                    structFilterImage.setName("Technicolor");
                    structFilterImage.setStyle(Styler.Mode.TECHNICOLOR);
                }
                break;
                case 9: {
                    structFilterImage.setName("Saturation");
                    structFilterImage.setStyle(Styler.Mode.SATURATION);
                }
                break;
            }

            options.add(structFilterImage);
        }


    }

//    class ListAdapter extends BaseAdapter {
//        @Override
//        public int getCount() {
//            return options.size() + 1;
//        }
//
//        @Override
//        public Object getItem(int i) {
//            if (i >= options.size()) {
//                return 100;
//            }
//            return options.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(final int i, View view, ViewGroup viewGroup) {
//            final View result = getLayoutInflater().inflate(R.layout.option_item, null);
//            if (i < options.size() && image.getMode() == options.get(i) || i >= options.size() && image.getMode() == Styler.Mode.NONE) {
//                result.setBackgroundColor(G.context.getResources().getColor(R.color.gray_3c));
//                lastChosenOptionView = result;
//            }
//            TextView title = (TextView) result.findViewById(R.id.text);
//            title.setText(optionTexts.get(i));
//            if (options.get(i) == Styler.Mode.SATURATION) {
//                saturationBar = (SeekBar) result.findViewById(R.id.seekbar_saturation);
//                saturationBar.setVisibility(View.VISIBLE);
//                saturationBar.setProgress((int) (image.getSaturation() * 100));
//                saturationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                        image.setSaturation(i / 100F).updateStyle();
//                        if (lastChosenOptionView != result) {
//                            if (lastChosenOptionView != null) {
//                                lastChosenOptionView.setBackgroundColor(G.context.getResources().getColor(R.color.gray_3c));
//                            }
//                            result.setBackgroundColor(G.context.getResources().getColor(R.color.gray_3c));
//                            lastChosenOptionView = result;
//                        }
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//                    }
//                });
//            }
//
//        }
//    }

    private class AdapterFilterImage extends RecyclerView.Adapter<AdapterFilterImage.ViewHolder> {

        @Override
        public AdapterFilterImage.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.option_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AdapterFilterImage.ViewHolder holder, int position) {

            options.get(position).setCreate(true);
            holder.txtName.setText(options.get(position).getName());
            G.imageLoader.displayImage(suitablePath(path), holder.imgFilter);
            holder.imgFilter.setMode(options.get(position).getStyle()).updateStyle();

        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private StyleImageView imgFilter;
            private TextView txtName;

            public ViewHolder(final View itemView) {
                super(itemView);

                imgFilter = (StyleImageView) itemView.findViewById(R.id.imgFilter);
                txtName = (TextView) itemView.findViewById(R.id.txtName);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        if (lastChosenOptionView != null) {
//                            lastChosenOptionView.setBackgroundColor(G.context.getResources().getColor(R.color.black));
//                        }
//                        itemView.setBackgroundColor(G.context.getResources().getColor(R.color.gray_3c));

                        image.setMode(options.get(getAdapterPosition()).getStyle()).updateStyle();

                        if (saturationBar != null && options.get(getAdapterPosition()).getStyle() != Styler.Mode.SATURATION) {
                            saturationBar.setProgress(100);
                        }

                        lastChosenOptionView = itemView;


                    }
                });


            }
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public class StructFilterImage {

        private String name;
        private int style;
        private boolean isCreate;

        public boolean isCreate() {
            return isCreate;
        }

        public void setCreate(boolean create) {
            isCreate = create;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getStyle() {
            return style;
        }

        public void setStyle(int style) {
            this.style = style;
        }
    }
}
