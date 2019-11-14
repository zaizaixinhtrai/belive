package com.appster.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox

class CustomFontCheckBox : AppCompatCheckBox {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        CustomFontUtils().applyCustomFont(this, context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        CustomFontUtils().applyCustomFont(this, context, attrs)
    }

}