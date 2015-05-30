/*
 * Copyright 2015, Randy Saborio & Tinbytes, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.tinbytes.samples.showhidetoolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.tinbytes.samples.showhidetoolbar.util.AndroidUtils;
import com.tinbytes.samples.showhidetoolbar.util.CityUtils;
import com.tinbytes.samples.showhidetoolbar.util.HelpUtils;

public class MainActivity extends AppCompatActivity {
  // The elevation of the toolbar when content is scrolled behind
  private static final float TOOLBAR_ELEVATION = 14f;
  // To save/restore recyclerview state on configuration changes
  private static final String STATE_RECYCLER_VIEW = "state-recycler-view";
  private static final String STATE_VERTICAL_OFFSET = "state-vertical-offset";
  private static final String STATE_SCROLLING_UP = "state-scrolling-up";
  private static final String STATE_TOOLBAR_ELEVATION = "state-toolbar-elevation";
  private static final String STATE_TOOLBAR_TRANSLATION_Y = "state-toolbar-translation-y";

  // We need a reference to the toolbar for hide/show animation
  private Toolbar tToolbar;
  // We need a reference to the recyclerview to save/restore its state
  private RecyclerView rvCities;
  // Keeps track of the overall vertical offset in the list
  private int verticalOffset;
  // Determines the scroll UP/DOWN direction
  private boolean scrollingUp;

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    // Assign Toolbar to the activity
    tToolbar = (Toolbar) findViewById(R.id.tToolbar);
    setSupportActionBar(tToolbar);
    getSupportActionBar().setTitle(R.string.app_name);

    // RecyclerView with sample data
    rvCities = (RecyclerView) findViewById(R.id.rvCities);
    rvCities.setLayoutManager(new LinearLayoutManager(this));
    rvCities.setAdapter(new CitiesAdapter(CityUtils.CITIES));

    if (savedInstanceState != null) {
      if (AndroidUtils.isLollipop()) {
        tToolbar.setElevation(savedInstanceState.getFloat(STATE_TOOLBAR_ELEVATION));
      }
      tToolbar.setTranslationY(savedInstanceState.getFloat(STATE_TOOLBAR_TRANSLATION_Y));
      verticalOffset = savedInstanceState.getInt(STATE_VERTICAL_OFFSET);
      scrollingUp = savedInstanceState.getBoolean(STATE_SCROLLING_UP);
      rvCities.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(STATE_RECYCLER_VIEW));
    }

    // We need to detect scrolling changes in the RecyclerView
    rvCities.setOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          if (scrollingUp) {
            if (verticalOffset > tToolbar.getHeight()) {
              toolbarAnimateHide();
            } else {
              toolbarAnimateShow(verticalOffset);
            }
          } else {
            if (tToolbar.getTranslationY() < tToolbar.getHeight() * -0.6 && verticalOffset > tToolbar.getHeight()) {
              toolbarAnimateHide();
            } else {
              toolbarAnimateShow(verticalOffset);
            }
          }
        }
      }

      @Override
      public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        verticalOffset += dy;
        scrollingUp = dy > 0;
        int toolbarYOffset = (int) (dy - tToolbar.getTranslationY());
        tToolbar.animate().cancel();
        if (scrollingUp) {
          if (toolbarYOffset < tToolbar.getHeight()) {
            if (verticalOffset > tToolbar.getHeight()) {
              toolbarSetElevation(TOOLBAR_ELEVATION);
            }
            tToolbar.setTranslationY(-toolbarYOffset);
          } else {
            toolbarSetElevation(0);
            tToolbar.setTranslationY(-tToolbar.getHeight());
          }
        } else {
          if (toolbarYOffset < 0) {
            if (verticalOffset <= 0) {
              toolbarSetElevation(0);
            }
            tToolbar.setTranslationY(0);
          } else {
            if (verticalOffset > tToolbar.getHeight()) {
              toolbarSetElevation(TOOLBAR_ELEVATION);
            }
            tToolbar.setTranslationY(-toolbarYOffset);
          }
        }
      }
    });
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    if (AndroidUtils.isLollipop()) {
      outState.putFloat(STATE_TOOLBAR_ELEVATION, tToolbar.getElevation());
    }
    outState.putFloat(STATE_TOOLBAR_TRANSLATION_Y, tToolbar.getTranslationY());
    outState.putInt(STATE_VERTICAL_OFFSET, verticalOffset);
    outState.putBoolean(STATE_SCROLLING_UP, scrollingUp);
    outState.putParcelable(STATE_RECYCLER_VIEW, rvCities.getLayoutManager().onSaveInstanceState());
    super.onSaveInstanceState(outState);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void toolbarSetElevation(float elevation) {
    // setElevation() only works on Lollipop
    if (AndroidUtils.isLollipop()) {
      tToolbar.setElevation(elevation);
    }
  }

  private void toolbarAnimateShow(final int verticalOffset) {
    tToolbar.animate()
        .translationY(0)
        .setInterpolator(new LinearInterpolator())
        .setDuration(180)
        .setListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationStart(Animator animation) {
            toolbarSetElevation(verticalOffset == 0 ? 0 : TOOLBAR_ELEVATION);
          }
        });
  }

  private void toolbarAnimateHide() {
    tToolbar.animate()
        .translationY(-tToolbar.getHeight())
        .setInterpolator(new LinearInterpolator())
        .setDuration(180)
        .setListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            toolbarSetElevation(0);
          }
        });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_about:
        HelpUtils.showAbout(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Cities adapter to hold sample data for our RecyclerView.
   */
  static class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.ViewHolder> {
    private String[] data;

    static class ViewHolder extends RecyclerView.ViewHolder {
      TextView tvName;

      ViewHolder(View v) {
        super(v);
        tvName = (TextView) v.findViewById(R.id.tvName);
      }
    }

    CitiesAdapter(String... data) {
      this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      holder.tvName.setText(data[position]);
    }

    @Override
    public int getItemCount() {
      return data.length;
    }
  }
}
