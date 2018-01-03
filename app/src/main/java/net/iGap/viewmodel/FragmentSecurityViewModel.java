package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentSecurity;
import net.iGap.fragments.FragmentSecurityRecovery;
import net.iGap.fragments.FragmentSetSecurityPassword;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.interfaces.OnTwoStepPassword;
import net.iGap.module.enums.Security;
import net.iGap.request.RequestUserTwoStepVerificationChangeHint;
import net.iGap.request.RequestUserTwoStepVerificationChangeRecoveryEmail;
import net.iGap.request.RequestUserTwoStepVerificationChangeRecoveryQuestion;
import net.iGap.request.RequestUserTwoStepVerificationCheckPassword;
import net.iGap.request.RequestUserTwoStepVerificationGetPasswordDetail;
import net.iGap.request.RequestUserTwoStepVerificationResendVerifyEmail;
import net.iGap.request.RequestUserTwoStepVerificationUnsetPassword;
import net.iGap.request.RequestUserTwoStepVerificationVerifyRecoveryEmail;

import java.util.regex.Pattern;

public class FragmentSecurityViewModel {

    private static String password;
    private String txtQuestionOne = "";
    private String txtQuestionTwo = "";
    private String txtPatternEmail = "";
    private boolean isRecoveryByEmail = false;
    private int page;
    private static final int CHANGE_HINT = 1;
    private static final int CHANGE_EMAIL = 2;
    private static final int CONFIRM_EMAIL = 3;
    private static final int CHANGE_QUESTION = 4;
    public boolean isFirstArrive = true;
    public static boolean isFirstSetPassword = true;
    private boolean isConfirmedRecoveryEmail;
    private String mUnconfirmedEmailPattern = "";

    public FragmentSecurityViewModel() {
        getInfo();
    }

