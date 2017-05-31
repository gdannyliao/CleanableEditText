package com.ggdsn.cleanableedittextcore;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by LiaoXingyu on 23/01/2017.
 */

public class CleanableEditText extends AppCompatEditText {
	private static final String TAG = "CleanableEditText";
	private Drawable cleanDrawable;
	private boolean isCleanPressed;
	private int cleanButtonStyle;

	public CleanableEditText(Context context) {
		super(context);
		init(context, null, 0);
	}

	public CleanableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public CleanableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CleanableEditText, 0, 0);

		try {
			cleanButtonStyle = typedArray.getResourceId(R.styleable.CleanableEditText_cleanButton, 0);
		} finally {
			typedArray.recycle();
		}

		if (cleanButtonStyle == 0) {
			cleanButtonStyle = R.drawable.selector_clear;
		}

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			cleanDrawable = context.getResources().getDrawable(cleanButtonStyle, context.getTheme());
		} else {
			cleanDrawable = context.getResources().getDrawable(cleanButtonStyle);
		}

		if (isInEditMode() || getText().length() > 0) {
			Drawable[] drawables = getCompoundDrawables();
			setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], cleanDrawable, drawables[3]);
		}
	}

	public boolean hasCleanButton() {
		return cleanButtonStyle != 0;
	}

	@Override protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		if (!hasCleanButton()) {
			return;
		}
		Drawable[] drawables = getCompoundDrawables();
		if (text.length() != 0) {
			setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], cleanDrawable, drawables[3]);
		} else {
			setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], null, drawables[3]);
		}
	}

	boolean isInCleanButton(float x, float y) {
		int width = getWidth();
		int height = getHeight();
		return width - getPaddingRight() - cleanDrawable.getIntrinsicWidth() <= x
			&& x <= width
			&& height - getPaddingBottom() - cleanDrawable.getIntrinsicHeight() <= y
			&& y <= height;
	}

	@Override public boolean onTouchEvent(MotionEvent event) {
		if (!hasCleanButton()) {
			return super.onTouchEvent(event);
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isCleanPressed = isInCleanButton(event.getX(), event.getY());
				break;
			case MotionEvent.ACTION_UP:
				if (isCleanPressed) {
					if (getText().length() > 0 && isInCleanButton(event.getX(), event.getY())) {
						setText("");
					}
				}
				break;
		}
		return super.onTouchEvent(event);
	}

	@Override protected void drawableStateChanged() {
		/*
		 * 这里禁用了超类的state change逻辑，以解决EdiText 其它部位被点击时clean按钮也显示点击效果的问题。
		 * 更好的做法是替换掉StateListDrawable中pressed相关的drawable，使它与未点击时的效果一致。
		 * 但编译器似乎屏蔽了获取旧drawable state的方法
		 */
		if (hasCleanButton() && isCleanPressed) {
			super.drawableStateChanged();
		}
	}
}
