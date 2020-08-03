package io.github.turskyi.customkeyboard.components.keyboard

import io.github.turskyi.customkeyboard.components.keyboard.controllers.SpecialKey

interface KeyboardListener {
    fun characterClicked(c: Char)
    fun specialKeyClicked(key: SpecialKey)
}