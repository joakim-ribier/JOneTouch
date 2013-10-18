package fr.rjoakim.android.jonetouch;

import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.common.collect.Maps;

import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.bean.MyAuthentication;
import fr.rjoakim.android.jonetouch.dialog.LogInDialog;
import fr.rjoakim.android.jonetouch.service.ActionService;
import fr.rjoakim.android.jonetouch.service.AuthenticationTypeService;
import fr.rjoakim.android.jonetouch.service.ServerService;
import fr.rjoakim.android.jonetouch.service.ServiceException;
import fr.rjoakim.android.jonetouch.service.UserService;
import fr.rjoakim.android.jonetouch.util.APIUtils;
import fr.rjoakim.android.jonetouch.view.ActionEditView;
import fr.rjoakim.android.jonetouch.view.ActionView;
import fr.rjoakim.android.jonetouch.widget.JOneTouchWidgetActivity;

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
public class JOneTouchActivity extends MyActivity implements OnGestureListener {

	private static final String KEY_AUTH = "key_auth";
	
	private MyAuthentication myAuthentication;
	
	private MyViewAnimator myViewAnimator;
	private MyMenu myMenu;
	private MyTerminal myTerminal;
	
	private AuthenticationTypeService authenticationTypeService;
	private ServerService serverService;
	private UserService userService;
	private ActionService actionService;
	
	private GestureDetector gesureDetector;
	private View.OnTouchListener gestureListener;
	private ActionListTopBarView actionListTopBarView;
	
