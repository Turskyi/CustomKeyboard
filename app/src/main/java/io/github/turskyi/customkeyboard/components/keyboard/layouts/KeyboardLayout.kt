package io.github.turskyi.customkeyboard.components.keyboard.layouts

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import io.github.turskyi.customkeyboard.R
import io.github.turskyi.customkeyboard.components.keyboard.KeyboardListener
import io.github.turskyi.customkeyboard.components.keyboard.controllers.KeyboardController
import io.github.turskyi.customkeyboard.components.keyboard.controllers.SpecialKey
import io.github.turskyi.customkeyboard.components.utilities.ComponentUtils

abstract class KeyboardLayout(context: Context, private val controller: KeyboardController?,
                              var hasNextFocus: Boolean = false) : LinearLayout(context) {

    private var screenWidth = 0.0f
    internal var textSize = 20.0f

    private val Int.toDp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()

    init {
        layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun createKeyboard(screenWidth: Float = this.screenWidth) {
        removeAllViews()
        this.screenWidth = screenWidth

        val keyboardWrapper = createWrapperLayout()
        for (row in createRows()) {
            keyboardWrapper.addView(row)
        }
        addView(keyboardWrapper)
    }

    private fun createWrapperLayout(): LinearLayout {
        val wrapper = LinearLayout(context)
        val layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = 16.toDp
        layoutParams.bottomMargin = 16.toDp
        wrapper.layoutParams = layoutParams
        wrapper.orientation = VERTICAL
        return wrapper
    }

    private fun createButton(text: String, widthAsPctOfScreen: Float): Button {
        val button = Button(context)
        button.layoutParams = LayoutParams(
                (screenWidth * widthAsPctOfScreen).toInt(),
                LayoutParams.WRAP_CONTENT
        )
        button.setPadding(0, 80.toDp, 0, 80.toDp)
        ComponentUtils.setBackground(button, ContextCompat.getColor(context, R.color.colorAccent))
        val param = button.layoutParams as MarginLayoutParams
        param.setMargins(1, 1, 1, 1)
        button.layoutParams = param
        button.isAllCaps = false
        button.textSize = textSize
        button.text = text
        return button
    }

    private fun createTopButton(text: String, widthAsPctOfScreen: Float): Button {
        val button = Button(context)
        button.layoutParams = LayoutParams(
                (screenWidth * widthAsPctOfScreen).toInt(),
                LayoutParams.WRAP_CONTENT
        )
        ComponentUtils.setBackground(button, Color.GRAY)
        button.isAllCaps = false
        button.textSize = textSize * 1.2F
        button.text = text
        button.setTextColor(ContextCompat.getColor(context,R.color.colorAccent))
        return button
    }

    internal fun createButton(text: String, widthAsPctOfScreen: Float, value: Char): Button {
        val button = createButton(text, widthAsPctOfScreen)
        button.setOnClickListener { controller?.onKeyStroke(value) }
        return button
    }

    internal fun createTopButton(text: String, widthAsPctOfScreen: Float, value: Char): Button {
        val button = createTopButton(text, widthAsPctOfScreen)
        button.setOnClickListener { controller?.onKeyStroke(value) }
        return button
    }

    internal fun createTopButton(text: String, widthAsPctOfScreen: Float, key: SpecialKey): Button {
        val button = createTopButton(text, widthAsPctOfScreen)
        button.setTextColor(Color.YELLOW)
        button.setOnClickListener { controller?.onKeyStroke(key) }
        return button
    }

    internal fun createButton(text: String, widthAsPctOfScreen: Float,
                              key: SpecialKey
    ): Button {
        val button = createButton(text, widthAsPctOfScreen)
        button.setOnClickListener { controller?.onKeyStroke(key) }
        return button
    }

    internal fun createSpacer(widthAsPctOfScreen: Float): View {
        val view = View(context)
        view.layoutParams = LayoutParams(
                (screenWidth * widthAsPctOfScreen).toInt(), 0
        )
        view.isEnabled = false
        view.visibility = View.INVISIBLE
        return view
    }

    internal fun createRow(buttons: List<View>): LinearLayout {
        val row = LinearLayout(context)
        row.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        )
        orientation = HORIZONTAL
        row.gravity = Gravity.CENTER

        for (button in buttons) {
            row.addView(button)
        }
        return row
    }

    internal fun registerListener(listener: KeyboardListener) = controller?.registerListener(listener)

    internal abstract fun createRows(): List<LinearLayout>
}
