package net.iGap.fragments;



import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.io.IOException;
import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentNotificationAndSoundBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.FragmentNotificationAndSoundViewModel;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotificationAndSound extends BaseFragment {

    private SharedPreferences sharedPreferences;

    private FragmentNotificationAndSoundBinding fragmentNotificationAndSoundBinding;
    private FragmentNotificationAndSoundViewModel fragmentNotificationAndSoundViewModel;


    public FragmentNotificationAndSound() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentNotificationAndSoundBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification_and_sound, container, false);
        return attachToSwipeBack(fragmentNotificationAndSoundBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDataBinding();

        fragmentNotificationAndSoundBinding.asnToolbar.setBackgroundColor(Color.parseColor(G.appBarColor));

        fragmentNotificationAndSoundBinding.stnsRippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) throws IOException {
                popBackStackFragment();
            }
        });

        GradientDrawable bgShapeAlert = (GradientDrawable) fragmentNotificationAndSoundBinding.stnsImgLedColorMessage.getBackground();
        bgShapeAlert.setColor(fragmentNotificationAndSoundViewModel.ledColorMessage);

        GradientDrawable bgShapeGroup = (GradientDrawable) fragmentNotificationAndSoundBinding.stnsImgLedColorGroup.getBackground();
        bgShapeGroup.setColor(fragmentNotificationAndSoundViewModel.ledColorGroup);


        fragmentNotificationAndSoundBinding.stLayoutResetAllNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.st_title_reset).content(R.string.st_dialog_reset_all_notification).positiveText(R.string.st_dialog_reset_all_notification_yes).negativeText(R.string.st_dialog_reset_all_notification_no).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0);
                        editor.putString(SHP_SETTING.KEY_STNS_SOUND_MESSAGE, G.fragmentActivity.getResources().getString(R.string.array_Default_Notification_tone));
                        editor.putInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0);
                        editor.putString(SHP_SETTING.KEY_STNS_SOUND_GROUP, G.fragmentActivity.getResources().getString(R.string.array_Default_Notification_tone));
                        editor.putInt(SHP_SETTING.KEY_STNS_APP_SOUND, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_CONTACT_JOINED, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_PINNED_MESSAGE, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_BACKGROUND_CONNECTION, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_BADGE_CONTENT, 1);
                        editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, G.fragmentActivity.getResources().getString(R.string.array_1_hour));
                        editor.putInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
                        editor.putInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792);
                        editor.apply();
                        Toast.makeText(G.fragmentActivity, G.fragmentActivity.getResources().getString(R.string.st_reset_all_notification), Toast.LENGTH_SHORT).show();

                        removeFromBaseFragment(FragmentNotificationAndSound.this);
                        new HelperFragment(new FragmentNotificationAndSound()).setReplace(false).load();
                    }
                }).show();
            }
        });
    }

    private void initDataBinding() {

        fragmentNotificationAndSoundViewModel = new FragmentNotificationAndSoundViewModel(fragmentNotificationAndSoundBinding);
        fragmentNotificationAndSoundBinding.setFragmentNotificationAndSoundViewModel(fragmentNotificationAndSoundViewModel);

    }

}
