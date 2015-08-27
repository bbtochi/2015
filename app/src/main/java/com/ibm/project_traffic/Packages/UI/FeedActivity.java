package com.ibm.project_traffic.Packages.UI;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ibm.project_traffic.Packages.Adapters.FeedTabPagerAdapter;
import com.ibm.project_traffic.Packages.TabControllers.FeedTabLayout;
import com.ibm.project_traffic.Packages.Utils.PostDetails;
import com.ibm.project_traffic.R;

import java.util.Collections;
import java.util.List;

public class FeedActivity extends ActionBarActivity {

    private Toolbar toolBar;
    private ViewPager pager;
    private FeedTabPagerAdapter adapter;
    private FeedTabLayout tabs;
    private static final CharSequence titles[] = {"Route Feed", "Trending Traffic"};
    private static final int numberOfTabs = 2;
    private String destinationName;
    private String startingPointName;
    private Bundle routeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        //Get Data from Bundle
        destinationName = getIntent().getExtras().getString("destinationName");
        startingPointName = getIntent().getExtras().getString("startingPoint");
        routeDetails = new Bundle();
        routeDetails.putString("destinationName", destinationName);
        routeDetails.putString("startingPoint", startingPointName);

        //Binding Views
        toolBar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (FeedTabLayout) findViewById(R.id.tabs);

        //Initialize Pager adapter
        adapter =  new FeedTabPagerAdapter(getSupportFragmentManager(),titles, numberOfTabs);
        setSupportActionBar(toolBar);
        // Assigning ViewPager View and setting the adapter
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs.setDistributeEvenly(true);

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new FeedTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.blue_gray_darker);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
