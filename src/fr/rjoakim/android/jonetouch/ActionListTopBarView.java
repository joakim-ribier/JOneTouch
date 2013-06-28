package fr.rjoakim.android.jonetouch;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;

import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.dialog.AddActionMyDialog;
import fr.rjoakim.android.jonetouch.service.ActionService;
import fr.rjoakim.android.jonetouch.service.ServerService;
import fr.rjoakim.android.jonetouch.service.ServiceException;

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
public class ActionListTopBarView {

	private final JOneTouchActivity activity;
	private final ActionService actionService;
	
	private final List<LinearLayout> actionsLayouts;
	private final MyViewAnimator myViewAnimator;

	public ActionListTopBarView(final JOneTouchActivity activity, final ServerService serverService, final ActionService actionService,
			final MyViewAnimator viewAnimator, final OnTouchListener gestureListener) {
		
		this.activity = activity;
		this.actionService = actionService;
		this.myViewAnimator = viewAnimator;
		this.actionsLayouts = Lists.newArrayList();
		
		View newButton = activity.findViewById(R.id.actionTopBarLayoutNewButton);
		newButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final AddActionMyDialog myDialog = new AddActionMyDialog(
						activity, serverService, actionService) {
					@Override
					public void onSuccess(Long id) {
						ActionListTopBarView.this.activity.rebuildAllActionViewsAndSetIndexOnLastCreate();
					}
				};
				myDialog.show();
			}
		});
	}

	public void fill() {
		LinearLayout actionsListLayout = (LinearLayout) activity.findViewById(R.id.actionListMainViewContentTopBarLayout);

		actionsListLayout.removeAllViews();
		this.actionsLayouts.clear();
		
		try {
			List<Action> actions = this.actionService.list();
			int pos = 0;
			actionsListLayout.addView(
					buildActionView(
							activity.getString(R.string.action_list_top_layout_all), true, pos));
			
			if (actions != null) {
				for (Action action: actions) {
					pos ++;
					actionsListLayout.addView(
							buildActionView(action.getTitle(), false, pos));
				}
			}
		} catch (ServiceException e) {
			Toast.makeText(activity,
					activity.getString(R.string.failed), Toast.LENGTH_LONG).show();
			setClipboardError(e.getMessage());
		}
	}

	private LinearLayout buildActionView(String text, boolean isSelected, final int pos) {
		final LinearLayout linearLayout = (LinearLayout) activity.getLayoutInflater()
				.inflate(R.layout.textview_action_topbar, null);
		actionsLayouts.add(linearLayout);

		if (isSelected) {
			linearLayout.setBackgroundColor(activity.getResources().getColor(R.color.blue));	
		}
		
		linearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				displayedViewAnimator(myViewAnimator.get().getDisplayedChild(), pos);
			}
		});
		
		TextView textView = (TextView) linearLayout.findViewById(R.id.actionTopBarTextView);
		textView.setText(text);
		return linearLayout;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void setClipboardError(String message) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE); 
			ClipData clip = ClipData.newPlainText("error-message", message);
			clipboard.setPrimaryClip(clip);
		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(message);
		}
	}

	public void showNext(int displayedChild, int next) {
		LinearLayout actualDisplay = actionsLayouts.get(displayedChild);
		actualDisplay.setBackgroundColor(activity.getResources().getColor(R.color.gray));
		
		LinearLayout nextDisplaying = actionsLayouts.get(next);
		nextDisplaying.setBackgroundColor(activity.getResources().getColor(R.color.blue));
		
		int beforeDisplayedChild = displayedChild - 1;
		if (beforeDisplayedChild >= 0) {
			LinearLayout beforeActualDisplay = actionsLayouts.get(beforeDisplayedChild);
			RelativeLayout relativeLayout = (RelativeLayout) beforeActualDisplay.findViewById(R.id.actionTopBarSeparator);
			relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.blue));
		}

		int beforeNext = next - 1;
		if (beforeNext >= 0) {
			LinearLayout beforeNextDisplaying = actionsLayouts.get(beforeNext);
			RelativeLayout relativeLayout = (RelativeLayout) beforeNextDisplaying.findViewById(R.id.actionTopBarSeparator);
			relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.gray));
		}
		
		scrollTo(Math.round(nextDisplaying.getLeft()));
	}
	
	public void displayedViewAnimator(int displayedChild, int nextIndex) {
		if (nextIndex > displayedChild) {
			myViewAnimator.setInAnimationInFromRight();
			myViewAnimator.setInAnimationOutToLeft();
		} else {
			myViewAnimator.setInAnimationInFromLeft();
			myViewAnimator.setInAnimationOutToRight();
		}
		showNext(displayedChild, nextIndex);
		myViewAnimator.get().setDisplayedChild(nextIndex);
	}

	public void scrollTo(int i) {
		HorizontalScrollView scrollView = (HorizontalScrollView) activity
				.findViewById(R.id.actionListMainViewContentTopBarScrollView);
		scrollView.scrollTo(i, 0);
	}

	public void scrollTo() {
		int displayedChild = myViewAnimator.get().getDisplayedChild();
		LinearLayout actualDisplay = actionsLayouts.get(displayedChild);
		scrollTo(actualDisplay.getLeft());
	}
}
