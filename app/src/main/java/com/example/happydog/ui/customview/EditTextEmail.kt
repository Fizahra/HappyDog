package com.example.happydog.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.happydog.R

class EditTextEmail: AppCompatEditText {
    private lateinit var bg: Drawable
    private var isNormal = true

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        inputType = EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        elevation = 32f
        background = bg

//        if(isNormal){
//            background = normalBackground
//        }else {
//            background = errorBackground
//            error = "password at least 8 characters"
//        }

    }

    private fun init(){
        bg =  ContextCompat.getDrawable(context, R.drawable.bg_edit_text) as Drawable

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {


            }

            override fun afterTextChanged(s: Editable) {
                isNormal = s.toString().length >= 8

            }
        })
    }
}