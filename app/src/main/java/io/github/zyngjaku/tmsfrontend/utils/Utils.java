package io.github.zyngjaku.tmsfrontend.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.github.zyngjaku.tmsfrontend.R;

public class Utils {
    public static String API_ADDRESS = "http://192.168.1.64:8080/api";

    public static void linearLayoutSetMargins(LinearLayout linearLayout, int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        params.setMargins(0, 50, 0, 0);
        linearLayout.setLayoutParams(params);
    }

    public static Dialog createDialog(Activity activity, int layoutId, boolean isCancelable, String title, int contentLayoutId){
        Dialog dialog = createDialog(activity, layoutId, isCancelable, title);

        linearLayoutSetMargins((LinearLayout) dialog.findViewById(R.id.buttonsLinearLayout), 0, 20, 0, 0);

        RelativeLayout mainLayout = (RelativeLayout) dialog.findViewById(R.id.contentLayoutInclude);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(contentLayoutId, null);

        mainLayout.removeAllViews();
        mainLayout.addView(layout);

        return dialog;
    }


    public static Dialog createDialog(Activity activity, int layoutId, boolean isCancelable, String title){
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(isCancelable);
        dialog.setContentView(layoutId);

        TextView titleTextView = dialog.findViewById(R.id.titleTextView);
        titleTextView.setText(title);

        return dialog;
    }
}
