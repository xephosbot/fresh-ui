package com.xbot.ui.client.utils

import com.mojang.blaze3d.systems.RenderSystem
import com.xbot.ui.client.FreshUIClient
import com.xbot.ui.client.MOD_ID
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.client.resource.metadata.AnimationResourceMetadata
import net.minecraft.client.resource.metadata.GuiResourceMetadata
import net.minecraft.client.texture.Scaling
import net.minecraft.client.texture.Scaling.*
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasHolder
import net.minecraft.client.texture.TextureManager
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.metadata.ResourceMetadata
import net.minecraft.resource.metadata.ResourceMetadataSerializer
import net.minecraft.util.Identifier
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min

class GuiAtlasManager(
    textureManager: TextureManager
) : SpriteAtlasHolder(
    textureManager,
    Identifier.of(MOD_ID, "textures/atlas/gui.png"),
    Identifier.of(MOD_ID, "gui"),
    METADATA_READERS
), IdentifiableResourceReloadListener {

    public override fun getSprite(objectId: Identifier): Sprite {
        return super.getSprite(objectId)
    }

    fun getScaling(sprite: Sprite): Scaling {
        return getGuiMetadata(sprite).scaling()
    }

    private fun getGuiMetadata(sprite: Sprite): GuiResourceMetadata {
        return sprite.contents.metadata.decode(GuiResourceMetadata.SERIALIZER)
            .orElse(GuiResourceMetadata.DEFAULT) as GuiResourceMetadata
    }

    override fun getFabricId(): Identifier {
        return Identifier.of(MOD_ID, "gui_atlas_manager")
    }

    companion object {
        private val METADATA_READERS = setOf(
            AnimationResourceMetadata.READER,
            GuiResourceMetadata.SERIALIZER
        )
    }
}

fun DrawContext.drawWithFix(texture: Identifier, x: Int, y: Int, width: Int, height: Int) {
    //val sprite = FreshUIClient.guiAtlasManager.getSprite(texture)
    println(readMetaData(texture).scaling.toString())

    /*
    when (val scaling = metaData.scaling) {
        is Stretch -> this.drawWithFixSprite(sprite.atlasId, x, x + width, y, y + height, 0, sprite.minU, sprite.maxU, sprite.minV, sprite.maxV)
        is Tile -> this.drawSpriteTiled(
            sprite,
            x,
            y,
            0,
            width,
            height,
            0,
            0,
            scaling.width(),
            scaling.height(),
            scaling.width(),
            scaling.height()
        )

        is NineSlice -> this.drawSpriteNinePatch(sprite, scaling, x, y, 0, width, height)
    }*/
}

fun DrawContext.drawWithFixSprite(
    texture: Identifier,
    x1: Int,
    x2: Int,
    y1: Int,
    y2: Int,
    z: Int,
    u1: Float,
    u2: Float,
    v1: Float,
    v2: Float
) {
    RenderSystem.setShaderTexture(0, texture)
    RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
    val matrix4f = matrices.peek().positionMatrix
    val bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
    bufferBuilder.vertex(matrix4f, x1.toFloat(), y1.toFloat(), z.toFloat()).texture(u1, v1)
    bufferBuilder.vertex(matrix4f, x1.toFloat(), y2.toFloat(), z.toFloat()).texture(u1, v2)
    bufferBuilder.vertex(matrix4f, x2.toFloat(), y2.toFloat(), z.toFloat()).texture(u2, v2)
    bufferBuilder.vertex(matrix4f, x2.toFloat(), y1.toFloat(), z.toFloat()).texture(u2, v1)
    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
}

fun DrawContext.drawWithSprite(
    sprite: Sprite,
    i: Int,
    j: Int,
    k: Int,
    l: Int,
    x: Int,
    y: Int,
    z: Int,
    width: Int,
    height: Int
) {
    if (width != 0 && height != 0) {
        this.drawWithFixSprite(
            sprite.atlasId,
            x,
            x + width,
            y,
            y + height,
            z,
            sprite.getFrameU(k.toFloat() / i.toFloat()),
            sprite.getFrameU((k + width).toFloat() / i.toFloat()),
            sprite.getFrameV(l.toFloat() / j.toFloat()),
            sprite.getFrameV((l + height).toFloat() / j.toFloat())
        )
    }
}

