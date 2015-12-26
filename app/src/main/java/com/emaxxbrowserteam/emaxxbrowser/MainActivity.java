package com.emaxxbrowserteam.emaxxbrowser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.emaxxbrowserteam.emaxxbrowser.loader.DownloadTask;
import com.emaxxbrowserteam.emaxxbrowser.loader.Parser;
import com.emaxxbrowserteam.emaxxbrowser.model.Algorithm;
import com.emaxxbrowserteam.emaxxbrowser.model.SuperTopic;
import com.emaxxbrowserteam.emaxxbrowser.model.Topic;

public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private DownloadTask task;

    public static Handler handler;
    public static ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pd = new ProgressDialog(this);
        pd.setTitle("Title");
        pd.setMessage("Message");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);

        handler = new TheBestHandler();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ExpandableListView) findViewById(R.id.list_slidermenu);

        mDrawerList.setOnChildClickListener(new SlideMenuChildClickListener());

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable
                .ic_drawer, // nav menu toggle icon
                R.string.app_name, // nav drawer open - description for
                // accessibility
                R.string.app_name // nav drawer close - description for
                // accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setDisplayShowTitleEnabled(true);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setDisplayShowTitleEnabled(false);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            showWelcomeFragment();
            task = new DownloadTask(this);
            task.execute(getURL(Parser.E_MAXX_ALGO_URL));
        } else {
            //TODO: saving task
        }
    }

    private URL getURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.toString(), e);
            return null;
        }
    }

    private static List<SuperTopic> fetchSuperTopics() {
        List<SuperTopic> res = new ArrayList<>();
        List<Algorithm> alg = new ArrayList<>();
        alg.add(new Algorithm("Algorithm 0", "url"));
        alg.add(new Algorithm("Algorithm 1", "url"));
        alg.add(new Algorithm("Algorithm 2", "url"));
        List<Topic> temp = new ArrayList<>();
        temp.add(new Topic("Topic 0", alg));
        temp.add(new Topic("Topic 1", alg));
        res.add(new SuperTopic("SuperTopic 1", temp));
        res.add(new SuperTopic("SuperTopic 2", temp));
        return res;
    }

    public void updateSuperTopics(List<SuperTopic> groups) {
        Log.d(TAG, "finished fetching groups");
        ExpandableListAdapter adapter = new CoolAdapter(getApplicationContext(), groups);
        Log.d(TAG, "finished creating adapter groups");
        mDrawerList.setAdapter(adapter);
        Log.d(TAG, "adapter assigned");
    }

    private class SlideMenuChildClickListener implements OnChildClickListener {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                    int childPosition, long id) {
            getActionBar().setTitle("selected " + groupPosition + ", " + childPosition);
            mDrawerLayout.closeDrawer(mDrawerList);
            showThemeFragment((Topic) v.getTag());
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "Kek", Toast.LENGTH_SHORT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void showWelcomeFragment() {
        replaceFragment(new WelcomeFragment());
    }

    private void showThemeFragment(Topic topic) {
        replaceFragment(TopicFragment.newInstance(topic));
    }

    public void replaceFragment(Fragment fragment) {
        if (fragment == null) {
            Log.e(TAG, "Error replacing fragment. It's null.");
            return;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }

	/*
     *
	 * /** When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private static String TAG = "MainActivity.java";
}
