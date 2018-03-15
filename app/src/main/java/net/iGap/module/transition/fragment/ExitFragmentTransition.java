/*
 * Copyright (C) 2015 takahirom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.iGap.module.transition.fragment;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.transition.core.MoveData;
import net.iGap.module.transition.core.TransitionAnimation;

public class ExitFragmentTransition {
    private final MoveData moveData;
    private Fragment fragment;
    private android.support.v4.app.Fragment supportFragment;
    private TimeInterpolator interpolator;
    private Animator.AnimatorListener listener;


    public ExitFragmentTransition(Fragment fragment, MoveData moveData) {
        this.fragment = fragment;
        this.moveData = moveData;
    }

    public ExitFragmentTransition(final android.support.v4.app.Fragment fragment, MoveData moveData) {
        this.supportFragment = fragment;
        this.moveData = moveData;
    }

    public ExitFragmentTransition interpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public ExitFragmentTransition exitListener(Animator.AnimatorListener listener) {
        this.listener = listener;
        return this;
    }

    public void startExitListening(final View v) {
        startExitListening(null, v);
    }

    public void startExitListening(final Runnable popBackStackRunnable, final View view) {
        if (interpolator == null) {
            interpolator = new DecelerateInterpolator();
        }
        final View toView = moveData.toView;
        toView.setFocusableInTouchMode(true);
        toView.requestFocus();
        toView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (event.getAction() != KeyEvent.ACTION_UP) {
                        return true;
                    }
                    animateClose(view);
                    TransitionAnimation.startExitAnimation(moveData, interpolator, new Runnable() {
                        @Override
                        public void run() {
                            if (popBackStackRunnable != null) {
                                popBackStackRunnable.run();
                                return;
                            }
                            if (fragment == null) {
                                if (!supportFragment.isResumed()) {
                                    return;
                                }
                                final FragmentManager fragmentManager = supportFragment.getFragmentManager();
                                if (fragmentManager != null) {
                                    fragmentManager.popBackStack();
                                }
                            } else {
                                if (!fragment.isResumed()) {
                                    return;
                                }
                                final android.app.FragmentManager fragmentManager = fragment.getFragmentManager();
                                if (fragmentManager != null) {
                                    fragmentManager.popBackStack();
                                }
                            }
                        }
                    }, listener);
                    return true;
                }
                return false;
            }
        });
    }


    public void startButtonExitListening(View v) {
        startButtonExitListening(null, v);
    }

    public void startButtonExitListening(final Runnable popBackStackRunnable, final View v) {
        if (interpolator == null) {
            interpolator = new DecelerateInterpolator();
        }
        animateClose(v);
        TransitionAnimation.startExitAnimation(moveData, interpolator, new Runnable() {
            @Override
            public void run() {
                if (popBackStackRunnable != null) {
                    popBackStackRunnable.run();
                    return;
                }
                if (fragment == null) {
                    if (!supportFragment.isResumed()) {
                        return;
                    }
                    final FragmentManager fragmentManager = supportFragment.getFragmentManager();
                    if (fragmentManager != null) {

                        fragmentManager.popBackStack();
                    }
                } else {
                    if (!fragment.isResumed()) {
                        return;
                    }
                    final android.app.FragmentManager fragmentManager = fragment.getFragmentManager();
                    if (fragmentManager != null) {
                        fragmentManager.popBackStack();
                    }
                }
            }
        }, listener);
    }

    private void animateClose(final View v) {

        int colorFrom = G.context.getResources().getColor(R.color.black);
        int colorTo = G.context.getResources().getColor(R.color.transparent);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(300); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                v.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });

        colorAnimation.start();

    }
}
