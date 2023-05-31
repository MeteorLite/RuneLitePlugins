package nulled.plugins.ezclick

import nulled.plugins.ezclick.overlay.EZClickOverlayPanel
import kotlin.reflect.KFunction

open class EZClick {

    var validChecks = ArrayList<KFunction<Boolean>>()

    open lateinit var overlay: EZClickOverlayPanel

    var isRunning = false

    fun isValid(): Boolean {
        validChecks.forEach {
            if (!it.call())
                return false
        }
        return true
    }

    open fun handleAction() {

    }

    open fun onStart() {

    }

    open fun onStop() {

    }
}