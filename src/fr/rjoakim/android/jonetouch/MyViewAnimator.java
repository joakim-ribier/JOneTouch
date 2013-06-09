package fr.rjoakim.android.jonetouch;

import android.app.Activity;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ViewAnimator;

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
public class MyViewAnimator {

	public static final int SWIPE_MIN_DISTANCE = 120;
	public static final int SWIPE_MAX_OFF_PATH = 250;
	public static final int SWIPE_THRESHOLD_VELOCITY = 100;
	public static final int ANIMATION_DURATION = 300;
	
	private final ViewAnimator viewAnimator;

	public MyViewAnimator(Activity activity) {
		this.viewAnimator = (ViewAnimator) activity
				.findViewById(R.id.mainActivityContentViewAnimator);
	}

	private Animation inFromRightAnimation() {

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(ANIMATION_DURATION);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(ANIMATION_DURATION);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}
	
	private Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(ANIMATION_DURATION);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	private Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(ANIMATION_DURATION);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}
	
	public ViewAnimator get() {
		return viewAnimator;
	}

	public void setInAnimationInFromRight() {
		viewAnimator.setInAnimation(inFromRightAnimation());
	}

	public void setInAnimationOutToLeft() {
		viewAnimator.setOutAnimation(outToLeftAnimation());
	}
	
	public void setInAnimationInFromLeft() {
		viewAnimator.setInAnimation(inFromLeftAnimation());
	}

	public void setInAnimationOutToRight() {
		viewAnimator.setOutAnimation(outToRightAnimation());
	}
}
