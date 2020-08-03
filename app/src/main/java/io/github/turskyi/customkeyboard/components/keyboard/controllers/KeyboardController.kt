package io.github.turskyi.customkeyboard.components.keyboard.controllers

import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import io.github.turskyi.customkeyboard.components.keyboard.KeyboardListener

abstract class KeyboardController(private val inputConnection: InputConnection) {

    private val listeners = ArrayList<KeyboardListener>()
    private var cursorPosition: Int = 0
    private var inputText = ""

    companion object {
        private fun deleteCharacter(text: String, index: Int) =
                StringBuilder(text).deleteCharAt(index).toString()

        private fun addCharacter(text: String, addition: Char, index: Int) =
                text.substring(0, index) + addition + text.substring(index)
    }

    @Synchronized
    fun onKeyStroke(char: Char) {
        updateMembers()
        handleKeyStroke(char)
        for (listener in listeners) {
            listener.characterClicked(char)
        }
    }

    @Synchronized
    fun onKeyStroke(key: SpecialKey) {
        updateMembers()
        handleKeyStroke(key)
        for (listener in listeners) {
            listener.specialKeyClicked(key)
        }
    }

    fun registerListener(listener: KeyboardListener)= listeners.add(listener)

    private fun updateMembers() {
        val beforeText = beforeCursor()
        val afterText = afterCursor()
        cursorPosition = beforeText.length
        inputText = beforeText + afterText
    }

    private fun beforeCursor()= inputConnection.getTextBeforeCursor(100, 0).toString()

    private fun afterCursor()= inputConnection.getTextAfterCursor(100, 0).toString()

    private fun cursorPosition()= cursorPosition

    internal fun inputText()= inputText

    internal fun deleteNextCharacter() {
        if (cursorPosition >= inputText.length) {
            return
        }
        inputConnection.deleteSurroundingText(0, 1)
        inputText = deleteCharacter(inputText, cursorPosition)
    }

    internal fun deletePreviousCharacter() {
        if (cursorPosition == 0) {
            return
        }
        inputConnection.deleteSurroundingText(1, 0)
        inputText = deleteCharacter(inputText, --cursorPosition)
    }

    internal fun addCharacter(c: Char) {
        if (cursorPosition >= maxCharacters()) {
            return
        }
        inputConnection.commitText(c.toString(), 1)
        inputText = if (cursorPosition++ >= inputText.length) {
            inputText + c
        } else {
            addCharacter(inputText, c, (cursorPosition - 1))
        }
    }

    private fun replaceNextCharacter(c: Char) {
        deleteNextCharacter()
        addCharacter(c)
    }

    /* in the case a synchronous action is required, use this */
    internal fun synchronousMoveCursorForward()=  replaceNextCharacter(inputText()[cursorPosition()])

    /* cursor actions are asynchronous events */
    internal fun moveCursorForwardAction() {
        if (cursorPosition >= inputText.length) {
            return
        }
        inputConnection.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT)
        )
        inputConnection.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT)
        )
    }

    internal fun moveCursorBackAction() {
        if (cursorPosition == 0) {
            return
        }
        inputConnection.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT)
        )
        inputConnection.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT)
        )
    }

    internal fun clearAll() {
        while (cursorPosition < inputText.length) {
            deleteNextCharacter()
        }
        while (cursorPosition > 0) {
            deletePreviousCharacter()
        }
    }

    internal abstract fun handleKeyStroke(c: Char)

    internal abstract fun handleKeyStroke(key: SpecialKey)

    internal abstract fun maxCharacters(): Int
}
