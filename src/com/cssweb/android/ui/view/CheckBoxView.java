package com.cssweb.android.ui.view;

import com.cssweb.android.main.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CheckBoxView extends RelativeLayout implements
		View.OnClickListener {
	private ImageView checkImage;
	private RelativeLayout layout;
	private boolean flag = false;
	private View.OnClickListener vcOnClickListener = null;

	public CheckBoxView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public CheckBoxView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public CheckBoxView(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context) {
		layout = new RelativeLayout(context);
		layout.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.checkbox_bg));

		checkImage = new ImageView(context);
		checkImage.setImageDrawable(getResources().getDrawable(
				R.drawable.checkbox_swich));

		this.addView(layout);
		layout.addView(checkImage);

		setOnClickListener(this);
	}

	public void setChecked(boolean checked) {
		if (flag != checked) {
			flag = !checked;
			translate();
		}
	}

	public boolean getChecked() {
		return flag;
	}
	
	private void translate() {
		// (int fromXType, float fromXValue, int toXType, float toXValue, int
		// fromYType, float fromYValue, int toYType, float toYValue
		TranslateAnimation localTranslateAnimation = null;
		if (flag) {
			localTranslateAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0.5f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f);
			flag = false;
		} else {
			localTranslateAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.5f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f);
			flag = true;
		}

		localTranslateAnimation.setDuration(300);
		localTranslateAnimation.setFillAfter(true);
		AccelerateDecelerateInterpolator localAccelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
		localTranslateAnimation
				.setInterpolator(localAccelerateDecelerateInterpolator);
		checkImage.startAnimation(localTranslateAnimation);

	}

	@Override
	public void onClick(View v) {
		translate();
		vcOnClickListener.onClick(v);
	}

	public void setClickListener(View.OnClickListener paramOnClickListener) {
		this.vcOnClickListener = paramOnClickListener;
	}
}
