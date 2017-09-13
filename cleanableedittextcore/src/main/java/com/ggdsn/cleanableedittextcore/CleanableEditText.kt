package com.ggdsn.cleanableedittextcore

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by LiaoXingyu on 23/01/2017.
 */

class CleanableEditText : AppCompatEditText {
    private lateinit var cleanDrawable: Drawable
    private var isCleanPressed: Boolean = false
    private var cleanButtonStyle: Int = 0

    constructor(context: Context) : super(context) {
        init(context, null, 0)
        //FIXME 由于使用JvmOverloads会遇到editText样式变化的问题（直接变成一个textview）所以这里保留多构造器
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CleanableEditText, 0, 0)

        try {
            cleanButtonStyle = typedArray.getResourceId(R.styleable.CleanableEditText_cleanableEditTextCleanButton, 0)
        } finally {
            typedArray.recycle()
        }

        if (cleanButtonStyle == 0) {
            cleanButtonStyle = R.drawable.cleanable_edittext_selector_clear
        }

        cleanDrawable = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            context.resources.getDrawable(cleanButtonStyle, context.theme)
        } else {
            context.resources.getDrawable(cleanButtonStyle)
        }

        if (isInEditMode || text.isNotEmpty()) {
            setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1], cleanDrawable, compoundDrawables[3])
        }
    }

    fun hasCleanButton(): Boolean = cleanButtonStyle != 0

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (!hasCleanButton()) return

        if (text.isNotEmpty())
            setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1], cleanDrawable, compoundDrawables[3])
        else
            setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1], null, compoundDrawables[3])
    }

    private fun isInCleanButton(x: Float, y: Float): Boolean {
        return width - paddingRight - cleanDrawable.intrinsicWidth <= x
                && x <= width
                && height - paddingBottom - cleanDrawable.intrinsicHeight <= y
                && y <= height
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!hasCleanButton()) return super.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> isCleanPressed = isInCleanButton(event.x, event.y)
            MotionEvent.ACTION_UP -> if (isCleanPressed) {
                if (text.isNotEmpty() && isInCleanButton(event.x, event.y)) setText("")
            }
        }
        return super.onTouchEvent(event)
    }

    override fun drawableStateChanged() {
        /*
		 * 这里禁用了超类的state change逻辑，以解决EdiText 其它部位被点击时clean按钮也显示点击效果的问题。
		 * 更好的做法是替换掉StateListDrawable中pressed相关的drawable，使它与未点击时的效果一致。
		 * 但编译器似乎屏蔽了获取旧drawable state的方法
		 */
        if (hasCleanButton() && isCleanPressed) {
            super.drawableStateChanged()
        }
    }

    companion object {
        private val TAG = "CleanableEditText"
    }
}
