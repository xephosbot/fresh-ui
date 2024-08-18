package com.xbot.ui.client.screen

import com.xbot.ui.client.component.UIComponent
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

class ExampleScreen : Screen(Text.of("My Custom Screen")) {

    override fun init() {
        addDrawable(UIComponent())
    }
}