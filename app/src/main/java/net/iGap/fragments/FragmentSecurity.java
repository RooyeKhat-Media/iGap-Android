package net.iGap.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.regex.Pattern;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperFragment;
import net.iGap.interfaces.OnTwoStepPassword;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.enums.Security;
import net.iGap.request.RequestUserTwoStepVerificationChangeHint;
import net.iGap.request.RequestUserTwoStepVerificationChangeRecoveryEmail;
import net.iGap.request.RequestUserTwoStepVerificationChangeRecoveryQuestion;
import net.iGap.request.RequestUserTwoStepVerificationCheckPassword;
import net.iGap.request.RequestUserTwoStepVerificationGetPasswordDetail;
import net.iGap.request.RequestUserTwoStepVerificationResendVerifyEmail;
import net.iGap.request.RequestUserTwoStepVerificationUnsetPassword;
import net.iGap.request.RequestUserTwoStepVerificationVerifyRecoveryEmail;

import static net.iGap.R.id.tsv_setConfirmedEmail;
import static net.iGap.R.id.tsv_setRecoveryEmail;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecurity extends BaseFragment {

    private static String password;
    private boolean isChabgePassword = false;
    private String txtQuestionOne = "";
    private String txtQuestionTwo = "";
    private String txtPaternEmail = "";
    private boolean isRecoveryByEmail = false;
    public static boolean isSetRecoveryEmail = false;
    private ViewGroup rootSetPassword;
    private ViewGroup rootCheckPassword;
    private ViewGroup rootSetAdditionPassword;
    private ViewGroup root_ChangePassword;
    private ViewGroup rootQuestionPassword;
    private ViewGroup rootChangeEmail;
    private ViewGroup rootChangeHint;
    private ViewGroup rootConfirmedEmail;
    private TextView txtResendConfirmEmail;
    private ProgressBar prgWaiting;
    private RippleView rippleOk;
    private int page;
    private static final int CHANGE_HINT = 1;
    private static final int CHANGE_EMAIL = 2;
    private static final int CONFIRM_EMAIL = 3;
    private static final int CHANGE_QUESTION = 4;
    public boolean isFirstArrive = true;
    public static boolean isFirstSetPassword = true;
    private TextView txtSetConfirmedEmail;
    private TextView txtSetRecoveryEmail;
    private View lineConfirmView;
    private boolean isConfirmedRecoveryEmail;
    private String mUnconfirmedEmailPattern = "";
    private EditText edtConfirmedEmail;
    private RippleView btnBack;
    private TextView txtTitleToolbar;

    public FragmentSecurity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_fragment_security, container, false));
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prgWaiting = (ProgressBar) view.findViewById(R.id.tsv_prgWaiting_addContact);

        view.findViewById(R.id.stps_backgroundToolbar).setBackgroundColor(Color.parseColor(G.appBarColor));
        txtTitleToolbar = (TextView) view.findViewById(R.id.stps_titleToolbar);
        rootSetPassword = (ViewGroup) view.findViewById(R.id.rootSetPassword);
        rootCheckPassword = (ViewGroup) view.findViewById(R.id.rootCheckPassword);
        rootSetAdditionPassword = (ViewGroup) view.findViewById(R.id.rootSetAdditionPassword);
        root_ChangePassword = (ViewGroup) view.findViewById(R.id.root_ChangePassword);
        rootQuestionPassword = (ViewGroup) view.findViewById(R.id.tsv_rootQuestionPassword);
        rootChangeEmail = (ViewGroup) view.findViewById(R.id.tsv_rootChangeEmail);
        rootChangeHint = (ViewGroup) view.findViewById(R.id.tsv_rootChangeHint);
        rootConfirmedEmail = (ViewGroup) view.findViewById(R.id.tsv_rootConfirmedEmail);
        txtResendConfirmEmail = (TextView) view.findViewById(R.id.tsv_txtResendConfirmEmail);

        lineConfirmView = view.findViewById(R.id.tsv_viewConfirmedEmail);


        rippleOk = (RippleView) view.findViewById(R.id.verifyPassword_rippleOk);
        final EditText edtCheckPassword = (EditText) view.findViewById(R.id.setPassword_edtCheckPassword);
        final EditText edtSetQuestionPassOne = (EditText) view.findViewById(R.id.tsv_edtSetQuestionPassOne);
        final EditText edtSetAnswerPassOne = (EditText) view.findViewById(R.id.tsv_edtSetAnswerPassOne);
        final EditText edtSetQuestionPassTwo = (EditText) view.findViewById(R.id.tsv_edtSetQuestionPassTwo);
        final EditText edtSetAnswerPassTwo = (EditText) view.findViewById(R.id.tsv_edtSetAnswerPassTwo);
        final EditText edtSetEmail = (EditText) view.findViewById(R.id.tsv_edtSetEmail);
        final EditText edtChangeHint = (EditText) view.findViewById(R.id.tsv_edtChangeHint);
        edtConfirmedEmail = (EditText) view.findViewById(R.id.tsv_edtConfirmedEmail);

        TextView txtChangePassword = (TextView) view.findViewById(R.id.tsv_changePassword);
        TextView txtTurnPasswordOff = (TextView) view.findViewById(R.id.tsv_turnPasswordOff);
        txtSetRecoveryEmail = (TextView) view.findViewById(tsv_setRecoveryEmail);
        txtSetConfirmedEmail = (TextView) view.findViewById(tsv_setConfirmedEmail);
        TextView txtSetRecoveryQuestion = (TextView) view.findViewById(R.id.tsv_setRecoveryQuestion);
        TextView txtChangeHint = (TextView) view.findViewById(R.id.tsv_changeHint);
        TextView txtForgotPassword = (TextView) view.findViewById(R.id.txtForgotPassword);

        if (isFirstArrive) {

            prgWaiting.setVisibility(View.VISIBLE);
            new RequestUserTwoStepVerificationGetPasswordDetail().getPasswordDetail();
        } else {
            if (!isFirstSetPassword) {
                setSecondView();
            } else {
                setFirstView();
            }
        }


        view.findViewById(R.id.rootFragmentSecurity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        btnBack = (RippleView) view.findViewById(R.id.tsv_ripple_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                        popBackStackFragment();

                }
                closeKeyboard(v);
            }
        });

        txtResendConfirmEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestUserTwoStepVerificationResendVerifyEmail().ResendVerifyEmail();
                closeKeyboard(v);
                error(G.fragmentActivity.getResources().getString(R.string.resend_verify_email_code));
            }
        });

        TextView txtSetAdditionPassword = (TextView) view.findViewById(R.id.tsv_txtSetAdditionPassword);
        txtSetAdditionPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isFirstArrive = false;
                new HelperFragment(new FragmentSetSecurityPassword()).load();
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        bundle.putString("PATERN_EMAIL", txtPaternEmail);
                        bundle.putBoolean("IS_EMAIL", isRecoveryByEmail);
                        bundle.putBoolean("IS_CONFIRM_EMAIL", isConfirmedRecoveryEmail);

                        fragmentSecurityRecovery.setArguments(bundle);
                        new HelperFragment(fragmentSecurityRecovery).load();
                    }
                }).show();
            }
        });

        //page 1
        txtChangeHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                page = CHANGE_HINT;
                rootSetPassword.setVisibility(View.GONE);
                rootSetAdditionPassword.setVisibility(View.GONE);
                rootChangeHint.setVisibility(View.VISIBLE);
                txtTitleToolbar.setText(G.fragmentActivity.getString(R.string.password_hint));
                rippleOk.setVisibility(View.VISIBLE);

            }
        });

        txtSetConfirmedEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edtConfirmedEmail.setHint(mUnconfirmedEmailPattern);
                page = CONFIRM_EMAIL;
                rootSetPassword.setVisibility(View.GONE);
                rootSetAdditionPassword.setVisibility(View.GONE);
                rootConfirmedEmail.setVisibility(View.VISIBLE);
                txtTitleToolbar.setText(G.fragmentActivity.getString(R.string.confirm_email_title));
                rippleOk.setVisibility(View.VISIBLE);
            }
        });

        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isFirstArrive = false;
                FragmentSetSecurityPassword fragmentSetSecurityPassword = new FragmentSetSecurityPassword();
                Bundle bundle = new Bundle();
                bundle.putString("OLD_PASSWORD", password);
                fragmentSetSecurityPassword.setArguments(bundle);
                new HelperFragment(fragmentSetSecurityPassword).load();
            }
        });

        txtSetRecoveryEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                page = CHANGE_EMAIL;
                rootSetPassword.setVisibility(View.GONE);
                rootSetAdditionPassword.setVisibility(View.GONE);
                rootChangeEmail.setVisibility(View.VISIBLE);
                txtTitleToolbar.setText(G.fragmentActivity.getString(R.string.change_email_recovery));
                //rootConfirmedEmail.setVisibility(View.VISIBLE);
                rippleOk.setVisibility(View.VISIBLE);

            }
        });

        txtTurnPasswordOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        txtSetRecoveryQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = CHANGE_QUESTION;
                rootSetPassword.setVisibility(View.GONE);
                rootSetAdditionPassword.setVisibility(View.GONE);
                rootQuestionPassword.setVisibility(View.VISIBLE);
                txtTitleToolbar.setText(G.fragmentActivity.getString(R.string.title_toolbar_password_Question));
                rippleOk.setVisibility(View.VISIBLE);
            }
        });

        G.onTwoStepPassword = new OnTwoStepPassword() {
            @Override
            public void getPasswordDetail(final String questionOne, final String questionTwo, final String hint, final boolean hasConfirmedRecoveryEmail, final String unconfirmedEmailPattern) {

                txtQuestionOne = questionOne;
                txtQuestionTwo = questionTwo;
                txtPaternEmail = unconfirmedEmailPattern;
                isConfirmedRecoveryEmail = hasConfirmedRecoveryEmail;
                mUnconfirmedEmailPattern = unconfirmedEmailPattern;

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        prgWaiting.setVisibility(View.GONE);
                        if (questionOne.length() > 0 && questionTwo.length() > 0) {

                            rootSetPassword.setVisibility(View.GONE);
                            rootSetAdditionPassword.setVisibility(View.GONE);
                            root_ChangePassword.setVisibility(View.VISIBLE);
                            rootCheckPassword.setVisibility(View.VISIBLE);
                            rippleOk.setVisibility(View.VISIBLE);
                            edtCheckPassword.setHint(hint);
                            isFirstSetPassword = false;

                            if (unconfirmedEmailPattern.length() == 0) {
                                txtSetRecoveryEmail.setVisibility(View.VISIBLE);
                                txtSetConfirmedEmail.setVisibility(View.GONE);
                                lineConfirmView.setVisibility(View.GONE);
                                isSetRecoveryEmail = false;
                            } else {
                                txtSetRecoveryEmail.setVisibility(View.VISIBLE);
                                view.findViewById(R.id.tsv_viewRecoveryEmail).setVisibility(View.VISIBLE);
                                txtSetConfirmedEmail.setVisibility(View.VISIBLE);
                                lineConfirmView.setVisibility(View.VISIBLE);
                                isSetRecoveryEmail = true;

                            }

                        } else {//دوبار اجرا شده
                            rootSetPassword.setVisibility(View.VISIBLE);
                            rootSetAdditionPassword.setVisibility(View.VISIBLE);
                            root_ChangePassword.setVisibility(View.GONE);
                            rootCheckPassword.setVisibility(View.GONE);
                            rippleOk.setVisibility(View.GONE);
                            isFirstSetPassword = true;
                        }
                    }
                });
            }



            @Override
            public void errorGetPasswordDetail(final int majorCode, final int minorCode) {

                if (isAdded()) {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (majorCode == 188 && minorCode == 1) {  //USER DON'T SET A PASSWORD
                                setFirstView();
                            } else { // CAN'T CONNECT TO SERVER
                                //  G.fragmentActivity.getSupportFragmentManager().popBackStack();

                                popBackStackFragment();
                            }
                        }
                    });
                }
            }

            @Override
            public void timeOutGetPasswordDetail() {
                if (isAdded()) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // G.fragmentActivity.getSupportFragmentManager().popBackStack();

                            popBackStackFragment();
                        }
                    });
                }
            }

            @Override
            public void checkPassword() {
                if (isAdded()) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            rootSetPassword.setVisibility(View.VISIBLE);
                            rootCheckPassword.setVisibility(View.GONE);
                            rippleOk.setVisibility(View.GONE);
                        }
                    });
                }

            }

            @Override
            public void errorCheckPassword(final int getWait) {
                if (isAdded()) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialogWaitTime(getWait);
                        }
                    });
                }
            }

            @Override
            public void unSetPassword() {
                if (isAdded()) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            rootSetPassword.setVisibility(View.VISIBLE);
                            rootSetAdditionPassword.setVisibility(View.VISIBLE);
                            root_ChangePassword.setVisibility(View.GONE);
                            rootCheckPassword.setVisibility(View.GONE);
                            rippleOk.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void changeRecoveryQuestion() {
                if (isAdded()) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            viewChangeRecoveryQuestion();
                        }
                    });
                }
            }

            @Override
            public void changeHint() {
                if (isAdded()) {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            viewChangeHint();
                        }
                    });
                }
            }

            @Override
            public void changeEmail(final String unConfirmEmailPatern) {
                if (isAdded()) {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mUnconfirmedEmailPattern = unConfirmEmailPatern;
                            isSetRecoveryEmail = true;
                            viewChangeEmail();
                        }
                    });
                }

            }

            @Override
            public void confirmEmail() {
                if (isAdded()) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            viewConfirmEmail();
                        }
                    });
                }

            }

            @Override
            public void errorConfirmEmail() {
                if (isAdded()) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            error(G.fragmentActivity.getResources().getString(R.string.invalid_verify_email_code));
                        }
                    });
                }
            }

            @Override
            public void errorInvalidPassword() {
                if (isAdded()) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            error(G.fragmentActivity.getResources().getString(R.string.invalid_password));
                        }
                    });
                }
            }
        };



        // check password for enter on page
        rippleOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rootCheckPassword.getVisibility() == View.VISIBLE) {
                    if (edtCheckPassword.length() > 1) {
                        password = edtCheckPassword.getText().toString();
                        new RequestUserTwoStepVerificationCheckPassword().checkPassword(password);
                        closeKeyboard(v);
                        edtCheckPassword.setText("");
                    } else {
                        closeKeyboard(v);
                        error(G.fragmentActivity.getResources().getString(R.string.please_set_password));
                    }
                    return;
                }

                //change Question

                if (rootQuestionPassword.getVisibility() == View.VISIBLE) {
                    if (edtSetQuestionPassOne.length() > 0 && edtSetAnswerPassOne.length() > 0 && edtSetQuestionPassTwo.length() > 0 && edtSetAnswerPassTwo.length() > 0) {
                        new RequestUserTwoStepVerificationChangeRecoveryQuestion().changeRecoveryQuestion(password, edtSetQuestionPassOne.getText().toString(), edtSetAnswerPassOne.getText().toString(), edtSetQuestionPassTwo.getText().toString(), edtSetAnswerPassTwo.getText().toString());
                        closeKeyboard(v);
                        edtSetQuestionPassOne.setText("");
                        edtSetAnswerPassOne.setText("");
                        edtSetQuestionPassTwo.setText("");
                        edtSetAnswerPassTwo.setText("");
                    } else {
                        closeKeyboard(v);
                        error(G.fragmentActivity.getResources().getString(R.string.Please_complete_all_Item));
                    }
                    return;
                }

                //change email
                if (rootChangeEmail.getVisibility() == View.VISIBLE) {
                    if (edtSetEmail != null && edtSetEmail.length() > 0 && password != null && password.length() > 1) {

                        Pattern EMAIL_ADDRESS = patternEmail();
                        if (EMAIL_ADDRESS.matcher(edtSetEmail.getText().toString()).matches()) {
                            new RequestUserTwoStepVerificationChangeRecoveryEmail().changeRecoveryEmail(password, edtSetEmail.getText().toString());
                            closeKeyboard(v);
                            edtSetEmail.setText("");
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
                if (rootChangeHint.getVisibility() == View.VISIBLE) {
                    if (edtChangeHint.length() > 0) {
                        if (!password.equals(edtChangeHint.getText().toString())) {
                            new RequestUserTwoStepVerificationChangeHint().changeHint(password, edtChangeHint.getText().toString());
                            closeKeyboard(v);
                            edtChangeHint.setText("");
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
                if (rootConfirmedEmail.getVisibility() == View.VISIBLE) {
                    if (edtConfirmedEmail.length() > 0) {
                        new RequestUserTwoStepVerificationVerifyRecoveryEmail().recoveryEmail(edtConfirmedEmail.getText().toString());
                        txtPaternEmail = edtConfirmedEmail.getText().toString();
                        edtConfirmedEmail.setText("");
                        closeKeyboard(v);
                    } else {
                        closeKeyboard(v);
                        error(G.fragmentActivity.getResources().getString(R.string.please_enter_code));
                    }
                }
            }
        });

    }

    private void setFirstView() {
        if (isAdded()) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {

                    rootSetPassword.setVisibility(View.VISIBLE);
                    rootSetAdditionPassword.setVisibility(View.VISIBLE);
                    root_ChangePassword.setVisibility(View.GONE);
                    rootCheckPassword.setVisibility(View.GONE);
                    rippleOk.setVisibility(View.GONE);
                    prgWaiting.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setSecondView() {
        if (isAdded()) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    rootSetPassword.setVisibility(View.VISIBLE);
                    root_ChangePassword.setVisibility(View.VISIBLE);
                    rootSetAdditionPassword.setVisibility(View.GONE);
                    rootCheckPassword.setVisibility(View.GONE);
                    rippleOk.setVisibility(View.GONE);
                    prgWaiting.setVisibility(View.GONE);
                    if (!isSetRecoveryEmail) {
                        txtSetConfirmedEmail.setVisibility(View.GONE);
                        lineConfirmView.setVisibility(View.GONE);
                    } else {
                        txtSetConfirmedEmail.setVisibility(View.VISIBLE);
                        lineConfirmView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }

    private void viewConfirmEmail() {
        if (isAdded()) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    page = 0;
                    rootSetPassword.setVisibility(View.VISIBLE);
                    rootConfirmedEmail.setVisibility(View.GONE);
                    txtTitleToolbar.setText(G.fragmentActivity.getString(R.string.two_step_verification_title));
                    rippleOk.setVisibility(View.GONE);
                }
            });
        }
    }

    private void viewChangeEmail() {
        if (isAdded()) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    page = 0;
                    rootSetPassword.setVisibility(View.VISIBLE);
                    rootConfirmedEmail.setVisibility(View.GONE);
                    rootChangeEmail.setVisibility(View.GONE);
                    txtTitleToolbar.setText(G.fragmentActivity.getString(R.string.two_step_verification_title));
                    rippleOk.setVisibility(View.GONE);
                    if (mUnconfirmedEmailPattern.length() > 0) {
                        txtSetConfirmedEmail.setVisibility(View.VISIBLE);
                        lineConfirmView.setVisibility(View.VISIBLE);
                    } else {
                        txtSetConfirmedEmail.setVisibility(View.GONE);
                        lineConfirmView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void viewChangeHint() {
        if (isAdded()) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    page = 0;
                    rootSetPassword.setVisibility(View.VISIBLE);
                    rootChangeHint.setVisibility(View.GONE);
                    txtTitleToolbar.setText(G.fragmentActivity.getString(R.string.two_step_verification_title));
                    rippleOk.setVisibility(View.GONE);
                }
            });
        }
    }

    private void viewChangeRecoveryQuestion() {
        if (isAdded()) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    page = 0;
                    rootSetPassword.setVisibility(View.VISIBLE);
                    rootQuestionPassword.setVisibility(View.GONE);
                    txtTitleToolbar.setText(G.fragmentActivity.getString(R.string.two_step_verification_title));
                    rippleOk.setVisibility(View.GONE);
                }
            });
        }
    }

    private void closeKeyboard(View v) {
        if (isAdded()) {
            try {
                InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    private void error(String error) {
        if (isAdded()) {
            try {
                Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                vShort.vibrate(200);
                final Snackbar snack = Snackbar.make(G.fragmentActivity.findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
                snack.setAction(G.fragmentActivity.getResources().getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    private void dialogWaitTime(long time) {
        boolean wrapInScrollView = true;
        if (isAdded()) {

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
    }

    private Pattern patternEmail() {
        return Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{2,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,25}" + ")+");
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
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
                    btnBack.performClick();
                    return true;
                }
                return false;
            }
        });
    }
}
