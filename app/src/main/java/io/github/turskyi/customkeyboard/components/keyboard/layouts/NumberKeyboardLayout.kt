package io.github.turskyi.customkeyboard.components.keyboard.layouts

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import io.github.turskyi.customkeyboard.components.keyboard.controllers.KeyboardController
import io.github.turskyi.customkeyboard.components.keyboard.controllers.SpecialKey

class NumberKeyboardLayout(context: Context, controller: KeyboardController?) :
        KeyboardLayout(context, controller) {

    constructor(context: Context) : this(context, null)

    override fun createRows(): List<LinearLayout> {
        val columnWidth = 0.34f
        textSize = 22.0f

        val rowTop = ArrayList<View>()
        rowTop.add(createTopButton("+", (columnWidth / 1.7F), '+'))
        rowTop.add(createTopButton("−", (columnWidth / 1.7F), '-'))
        rowTop.add(createTopButton("×", (columnWidth / 1.7F), '*'))
        rowTop.add(createTopButton("÷", (columnWidth / 1.7F), '/'))
        rowTop.add(createTopButton("✓️", (columnWidth / 1.7f), SpecialKey.DONE))

        val rowOne = ArrayList<View>()
        rowOne.add(createButton("1", columnWidth, '1'))
        rowOne.add(createButton("2", columnWidth, '2'))
        rowOne.add(createButton("3", columnWidth, '3'))

        val rowTwo = ArrayList<View>()
        rowTwo.add(createButton("4", columnWidth, '4'))
        rowTwo.add(createButton("5", columnWidth, '5'))
        rowTwo.add(createButton("6", columnWidth, '6'))

        val rowThree = ArrayList<View>()
        rowThree.add(createButton("7", columnWidth, '7'))
        rowThree.add(createButton("8", columnWidth, '8'))
        rowThree.add(createButton("9", columnWidth, '9'))

        val rowFour = ArrayList<View>()
        rowFour.add(createButton(".", columnWidth, '.'))
        rowFour.add(createButton("0", columnWidth, '0'))
        rowFour.add(createButton("⌫", columnWidth, SpecialKey.BACKSPACE))

        val rows = ArrayList<LinearLayout>()
        rows.add(createRow(rowTop))
        rows.add(createRow(rowOne))
        rows.add(createRow(rowTwo))
        rows.add(createRow(rowThree))
        rows.add(createRow(rowFour))

        return rows
    }
}
