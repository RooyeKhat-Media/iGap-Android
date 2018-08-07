/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap.fragments.filterImage;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.ImageHelper;
import net.iGap.module.AttachFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentFilterImage extends BaseFragment implements FiltersListFragment.FiltersListFragmentListener, EditImageFragment.EditImageFragmentListener, ThumbnailsAdapter.ThumbnailsAdapterListener {

    //    private RecyclerView rcvEditImage;
    private ImageView imageFilter;
    private final static String PATH = "PATH";
    private String path;

    private Bitmap originalImage;
    // to backup image with filter applied
    private Bitmap filteredImage;

    // the final image after applying
    // brightness, saturation, contrast
    private Bitmap finalImage;

    private FiltersListFragment filtersListFragment;
    private EditImageFragment editImageFragment;

    // modified image values
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;

    public boolean isChange = false;

    // load native image filters library
    static {
        System.loadLibrary("NativeImageProcessor");
    }

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

        if (getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle bundle = getArguments();
        if (bundle != null) {
            path = bundle.getString(PATH);
            if (path == null) {
                return;
            }
        } else {
            return;
        }

        imageFilter = (ImageView) view.findViewById(R.id.imageFilter);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        loadImage();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        view.findViewById(R.id.pu_txt_agreeImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChange) {
                    new MaterialDialog.Builder(G.fragmentActivity)
                            .title(R.string.tab_filters)
                            .content(R.string.filter_cancel_content)
                            .positiveText(R.string.save)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    final String path = BitmapUtils.insertImage(getActivity().getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
                                    FragmentEditImage.updateImage.result(AttachFile.getFilePathFromUri(Uri.parse(path)));
                                    popBackStackFragment();
                                }
                            }).onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            popBackStackFragment();
                        }
                    })
                            .negativeText(R.string.close)
                            .show();
                } else {
                    popBackStackFragment();
                }

            }
        });

        view.findViewById(R.id.pu_txt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String path = BitmapUtils.insertImage(getActivity().getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
                FragmentEditImage.updateImage.result(AttachFile.getFilePathFromUri(Uri.parse(path)));
                popBackStackFragment();
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

//         adding filter list fragment
        filtersListFragment = FiltersListFragment.newInstance(path);
        filtersListFragment.setListener(this);
        // adding edit image fragment
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);
        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        adapter.addFragment(editImageFragment, getString(R.string.tab_edit));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls
        isChange = true;
        new FilterImageTask().execute(filter, null, filter);
    }

    @Override
    public void onBrightnessChanged(final int brightness) {
        new BrightnessChangedTask().execute(brightness);
    }

    @Override
    public void onSaturationChanged(final float saturation) {
        new SaturationChangedTask().execute(saturation);
    }

    @Override
    public void onContrastChanged(final float contrast) {
        new ContrastChangedTask().execute(contrast);
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        // once the editing is done i.e seekbar is drag is completed,
        // apply the values on to filtered image
        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalImage = myFilter.processFilter(bitmap);
    }

    /**
     * Resets image edit controls to normal when new filter
     * is selected
     */
    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // load the default image from assets on app launch
    private void loadImage() {
        originalImage = getBitmapFile(getActivity(), path, 300, 300);
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imageFilter.setImageBitmap(originalImage);
    }

    public static Bitmap getBitmapFile(Context context, String fileName, int width, int height) {
        File image = new File(fileName);
        return ImageHelper.decodeFile(image);
    }

    private class FilterImageTask extends AsyncTask<Filter, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(Filter... filters) {
            // applying the selected filter
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            // preview filtered image
            Bitmap bitmap = filters[0].processFilter(filteredImage);
            finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            resetControls();
            imageFilter.setImageBitmap(bitmap);
        }
    }


    private class BrightnessChangedTask extends AsyncTask<Integer, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Integer... integers) {

            brightnessFinal = integers[0];
            Filter myFilter = new Filter();
            myFilter.addSubFilter(new BrightnessSubFilter(integers[0]));
            return myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true));
        }

        @Override
        protected void onPostExecute(Bitmap myFilter) {
            super.onPostExecute(myFilter);
            imageFilter.setImageBitmap(myFilter);
        }
    }

    private class SaturationChangedTask extends AsyncTask<Float, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Float... values) {
            saturationFinal = values[0];
            Filter myFilter = new Filter();
            myFilter.addSubFilter(new SaturationSubfilter(values[0]));
            return myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true));
        }

        @Override
        protected void onPostExecute(Bitmap myFilter) {
            super.onPostExecute(myFilter);
            imageFilter.setImageBitmap(myFilter);
        }
    }

    private class ContrastChangedTask extends AsyncTask<Float, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Float... values) {
            contrastFinal = values[0];
            Filter myFilter = new Filter();
            myFilter.addSubFilter(new ContrastSubFilter(values[0]));
            return myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true));
        }

        @Override
        protected void onPostExecute(Bitmap myFilter) {
            super.onPostExecute(myFilter);
            imageFilter.setImageBitmap(myFilter);
        }
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
                    popBackStackFragment();
                    return true;
                }
                return false;
            }
        });
    }
}