	private Map<Long, ActionEditView> mapActionIdWithActionEditView;
	private int backPressed = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_action_ssrv);
		
		this.myViewAnimator = new MyViewAnimator(this);
		this.gesureDetector = new GestureDetector(this, this);
		this.gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gesureDetector.onTouchEvent(event);
			}
		};
		this.myAuthentication = new MyAuthentication();
		
		this.authenticationTypeService = new AuthenticationTypeService(getApplicationContext());
		this.serverService = new ServerService(getApplicationContext());
		this.userService = new UserService(getApplicationContext());
		this.actionService = new ActionService(getApplicationContext());
		
		this.myTerminal = new MyTerminal(this);
		this.myMenu = new MyMenu(this, myAuthentication, serverService,
				authenticationTypeService, actionService);
		
		this.myTerminal.initMenu(myMenu);
		this.myMenu.initTerminal(myTerminal);
		
		this.actionListTopBarView = new ActionListTopBarView(this, serverService,
				actionService, myViewAnimator, gestureListener);
		
		this.mapActionIdWithActionEditView = Maps.newHashMap();
		
		if (!userService.isExists()) {
			startActivity(
					new Intent(this, CreateAccountActivity.class));
			finish();
			
		} else {
			startApp(savedInstanceState);
			buildAllActionViews(0);
		}
	}

	private ActionEditView getIndexOfViewAnimatorFromActionId(long actionId) {
		if (mapActionIdWithActionEditView.containsKey(actionId)) {
			return mapActionIdWithActionEditView.get(actionId);
		}
		return null;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	private void startApp(Bundle savedInstanceState) {
		Intent intent = getIntent();
		String pwd = intent.getStringExtra("user_pwd");
		if (pwd != null) {
			myAuthentication.setKey(pwd);
			intent.removeExtra("user_pwd");
			if (APIUtils.ifAvailableAPI(Build.VERSION_CODES.HONEYCOMB)) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
				ClipData clip = ClipData.newPlainText("mdp", pwd);
				clipboard.setPrimaryClip(clip);
			} else {
				android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(pwd);
			}
			Toast.makeText(this,
					getString(R.string.set_clipboard_password), Toast.LENGTH_SHORT).show();
			myMenu.displayHelpDialog();
		}
		authentication(savedInstanceState);
	}
	
	private void authentication(Bundle savedInstanceState) {
		if (myAuthentication != null && myAuthentication.is()) {
			return;
		}
		
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(KEY_AUTH)) {
				MyAuthentication object = (MyAuthentication) savedInstanceState.get(KEY_AUTH);
				if (object.is()) {
					myAuthentication.setKey(object.getKey());
					return;
				}
			}
		} 
		displayLogInDialog();
	}

	private void displayLogInDialog() {
		final LogInDialog logInDialog = new LogInDialog(JOneTouchActivity.this) {
			@Override
			public void onSuccess(String pwd) {
				myAuthentication.setKey(pwd);
				displayEditActionViewFromAppWidget();
			}
			@Override
			public void onFailed() {
				myAuthentication.setKey(null);
				finish();
			}
		};
		logInDialog.show();
	}
	
	private void displayEditActionViewFromAppWidget() {
		Intent intent = getIntent();
		if (intent != null) {
			long actionId = intent.getLongExtra(JOneTouchWidgetActivity.ACTION_ID_FIELD, -1);
			boolean execute = intent.getBooleanExtra(JOneTouchWidgetActivity.ACTION_EXECUTE_FIELD, false);
			if (actionId != -1) {
				ActionEditView actionEditView = getIndexOfViewAnimatorFromActionId(actionId);
				if (actionEditView != null) {
					actionListTopBarView.displayedViewAnimator(
							0, actionEditView.getActionView().getIndex());
					if (execute) {
						actionEditView.getActionView().execute();
					}
				} else {
					Toast.makeText(this,
							getString(R.string.app_widget_delete_because_action_is_deleted),
							Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void buildAllActionViews(int index) {
		actionListTopBarView.fill();

		myViewAnimator.get().removeAllViews();
		
		fillAllViewsWithActions();
		positionIndexOnViewAnimator(index);
		
		refreshAllAppWidgets();
	}

	private void refreshAllAppWidgets() {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
        		new ComponentName(getApplicationContext(), JOneTouchWidgetActivity.class));
        JOneTouchWidgetActivity.updateAppWidget(getApplicationContext(), appWidgetManager, appWidgetIds);
	}
	
	public void rebuildAllActionViews() {
		int index = myViewAnimator.get().getDisplayedChild();
		buildAllActionViews(index);
	}

	public void rebuildAllActionViews(int index) {
		buildAllActionViews(index);
	}
	
	public void rebuildAllActionViewsAndSetIndexOnLastCreate() {
		int index = myViewAnimator.get().getChildCount();
		buildAllActionViews(index);
	}
	
	private void positionIndexOnViewAnimator(int actual) {
		myViewAnimator.setInAnimationInFromRight();
		myViewAnimator.setInAnimationOutToLeft();
		myViewAnimator.get().setDisplayedChild(actual);
		actionListTopBarView.showNext(0, actual);
	}
	
	private void fillAllViewsWithActions() {
		try {
			View actionListMainView = getLayoutInflater().inflate(R.layout.action_list_main_view, null);
			actionListMainView.setOnTouchListener(gestureListener);
			
			myViewAnimator.get().addView(actionListMainView);
			
			List<Action> actions = actionService.list();
			int index = 0;
			for (Action action: actions) {
				index ++;
				addOnActionListView(action, actionListMainView, index);
				addOnActionDetailView(action, index);
			}
		} catch (ServiceException e) {
			Toast.makeText(this,
					getString(R.string.failed), Toast.LENGTH_LONG).show();
		}
	}

	private void addOnActionListView(Action action, View actionListMainView, int index) {
		View contentView = actionListMainView.findViewById(R.id.actionListMainViewContentScroll);
		contentView.setOnTouchListener(gestureListener);
		
		LinearLayout activityMainLayout = (LinearLayout) actionListMainView
				.findViewById(R.id.actionListMainViewContentLayout);
		activityMainLayout.setOnTouchListener(gestureListener);
		
		View buildActionView = buildActionView(action, index);
		buildActionView.setOnTouchListener(gestureListener);
		
		activityMainLayout.addView(buildActionView);
	}

	private View buildActionView(final Action action, int index) {
		ActionView actionView = new ActionView(
				this, serverService, myTerminal, myAuthentication, actionService);
		return actionView.build(action, index);
	}

	private void addOnActionDetailView(Action action, int index) {
		ActionEditView actionDetailView = new ActionEditView(this, gestureListener,
				serverService, myTerminal, myAuthentication, actionService);
		myViewAnimator.get().addView(
				actionDetailView.build(action, index));
		mapActionIdWithActionEditView.put(action.getId(), actionDetailView);
	}
	
	public void showAnimatorView(int index) {
		actionListTopBarView.displayedViewAnimator(myViewAnimator.get().getDisplayedChild(), index);
	}
	
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity, menu);
		if (APIUtils.ifAvailableAPI(Build.VERSION_CODES.HONEYCOMB)) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		} else {
			final MenuItem findItem = menu.findItem(R.id.action_tool);
			if (findItem != null) {
				findItem.setVisible(true);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			myMenu.hideOrShow();
			break;
		case R.id.action_options_help:
			myMenu.displayHelpDialog();
			break;
		case R.id.action_tool:
			myMenu.show();
			break;
		case R.id.action_logout:
			finish();
			break;
		case R.id.action_options_refresh:
			myMenu.rebuildAllServerConnections();
			rebuildAllActionViews(0);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gesureDetector.onTouchEvent(event)) {
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		if (e1 == null || e2 == null ||
				Math.abs(e1.getY() - e2.getY()) > MyViewAnimator.SWIPE_MAX_OFF_PATH) {
			return false;
		}
		
		if (myViewAnimator.get().getChildCount() < 2) {
			return false;
		}

		int actual = myViewAnimator.get().getDisplayedChild();
		if (e1.getX() - e2.getX() > MyViewAnimator.SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > MyViewAnimator.SWIPE_THRESHOLD_VELOCITY) {
			
			myViewAnimator.setInAnimationInFromRight();
			myViewAnimator.setInAnimationOutToLeft();
			myViewAnimator.get().showNext();
			actionListTopBarView.showNext(actual, myViewAnimator.get().getDisplayedChild());
		} else if (e2.getX() - e1.getX() > MyViewAnimator.SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > MyViewAnimator.SWIPE_THRESHOLD_VELOCITY) {

			myViewAnimator.setInAnimationInFromLeft();
			myViewAnimator.setInAnimationOutToRight();
			myViewAnimator.get().showPrevious();
			actionListTopBarView.showNext(actual, myViewAnimator.get().getDisplayedChild());
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2,
			float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	@Override
	public void onBackPressed() {
		backPressed ++;
		if (backPressed == 2) {
			backPressed = 0;
			finish();
		} else {
			rebuildAllActionViews(0);
			myMenu.hide();
			Toast.makeText(this,
					getString(R.string.back_pressed), Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
	    actionListTopBarView.scrollTo();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(KEY_AUTH, myAuthentication);
	}
}
