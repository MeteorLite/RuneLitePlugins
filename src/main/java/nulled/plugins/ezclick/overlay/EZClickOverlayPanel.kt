/*
 * Copyright (c) 2018, Raqes <j.raqes@gmail.com>
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
package nulled.plugins.ezclick.overlay

import net.runelite.client.ui.overlay.OverlayPanel
import net.runelite.client.ui.overlay.OverlayPosition
import net.runelite.client.ui.overlay.components.LineComponent
import nulled.plugins.ezclick.EZClick
import nulled.plugins.ezclick.EZClickPlugin
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D

open class EZClickOverlayPanel(
        var plugin: EZClickPlugin,
        var ezClick: EZClick,
        var text: String,
        var textColor: Color = Color.WHITE,
        var descriptionText: String = "") : OverlayPanel(plugin) {
    init {
        position = OverlayPosition.TOP_LEFT
        panelComponent.backgroundColor = plugin.notRunningColor
        panelComponent.children.add(LineComponent.builder()
                .left(text)
                .build())
        if (descriptionText.isNotBlank()) {
            panelComponent.children.add(LineComponent.builder()
                    .left(descriptionText)
                    .build())
        }
    }

    private var setSize = false

    override fun render(graphics: Graphics2D): Dimension {
        if (!setSize) {
            panelComponent.preferredSize = Dimension(
                    graphics.fontMetrics.stringWidth(text) + 10,
                    0)
            setSize = true
        }
        return panelComponent.render(graphics)
    }
}
