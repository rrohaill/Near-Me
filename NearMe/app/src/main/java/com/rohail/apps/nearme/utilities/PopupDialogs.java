package com.rohail.apps.nearme.utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rohail.apps.nearme.R;


public class PopupDialogs {
    private static Dialog dialog;

    // private static Dialog dialogStatic;

    public static Dialog createAlertDialog(final String reason,
                                           final String title, final Context context,
                                           View.OnClickListener listener, Status status) {

        dialog = createDialog(context, status, false, R.layout.dialog_alert);

        TextView heading = (TextView) dialog
                .findViewById(R.id.textViewAlertHeading);
        TextView message = (TextView) dialog.findViewById(R.id.textViewMessage);

        heading.setText(title);
        message.setText(reason);

        Button btnCancel = (Button) dialog.findViewById(R.id.btnOK);
        btnCancel.setOnClickListener(listener);
        return dialog;
    }

    public static Dialog createAlertDialog(final String reason,
                                           final String title, final Context context, Status status) {
        dialog = createDialog(context, status, true, R.layout.dialog_alert);

        TextView heading = (TextView) dialog
                .findViewById(R.id.textViewAlertHeading);
        TextView message = (TextView) dialog.findViewById(R.id.textViewMessage);

        heading.setText(title);
        message.setText(reason);

        Button btnOk = (Button) dialog.findViewById(R.id.btnOK);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static void createAlertDialog(final String reason,
                                         final String title, final Context context, Status status,
                                         final Dialog parentDialog) {
        dialog = createDialog(context, status, true, R.layout.dialog_alert);

        TextView heading = (TextView) dialog
                .findViewById(R.id.textViewAlertHeading);
        TextView message = (TextView) dialog.findViewById(R.id.textViewMessage);

        heading.setText(title);
        message.setText(reason);

        Button btnOk = (Button) dialog.findViewById(R.id.btnOK);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (parentDialog != null) {
                    dialog = parentDialog;
                    dialog.show();
                }
            }
        });
    }

    public static void createConfirmationDialog(final String reason,
                                                final String title, final Context context,
                                                View.OnClickListener listner_yes) {

        dialog = createDialog(context, Status.INFO, false,
                R.layout.dialog_confirm);

        TextView heading = (TextView) dialog
                .findViewById(R.id.textViewAlertHeading);
        TextView message = (TextView) dialog.findViewById(R.id.textViewMessage);

        heading.setText(title);
        message.setText(reason);

        Button btyes = (Button) dialog.findViewById(R.id.btnYes);
        btyes.setOnClickListener(listner_yes);
        Button btnno = (Button) dialog.findViewById(R.id.btnNo);
        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public static Dialog createConfirmationDialog(final String reason,
                                                  final String title, final Context context,
                                                  View.OnClickListener yesListener, String yesTitle,
                                                  Status status) {

        dialog = createDialog(context, status, false, R.layout.dialog_confirm);
        TextView heading = (TextView) dialog
                .findViewById(R.id.textViewAlertHeading);
        TextView message = (TextView) dialog.findViewById(R.id.textViewMessage);

        heading.setText(title);
        message.setText(reason);

        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
        btnYes.setText(yesTitle);
        btnYes.setOnClickListener(yesListener);
        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static Dialog createConfirmationDialog(final String reason,
                                                  final String title, final Context context,
                                                  View.OnClickListener yesListener, String yesTitle,
                                                  String noTitle, Status status) {

        dialog = createDialog(context, status, false, R.layout.dialog_confirm);

        TextView heading = (TextView) dialog
                .findViewById(R.id.textViewAlertHeading);
        TextView message = (TextView) dialog.findViewById(R.id.textViewMessage);

        heading.setText(title);
        message.setText(reason);

        Button btYes = (Button) dialog.findViewById(R.id.btnYes);
        btYes.setText(yesTitle);
        btYes.setOnClickListener(yesListener);

        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        btnNo.setText(noTitle);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static Dialog createConfirmationDialog(final String reason,
                                                  final String title, final Context context,
                                                  View.OnClickListener yesListener, String yesTitle,
                                                  View.OnClickListener noListener, String noTitle,
                                                  Status status) {
        dialog = createDialog(context, status, false, R.layout.dialog_confirm);

        TextView heading = (TextView) dialog
                .findViewById(R.id.textViewAlertHeading);
        TextView message = (TextView) dialog.findViewById(R.id.textViewMessage);

        heading.setText(title);
        message.setText(reason);

        ScrollView scView = (ScrollView) dialog.findViewById(R.id.scroll_view);
        scView.fullScroll(ScrollView.FOCUS_UP);
        scView.smoothScrollTo(0, 0);
        scView.scrollTo(0, 0);

        Button btYes = (Button) dialog.findViewById(R.id.btnYes);
        btYes.setText(yesTitle);
        btYes.setOnClickListener(yesListener);
        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        btnNo.setText(noTitle);
        btnNo.setOnClickListener(noListener);
        return dialog;
    }

    public static Dialog createListDialog(final String title, final String list[], final Context context, ListView.OnItemClickListener listener,
                                          Status status) {

        RadioGroup radioGroup = null;
        dialog = createDialog(context, status, false,
                R.layout.dialog_list_searchable);

        TextView heading = (TextView) dialog
                .findViewById(R.id.textViewAlertHeading);
        EditText etSearch = (EditText) dialog.findViewById(R.id.etSearch);
        ListView listPlaceTypes = (ListView) dialog.findViewById(R.id.listView);

        heading.setText(title);

        final ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.list_item_place_types, R.id.tvItem, list);
        listPlaceTypes.setAdapter(adapter);

        listPlaceTypes.setOnItemClickListener(listener);
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        return dialog;
    }

    public static Dialog createRadioDialog(final String title,
                                           OnCheckedChangeListener listner, final Context context,
                                           Status status, int radioId) {

        RadioGroup radioGroup = null;
        dialog = createDialog(context, status, false,
                R.layout.dialog_locator_layout);

        TextView heading = (TextView) dialog
                .findViewById(R.id.textViewAlertHeading);

        heading.setText(title);

        if (status.equals(Status.PLACE)) {
            radioGroup = (RadioGroup) dialog
                    .findViewById(R.id.rgPlaceOptions);
            DisplayMetrics displayMetrics = context.getResources()
                    .getDisplayMetrics();
            int dialogHeight = (int) context.getResources().getDimension(
                    R.dimen.dialogBranchLocatorHeight);
            int height = (int) (TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, dialogHeight, displayMetrics));
//            dialog.getWindow().setLayout(
//                    RelativeLayout.LayoutParams.MATCH_PARENT, height);

            RelativeLayout layoutHeader = (RelativeLayout) dialog
                    .findViewById(R.id.layoutHeader);
            height = (int) (TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 60, displayMetrics));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, height);
            int margin = (int) (TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 50, displayMetrics));
            params.topMargin = margin;
