package io.github.turskyi.customkeyboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.turskyi.customkeyboard.components.keyboard.KeyboardType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initListener()
    }

    private fun initListener() {
        switchActivitiesButton.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    AdvancedFeaturesActivity::class.java
                )
            )
        }
    }

    private fun initView() {
        customKeyboardView.registerEditText(KeyboardType.NUMBER, etNumberField)
        customKeyboardView.registerEditText(KeyboardType.NUMBER_DECIMAL, etNumberDecimalField)
        customKeyboardView.registerEditText(KeyboardType.QWERTY, etQwertyField)
    }

    override fun onBackPressed() = if (customKeyboardView.isExpanded) {
        customKeyboardView.translateLayout()
    } else {
        super.onBackPressed()
    }
}