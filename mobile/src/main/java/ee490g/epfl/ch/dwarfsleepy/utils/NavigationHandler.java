package ee490g.epfl.ch.dwarfsleepy.utils;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ee490g.epfl.ch.dwarfsleepy.DashboardActivity;
import ee490g.epfl.ch.dwarfsleepy.GoogleAccountActivity;
import ee490g.epfl.ch.dwarfsleepy.LoginActivity;
import ee490g.epfl.ch.dwarfsleepy.NightMonitoringActivity;
import ee490g.epfl.ch.dwarfsleepy.SleepAnalysesActivity;
import ee490g.epfl.ch.dwarfsleepy.models.User;
import ee490g.epfl.ch.dwarfsleepy.polarbelt.DeviceScanActivity;

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

    public static void goToSleepAnalysesActivity(Activity activity, User user) {
        Intent intent = new Intent(activity, SleepAnalysesActivity.class);
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