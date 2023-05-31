/*
 * Copyright (c) 2018, Seth <https://github.com/sethtroll>
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
package nulled.plugins.ezclick

import net.runelite.client.input.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities

class EZClickMouseListener(var plugin: EZClickPlugin) : MouseAdapter() {

    override fun mousePressed(e: MouseEvent): MouseEvent {
        if (!SwingUtilities.isLeftMouseButton(e))
            return super.mousePressed(e)

        filterEZClickToggle(e, true)
        if (EZClickPlugin.ezClickActive)
            if (!e.isConsumed) {
                e.consume()
                plugin.clientThread.invoke(Runnable {
                    plugin.runningEZClick?.handleAction()
                })
            }
        return e
    }

    override fun mouseClicked(e: MouseEvent): MouseEvent {
        if (!SwingUtilities.isLeftMouseButton(e))
            return super.mouseClicked(e)

        filterEZClickToggle(e, false)
        return e
    }

    fun filterEZClickToggle(e: MouseEvent, toggle: Boolean) {
        val mousePos = e.point
        for (ezClick in plugin.validEZClicks) {
            if (ezClick.overlay.bounds.contains(mousePos)) {
                if (toggle)
                    plugin.handleOverlayClick(ezClick)
                e.consume()
            }
        }
    }
}
