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
import android.widget.TextView;

import com.tinbytes.samples.showhidetoolbar.util.CityUtils;
import com.tinbytes.samples.showhidetoolbar.util.HelpUtils;
import com.tinbytes.samples.showhidetoolbar.util.RecyclerViewUtils;

public class MainActivity2 extends AppCompatActivity {
  // We need a reference to save/restore its state
  private RecyclerViewUtils.ShowHideToolbarOnScrollingListener showHideToolbarListener;

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    // Assign Toolbar to the activity
    Toolbar tToolbar = (Toolbar) findViewById(R.id.tToolbar);
    setSupportActionBar(tToolbar);
    getSupportActionBar().setTitle(R.string.app_name);

    // RecyclerView with sample data
    RecyclerView rvCities = (RecyclerView) findViewById(R.id.rvCities);
    rvCities.setLayoutManager(new LinearLayoutManager(this));
    rvCities.setAdapter(new CitiesAdapter(CityUtils.CITIES));
    rvCities.addOnScrollListener(showHideToolbarListener = new RecyclerViewUtils.ShowHideToolbarOnScrollingListener(tToolbar));

    if (savedInstanceState != null) {
      showHideToolbarListener.onRestoreInstanceState((RecyclerViewUtils.ShowHideToolbarOnScrollingListener.State) savedInstanceState
          .getParcelable(RecyclerViewUtils.ShowHideToolbarOnScrollingListener.SHOW_HIDE_TOOLBAR_LISTENER_STATE));
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(RecyclerViewUtils.ShowHideToolbarOnScrollingListener.SHOW_HIDE_TOOLBAR_LISTENER_STATE,
        showHideToolbarListener.onSaveInstanceState());
    super.onSaveInstanceState(outState);
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
