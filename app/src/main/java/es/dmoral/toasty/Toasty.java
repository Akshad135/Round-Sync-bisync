package es.dmoral.toasty;

import android.content.Context;
import android.widget.Toast;

public class Toasty {

    public static Toast error(Context context, CharSequence message) {
        return Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }

    public static Toast error(Context context, CharSequence message, int duration) {
        return Toast.makeText(context, message, duration);
    }

    public static Toast error(Context context, CharSequence message, int duration, boolean withIcon) {
        return Toast.makeText(context, message, duration);
    }

    public static Toast success(Context context, CharSequence message) {
        return Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }

    public static Toast success(Context context, CharSequence message, int duration) {
        return Toast.makeText(context, message, duration);
    }

    public static Toast success(Context context, CharSequence message, int duration, boolean withIcon) {
        return Toast.makeText(context, message, duration);
    }

    public static Toast info(Context context, CharSequence message) {
        return Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }

    public static Toast info(Context context, CharSequence message, int duration) {
        return Toast.makeText(context, message, duration);
    }

    public static Toast info(Context context, CharSequence message, int duration, boolean withIcon) {
        return Toast.makeText(context, message, duration);
    }

    public static Toast warning(Context context, CharSequence message) {
        return Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }

    public static Toast warning(Context context, CharSequence message, int duration) {
        return Toast.makeText(context, message, duration);
    }

    public static Toast warning(Context context, CharSequence message, int duration, boolean withIcon) {
        return Toast.makeText(context, message, duration);
    }

    public static Toast normal(Context context, CharSequence message) {
        return Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }

    public static Toast normal(Context context, CharSequence message, int duration) {
        return Toast.makeText(context, message, duration);
    }
}
