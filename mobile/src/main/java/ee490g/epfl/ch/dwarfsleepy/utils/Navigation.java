package ee490g.epfl.ch.dwarfsleepy.utils;


import android.app.Activity;
import android.content.Intent;

import ee490g.epfl.ch.dwarfsleepy.DashboardActivity;
import ee490g.epfl.ch.dwarfsleepy.user.User;

public class Navigation {

    public static final String USER = "USER";

    public static void goToDashboardActivity(Activity activity, User user){
        Intent intent = new Intent(activity, DashboardActivity.class);
        intent.putExtra(USER, user);
        activity.startActivity(intent);
        activity.finish();
    }
}
