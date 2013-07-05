package fr.rjoakim.android.jonetouch.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.common.primitives.Ints;

import fr.rjoakim.android.jonetouch.JOneTouchActivity;
import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.service.ActionService;
import fr.rjoakim.android.jonetouch.service.ServiceException;
import fr.rjoakim.android.jonetouch.util.ColorHexFactory;

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
public class JOneTouchWidgetActivity extends AppWidgetProvider {

	private static final String ACTION_NAME = "DISPLAY_EDIT_ACTION_VIEW";
	public static final String ACTION_EXECUTE_FIELD = "WIDGET_ACTION_ID_EXECUTE";
	public static final String ACTION_ID_FIELD = "WIDGET_ACTION_ID";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		updateAppWidget(context, appWidgetManager, appWidgetIds);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Action action) {
    	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_main_widget);
        views.setTextViewText(R.id.activityMainWidgetTitleActionTextView, toFormat(action));

        views.setInt(R.id.activityMainWidgetLayoutSeparator, "setBackgroundResource",
        		ColorHexFactory.hexColorToInt(action.getBackgroundHexColor()));
		
        views.setInt(R.id.activityMainWidgetTitleActionLayout, "setBackgroundResource",
        		ColorHexFactory.hexColorToInt(action.getBackgroundHexColor()));
        
        createIntentToLauchBrowserOnHomeSite(context, views);
		createIntentToExecuteScript(context, appWidgetId, views, action);
		createIntentToRedirectMainActivity(context, appWidgetId, views);
		createIntentToDisplayEditActionView(context, appWidgetId, views, action);
		
		setPreferencesIfWidgetNotExist(context, action, appWidgetId);
		
		appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	final ActionService actionService = new ActionService(context);
    	final int length = appWidgetIds.length;
		for (int i = 0; i < length; i++) {
			int appWidgetId = appWidgetIds[i];
			
			SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			if (defaultSharedPreferences.contains(String.valueOf(appWidgetId))) {
				
				long actionId = defaultSharedPreferences.getLong(String.valueOf(appWidgetId), -1);
				Log.d("WIDGET", "actionId = " + actionId);
				if (actionId != -1) {
					try {
						Action action = actionService.get(actionId);
						updateAppWidget(context, appWidgetManager, appWidgetId, action);
					} catch (ServiceException e) {
						updateAppWidgetIfActionIsDeleted(context, appWidgetManager, appWidgetId);
					}
				} else {
					updateAppWidgetIfActionIsDeleted(context, appWidgetManager, appWidgetId);
				}
			} else {
				updateAppWidgetIfActionIsDeleted(context, appWidgetManager, appWidgetId);
			}
		}
    }

	private static void updateAppWidgetIfActionIsDeleted(
			Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_main_widget);
		views.setTextViewText(R.id.activityMainWidgetTitleActionTextView,
				context.getString(R.string.app_widget_delete_because_action_is_deleted));
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

	private static String toFormat(Action action) {
		String title = action.getTitle();
		if (title.length() < 46) {
			return title;
		} else {
			return title.substring(0, 45) + "...";
		}
	}

	private static void setPreferencesIfWidgetNotExist(Context context, Action action, int appWidgetId) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	    if (!sharedPreferences.contains(String.valueOf(appWidgetId))) {
	    	SharedPreferences.Editor editor = sharedPreferences.edit();
	    	editor.putLong(String.valueOf(appWidgetId), action.getId());
	    	editor.commit();
	    }
	}

    private static void createIntentToLauchBrowserOnHomeSite(
    		Context context, RemoteViews views) {
    	
    	Intent intent = new Intent(Intent.ACTION_VIEW,
    			Uri.parse(context.getString(R.string.http_www_joakim_ribier_fr)));
    	
    	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    	views.setOnClickPendingIntent(R.id.activityMainWidgetRedirectHomeUrl, pendingIntent);
    }

    private static void createIntentToExecuteScript(Context context,
			int appWidgetId, RemoteViews views, Action action) {
		
    	 Intent mainIntent = new Intent(context, JOneTouchActivity.class);
    	 mainIntent.setAction(ACTION_NAME);
    	 mainIntent.putExtra(ACTION_ID_FIELD, action.getId());
    	 mainIntent.putExtra(ACTION_EXECUTE_FIELD, true);
    	 PendingIntent mainPending = PendingIntent.getActivity(context, Ints.checkedCast(action.getId()), mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
         views.setOnClickPendingIntent(R.id.activityMainWidgetExecuteScript, mainPending);
	}
    
    private static void createIntentToDisplayEditActionView(Context context,
			int appWidgetId, RemoteViews views, Action action) {
		
    	 Intent mainIntent = new Intent(context, JOneTouchActivity.class);
    	 mainIntent.setAction(ACTION_NAME);
    	 mainIntent.putExtra(ACTION_ID_FIELD, action.getId());
    	 mainIntent.putExtra(ACTION_EXECUTE_FIELD, false);
    	 PendingIntent mainPending = PendingIntent.getActivity(context, Ints.checkedCast(action.getId()) + 100, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
         views.setOnClickPendingIntent(R.id.activityMainWidgetEditActionView, mainPending);
	}
    
    private static void createIntentToRedirectMainActivity(Context context,
			int appWidgetId, RemoteViews views) {
		
    	 Intent mainIntent = new Intent(context, JOneTouchActivity.class);
         PendingIntent mainPending = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
         views.setOnClickPendingIntent(R.id.activityMainWidgetTitleAction, mainPending);
	}
}
