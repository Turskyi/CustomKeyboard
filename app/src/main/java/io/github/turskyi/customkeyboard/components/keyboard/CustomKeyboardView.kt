package io.github.turskyi.customkeyboard.components.keyboard

import android.content.Context
import android.graphics.Color
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import io.github.turskyi.customkeyboard.components.expandableView.ExpandableState
import io.github.turskyi.customkeyboard.components.expandableView.ExpandableStateListener
import io.github.turskyi.customkeyboard.components.expandableView.ExpandableView
import io.github.turskyi.customkeyboard.components.keyboard.controllers.DefaultKeyboardController
import io.github.turskyi.customkeyboard.components.keyboard.controllers.KeyboardController
import io.github.turskyi.customkeyboard.components.keyboard.controllers.NumberDecimalKeyboardController
import io.github.turskyi.customkeyboard.components.keyboard.controllers.SpecialKey
import io.github.turskyi.customkeyboard.components.keyboard.layouts.KeyboardLayout
import io.github.turskyi.customkeyboard.components.keyboard.layouts.NumberDecimalKeyboardLayout
import io.github.turskyi.customkeyboard.components.keyboard.layouts.NumberKeyboardLayout
import io.github.turskyi.customkeyboard.components.keyboard.layouts.QwertyKeyboardLayout
import io.github.turskyi.customkeyboard.components.textFields.CustomTextField
import io.github.turskyi.customkeyboard.components.utilities.ComponentUtils
import java.util.*

class CustomKeyboardView(context: Context, attr: AttributeSet) : ExpandableView(context, attr) {
    private var fieldInFocus: EditText? = null
    private val keyboards = HashMap<EditText, KeyboardLayout?>()
    private val keyboardListener: KeyboardListener

    init {
        setBackgroundColor(Color.GRAY)

        keyboardListener = object : KeyboardListener {
            override fun characterClicked(c: Char) {
                /* don't need to do anything here */
            }

            override fun specialKeyClicked(key: SpecialKey) {
                if (key === SpecialKey.DONE) {
                    translateLayout()
                } else if (key === SpecialKey.NEXT) {
                    fieldInFocus?.focusSearch(View.FOCUS_DOWN)?.let {
                        it.requestFocus()
                        checkLocationOnScreen()
                        return
                    }
                }
            }
        }

        /* register listener with parent (listen for state changes) */
        registerListener(object : ExpandableStateListener {
            override fun onStateChange(state: ExpandableState) {
                if (state === ExpandableState.EXPANDED) checkLocationOnScreen()
            }
        })

        /* empty onClickListener prevents user from
         accidentally clicking views under the keyboard */
        setOnClickListener {}
        isSoundEffectsEnabled = false
    }

    fun registerEditText(type: KeyboardType, field: EditText) {

        if (!field.isEnabled) {
            /* disabled fields which do not have input connections */
            return
        }

        field.setRawInputType(InputType.TYPE_CLASS_TEXT)
        field.setTextIsSelectable(true)
        field.showSoftInputOnFocus = false
        field.isSoundEffectsEnabled = false
        field.isLongClickable = false

        val inputConnection = field.onCreateInputConnection(EditorInfo())
        keyboards[field] = createKeyboardLayout(type, inputConnection)
        keyboards[field]?.registerListener(keyboardListener)

        field.onFocusChangeListener = OnFocusChangeListener { _: View, hasFocus: Boolean ->
            if (hasFocus) {
                ComponentUtils.hideSystemKeyboard(context, field)

                /* if we can find a view below this field, we want to replace the
                 "done" button with the next button in the attached keyboard */
                field.focusSearch(View.FOCUS_DOWN)?.run {
                    if (this is EditText) keyboards[field]?.hasNextFocus = true
                }
                fieldInFocus = field

                renderKeyboard()
                if (!isExpanded) {
                    translateLayout()
                }
            } else if (!hasFocus && isExpanded) {
                for (editText in keyboards.keys) {
                    if (editText.hasFocus()) {
                        return@OnFocusChangeListener
                    }
                }
                translateLayout()
            }
        }

        field.setOnClickListener {
            if (!isExpanded) translateLayout()
        }
    }

    fun autoRegisterEditTexts(rootView: ViewGroup) = registerEditTextsRecursive(rootView)

    private fun registerEditTextsRecursive(view: View) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                registerEditTextsRecursive(view.getChildAt(i))
            }
        } else {
            if (view is CustomTextField) {
                registerEditText(view.keyboardType, view)
            } else if (view is EditText) {
                when (view.inputType) {
                    InputType.TYPE_CLASS_NUMBER -> registerEditText(KeyboardType.NUMBER, view)
                    InputType.TYPE_NUMBER_FLAG_DECIMAL -> registerEditText(KeyboardType.NUMBER_DECIMAL, view)
                    else -> registerEditText(KeyboardType.QWERTY, view)
                }
            }
        }
    }

    private fun renderKeyboard() {
        removeAllViews()
        val keyboard: KeyboardLayout? = keyboards[fieldInFocus]
        keyboard?.let {
            it.orientation = LinearLayout.VERTICAL
            it.createKeyboard(measuredWidth.toFloat())
            addView(keyboard)
        }
    }

    private fun createKeyboardLayout(type: KeyboardType, ic: InputConnection): KeyboardLayout? {
        return when (type) {
            KeyboardType.NUMBER -> NumberKeyboardLayout(context, createKeyboardController(type, ic))
            KeyboardType.NUMBER_DECIMAL -> NumberDecimalKeyboardLayout(context, createKeyboardController(type, ic))
            KeyboardType.QWERTY -> QwertyKeyboardLayout(context, createKeyboardController(type, ic))
        }
    }

    private fun createKeyboardController(type: KeyboardType, ic: InputConnection): KeyboardController? {
        return when (type) {
            KeyboardType.NUMBER_DECIMAL -> NumberDecimalKeyboardController(ic)
            /* not all keyboards require a custom controller */
            else -> DefaultKeyboardController(ic)
        }
    }

    override fun configureSelf() {
        renderKeyboard()
        checkLocationOnScreen()
    }

    /**
     * Check if fieldInFocus has a parent that is a ScrollView.
     * Ensure that ScrollView is enabled.
     * Check if the fieldInFocus is below the KeyboardLayout (measured on the screen).
     * If it is, find the deltaY between the top of the KeyboardLayout and the top of the
     * fieldInFocus, add 20dp (for padding), and scroll to the deltaY.
     * This will ensure the keyboard doesn't cover the field (if conditions above are met).
     */
    private fun checkLocationOnScreen() {
        fieldInFocus?.run {
            var fieldParent = this.parent
            while (fieldParent !== null) {
                if (fieldParent is ScrollView) {
                    if (!fieldParent.isSmoothScrollingEnabled) {
                        break
                    }

                    val fieldLocation = IntArray(2)
                    this.getLocationOnScreen(fieldLocation)

                    val keyboardLocation = IntArray(2)
                    this@CustomKeyboardView.getLocationOnScreen(keyboardLocation)

                    val fieldY = fieldLocation[1]
                    val keyboardY = keyboardLocation[1]

                    if (fieldY > keyboardY) {
                        val deltaY = (fieldY - keyboardY)
                        val scrollTo = (fieldParent.scrollY + deltaY + this.measuredHeight + 10.toDp)
                        fieldParent.smoothScrollTo(0, scrollTo)
                    }
                    break
                }
                fieldParent = fieldParent.parent
            }
        }
    }
}
