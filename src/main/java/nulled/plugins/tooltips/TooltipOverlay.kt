/*
 * Copyright (c) 2017, Tomas Slusny <slusnucky@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package nulled.plugins.tooltips

import net.runelite.api.Client
import net.runelite.api.widgets.WidgetID
import net.runelite.client.config.RuneLiteConfig
import net.runelite.client.config.TooltipPositionType
import net.runelite.client.ui.overlay.Overlay
import net.runelite.client.ui.overlay.OverlayLayer
import net.runelite.client.ui.overlay.OverlayPosition
import net.runelite.client.ui.overlay.OverlayPriority
import net.runelite.client.ui.overlay.components.ComponentConstants
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity
import net.runelite.client.ui.overlay.components.PanelComponent
import net.runelite.client.ui.overlay.components.TooltipComponent
import net.runelite.client.ui.overlay.tooltip.Tooltip
import net.runelite.client.ui.overlay.tooltip.TooltipManager
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TooltipOverlay @Inject private constructor(private val client: Client, private val runeLiteConfig: RuneLiteConfig, private val tooltipManager: TooltipManager) : Overlay() {
    private var prevWidth = 0
    private var prevHeight = 0

    init {
        position = OverlayPosition.TOOLTIP
        priority = OverlayPriority.HIGHEST
        layer = OverlayLayer.ABOVE_WIDGETS
        // additionally allow tooltips above the full screen world map and welcome screen
        drawAfterInterface(WidgetID.FULLSCREEN_CONTAINER_TLI)
    }

    override fun render(graphics: Graphics2D): Dimension? {
        val tooltips = tooltipManager.tooltips
        return if (tooltips.isEmpty()) {
            null
        } else try {
            renderTooltips(graphics, tooltips)!!
        } finally {
            // Tooltips must always be cleared each frame
            tooltipManager.clear()
        }
    }

    private fun renderTooltips(graphics: Graphics2D, tooltips: List<Tooltip>): Dimension? {
        val canvasWidth = client.canvasWidth
        val canvasHeight = client.canvasHeight
        val mouseCanvasPosition = client.mouseCanvasPosition
        val tooltipX = Math.min(canvasWidth - prevWidth, mouseCanvasPosition.x)
        val tooltipY = if (runeLiteConfig.tooltipPosition() == TooltipPositionType.ABOVE_CURSOR) Math.max(0, mouseCanvasPosition.y - prevHeight) else Math.min(canvasHeight - prevHeight, mouseCanvasPosition.y + UNDER_OFFSET)
        var width = 0
        var height = 0
        for (tooltip in tooltips) {
            val entity: LayoutableRenderableEntity
            val backgroundColor = ComponentConstants.STANDARD_BACKGROUND_COLOR
            if (tooltip.component != null) {
                entity = tooltip.component
                if (entity is PanelComponent) {
                    entity.backgroundColor = backgroundColor
                }
            } else {
                val tooltipComponent = TooltipComponent()
                tooltipComponent.setModIcons(client.modIcons)
                tooltipComponent.setText(tooltip.text)
                tooltipComponent.setBackgroundColor(backgroundColor)
                entity = tooltipComponent
            }
            entity.setPreferredLocation(Point(tooltipX, tooltipY + height))
            val dimension = entity.render(graphics)

            // Create incremental tooltip newBounds
            height += dimension.height + PADDING
            width = Math.max(width, dimension.width)
        }
        prevWidth = width
        prevHeight = height
        return null
    }

    companion object {
        private const val UNDER_OFFSET = 24
        private const val PADDING = 2
    }
}
