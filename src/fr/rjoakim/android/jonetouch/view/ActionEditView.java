package fr.rjoakim.android.jonetouch.view;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import fr.rjoakim.android.jonetouch.JOneTouchActivity;
import fr.rjoakim.android.jonetouch.MyTerminal;
import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.bean.MyAuthentication;
import fr.rjoakim.android.jonetouch.service.ActionService;
import fr.rjoakim.android.jonetouch.service.ServerService;
import fr.rjoakim.android.jonetouch.util.APIUtils;

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
public class ActionEditView {

	private final JOneTouchActivity activity;
	private final ServerService serverService;
	private final MyTerminal myTerminal;
	private final MyAuthentication myAuthentication;
	private final ActionService actionService;
	
	private final View view;
	private ActionView actionView;

	public ActionEditView(JOneTouchActivity activity, OnTouchListener gestureListener, ServerService serverService,
			MyTerminal myTerminal, MyAuthentication myAuthentication, ActionService actionService) {

		this.activity = activity;
		this.serverService = serverService;
		this.myTerminal = myTerminal;
		this.myAuthentication = myAuthentication;
		this.actionService = actionService;
		
		this.view = activity.getLayoutInflater().inflate(R.layout.action_edit_view, null);
		this.view.findViewById(R.id.actionDetailViewScroll).setOnTouchListener(gestureListener);
	}
	
	public View build(Action action, int index) {
		addActionView(action, index);
		return view;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void addActionView(Action action, int index) {
		this.actionView = new ActionView(
				activity, serverService, myTerminal, myAuthentication, actionService);
		
		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.actionDetailViewLayout);
		// FIXME : LayoutTransition is available from HONEYCOMB but the LayoutTransition.CHANGING is available only from JELLY_BEAN
		if (APIUtils.ifAvailableAPI(Build.VERSION_CODES.JELLY_BEAN)) {
			LayoutTransition layoutTransition = new LayoutTransition();
			layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
			linearLayout.setLayoutTransition(layoutTransition);
		}
		linearLayout.addView(actionView.buildEditView(action, index));
	}
	
	public ActionView getActionView() {
		return actionView;
	}
}
