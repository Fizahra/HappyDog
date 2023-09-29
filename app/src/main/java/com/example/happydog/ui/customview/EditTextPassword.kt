package com.example.happydog.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.happydog.R

class EditTextPassword: AppCompatEditText, View.OnTouchListener{
    private lateinit var show: Drawable
    private lateinit var hide: Drawable
    private lateinit var btnShow: Drawable
    private lateinit var bg: Drawable
    private var isNormal = true
    private var isShowed = false

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
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
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
        hide = ContextCompat.getDrawable(context, R.drawable.ic_hide_pass) as Drawable
        show = ContextCompat.getDrawable(context, R.drawable.ic_show_pass) as Drawable
        bg =  ContextCompat.getDrawable(context, R.drawable.bg_edit_text) as Drawable
        btnShow = show

        setOnTouchListener(this)


        setButtonDrawables(endOfTheText = btnShow )

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

//    private fun setButtonShow(){
//        if(isShowed) btnShow = hide
//        else btnShow = show
//    }

    private fun setButtonDrawables(startOfTheText: Drawable? = null, topOfTheText:Drawable? = null, endOfTheText:Drawable? = null, bottomOfTheText: Drawable? = null){
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
    }



    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (btnShow.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - btnShow.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        btnShow = show
                        hideShowPass()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        btnShow = hide
                        isShowed = !isShowed
                        hideShowPass()
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }

    private fun hideShowPass(){
        if (isShowed == true) inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        else inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
    }
}