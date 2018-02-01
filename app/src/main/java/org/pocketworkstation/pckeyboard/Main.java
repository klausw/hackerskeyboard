/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.pocketworkstation.pckeyboard;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

public class Main extends Activity {

    private final static String MARKET_URI = "market://search?q=pub:\"Klaus Weidner\"";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        String html = getString(R.string.main_body);
        html += "<p><i>Version: " + getString(R.string.auto_version) + "</i></p>";
        Spanned content = Html.fromHtml(html);
        TextView description = (TextView) findViewById(R.id.main_description);
        description.setMovementMethod(LinkMovementMethod.getInstance());
        description.setText(content, BufferType.SPANNABLE);


        final Button setup1 = (Button) findViewById(R.id.main_setup_btn_configure_imes);
        setup1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
            }
        });

        final Button setup2 = (Button) findViewById(R.id.main_setup_btn_set_ime);
        setup2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showInputMethodPicker();
            }
        });
        
        final Activity that = this;

        final Button setup4 = (Button) findViewById(R.id.main_setup_btn_input_lang);
        setup4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(new Intent(that, InputLanguageSelection.class), 0);
            }
        });

        final Button setup3 = (Button) findViewById(R.id.main_setup_btn_get_dicts);
        setup3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URI));
                try {
                	startActivity(it);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(
                            		R.string.no_market_warning), Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        // PluginManager.getPluginDictionaries(getApplicationContext()); // why?
    }    
}

