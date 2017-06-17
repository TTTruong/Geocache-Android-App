package project.csci.geocaching;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int CACHE_LIST_CODE = 2;
    private static final int TRACKING_CODE = 3;

    private Cache trackingCache = new Cache();
    int userCaches;
    String username;
    UserDBHelper database = new UserDBHelper(this);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Menu inflater for main menu. Has logout button.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main:
                // Logout menu button.
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = getIntent().getStringExtra("username");
        userCaches = getIntent().getIntExtra("userCaches", 0);

        // Set welcome message and no selected cache message.
        TextView usernameText = (TextView) findViewById(R.id.username_textview);
        usernameText.setText(getString(R.string.welcome_message1,username));
        TextView welcomeText = (TextView) findViewById(R.id.welcome_textview);
        welcomeText.setText(getString(R.string.welcome_message2));
        TextView trackingText = (TextView) findViewById(R.id.tracking_textview);
        trackingText.setText(getString(R.string.no_active_cache));

        // Set button click highlights.
        ButtonHelper buttonHelper = new ButtonHelper();
        buttonHelper.buttonClickSetter(this, findViewById(R.id.showMapButton));
        buttonHelper.buttonClickSetter(this, findViewById(R.id.showCacheButton));
        buttonHelper.buttonClickSetter(this, findViewById(R.id.trackingButton));
        buttonHelper.buttonClickSetter(this, findViewById(R.id.logoutButton));
    }

    public void showMap(View view){
        // Start map activity.
        Intent i = new Intent(this, MapActivity.class);

        if (trackingCache.getCacheID() != -1){
            // Give selected cache information.
            i.putExtra("cacheSelected", true);
            i.putExtra("cacheID", trackingCache.getCacheID());
            i.putExtra("cacheName", trackingCache.getName());
            i.putExtra("cacheLat", trackingCache.getLat());
            i.putExtra("cacheLong", trackingCache.getLong());
        } else {
            i.putExtra("cacheSelected", false);
        }

        // Give user database and current user.
        i.putExtra("userCaches", userCaches);
        i.putExtra("username", getIntent().getStringExtra("username"));
        this.startActivity(i);
    }

    public void showCacheList(View view) {
        // Start cache list activity.
        Intent intent = new Intent(this, CacheListActivity.class);
        //pass data the activity
        intent.putExtra("userCaches", userCaches);
        intent.putExtra("username", getIntent().getStringExtra("username"));
        //If we're tracking a cache, also pass along which one we're passing
        if (trackingCache.getCacheID() != -1){
            intent.putExtra("cacheSelected", true);
            intent.putExtra("cacheID", trackingCache.getCacheID());
        }
        this.startActivityForResult(intent, CACHE_LIST_CODE);
    }

    public void showTracking(View view) {
        Intent i = new Intent(this, TrackingActivity.class);

        if (trackingCache.getCacheID() != -1){
            // Give selected cache information.
            i.putExtra("cacheSelected", true);
            i.putExtra("cacheID", trackingCache.getCacheID());
            i.putExtra("cacheName", trackingCache.getName());
            i.putExtra("cacheLat", trackingCache.getLat());
            i.putExtra("cacheLong", trackingCache.getLong());
        } else {
            i.putExtra("cacheSelected", false);
        }

        // Give user database and current user.
        i.putExtra("userCaches", userCaches);
        i.putExtra("username", getIntent().getStringExtra("username"));
        this.startActivityForResult(i, TRACKING_CODE);
    }

    public void sendLogoutMessage(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == CACHE_LIST_CODE) && (resultCode == RESULT_OK)) {
            // Save the tracked cache to a file.
            trackingCache.setCacheID(data.getIntExtra("cacheID", 0));
            trackingCache.setName(data.getStringExtra("cacheName"));
            trackingCache.setLat(data.getDoubleExtra("cacheLat", 0));
            trackingCache.setLong(data.getDoubleExtra("cacheLong", 0));
            trackingCache.setDescription(data.getStringExtra("cacheDesc"));

            // Display selected cache.
            TextView trackingInfo = (TextView) findViewById(R.id.tracking_textview);
            trackingInfo.setText(getString(R.string.tracking_information,
                    trackingCache.getName(),
                    trackingCache.getLat(),
                    trackingCache.getLong()));

        } else if ((requestCode == TRACKING_CODE) && (resultCode == RESULT_OK)) {
            // Cache successfully claimed. Set new cache value for user.
            int newCacheValue = 0;
            String oldCaches = Integer.toBinaryString(database.getUserCaches(getIntent().getStringExtra("username")));

            //only update if the cache value is 0, that is the cache is unclaimed.
            if ((trackingCache.getCacheID() >= oldCaches.length()) ||
                    (oldCaches.substring(oldCaches.length() - trackingCache.getCacheID() - 1).charAt(0) == '0')
                    ){

                        double power = Math.pow(2, trackingCache.getCacheID());
                        int intPower = (int) power;

                //flip the bit value of the cache id
                    newCacheValue = database.getUserCaches(getIntent().getStringExtra("username"))
                        +  intPower;

                //update in the database
                database.updateUserCache(getIntent().getStringExtra("username"),newCacheValue );
                //update the local variable
                userCaches = newCacheValue;
            }

            if (data.getBooleanExtra("claimed",false)) {
                // Display that cache was successfully claimed.
                TextView trackingInfo = (TextView) findViewById(R.id.tracking_textview);
                trackingInfo.setText(R.string.claimed_cache);
            }

            // Set selected cache to none.
            trackingCache.setCacheID(data.getIntExtra("cacheID", -1));
            trackingCache.setName(data.getStringExtra("cacheName"));
            trackingCache.setLat(data.getDoubleExtra("cacheLat", 0));
            trackingCache.setLong(data.getDoubleExtra("cacheLong", 0));
            trackingCache.setDescription(data.getStringExtra("cacheDesc"));
        }
    }
}