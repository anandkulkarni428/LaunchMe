package com.anand.launchme.Home;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anand.launchme.Apps.GetApps;
import com.anand.launchme.R;
import com.anand.launchme.Utills.AppPreferences;
import com.anand.launchme.Utills.PreferenceManager;
import com.anand.launchme.Adadters.myListAdap;
import com.anand.launchme.Appinfo.AppInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity implements SimpleGestureFilter.SimpleGestureListener {

    private Context context;
    private ImageView imageView;
    private RelativeLayout relativeLayout;
    private CardView rootCardView;
    private RecyclerView recVerticalList;

    private Animation startAnimation, rightToLeftAnim;
    private SimpleGestureFilter detector;
    private PackageManager packageManager;
    private ActivityManager mActivityManager;
    private PreferenceManager preferenceManager;

    private List<AppInfo> apps;

    private String gridCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        preferenceManager = new PreferenceManager(MainActivity.this);


        imageView = findViewById(R.id.apps_btn);
        relativeLayout = findViewById(R.id.root_layout);
        recVerticalList = findViewById(R.id.rec_vertical_list);
        rootCardView = findViewById(R.id.root_card_rec);

        detector = new SimpleGestureFilter(MainActivity.this, this);

        startAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);
        rightToLeftAnim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.up_to_down);

        imageView.startAnimation(startAnimation);


        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        final LayoutAnimationController controller = new LayoutAnimationController(startAnimation, 0.2f);


    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {

        //Detect the swipe gestures and display toast
        String showToastMessage = "Success";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:
                getRunningTasks();
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                displayAllApps();
                break;
            case SimpleGestureFilter.SWIPE_DOWN:

                Object sbservice = getSystemService("statusbar");
                Class<?> statusbarManager = null;
                try {
                    statusbarManager = Class.forName("android.app.StatusBarManager");
                    Method showsb;
                    if (Build.VERSION.SDK_INT >= 17) {
                        showsb = statusbarManager.getMethod("expandNotificationsPanel");
                    } else {
                        showsb = statusbarManager.getMethod("expand");
                    }
                    showsb.invoke(sbservice);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                break;
            case SimpleGestureFilter.SWIPE_UP:
                showApps();
                break;

        }

    }

    @Override
    public void onDoubleTap() {

        Intent intent = new Intent(Intent.ACTION_DIAL);
        startActivity(intent);

    }

    public void showApps() {
        Intent i = new Intent(MainActivity.this, GetApps.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        if (gridCount == null) {
            if (AppPreferences.Key.GRID_NO == null) {
                gridCount = "5";
            } else {
                gridCount = AppPreferences.getInstance(getApplicationContext()).getString(AppPreferences.Key.GRID_NO);
            }

        } else {
            gridCount = AppPreferences.getInstance(getApplicationContext()).getString(AppPreferences.Key.GRID_NO);
        }

        Log.d("TAG_NULL", gridCount + "");

        i.putExtra("GRID_NO", gridCount);
        startActivity(i);
    }

    public void getRunningTasks() {

        Log.d("TAG_RECENT", "Success");

        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(0);

        mActivityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);

        for (ApplicationInfo packageInfo : packages) {
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) continue;
            if (packageInfo.packageName.equals("mypackage")) continue;
            mActivityManager.killBackgroundProcesses(packageInfo.packageName);
        }


    }


    public void displayAllApps() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        recVerticalList.setLayoutManager(linearLayoutManager);
        rootCardView.setVisibility(View.VISIBLE);
        recVerticalList.startAnimation(rightToLeftAnim);
        loadApps();
        loadNewListView();
        detector.setEnabled(false);


    }


    private void loadNewListView() {
        recVerticalList.setAdapter(new myListAdap(MainActivity.this, apps));
    }


    private void loadApps() {
        try {

            packageManager = getPackageManager();
            if (apps == null) {
                apps = new ArrayList<AppInfo>();

                Intent i = new Intent(Intent.ACTION_MAIN, null);
                i.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> availableApps = packageManager.queryIntentActivities(i, 0);
                Collections.sort(availableApps, new ResolveInfo.DisplayNameComparator(packageManager));
                for (ResolveInfo ri : availableApps) {
                    AppInfo appinfo = new AppInfo();
                    appinfo.label = ri.loadLabel(packageManager);
                    appinfo.name = ri.activityInfo.packageName;
                    appinfo.icon = ri.activityInfo.loadIcon(packageManager);
                    apps.add(appinfo);

                }
            }

        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, ex.getMessage() + " loadApps", Toast.LENGTH_LONG).show();
            Log.e("Error loadApps", ex.getMessage() + " loadApps");
        }

    }

    @Override
    public void onBackPressed() {
        detector.setEnabled(true);
        rootCardView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                    final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
                    relativeLayout.post(new Runnable() {

                        @Override
                        public void run() {
                            relativeLayout.setBackground(wallpaperDrawable);
                        }
                    });
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    relativeLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.wallpaper_1));

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage so you cannot set your own wallpaper", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}