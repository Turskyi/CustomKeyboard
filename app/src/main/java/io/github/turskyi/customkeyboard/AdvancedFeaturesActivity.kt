package io.github.turskyi.customkeyboard

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import io.github.turskyi.customkeyboard.components.keyboard.KeyboardType
import io.github.turskyi.customkeyboard.components.textFields.CustomTextField
import io.github.turskyi.customkeyboard.components.utilities.ComponentUtils
import kotlinx.android.synthetic.main.activity_advanced_features.*

class AdvancedFeaturesActivity : AppCompatActivity(R.layout.activity_advanced_features) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initListener()
    }

    private fun initListener() {
        switchActivitiesButton.setOnClickListener {
            startActivity(
                Intent(
                    this@AdvancedFeaturesActivity,
                    MainActivity::class.java
                )
            )
        }
    }

    private fun initView() {
        val customFieldWrapper: LinearLayout = findViewById(R.id.customFieldWrapper)
        customFieldWrapper.addView(
            createCustomTextField(
                "Custom Number Keyboard",
                KeyboardType.NUMBER
            )
        )
        customFieldWrapper.addView(
            createCustomTextField(
                "Custom Number Decimal Keyboard",
                KeyboardType.NUMBER_DECIMAL
            )
        )
        customFieldWrapper.addView(
            createCustomTextField(
                "Custom QWERTY Keyboard",
                KeyboardType.QWERTY
            )
        )
        customKeyboardView.autoRegisterEditTexts(customFieldWrapper)
    }

    private fun createCustomTextField(
            hint: String, keyboardType: KeyboardType): CustomTextField {
        val field = CustomTextField(applicationContext)
        val lp = LinearLayout.LayoutParams(
                ComponentUtils.dpToPx(applicationContext, 400),
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val marginBottom = applicationContext.resources.getDimension(R.dimen.fieldMarginBottom).toInt()
        lp.setMargins(0, 0, 0, marginBottom)
        field.layoutParams = lp

        field.hint = hint
        field.keyboardType = keyboardType

        return field
    }

    override fun onBackPressed() = if (customKeyboardView.isExpanded) {
        customKeyboardView.translateLayout()
    } else {
        super.onBackPressed()
    }
}