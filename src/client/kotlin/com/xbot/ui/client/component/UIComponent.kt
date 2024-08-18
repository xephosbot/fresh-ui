package com.xbot.ui.client.component

import com.mojang.blaze3d.systems.RenderSystem
import com.xbot.ui.client.utils.drawWithFix
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.screen.ButtonTextures
import net.minecraft.util.Identifier

class UIComponent : Drawable {

    var visible: Boolean = true
    var active: Boolean = true

    private var _state: State = State.IDLE
    val state: State get() = _state

    protected var width: Int = 0
    protected var height: Int = 0
    private val x = 0
    private val y = 0

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (!visible) return

        RenderSystem.enableBlend()
        RenderSystem.enableDepthTest()
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        context.drawWithFix(
            TEXTURES[active, state == State.FOCUSSED],
            100,
            100,
            98,
            98
        )
        context.drawBorder(100, 100, 100, 100, 0xFFFFFFFF.toInt())
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    }

    enum class State {
        IDLE,
        HOVERED,
        FOCUSSED
    }

    private val TEXTURES: ButtonTextures = ButtonTextures(
        Identifier.of("fresh-ui", "textures/gui/button_primary"),
        Identifier.of("fresh-ui", "textures/gui/button_primary"),
        Identifier.of("fresh-ui", "textures/gui/button_primary"),
    )
}
