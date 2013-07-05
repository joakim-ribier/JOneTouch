package fr.rjoakim.android.jonetouch.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.dialog.ChoiceActionForWidgetMyDialog;
import fr.rjoakim.android.jonetouch.service.ActionService;

/**
 * 
 * Copyright 2013 Joakim Ribier
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
public class JOneTouchWidgetConfigurationActivity extends Activity {

	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_app_widget_configuration);
        setResult(RESULT_CANCELED);
        
        final ActionService actionService = new ActionService(this);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}
		
		ChoiceActionForWidgetMyDialog choiceActionForWidgetMyDialog = new ChoiceActionForWidgetMyDialog(this, actionService) {
			@Override
			public void onSuccess(Action action) {
				configureWidget(JOneTouchWidgetConfigurationActivity.this, action);
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
				setResult(RESULT_OK, resultValue);
				finish();
			}
			
			@Override
			public void onFailed() {
				finish();
			}
		};
		choiceActionForWidgetMyDialog.show();
	}
	
    private void configureWidget(Context context, Action action) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        JOneTouchWidgetActivity.updateAppWidget(context, appWidgetManager, mAppWidgetId, action);
    }
}
