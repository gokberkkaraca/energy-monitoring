package ee490g.epfl.ch.dwarfsleepy.dataLayer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class Profile {
    private static final String TAG = "Profile";

    public String name;
    public String nickname;
    public String description;
    public BitmapDrawable photo;

    public Profile() {
        // Empty constructor
    }

    public Profile(DataMap map, Resources res, GoogleApiClient mGoogleApiClient) {
        // Construct instance from the datamap
        name = map.getString("name");
        nickname = map.getString("nickname");
        description = map.getString("description");
        photo = new BitmapDrawable(res, loadBitmapFromAsset(map.getAsset("photo"), mGoogleApiClient));
    }

    public DataMap toDataMap() {
        DataMap map = new DataMap();
        map.putString("name", name);
        map.putString("nickname", nickname);
        map.putString("description", description);
        map.putAsset("photo", toAsset(photo.getBitmap()));
        return map;
    }

    private static Asset toAsset(Bitmap bitmap) {
        // Code inspired from https://developer.android.com/training/wearables/data-layer/assets.html
        ByteArrayOutputStream byteStream = null;
        try {
            byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            return Asset.createFromBytes(byteStream.toByteArray());
        } finally {
            if (null != byteStream) {
                try {
                    byteStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private Bitmap loadBitmapFromAsset(Asset asset, GoogleApiClient mGoogleApiClient) {
        // Code from https://developer.android.com/training/wearables/data-layer/assets.html
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mGoogleApiClient.blockingConnect(1, TimeUnit.SECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }}
