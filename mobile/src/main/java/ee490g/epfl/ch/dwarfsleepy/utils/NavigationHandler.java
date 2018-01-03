package ee490g.epfl.ch.dwarfsleepy.utils;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ee490g.epfl.ch.dwarfsleepy.AbnormalAccelerometerActivity;
import ee490g.epfl.ch.dwarfsleepy.AbnormalHeartRateActivity;
import ee490g.epfl.ch.dwarfsleepy.DashboardActivity;
import ee490g.epfl.ch.dwarfsleepy.DayMonitoringActivity;
import ee490g.epfl.ch.dwarfsleepy.GoogleAccountActivity;
import ee490g.epfl.ch.dwarfsleepy.GoogleFitActivity;
import ee490g.epfl.ch.dwarfsleepy.LoginActivity;
import ee490g.epfl.ch.dwarfsleepy.NightMonitoringActivity;
import ee490g.epfl.ch.dwarfsleepy.polarbelt.DeviceScanActivity;
import ee490g.epfl.ch.dwarfsleepy.SleepAnalysesActivity;
import ee490g.epfl.ch.dwarfsleepy.models.User;

public class NavigationHandler {

    public static final String USER = "USER";

    public static void goToDashboardActivity(Activity activity, User user) {
        Intent intent = new Intent(activity, DashboardActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(USER, user);
        intent.putExtras(extras);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToLoginActivity(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToDayMonitoringActivity(Activity activity, User user) {
        Intent intent = new Intent(activity, DayMonitoringActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(USER, user);
        intent.putExtras(extras);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToNightMonitoringActivity(Activity activity, User user) {
        Intent intent = new Intent(activity, NightMonitoringActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(USER, user);
        intent.putExtras(extras);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToGoogleAccountActivity(Activity activity) {
        Intent intent = new Intent(activity, GoogleAccountActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToGoogleFitActivity(Activity activity, User user) {
        Intent intent = new Intent(activity, GoogleFitActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(USER, user);
        intent.putExtras(extras);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToAbnormalHeartRateActivity(Activity activity, User user) {
        Intent intent = new Intent(activity, AbnormalHeartRateActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(USER, user);
        intent.putExtras(extras);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToSleepAnalysesActivity(Activity activity, User user) {
        Intent intent = new Intent(activity, SleepAnalysesActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(USER, user);
        intent.putExtras(extras);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToAbnormalAccelerometerActivity(Activity activity, User user) {
        Intent intent = new Intent(activity, AbnormalAccelerometerActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(USER, user);
        intent.putExtras(extras);
        activity.startActivity(intent);
        activity.finish();
    }
    public static void goToPolarBeltActivity(Activity activity, User user) {
        Intent intent = new Intent(activity, DeviceScanActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(USER, user);
        intent.putExtras(extras);
        activity.startActivity(intent);
        activity.finish();
    }
}
/*
public static void goToPolarBeltActivity(Context context, User user) {
    Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.example.android.bluetoothlegatt");
    if (intent == null) {
        // Bring user to the market or let them choose an app?
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + "com.example.android.bluetoothlegatt"));
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
}
*/