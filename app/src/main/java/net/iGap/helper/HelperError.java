/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap.helper;

import android.support.design.widget.Snackbar;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.WebSocketClient;
import net.iGap.module.AppUtils;
import net.iGap.module.LoginActions;

public class HelperError {

    /**
     * show all error in app
     */

    private static final int needTime = 5000;
    private static long currentTime;

    public static String getErrorFromCode(int majorCode, int minorCode) {

        String error = "";
        switch (majorCode) {
            case 2:
                if (minorCode == 1) {
                    G.userLogin = false;
                    error = G.fragmentActivity.getResources().getString(R.string.E_2);
                    LoginActions.login();
                }
                break;
            case 3: //Error 3 - NEW_CLIENT_IN_SESSION (New client connected in this session , so you will be kicked out)
                G.allowForConnect = false;
                break;
            case 5:
                //if (minorCode == 1) error = "time out  server not response";
                break;
            case 7:
                WebSocketClient.getInstance().disconnect();
                break;
            case 9:
                /*if (G.currentActivity != null) {
                    G.currentActivity.finish();
                }
                Intent intent = new Intent(G.context, ActivityProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                G.context.startActivity(intent);*/
                break;
            case 109:
                error = G.fragmentActivity.getResources().getString(R.string.E_109);
                HelperLogout.logout();
                break;
            case 110:
                error = G.fragmentActivity.getResources().getString(R.string.E_110);
                break;
            case 111:
                if (minorCode != 4) {
                    HelperLogout.logout();
                } else {
                    error = G.fragmentActivity.getResources().getString(R.string.E_111);
                }
                break;
            case 112:
                error = G.fragmentActivity.getResources().getString(R.string.E_112);
                break;
            case 113:
                error = G.fragmentActivity.getResources().getString(R.string.E_113);
                break;
            case 114:
                error = G.fragmentActivity.getResources().getString(R.string.E_114);
                break;
            case 115:
                error = G.fragmentActivity.getResources().getString(R.string.E_115);
                break;
            case 116:
                error = G.fragmentActivity.getResources().getString(R.string.E_116);
                break;
            case 122:
                error = G.fragmentActivity.getResources().getString(R.string.E_122);
                break;
            case 123:
                error = G.fragmentActivity.getResources().getString(R.string.E_123);
                break;
            case 124:
                if (minorCode == 1) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_124_1);
                } else if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_124_2);
                } else if (minorCode == 3) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_124_3);
                } else if (minorCode == 4) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_124_4);
                }
                break;
            case 125:
                error = G.fragmentActivity.getResources().getString(R.string.E_125);
                break;
            case 154:
                if (minorCode == 1) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_154_1);
                } else if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_154_2);
                }
                break;

            case 155:
                error = G.fragmentActivity.getResources().getString(R.string.E_155);
                break;
            case 156:
                error = G.fragmentActivity.getResources().getString(R.string.E_156);
                break;
            case 157:
                error = G.fragmentActivity.getResources().getString(R.string.E_157);
                break;
            case 158:
                error = G.fragmentActivity.getResources().getString(R.string.E_158);
                break;

            case 163:
                //error = G.fragmentActivity.getResources().getString(R.string.normal_error) + 163;
                break;

            case 173:
                error = G.fragmentActivity.getResources().getString(R.string.error);
                break;
            case 174:
                error = G.fragmentActivity.getResources().getString(R.string.error);
                break;
            case 194:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_password);
                break;

            case 200:
                error = G.fragmentActivity.getResources().getString(R.string.E_200);
                break;
            case 201:
                error = G.fragmentActivity.getResources().getString(R.string.E_201);
                break;
            case 202:
                error = G.fragmentActivity.getResources().getString(R.string.E_202);
                break;

            case 209:
                if (minorCode == 1) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_209_1);
                } else if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_209_2);
                } else if (minorCode == 3) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_209_3);
                }
                break;
            case 210:
                error = G.fragmentActivity.getResources().getString(R.string.E_210);
                break;
            case 211:
                error = G.fragmentActivity.getResources().getString(R.string.E_211);
                break;
            case 212:
                if (minorCode == 1) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_212_1);
                } else if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_212_1);
                }
                break;

            case 213:
                if (minorCode == 1)
                    error = G.fragmentActivity.getResources().getString(R.string.E_213);
                break;
            case 214:
                if (minorCode == 1)
                    error = G.fragmentActivity.getResources().getString(R.string.E_214);
                break;
            case 218:
                if (minorCode == 1)
                    error = G.fragmentActivity.getResources().getString(R.string.E_218);
                break;
            case 219:
                error = G.fragmentActivity.getResources().getString(R.string.E_219);
                break;
            case 220:
                error = G.fragmentActivity.getResources().getString(R.string.E_220);
                break;
            case 233:
                if (minorCode == 1) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_233_1);
                }

                break;
            case 300:
                if (minorCode == 1) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_300_1);
                } else if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_300_2);
                }
                break;
            case 301:
                error = G.fragmentActivity.getResources().getString(R.string.E_301);
                break;

            case 302:
                if (minorCode == 1) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_302_1);
                } else if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_302_2);
                } else if (minorCode == 3) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_302_3);
                } else if (minorCode == 4) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_302_4);
                }
                break;
            case 303:
                error = G.fragmentActivity.getResources().getString(R.string.E_303);
                break;
            case 304:
                error = G.fragmentActivity.getResources().getString(R.string.E_304);
                break;
            case 305:
                error = G.fragmentActivity.getResources().getString(R.string.E_305);
                break;

            case 3356:
                G.fragmentActivity.getResources().getString(R.string.just_owner_can_delete);
                break;
            case 357:
                G.fragmentActivity.getResources().getString(R.string.just_owner_can_delete);
                break;
            case 358:
                G.fragmentActivity.getResources().getString(R.string.just_owner_can_delete);
                break;

            case 318:
                if (minorCode == 1) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_318_1);
                } else if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_318_2);
                }
                break;
            case 319:
                error = G.fragmentActivity.getResources().getString(R.string.E_319);
                break;
            case 320:
                error = G.fragmentActivity.getResources().getString(R.string.E_320);
                break;
            case 321:
                error = G.fragmentActivity.getResources().getString(R.string.E_321);
                break;
            case 322:
                error = G.fragmentActivity.getResources().getString(R.string.E_322);
                break;
            case 323:
                error = G.fragmentActivity.getResources().getString(R.string.E_323);
                break;
            case 324:
                if (minorCode == 1) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_324_1);
                } else if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_324_2);
                }
                break;
            case 325:
                error = G.fragmentActivity.getResources().getString(R.string.E_325);
                break;
            case 326:
                error = G.fragmentActivity.getResources().getString(R.string.E_326);
                break;

            case 327:
                if (minorCode == 1) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_327_A);
                } else if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_327_B);
                }
                break;
            case 328:
                error = G.fragmentActivity.getResources().getString(R.string.E_328);
                break;
            case 329:
                error = G.fragmentActivity.getResources().getString(R.string.E_329);
                break;

            case 330:
                if (minorCode == 1) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_330_1);
                } else if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_330_2);
                } else if (minorCode == 3) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_330_3);
                }
                break;
            case 331:
                if (minorCode == 1)
                    error = G.fragmentActivity.getResources().getString(R.string.E_331);
                break;
            case 332:
                if (minorCode == 1) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_332_1);
                } else if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_332_2);
                }
                break;

            case 333:
                if (minorCode == 1)
                    error = G.fragmentActivity.getResources().getString(R.string.E_333);
                break;
            case 334:
                if (minorCode == 1)
                    error = G.fragmentActivity.getResources().getString(R.string.E_334);
                break;
            case 335:
                error = G.fragmentActivity.getResources().getString(R.string.E_335);
                break;
            case 336:
                if (minorCode == 1)
                    error = G.fragmentActivity.getResources().getString(R.string.E_336);
                break;
            case 337:
                if (minorCode == 1)
                    error = G.fragmentActivity.getResources().getString(R.string.E_337);
                break;


            case 373:

                //error = G.fragmentActivity.getResources().getString(R.string.normal_error) + 373;

                break;
            case 374:
                //error = G.fragmentActivity.getResources().getString(R.string.normal_error) + 374;
                break;
            case 375:
                //error = G.fragmentActivity.getResources().getString(R.string.normal_error) + 375;
                break;

            case 379:
                if (currentTime + needTime < System.currentTimeMillis()) { // show error each 5 secend
                    error = G.fragmentActivity.getResources().getString(R.string.E_379);
                }
                currentTime = System.currentTimeMillis();


                break;


            case 453:
                //error = G.fragmentActivity.getResources().getString(R.string.normal_error) + 453;
                break;
            case 455:
                //error = G.fragmentActivity.getResources().getString(R.string.normal_error) + 455;
                break;
            case 456:
                //error = G.fragmentActivity.getResources().getString(R.string.normal_error) + 456;
                break;
            case 454:
                //error = G.fragmentActivity.getResources().getString(R.string.normal_error) + 454;
                break;
            case 457:
                //error = G.fragmentActivity.getResources().getString(R.string.normal_error) + 457;
                break;
            case 458:
                //error = G.fragmentActivity.getResources().getString(R.string.normal_error) + 458;
                break;

            case 467:
                //error = G.fragmentActivity.getResources().getString(R.string.normal_error) + 467;
                break;
            case 468:
                //error = G.fragmentActivity.getResources().getString(R.string.normal_error) + 468;
                break;
            case 469:
                //error = G.fragmentActivity.getResources().getString(R.string.normal_error) + 469 + " minor : " + minorCode;
                break;
            case 405:
                if (currentTime + needTime < System.currentTimeMillis()) { // show error each 5 secend
                    error = G.fragmentActivity.getResources().getString(R.string.E_478);
                }
                currentTime = System.currentTimeMillis();

                break;

            case 478:
                if (currentTime + needTime < System.currentTimeMillis()) { // show error each 5 secend
                    error = G.fragmentActivity.getResources().getString(R.string.E_478);
                }
                currentTime = System.currentTimeMillis();


                break;

            case 500:
                if (minorCode == 1)
                    error = G.fragmentActivity.getResources().getString(R.string.Toast_Location_Not_Found);
                break;
            case 502:
                if (minorCode == 1)
                    error = G.fragmentActivity.getResources().getString(R.string.Toast_Location_Not_Found);
                break;
            case 503:
                if (minorCode == 1)
                    error = G.fragmentActivity.getResources().getString(R.string.Toast_Location_Not_Found);
                break;

            case 610:
                error = G.fragmentActivity.getResources().getString(R.string.E_610);
                break;
            case 611:
                error = G.fragmentActivity.getResources().getString(R.string.E_611);
                break;
            case 612:
                error = G.fragmentActivity.getResources().getString(R.string.E_612);
                break;
            case 613:
                error = G.fragmentActivity.getResources().getString(R.string.E_613);
                break;
            case 614:
                //error = G.fragmentActivity.getResources().getString(R.string.E_614);
                break;
            case 615:
                if (minorCode == 1) {
                    //error = G.fragmentActivity.getResources().getString(R.string.E_615_1);
                } else if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_615_2);
                }
                break;

            case 616:
                error = G.fragmentActivity.getResources().getString(R.string.E_616);
                break;
            case 617:
                error = "";// G.fragmentActivity.getResources().getString(R.string.E_617);
                break;
            case 620:
                error = "";// G.fragmentActivity.getResources().getString(R.string.there_is_no_sheared_media);
                break;
            case 623:
                if (minorCode == 1)
                    error = G.fragmentActivity.getResources().getString(R.string.E_623);
                break;
            case 629:
                if (minorCode == 1) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_713_1);
                } else if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_713_1);
                } else if (minorCode == 3)
                    error = G.fragmentActivity.getResources().getString(R.string.E_713_1);
                break;
            case 638:
                if (minorCode == 1)
                    error = G.fragmentActivity.getResources().getString(R.string.E_713_1);
                break;

            case 658:
                error = G.fragmentActivity.getResources().getString(R.string.E_658);
                break;

            case 659:
                error = G.fragmentActivity.getResources().getString(R.string.E_659);
                break;

            case 713:
                if (minorCode == 1) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_713_1);
                } else if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_713_2);
                } else if (minorCode == 3) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_713_3);
                } else if (minorCode == 4) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_713_4);
                } else if (minorCode == 5) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_713_5);
                }
                break;
            case 714:
                error = G.fragmentActivity.getResources().getString(R.string.E_714);
                break;
            case 715:
                error = G.fragmentActivity.getResources().getString(R.string.E_715);
                break;

            case 9001:
                //error = G.fragmentActivity.getResources().getString(R.string.E_9001);
                break;
            case 9002:
                error = G.fragmentActivity.getResources().getString(R.string.E_9002);
                break;
            case 9003:
                //error = G.fragmentActivity.getResources().getString(R.string.E_9003);
                break;
            case 9004:
                //error = G.fragmentActivity.getResources().getString(R.string.E_9004);
                break;
            case 9005:
                //error = G.fragmentActivity.getResources().getString(R.string.E_9005);
                break;
            case 9006:
                error = G.fragmentActivity.getResources().getString(R.string.E_9006);
                break;
            case 9007:
                error = G.fragmentActivity.getResources().getString(R.string.E_9007);
                break;
            case 9008:
                error = G.fragmentActivity.getResources().getString(R.string.E_9008);
                break;
            case 9009:
                error = G.fragmentActivity.getResources().getString(R.string.E_9009);
                break;
            case 9010:
                error = G.fragmentActivity.getResources().getString(R.string.E_9010);
                break;
            case 9011:
                //error = G.fragmentActivity.getResources().getString(R.string.E_9011);
                break;
            case 9012:
                //error = G.fragmentActivity.getResources().getString(R.string.E_9012);
                break;
            case 9013:
                error = G.fragmentActivity.getResources().getString(R.string.E_9013);
                break;


            case 10105:
                if (minorCode == 101) {
                    error = G.fragmentActivity.getResources().getString(R.string.E_713_1);
                }

                break;

            case 10108:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_verify_email_code);
                break;

            case 10109:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_verify_email_code);
                break;

            case 10110:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_verify_email_code);
                break;

            case 10111:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_verify_email_code);
                break;

            case 10113:

                if (minorCode == 2) {
                    error = G.fragmentActivity.getResources().getString(R.string.invalid_verify_email_code);
                } else {
                    error = G.fragmentActivity.getResources().getString(R.string.invalid_verify_email_code);
                }
                break;

            case 10114:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_email_token);
                break;

            case 10115:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_email_token);
                break;
            case 10116:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_email_token);
                break;

            case 10117:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_email_token);
                break;
            case 10118:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_email_token);
                break;
            case 10119:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_email_token);
                break;

            case 10129: {
                error = G.fragmentActivity.getResources().getString(R.string.invalid_email_token);
                break;
            }

            case 10134:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_question_token);
                break;

            case 10136:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_question_token);
                break;
            case 10137:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_question_token);
                break;
            case 10138:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_question_token);
                break;
            case 10139:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_question_token);
                break;
            case 10140:
                error = G.fragmentActivity.getResources().getString(R.string.invalid_question_token);
                break;

            case 10165:

                error = G.fragmentActivity.getResources().getString(R.string.E_10165);

                break;

            case 10166:

                error = G.fragmentActivity.getResources().getString(R.string.E_10166);

                break;
            case 10167:

                error = G.fragmentActivity.getResources().getString(R.string.E_10167);

                break;
            case 10168:

                error = G.fragmentActivity.getResources().getString(R.string.E_10168);

                break;
            case 99999: // client errors
                error = "Offset is negative";
            case -1: // client errors
                error = G.fragmentActivity.getResources().getString(R.string.please_try_again);
                break;
        }

        return error;
    }

    /**
     * use this method for detect text internal client error
     */
    public static String getClientErrorCode(int majorCode, int minorCode) {
        String error = "";
        switch (majorCode) {
            case -2:
                error = G.fragmentActivity.getResources().getString(R.string.room_not_exist);
                break;
        }
        return error;
    }

    public static void showSnackMessage(final String message, boolean isVibrate) {

        if (isVibrate) {
            AppUtils.setVibrator(200);
        }

        if (message.length() > 0 && G.currentActivity != null) {
            G.currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack = Snackbar.make(G.currentActivity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        }
    }
}