    public ObservableInt rootSetPassword = new ObservableInt(View.GONE);
    public ObservableInt rootSetAdditionPassword = new ObservableInt(View.VISIBLE);
    public ObservableInt rootChangeHint = new ObservableInt(View.GONE);
    public ObservableInt rootChangeEmail = new ObservableInt(View.GONE);
    public ObservableInt rootConfirmedEmail = new ObservableInt(View.GONE);
    public ObservableInt rootQuestionPassword = new ObservableInt(View.GONE);
    public ObservableInt rootCheckPassword = new ObservableInt(View.GONE);
    public ObservableInt rootChangePassword = new ObservableInt(View.GONE);
    public ObservableInt txtResendConfirmEmail = new ObservableInt(View.VISIBLE);
    public ObservableInt prgWaiting = new ObservableInt(View.VISIBLE);
    public ObservableInt rippleOkVisibility = new ObservableInt(View.GONE);
    public ObservableInt setRecoveryEmail = new ObservableInt(View.VISIBLE);
    public ObservableInt lineConfirmView = new ObservableInt(View.VISIBLE);
    public ObservableInt setConfirmedEmail = new ObservableInt(View.VISIBLE);
    public ObservableInt viewRecoveryEmail = new ObservableInt(View.VISIBLE);
    public ObservableField<String> titleToolbar = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.two_step_verification_title));
    public ObservableField<String> edtConfirmedEmailHint = new ObservableField<>(mUnconfirmedEmailPattern);
    public ObservableField<String> edtConfirmedEmailText = new ObservableField<>("");
    public ObservableField<String> edtChangeHintText = new ObservableField<>("");
    public ObservableField<String> edtSetEmailText = new ObservableField<>("");
    public ObservableField<String> edtSetQuestionPassOne = new ObservableField<>("");
    public ObservableField<String> edtSetAnswerPassOne = new ObservableField<>("");
    public ObservableField<String> edtSetAnswerPassTwo = new ObservableField<>("");
    public ObservableField<String> edtSetQuestionPassTwo = new ObservableField<>("");
    public ObservableField<String> edtCheckPassword = new ObservableField<>("");
    public ObservableField<String> edtCheckPasswordHint = new ObservableField<>("");

    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================

    public void onClickRippleBack(View v) {
        rippleBack(v);
    }

    public void onClickRippleOk(View v) {

        if (rootCheckPassword.get() == View.VISIBLE) {
            if (edtCheckPassword.get().length() > 1) {
                password = edtCheckPassword.get();
                new RequestUserTwoStepVerificationCheckPassword().checkPassword(password);
                closeKeyboard(v);
                edtCheckPassword.set("");
            } else {
                closeKeyboard(v);
                error(G.fragmentActivity.getResources().getString(R.string.please_set_password));
            }
            return;
        }

        //change Question
        if (rootQuestionPassword.get() == View.VISIBLE) {
            if (edtSetQuestionPassOne.get().length() > 0 && edtSetAnswerPassOne.get().length() > 0 && edtSetQuestionPassTwo.get().length() > 0 && edtSetAnswerPassTwo.get().length() > 0) {
                new RequestUserTwoStepVerificationChangeRecoveryQuestion().changeRecoveryQuestion(password, edtSetQuestionPassOne.get(), edtSetAnswerPassOne.get(), edtSetQuestionPassTwo.get(), edtSetAnswerPassTwo.get());
                closeKeyboard(v);
                edtSetQuestionPassOne.set("");
                edtSetAnswerPassOne.set("");
                edtSetQuestionPassTwo.set("");
                edtSetAnswerPassTwo.set("");
            } else {
                closeKeyboard(v);
                error(G.fragmentActivity.getResources().getString(R.string.Please_complete_all_Item));
            }
            return;
        }

        //change email
        if (rootChangeEmail.get() == View.VISIBLE) {
            if (edtSetEmailText != null && edtSetEmailText.get().length() > 0 && password != null && password.length() > 1) {

                Pattern EMAIL_ADDRESS = patternEmail();
                if (EMAIL_ADDRESS.matcher(edtSetEmailText.get()).matches()) {
                    new RequestUserTwoStepVerificationChangeRecoveryEmail().changeRecoveryEmail(password, edtSetEmailText.get());
                    closeKeyboard(v);
                    edtSetEmailText.set("");
                } else {
                    closeKeyboard(v);
                    error(G.fragmentActivity.getResources().getString(R.string.invalid_email));
                }
            } else {
                closeKeyboard(v);
                error(G.fragmentActivity.getResources().getString(R.string.Please_enter_your_email));
            }

            return;
        }


        //hint
        if (rootChangeHint.get() == View.VISIBLE) {
            if (edtChangeHintText.get().length() > 0) {
                if (!password.equals(edtChangeHintText.get())) {
                    new RequestUserTwoStepVerificationChangeHint().changeHint(password, edtChangeHintText.get());
                    closeKeyboard(v);
                    edtChangeHintText.set("");
                } else {
                    closeKeyboard(v);
                    error(G.fragmentActivity.getResources().getString(R.string.hint_can_same_password));
                }
            } else {
                closeKeyboard(v);
                error(G.fragmentActivity.getResources().getString(R.string.Please_enter_your_hint));
            }
            return;
        }


        //confirm
        if (rootConfirmedEmail.get() == View.VISIBLE) {
            if (edtConfirmedEmailText.get().length() > 0) {
                new RequestUserTwoStepVerificationVerifyRecoveryEmail().recoveryEmail(edtConfirmedEmailText.get());
                txtPatternEmail = edtConfirmedEmailText.get();
                edtConfirmedEmailText.set("");
                closeKeyboard(v);
            } else {
                closeKeyboard(v);
                error(G.fragmentActivity.getResources().getString(R.string.please_enter_code));
            }
        }
    }

    public void onClickSetAdditionPassword(View view) {
        isFirstArrive = false;
        new HelperFragment(new FragmentSetSecurityPassword()).load();
    }

    public void onClickChangePassword(View view) {
        isFirstArrive = false;
        FragmentSetSecurityPassword fragmentSetSecurityPassword = new FragmentSetSecurityPassword();
        Bundle bundle = new Bundle();
        bundle.putString("OLD_PASSWORD", password);
        fragmentSetSecurityPassword.setArguments(bundle);
        new HelperFragment(fragmentSetSecurityPassword).setReplace(false).load();
    }

    public void onClickChangeHint(View view) {
        page = CHANGE_HINT;
        rootSetPassword.set(View.GONE);
        rootSetAdditionPassword.set(View.GONE);
        rootChangeHint.set(View.VISIBLE);
        titleToolbar.set(G.fragmentActivity.getString(R.string.password_hint));
        rippleOkVisibility.set(View.VISIBLE);
    }

    public void onClickTurnPasswordOff(View view) {

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.turn_Password_off).content(R.string.turn_Password_off_desc).positiveText(G.fragmentActivity.getResources().getString(R.string.yes)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                isFirstSetPassword = true;
                new RequestUserTwoStepVerificationUnsetPassword().unsetPassword(password);
            }
        }).negativeText(G.fragmentActivity.getResources().getString(R.string.no)).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();
    }


    public void onClickSetRecoveryEmail(View view) {
        page = CHANGE_EMAIL;
        rootSetPassword.set(View.GONE);
        rootSetAdditionPassword.set(View.GONE);
        rootChangeEmail.set(View.VISIBLE);
        titleToolbar.set(G.fragmentActivity.getString(R.string.change_email_recovery));
        //rootConfirmedEmail.setVisibility(View.VISIBLE);
        rippleOkVisibility.set(View.VISIBLE);
    }


    public void onClickSetConfirmedEmail(View view) {

        edtConfirmedEmailHint.set(mUnconfirmedEmailPattern);
        page = CONFIRM_EMAIL;
        rootSetPassword.set(View.GONE);
        rootSetAdditionPassword.set(View.GONE);
        rootConfirmedEmail.set(View.VISIBLE);
        titleToolbar.set(G.fragmentActivity.getString(R.string.confirm_email_title));
        rippleOkVisibility.set(View.VISIBLE);
    }


    public void onClicksetRecoveryQuestion(View view) {

        page = CHANGE_QUESTION;
        rootSetPassword.set(View.GONE);
        rootSetAdditionPassword.set(View.GONE);
        rootQuestionPassword.set(View.VISIBLE);
        titleToolbar.set(G.fragmentActivity.getString(R.string.title_toolbar_password_Question));
        rippleOkVisibility.set(View.VISIBLE);

    }


    public void onClickForgotPassword(View view) {
        int item;
        if (isConfirmedRecoveryEmail) {
            item = R.array.securityRecoveryPassword;
        } else {
            item = R.array.securityRecoveryPasswordWithoutEmail;
        }
        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.set_recovery_dialog_title).items(item).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                if (text.equals(G.fragmentActivity.getResources().getString(R.string.recovery_by_email_dialog))) {
                    isRecoveryByEmail = true;
                } else {
                    isRecoveryByEmail = false;
                }

                FragmentSecurityRecovery fragmentSecurityRecovery = new FragmentSecurityRecovery();
                Bundle bundle = new Bundle();
                bundle.putSerializable("PAGE", Security.SETTING);
                bundle.putString("QUESTION_ONE", txtQuestionOne);
                bundle.putString("QUESTION_TWO", txtQuestionTwo);
                bundle.putString("PATERN_EMAIL", txtPatternEmail);
                bundle.putBoolean("IS_EMAIL", isRecoveryByEmail);
                bundle.putBoolean("IS_CONFIRM_EMAIL", isConfirmedRecoveryEmail);

                fragmentSecurityRecovery.setArguments(bundle);
                new HelperFragment(fragmentSecurityRecovery).load();
            }
        }).show();
    }


    public void onClickResendConfirmEmail(View v) {
        new RequestUserTwoStepVerificationResendVerifyEmail().ResendVerifyEmail();
        closeKeyboard(v);
        error(G.fragmentActivity.getResources().getString(R.string.resend_verify_email_code));
    }

    //===============================================================================
    //====================================Methods====================================
    //===============================================================================

    private void getInfo() {

        if (isFirstArrive) {

            prgWaiting.set(View.VISIBLE);
            new RequestUserTwoStepVerificationGetPasswordDetail().getPasswordDetail();
        } else {
            if (!isFirstSetPassword) {
                setSecondView();
            } else {
                setFirstView();
            }
        }



        G.onTwoStepPassword = new OnTwoStepPassword() {
            @Override
            public void getPasswordDetail(final String questionOne, final String questionTwo, final String hint, final boolean hasConfirmedRecoveryEmail, final String unconfirmedEmailPattern) {

                txtQuestionOne = questionOne;
                txtQuestionTwo = questionTwo;
                txtPatternEmail = unconfirmedEmailPattern;
                isConfirmedRecoveryEmail = hasConfirmedRecoveryEmail;
                mUnconfirmedEmailPattern = unconfirmedEmailPattern;

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        prgWaiting.set(View.GONE);
                        if (questionOne.length() > 0 && questionTwo.length() > 0) {

                            rootSetPassword.set(View.GONE);
                            rootSetAdditionPassword.set(View.GONE);
                            rootChangePassword.set(View.VISIBLE);
                            rootCheckPassword.set(View.VISIBLE);
                            rippleOkVisibility.set(View.VISIBLE);
                            edtCheckPasswordHint.set(hint);
                            isFirstSetPassword = false;

                            if (unconfirmedEmailPattern.length() == 0) {

                                setRecoveryEmail.set(View.VISIBLE);
                                setConfirmedEmail.set(View.GONE);
                                lineConfirmView.set(View.GONE);
                                FragmentSecurity.isSetRecoveryEmail = false;
                            } else {
                                setRecoveryEmail.set(View.VISIBLE);
                                viewRecoveryEmail.set(View.VISIBLE);
                                setConfirmedEmail.set(View.VISIBLE);
                                lineConfirmView.set(View.VISIBLE);
                                FragmentSecurity.isSetRecoveryEmail = true;

                            }

                        } else {//دوبار اجرا شده
                            rootSetPassword.set(View.VISIBLE);
                            rootSetAdditionPassword.set(View.VISIBLE);
                            rootChangePassword.set(View.GONE);
                            rootCheckPassword.set(View.GONE);
                            rippleOkVisibility.set(View.GONE);
                            isFirstSetPassword = true;
                        }
                    }
                });
            }



            @Override
            public void errorGetPasswordDetail(final int majorCode, final int minorCode) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (majorCode == 188 && minorCode == 1) {  //USER DON'T SET A PASSWORD
                            setFirstView();
                        } else { // CAN'T CONNECT TO SERVER
                            //  G.fragmentActivity.getSupportFragmentManager().popBackStack();
                            if (FragmentSecurity.onPopBackStackFragment != null) {
                                FragmentSecurity.onPopBackStackFragment.onBack();
                            }

                        }
                    }
                });

            }

            @Override
            public void timeOutGetPasswordDetail() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // G.fragmentActivity.getSupportFragmentManager().popBackStack();
                        if (FragmentSecurity.onPopBackStackFragment != null) {
                            FragmentSecurity.onPopBackStackFragment.onBack();
                        }

                    }
                });
            }

            @Override
            public void checkPassword() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        rootSetPassword.set(View.VISIBLE);
                        rootCheckPassword.set(View.GONE);
                        rippleOkVisibility.set(View.GONE);
                    }
                });

            }

            @Override
            public void errorCheckPassword(final int getWait) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialogWaitTime(getWait);
                    }
                });
            }

            @Override
            public void unSetPassword() {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        rootSetPassword.set(View.VISIBLE);
                        rootSetAdditionPassword.set(View.VISIBLE);
                        rootChangePassword.set(View.GONE);
                        rootCheckPassword.set(View.GONE);
                        rippleOkVisibility.set(View.GONE);
                    }
                });

            }

            @Override
            public void changeRecoveryQuestion() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        viewChangeRecoveryQuestion();
                    }
                });
            }

            @Override
            public void changeHint() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        viewChangeHint();
                    }
                });
            }

            @Override
            public void changeEmail(final String unConfirmEmailPatern) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mUnconfirmedEmailPattern = unConfirmEmailPatern;
                        FragmentSecurity.isSetRecoveryEmail = true;
                        viewChangeEmail();
                    }
                });
            }

            @Override
            public void confirmEmail() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        viewConfirmEmail();
                    }
                });
            }

            @Override
            public void errorConfirmEmail() {

            }

            @Override
            public void errorInvalidPassword() {

            }
        };


    }

    public void onResume() {

    }


    private void closeKeyboard(View v) {

        try {
            InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (IllegalStateException e) {
            e.getStackTrace();

        }
    }

    private void error(String error) {
        try {

            HelperError.showSnackMessage(error, true);

        } catch (IllegalStateException e) {
            e.getStackTrace();
        }
    }

    private Pattern patternEmail() {
        return Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{2,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,25}" + ")+");
    }

    private void setFirstView() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                rootSetPassword.set(View.VISIBLE);
                rootSetAdditionPassword.set(View.VISIBLE);
                rootChangePassword.set(View.GONE);
                rootCheckPassword.set(View.GONE);
                rippleOkVisibility.set(View.GONE);
                prgWaiting.set(View.GONE);
            }
        });

    }

    private void setSecondView() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                rootSetPassword.set(View.VISIBLE);
                rootChangePassword.set(View.VISIBLE);
                rootSetAdditionPassword.set(View.GONE);
                rootCheckPassword.set(View.GONE);
                rippleOkVisibility.set(View.GONE);
                prgWaiting.set(View.GONE);
                if (!FragmentSecurity.isSetRecoveryEmail) {
                    setConfirmedEmail.set(View.GONE);
                    lineConfirmView.set(View.GONE);
                } else {
                    setConfirmedEmail.set(View.VISIBLE);
                    lineConfirmView.set(View.VISIBLE);
                }
            }
        });

    }

    private void viewConfirmEmail() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                page = 0;
                rootSetPassword.set(View.VISIBLE);
                rootConfirmedEmail.set(View.GONE);
                titleToolbar.set(G.fragmentActivity.getString(R.string.two_step_verification_title));
                rippleOkVisibility.set(View.GONE);
            }
        });

    }

    private void viewChangeEmail() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                page = 0;
                rootSetPassword.set(View.VISIBLE);
                rootConfirmedEmail.set(View.GONE);
                rootChangeEmail.set(View.GONE);
                titleToolbar.set(G.fragmentActivity.getString(R.string.two_step_verification_title));
                rippleOkVisibility.set(View.GONE);
                if (mUnconfirmedEmailPattern.length() > 0) {
                    setConfirmedEmail.set(View.VISIBLE);
                    lineConfirmView.set(View.VISIBLE);
                } else {
                    setConfirmedEmail.set(View.GONE);
                    lineConfirmView.set(View.GONE);
                }
            }
        });

    }

    private void viewChangeHint() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                page = 0;
                rootSetPassword.set(View.VISIBLE);
                rootChangeHint.set(View.GONE);
                titleToolbar.set(G.fragmentActivity.getString(R.string.two_step_verification_title));
                rippleOkVisibility.set(View.GONE);
            }
        });

    }

    private void viewChangeRecoveryQuestion() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                page = 0;
                rootSetPassword.set(View.VISIBLE);
                rootQuestionPassword.set(View.GONE);
                titleToolbar.set(G.fragmentActivity.getString(R.string.two_step_verification_title));
                rippleOkVisibility.set(View.GONE);
            }
        });

    }


    private void dialogWaitTime(long time) {
        boolean wrapInScrollView = true;

        final MaterialDialog dialogWait = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.error_check_password).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(true).canceledOnTouchOutside(true).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialog.dismiss();
            }
        }).show();

        View v = dialogWait.getCustomView();

        final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();

    }

    public void rippleBack(View v) {

        switch (page) {
            case CHANGE_HINT:
                viewChangeHint();
                break;
            case CHANGE_EMAIL:
                viewChangeEmail();
                break;
            case CONFIRM_EMAIL:
                viewConfirmEmail();
                break;
            case CHANGE_QUESTION:
                viewChangeRecoveryQuestion();
                break;
            default:
                isFirstArrive = true;
                //G.fragmentActivity.getSupportFragmentManager().popBackStack();


                if (FragmentSecurity.onPopBackStackFragment != null) {
                    FragmentSecurity.onPopBackStackFragment.onBack();
                }


        }

        closeKeyboard(v);
    }

}