//            layoutHeader.setLayoutParams(params);
        } else if (status.equals(Status.DISTANCE)) {
            radioGroup = (RadioGroup) dialog
                    .findViewById(R.id.rgDistanceOptions);
        }

        radioGroup.setVisibility(View.VISIBLE);
        radioGroup.setOnCheckedChangeListener(listner);
        radioGroup.check(radioId);
        return dialog;
    }

    @SuppressWarnings("ResourceAsColor")
    private static Dialog createDialog(final Context context, Status status,
                                       Boolean cancel, int dialogId) {
        dialog = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar) {

            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(R.color.dialog_background));
        dialog.setContentView(dialogId);
        dialog.setCancelable(cancel);

//        ImageView headerImage = (ImageView) dialog
//                .findViewById(R.id.imageViewAlert);
        RelativeLayout layoutHeader = (RelativeLayout) dialog
                .findViewById(R.id.layoutHeader);

        switch (status) {
            case INFO:
//                headerImage.setImageResource(R.drawable.common_full_open_on_phone);
                break;

            case SUCCESS:
//                headerImage.setImageResource(R.drawable.common_full_open_on_phone);
                break;

            case ERROR:
//                headerImage.setImageResource(R.drawable.common_full_open_on_phone);
                break;

            case ALERT:
//                headerImage.setImageResource(R.drawable.common_full_open_on_phone);
                break;

            case LOCK:
//                headerImage.setImageResource(R.drawable.common_full_open_on_phone);
                break;

            default:
                break;
        }

        layoutHeader.setBackgroundResource(R.color.red);
        try {
            dialog.show();
        } catch (BadTokenException btexp) {
            btexp.printStackTrace();
            Logger.e(btexp);
        }

        return dialog;
    }

    public enum Status {
        INFO, SUCCESS, ERROR, ALERT, LOCATOR, DISTANCE, LOCK, PLACE;
    }

    /**
     * Call Back listeners
     */
    public interface OnCustomClickListener {
        public void onClick(View v, Object obj);
    }
}