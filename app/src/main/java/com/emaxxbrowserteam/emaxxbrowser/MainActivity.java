package com.emaxxbrowserteam.emaxxbrowser;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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
import com.emaxxbrowserteam.emaxxbrowser.loader.FileUtils;
import com.emaxxbrowserteam.emaxxbrowser.loader.IListener;
import com.emaxxbrowserteam.emaxxbrowser.loader.Parser;
import com.emaxxbrowserteam.emaxxbrowser.model.Algorithm;
import com.emaxxbrowserteam.emaxxbrowser.model.SuperTopic;
import com.emaxxbrowserteam.emaxxbrowser.model.Topic;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private DownloadTask superTopicTask;

    public static Handler handler;
    public static ProgressDialog pd;

    public static ArrayList fragmentStack;
    private List<SuperTopic> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pd = new ProgressDialog(this);
        pd.setTitle(R.string.loading);
        pd.setMessage(getResources().getString(R.string.please_be_patient));
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
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setDisplayShowTitleEnabled(false);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            fragmentStack = new ArrayList();
            fragmentStack.add(null);
            showWelcomeFragment();
            superTopicTask = new DownloadTask("", this, new IListener() {
                @Override
                public void listen(Document document) {
                    updateSuperTopics(Parser.parse(MainActivity.this, document));
                }
            });
            superTopicTask.execute(FileUtils.getURL(Parser.E_MAXX_ALGO_URL));
        } else {
            Log.w(TAG, "saved instanse is not null");
            RetainInstance retainInstance = (RetainInstance) getLastNonConfigurationInstance();
            fragmentStack = retainInstance.fragmentStack;
            superTopicTask = retainInstance.downloadTask;
            superTopicTask.attachActivity(this);
            if (retainInstance.superTopics != null) {
                Log.w(TAG, "show super topics");
                updateSuperTopics(retainInstance.superTopics);
            }
            //TODO: saving superTopicTask
        }
    }

    private class RetainInstance {
        public DownloadTask downloadTask;
        public List<SuperTopic> superTopics;
        public ArrayList fragmentStack;

        public RetainInstance(DownloadTask downloadTask, List<SuperTopic> superTopics, ArrayList fragmentStack) {
            this.downloadTask = downloadTask;
            this.superTopics = superTopics;
            this.fragmentStack = fragmentStack;
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        Log.w(TAG, "on retain: super topic task = " + (superTopicTask == null ? "null" : "not null") + ", " + "list = " + String.valueOf(groups));
        return new RetainInstance(superTopicTask, groups, fragmentStack);
    }

    private void updateSuperTopics(List<SuperTopic> groups) {
        this.groups = groups;
        Log.d(TAG, "finished fetching groups");
        ExpandableListAdapter adapter = new CoolAdapter(getApplicationContext(), groups);
        Log.d(TAG, "finished creating adapter groups");
        mDrawerList.setAdapter(adapter);
        Log.d(TAG, "adapter assigned");
    }

    public void buttononclick(View v) {
        if (v != null && v.getId() == R.id.button) {
            FileUtils.clearCache(getCacheDir());
        }
    }

    void outStack() {
        Log.w(TAG, "size of stack = " + fragmentStack.size());
        for (Object reason : fragmentStack) {
            if (reason == null) {
                Log.e(TAG, "Welcome");
            } else if (reason instanceof Topic) {
                Log.e(TAG, "topic");
            } else if (reason instanceof Algorithm) {
                Log.e(TAG, "algorithm");
            } else {
                throw new AssertionError();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentStack.isEmpty()) {
            fragmentStack.add(null);
        } else if (fragmentStack.size() > 1) {
            fragmentStack.remove(fragmentStack.size() - 1);
            Object reason = fragmentStack.get(fragmentStack.size() - 1);
            if (reason == null) {
                replaceFragment(new WelcomeFragment());
            } else if (reason instanceof Topic) {
                replaceFragment(TopicFragment.newInstance((Topic) reason));
            } else if (reason instanceof Algorithm) {
                replaceFragment(AlgorithmFragment.newInstance((Algorithm) reason));
            } else {
                throw new AssertionError();
            }
            fragmentStack.remove(fragmentStack.size() - 1);
        }
//        outStack();
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
            case R.id.action_reload:
                Toast.makeText(this, "Kek", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                Toast.makeText(this, "Kek", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_about:
                Toast.makeText(this, "Kek", Toast.LENGTH_SHORT).show();
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
        if (fragment instanceof WelcomeFragment) {
            fragmentStack.add(null);
        } else if (fragment instanceof TopicFragment) {
            fragmentStack.add(fragment.getArguments().getParcelable("topic"));
        } else if (fragment instanceof AlgorithmFragment) {
            fragmentStack.add(fragment.getArguments().getParcelable("algorithm"));
        } else {
            throw new AssertionError();
        }
//        outStack();
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
