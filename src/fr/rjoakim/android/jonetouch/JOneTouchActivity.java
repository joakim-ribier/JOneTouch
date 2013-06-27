package fr.rjoakim.android.jonetouch;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.bean.MyAuthentication;
import fr.rjoakim.android.jonetouch.dialog.LogInDialog;
import fr.rjoakim.android.jonetouch.service.ActionService;
import fr.rjoakim.android.jonetouch.service.AuthenticationTypeService;
import fr.rjoakim.android.jonetouch.service.ScriptService;
import fr.rjoakim.android.jonetouch.service.ServerService;
import fr.rjoakim.android.jonetouch.service.ServiceException;
import fr.rjoakim.android.jonetouch.service.UserService;
import fr.rjoakim.android.jonetouch.view.ActionDetailView;
import fr.rjoakim.android.jonetouch.view.ActionView;

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
public class JOneTouchActivity extends Activity implements OnGestureListener {

	private static final String KEY_AUTH = "key_auth";
	
	private MyAuthentication myAuthentication;
	
	private MyViewAnimator myViewAnimator;
	private MyMenu myMenu;
	private MyTerminal myTerminal;
	
	private AuthenticationTypeService authenticationTypeService;
	private ServerService serverService;
	private UserService userService;
	private ScriptService scriptService;
	private ActionService actionService;
	
	private GestureDetector gesureDetector;
	private View.OnTouchListener gestureListener;
	private ActionListTopBarView actionListTopBarView;
	
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
		this.scriptService = new ScriptService(getApplicationContext());
		this.actionService = new ActionService(getApplicationContext());
		
		this.myTerminal = new MyTerminal(this);
		this.myMenu = new MyMenu(this, myAuthentication, serverService,
				authenticationTypeService, scriptService, actionService);
		
		this.myTerminal.initMenu(myMenu);
		this.myMenu.initTerminal(myTerminal);
		
		this.actionListTopBarView = new ActionListTopBarView(this, serverService,
				actionService, myViewAnimator, gestureListener);
		
		if (!userService.isExists()) {
			startActivity(
					new Intent(this, CreateAccountActivity.class));
			finish();
			
		} else {
			startApp(savedInstanceState);
			buildAllActionViews(0);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	private void startApp(Bundle savedInstanceState) {
		Intent intent = getIntent();
		String pwd = intent.getStringExtra("user_pwd");
		if (pwd != null) {
			myAuthentication.setKey(pwd);
			intent.removeExtra("user_pwd");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
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
			}
			@Override
			public void onFailed() {
				myAuthentication.setKey(null);
			}
		};
		logInDialog.show();
	}
	
	private void buildAllActionViews(int index) {
		actionListTopBarView.fill();

		myViewAnimator.get().removeAllViews();
		
		fillAllViewsWithActions();
		positionIndexOnViewAnimator(index);
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
			myViewAnimator.get().addView(actionListMainView);
			
			List<Action> actions = actionService.list();
			for (Action action: actions) {
				
				addOnActionListView(action, actionListMainView);
				addOnActionDetailView(action);
			}
		} catch (ServiceException e) {
			Toast.makeText(this,
					getString(R.string.failed), Toast.LENGTH_LONG).show();
		}
	}

	private void addOnActionListView(Action action, View actionListMainView) {
		View contentView = actionListMainView.findViewById(R.id.actionListMainViewContentScroll);
		contentView.setOnTouchListener(gestureListener);
		
		LinearLayout activityMainLayout = (LinearLayout) actionListMainView
				.findViewById(R.id.actionListMainViewContentLayout);
		
		activityMainLayout.addView(buildActionView(action));
	}

	private View buildActionView(final Action action) {
		ActionView actionView = new ActionView(
				this, serverService, myTerminal, myAuthentication, actionService);
		return actionView.build(action);
	}

	private void addOnActionDetailView(Action action) {
		ActionDetailView actionDetailView = new ActionDetailView(this, gestureListener,
				serverService, myTerminal, myAuthentication, actionService, scriptService);
		myViewAnimator.get().addView(
				actionDetailView.build(action));
	}
	
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity, menu);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
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
