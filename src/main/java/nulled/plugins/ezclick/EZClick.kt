package nulled.plugins.ezclick

import net.runelite.api.events.ClientTick
import nulled.plugins.ezclick.overlay.EZClickOverlayPanel
import kotlin.reflect.KFunction

open class EZClick {

    var validChecks = ArrayList<KFunction<Boolean>>()

    open lateinit var overlay: EZClickOverlayPanel

    var isRunning = false

    fun isValid(): Boolean {
        validChecks.forEach {
            if (it.call()) {
                return true
            }
        }
        return false
    }

    open fun handleAction() {

    }

    open fun onStart() {

    }

    open fun onStop() {

    }

    open fun onClientTick() {

    }
}