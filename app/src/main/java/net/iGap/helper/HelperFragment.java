package net.iGap.helper;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import java.util.ArrayList;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.FragmentCall;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentMain;

import static net.iGap.fragments.FragmentCall.OPEN_IN_FRAGMENT_MAIN;

public class HelperFragment {

    private Fragment fragment;
    private boolean addToBackStack = true;
    private boolean animated = true;
    private boolean replace = true;
    private boolean stateLoss;
    private boolean hasCustomAnimation;
    private String tag;
    private int resourceContainer = 0;
    private int enter;
    private int exit;
    private int popEnter;
    private int popExit;

    private static String chatName = FragmentChat.class.getName();

    public HelperFragment() {
    }

    public HelperFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public HelperFragment setFragment(Fragment fragment) {
        this.fragment = fragment;
        return this;
    }

    public HelperFragment setAddToBackStack(boolean addToBackStack) {
        this.addToBackStack = addToBackStack;
        return this;
    }

    public HelperFragment setAnimated(boolean animated) {
        this.animated = animated;
        return this;
    }

    public HelperFragment setReplace(boolean replace) {
        this.replace = replace;
        return this;
    }

    public HelperFragment setStateLoss(boolean stateLoss) {
        this.stateLoss = stateLoss;
        return this;
    }

    public HelperFragment setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public HelperFragment setResourceContainer(int resourceContainer) {
        this.resourceContainer = resourceContainer;
        return this;
    }

    public HelperFragment setAnimation(int enter, int exit, int popEnter, int popExit) {
        hasCustomAnimation = true;
        this.enter = enter;
        this.exit = exit;
        this.popEnter = popEnter;
        this.popExit = popExit;
        return this;
    }

    public void load() {
        if (fragment == null) {
            return;
        }
        if (G.fragmentManager == null) {
            HelperLog.setErrorLog("helper fragment loadFragment -> " + fragment.getClass().getName());
            return;
        }

        FragmentTransaction fragmentTransaction = G.fragmentManager.beginTransaction();

        if (tag == null) {
            tag = fragment.getClass().getName();
        }

        if (getAnimation(tag)) {
            if (hasCustomAnimation) {
                fragmentTransaction.setCustomAnimations(enter, exit, popEnter, popExit);
            } else {
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left);
            }
        }

        if (resourceContainer == 0) {
            resourceContainer = getResContainer(tag);
        }

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }

        if (replace) {
            fragmentTransaction.replace(resourceContainer, fragment, tag);
        } else {
            fragmentTransaction.add(resourceContainer, fragment, tag);
        }

        if (stateLoss) {
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            fragmentTransaction.commit();
        }

        if (G.oneFragmentIsOpen != null && G.twoPaneMode) {
            G.oneFragmentIsOpen.justOne();
        }
    }

    public void remove() {
        if (fragment == null) {
            return;
        }
        G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        G.fragmentActivity.getSupportFragmentManager().popBackStack();

        if (G.iTowPanModDesinLayout != null) {
            G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.none);
        }
    }

    public void removeAll(boolean keepMain) {
        if (G.fragmentActivity != null) {
            for (Fragment fragment : G.fragmentActivity.getSupportFragmentManager().getFragments()) {
                if (fragment != null) {

                    if (keepMain) {
                        if (fragment.getClass().getName().equals(FragmentMain.class.getName())) {
                            continue;
                        }
                        if (fragment instanceof FragmentCall) {
                            if (fragment.getArguments().getBoolean(OPEN_IN_FRAGMENT_MAIN)) {
                                continue;
                            }
                        }

                        G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    } else {
                        G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                }
            }
            G.fragmentActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if (G.iTowPanModDesinLayout != null) {
            G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.none);
        }
    }

    public void popBackStack() {
        G.fragmentActivity.getSupportFragmentManager().popBackStack();
    }

    private boolean getAnimation(String tag) {

        for (String immovableClass : G.generalImmovableClasses) {
            if (tag.equals(immovableClass)) {
                return false;
            }
        }

        if (G.twoPaneMode) {
            if ((G.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) && (tag.equals(FragmentChat.class.getName()))) {
                return true;
            }

            if (G.iTowPanModDesinLayout != null && G.iTowPanModDesinLayout.getBackChatVisibility()) {
                return true;
            }
            return false;
        } else {
            return true;
        }

    }

    public static Fragment isFragmentVisible(String fragmentTag) {
        Fragment fragment = G.fragmentActivity.getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment != null && fragment.isVisible()) {
            return fragment;
        }
        return null;
    }

    public static Fragment isFragmentVisible(ArrayList<String> fragmentTags) {
        for (String fragmentTag : fragmentTags) {
            FragmentChat fragment = (FragmentChat) G.fragmentActivity.getSupportFragmentManager().findFragmentByTag(fragmentTag);
            if (fragment != null && fragment.isVisible()) {
                return fragment;
            }
        }
        return null;
    }

    private boolean isChatFragment(String fragmentClassName) {

        if (fragmentClassName.equals(chatName)) {
            return true;
        } else {
            return false;
        }
    }

    private int getResContainer(String fragmentClassName) {

        if (fragmentClassName == null || fragmentClassName.length() == 0) {
            return 0;
        }

        int resId = 0;

        if (G.twoPaneMode && !fragmentClassName.equals("net.iGap.fragments.FragmentShowImage")) {

            if (isChatFragment(fragmentClassName)) {

                resId = R.id.am_frame_chat_container;

                if (G.iTowPanModDesinLayout != null) {
                    G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.show);
                }

            } else {

                resId = R.id.am_frame_fragment_container;

                if (G.iTowPanModDesinLayout != null) {
                    G.iTowPanModDesinLayout.setBackChatVisibility(true);
                }
            }
        } else {
            resId = R.id.frame_main;
        }

        return resId;
    }
}
