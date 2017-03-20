package com.wisdomrouter.app.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wisdomrouter.app.R;

/**
 * 所有提示信息
 *
 * @author Monica
 */
public class WarnUtils {
    public static final void showDialog(Context context, String text) {
        new AlertDialog.Builder(context)
                .setMessage(text)
                .setNegativeButton("关闭", null).create().show();
    }

    public interface OnClickListener extends DialogInterface.OnClickListener {

    }

    public static final void toast(Context context, int textId) {
        toast(context, context.getResources().getString(textId));
    }

    public static final void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    public static void showToast(final Activity activity, final String word, final long time){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(activity, word, Toast.LENGTH_LONG);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        toast.cancel();
                    }
                }, time);
            }
        });
    }

    public static final void toast(Context context, String text, int score) {
        Toast toast = Toast.makeText(context,
                (text+ "  +"+score ), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(context);
        imageCodeProject.setImageResource(R.drawable.my_score_ic);
        toastView.addView(imageCodeProject, 0);
        toast.show();
    }
}
