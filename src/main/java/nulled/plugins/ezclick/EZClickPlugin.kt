package nulled.plugins.ezclick

import com.example.EthanApiPlugin.EthanApiPlugin
import com.example.PacketUtils.PacketUtilsPlugin
import net.runelite.api.Client
import net.runelite.api.TileObject
import net.runelite.api.events.GameTick
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.Widget
import net.runelite.client.callback.ClientThread
import net.runelite.client.eventbus.EventBus
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.input.MouseManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDependency
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.ui.overlay.OverlayManager
import net.runelite.client.ui.overlay.tooltip.Tooltip
import net.runelite.client.util.ColorUtil
import nulled.plugins.ezclick.plugins.hunter.nettrap.canifis.CanifisNetHuntingEZClick
import nulled.plugins.ezclick.plugins.prayer.housealtar.HouseAltarEZClick
import java.awt.Color
import javax.inject.Inject

@PluginDescriptor(name = "EZClick", tags = ["null"])
@PluginDependency(PacketUtilsPlugin::class)
@PluginDependency(EthanApiPlugin::class)
class EZClickPlugin : Plugin() {
    @Inject
    lateinit var client: Client

    @Inject
    lateinit var mouseManager: MouseManager

    @Inject
    lateinit var overlayManager: OverlayManager

    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var clientThread: ClientThread

    val mouseListener = EZClickMouseListener(this)

    var header = Tooltip(ColorUtil.prependColorTag("EZClick", Color.CYAN))

    var ezClicks = ArrayList<EZClick>()
    var validEZClicks = ArrayList<EZClick>()
    var runningEZClick: EZClick? = null

    var notRunningColor = Color(82, 28, 24)
    var runningColor = Color(24, 82, 24)

    init {
        ezClicks.addAll(arrayOf(
                CanifisNetHuntingEZClick(this),
                HouseAltarEZClick(this)))
    }

    override fun startUp() {
        mouseManager.registerMouseListener(mouseListener)
    }

    override fun shutDown() {
        mouseManager.unregisterMouseListener(mouseListener)
    }

    @Subscribe
    fun onGameTick(gameTick: GameTick) {
        removeInvalidEzClicks()
        startupNewEzClicks()
    }

    fun removeInvalidEzClicks() {
        val toRemove = ArrayList<EZClick>()
        for (ezClick in validEZClicks)
            if (!ezClick.isValid())
                toRemove.add(ezClick)
        for (ezClick in toRemove) {
            if (ezClick.isRunning) {
                ezClickActive = false
                stopEzClick(ezClick)
                runningEZClick = null
            }
            validEZClicks.remove(ezClick)
            overlayManager.remove(ezClick.overlay)
        }
    }

    fun startupNewEzClicks() {
        for (ezClick in ezClicks)
            if (!validEZClicks.contains(ezClick))
                if (ezClick.isValid()) {
                    validEZClicks.add(ezClick)
                    overlayManager.add(ezClick.overlay)
                }
    }

    fun stopEzClick(ezClick: EZClick) {
        eventBus.unregister(ezClick)
        ezClick.onStop()
        ezClick.isRunning = false
        ezClick.overlay.panelComponent.backgroundColor = notRunningColor
    }

    fun handleOverlayClick(ezClick: EZClick) {
        if (ezClick.isRunning) {
            ezClickActive = false
            stopEzClick(ezClick)
            runningEZClick = null
        } else {
            ezClickActive = true
            ezClick.onStart()
            eventBus.register(ezClick)
            ezClick.isRunning = true
            ezClick.overlay.panelComponent.backgroundColor = runningColor
            runningEZClick = ezClick
        }
    }

    fun addIfMissing(list: ArrayList<Tooltip>, tooltip: Tooltip) {
        if (!list.contains(tooltip)) {
            if (list === headerTooltips) tooltip.text = ColorUtil.prependColorTag(tooltip.text, Color.CYAN)
            if (list === problemTooltips) tooltip.text = ColorUtil.prependColorTag(tooltip.text, Color.RED)
            list.add(tooltip)
        }
    }

    var boneToOffer: Widget? = null
    var altar: TileObject? = null

    @Subscribe
    fun onMenuOptionClicked(menuOptionClicked: MenuOptionClicked) {
        if (ezClickActive) menuOptionClicked.consume()
    }

    fun reset() {
        headerTooltips.clear()
        problemTooltips.clear()
        validTooltips.clear()
    }
    companion object {
        var headerTooltips = ArrayList<Tooltip>()
        var problemTooltips = ArrayList<Tooltip>()
        var validTooltips = ArrayList<Tooltip>()
        var ezClickActive = false
    }
}
