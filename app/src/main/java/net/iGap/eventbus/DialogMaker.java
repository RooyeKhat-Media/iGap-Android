package net.iGap.eventbus;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.Window;

import net.iGap.R;


public class DialogMaker {

    private static Dialog dialog;

    @NonNull
    public static DialogMaker makeDialog(Context context) {
        DialogMaker dialogMaker = new DialogMaker();
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.gif_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }
        });

        return dialogMaker;
    }

    public static void disMissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void showDialog() {
        dialog.show();
    }


}
