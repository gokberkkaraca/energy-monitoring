package ee490g.epfl.ch.dwarfsleepy.utils;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ee490g.epfl.ch.dwarfsleepy.AccelerometerActivity;
import ee490g.epfl.ch.dwarfsleepy.DashboardActivity;
import ee490g.epfl.ch.dwarfsleepy.DeviceScanActivity;
import ee490g.epfl.ch.dwarfsleepy.LoginActivity;
import ee490g.epfl.ch.dwarfsleepy.UserProfileActivity;
import ee490g.epfl.ch.dwarfsleepy.models.User;

public class NavigationHandler {

    public static final String USER = "USER";

    public static void goToDashboardActivity(Activity activity, User user){
        Intent intent = new Intent(activity, DashboardActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(USER, user);
        intent.putExtras(extras);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToUserProfileActivity(Activity activity, User user){
        Intent intent = new Intent(activity, UserProfileActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(USER, user);
        intent.putExtras(extras);
        activity.startActivity(intent);
    }

    public static void goToLoginActivity(Activity activity){
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToAccelerometerActivity(Activity activity, User user) {
        Intent intent = new Intent(activity, AccelerometerActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(USER, user);
        intent.putExtras(extras);
        activity.startActivity(intent);
    }

    public static void goToPolarBeltActivity(Activity activity){
        Intent intent = new Intent(activity, DeviceScanActivity.class);
        activity.startActivity(intent);
    }
}