private fun DrawContext.drawSpriteNinePatch(sprite: Sprite, nineSlice: NineSlice, x: Int, y: Int, z: Int, width: Int, height: Int) {
    val border = nineSlice.border()
    val i = min(border.left().toDouble(), (width / 2).toDouble()).toInt()
    val j = min(border.right().toDouble(), (width / 2).toDouble()).toInt()
    val k = min(border.top().toDouble(), (height / 2).toDouble()).toInt()
    val l = min(border.bottom().toDouble(), (height / 2).toDouble()).toInt()
    if (width == nineSlice.width() && height == nineSlice.height()) {
        this.drawWithSprite(sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, z, width, height)
    } else if (height == nineSlice.height()) {
        this.drawWithSprite(sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, z, i, height)
        this.drawSpriteTiled(
            sprite,
            x + i,
            y,
            z,
            width - j - i,
            height,
            i,
            0,
            nineSlice.width() - j - i,
            nineSlice.height(),
            nineSlice.width(),
            nineSlice.height()
        )
        this.drawWithSprite(
            sprite,
            nineSlice.width(),
            nineSlice.height(),
            nineSlice.width() - j,
            0,
            x + width - j,
            y,
            z,
            j,
            height
        )
    } else if (width == nineSlice.width()) {
        this.drawWithSprite(sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, z, width, k)
        this.drawSpriteTiled(
            sprite,
            x,
            y + k,
            z,
            width,
            height - l - k,
            0,
            k,
            nineSlice.width(),
            nineSlice.height() - l - k,
            nineSlice.width(),
            nineSlice.height()
        )
        this.drawWithSprite(
            sprite,
            nineSlice.width(),
            nineSlice.height(),
            0,
            nineSlice.height() - l,
            x,
            y + height - l,
            z,
            width,
            l
        )
    } else {
        this.drawWithSprite(sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, z, i, k)
        this.drawSpriteTiled(
            sprite,
            x + i,
            y,
            z,
            width - j - i,
            k,
            i,
            0,
            nineSlice.width() - j - i,
            k,
            nineSlice.width(),
            nineSlice.height()
        )
        this.drawWithSprite(
            sprite,
            nineSlice.width(),
            nineSlice.height(),
            nineSlice.width() - j,
            0,
            x + width - j,
            y,
            z,
            j,
            k
        )
        this.drawWithSprite(
            sprite,
            nineSlice.width(),
            nineSlice.height(),
            0,
            nineSlice.height() - l,
            x,
            y + height - l,
            z,
            i,
            l
        )
        this.drawSpriteTiled(
            sprite,
            x + i,
            y + height - l,
            z,
            width - j - i,
            l,
            i,
            nineSlice.height() - l,
            nineSlice.width() - j - i,
            l,
            nineSlice.width(),
            nineSlice.height()
        )
        this.drawWithSprite(
            sprite,
            nineSlice.width(),
            nineSlice.height(),
            nineSlice.width() - j,
            nineSlice.height() - l,
            x + width - j,
            y + height - l,
            z,
            j,
            l
        )
        this.drawSpriteTiled(
            sprite,
            x,
            y + k,
            z,
            i,
            height - l - k,
            0,
            k,
            i,
            nineSlice.height() - l - k,
            nineSlice.width(),
            nineSlice.height()
        )
        this.drawSpriteTiled(
            sprite,
            x + i,
            y + k,
            z,
            width - j - i,
            height - l - k,
            i,
            k,
            nineSlice.width() - j - i,
            nineSlice.height() - l - k,
            nineSlice.width(),
            nineSlice.height()
        )
        this.drawSpriteTiled(
            sprite,
            x + width - j,
            y + k,
            z,
            i,
            height - l - k,
            nineSlice.width() - j,
            k,
            j,
            nineSlice.height() - l - k,
            nineSlice.width(),
            nineSlice.height()
        )
    }
}

private fun DrawContext.drawSpriteTiled(
    sprite: Sprite,
    x: Int,
    y: Int,
    z: Int,
    width: Int,
    height: Int,
    i: Int,
    j: Int,
    tileWidth: Int,
    tileHeight: Int,
    k: Int,
    l: Int
) {
    if (width > 0 && height > 0) {
        if (tileWidth > 0 && tileHeight > 0) {
            var m = 0
            while (m < width) {
                val n = min(tileWidth.toDouble(), (width - m).toDouble()).toInt()

                var o = 0
                while (o < height) {
                    val p = min(tileHeight.toDouble(), (height - o).toDouble()).toInt()
                    this.drawWithSprite(sprite, k, l, i, j, x + m, y + o, z, n, p)
                    o += tileHeight
                }
                m += tileWidth
            }
        } else {
            throw IllegalArgumentException("Tiled sprite texture size must be positive, got " + tileWidth + "x" + tileHeight)
        }
    }
}


fun readMetaData(resourceId: Identifier):  GuiResourceMetadata {
    val resourceManager: ResourceManager = MinecraftClient.getInstance().resourceManager
    val resource = resourceManager.getResource(resourceId)
    return resource.getOrNull()?.metadata?.decode(GuiResourceMetadata.SERIALIZER)?.getOrNull() ?: GuiResourceMetadata.DEFAULT
}
