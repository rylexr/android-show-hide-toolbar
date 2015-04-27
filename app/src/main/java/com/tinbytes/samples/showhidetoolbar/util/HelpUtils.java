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

package com.tinbytes.samples.showhidetoolbar.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tinbytes.samples.showhidetoolbar.R;

/**
 * Helper class to show a minimalistic About dialog.
 */
public final class HelpUtils {
  private static final String ABOUT_DIALOG_TAG = "about_dialog";

  public static void showAbout(Activity activity) {
    FragmentManager fm = activity.getFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();
    Fragment prev = fm.findFragmentByTag(ABOUT_DIALOG_TAG);
    if (prev != null) {
      ft.remove(prev);
    }
    ft.addToBackStack(null);

    new AboutDialog().show(ft, "about_dialog");
  }

  public static class AboutDialog extends DialogFragment {
    private static final String VERSION_UNAVAILABLE = "N/A";

    public AboutDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      // Get app version
      PackageManager pm = getActivity().getPackageManager();
      String packageName = getActivity().getPackageName();
      String versionName;
      try {
        PackageInfo info = pm.getPackageInfo(packageName, 0);
        versionName = info.versionName;
      } catch (PackageManager.NameNotFoundException e) {
        versionName = VERSION_UNAVAILABLE;
      }

      SpannableStringBuilder aboutBody = new SpannableStringBuilder();
      aboutBody.append(Html.fromHtml(getString(R.string.about_body, versionName)));

      LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View v = li.inflate(R.layout.about_dialog, null);
      TextView tvAbout = (TextView) v.findViewById(R.id.tvAbout);
      tvAbout.setText(aboutBody);
      tvAbout.setMovementMethod(new LinkMovementMethod());

      return new AlertDialog.Builder(getActivity())
          .setTitle(R.string.about)
          .setView(v)
          .setPositiveButton(android.R.string.ok,
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                  dialog.dismiss();
                }
              }
          ).create();
    }
  }
}
