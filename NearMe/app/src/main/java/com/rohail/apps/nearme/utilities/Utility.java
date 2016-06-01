package com.rohail.apps.nearme.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    public static boolean haveInternet(Context con) {

        ConnectivityManager connectivity = (ConnectivityManager) con
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static boolean isNull(String edittxt) {
        if (edittxt != null && !edittxt.equals("null") && !edittxt.equals(""))
            return false;
        else
            return true;
    }

    public static String getFourDigitAccountNum(String pan) {
        if (pan.equals("") || pan.equals("null") || pan == null) {
            return "";
        } else {
            pan = pan.substring(pan.length() - 4, pan.length());
            pan = "XXXX XXXX XXXX " + pan;
            return pan;
        }
    }


    public static void hideKeyboard(View view, Context con) {
        InputMethodManager imm = (InputMethodManager) con
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static final boolean containsNumeric(String text) {
        return text.matches(".*\\d+.*");
    }

    public static final boolean containsAlpha(String text) {
        return text.matches(".*[a-zA-Z]+.*");
    }

    public static void setCurrency_format(String currency, TextView tv) {
        String[] parts = currency.split("\\.");
        if (parts[0] != null && parts[1] != null) {
            tv.setText(Html.fromHtml("PKR " + parts[0] + "<sup>.</sup>"
                    + "<sup><small>" + parts[1] + "</small></sup>"));
        } else {
            tv.setText(currency);
        }
    }

    public static void setCurrency_format_rounded(String currency, TextView tv) {
        String[] parts = currency.split("\\.");
        if (parts[0] != null && parts[1] != null) {
            tv.setText(Html
                    .fromHtml("<small>" + "PKR " + "</small>" + parts[0]));
        } else {
            tv.setText(currency);
        }
    }

    public static Spanned getCurrency_format(String currency) {
        try {
            String[] parts = currency.split("\\.");

            if (parts[1].length() == 1) {
                parts[1] = parts[1] + "0";
            } else if (parts[1].length() >= 2) {
                parts[1] = parts[1].substring(0, 1);
            }

            return Html.fromHtml("<p>" + parts[0] + "." + parts[1]
                    + "<sub><small>" + " PKR" + "</small></sub>" + "</p>");
        } catch (Exception e) {
            return Html.fromHtml("0");
        }

    }

    public static String getAmountLimitMsg(String min, String max, Boolean less) {
        if (less) {
            return "Amount entered (" + min
                    + ") is less than minimum Product limit (" + max + ").";
        } else {
            return "Amount entered (" + min
                    + ") is greater than maximum Product limit (" + max + ").";
        }
    }

    public static Spanned getCurrency_format(String currency,
                                             String pCurrencyUnit) {
        String[] parts = currency.split("\\.");

        if (parts[1].length() == 1) {
            parts[1] = parts[1] + "0";
        } else if (parts[1].length() >= 2) {
            parts[1] = parts[1].substring(0, 1);
        }

        return Html.fromHtml("<p>" + parts[0] + "." + parts[1] + "<sub><small>"
                + " " + pCurrencyUnit + "</small></sub>" + "</p>");
    }

    public static String getUnFormattedAmount(String amt) {
        if (amt != null) {
            amt = amt.replaceAll("[^\\d.]", "");
            return amt;
        } else {
            return "";
        }

    }

    public static boolean haveNetworkConnection(Context con) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) con
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static int getScaledPixels(int i, Activity context) {
        // measuring pixels
        int pixel = i;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pixel * scale + 0.5f);
    }

    public static File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "HBL_POS_APP");
    }

    @SuppressLint("SimpleDateFormat")
    public static String getFormattedDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getFormattedTime() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm aaa");
        return dateFormat.format(new Date());
    }

    public static double distanceBetween(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        System.out.println(dist);
        return dist;
    }
}
