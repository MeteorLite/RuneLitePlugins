package nulled.plugins.ezclick

import com.example.EthanApiPlugin.Collections.Inventory
import com.example.EthanApiPlugin.Collections.TileObjects
import com.example.EthanApiPlugin.EthanApiPlugin
import com.example.PacketUtils.PacketUtilsPlugin
import com.example.Packets.MousePackets
import com.example.Packets.ObjectPackets
import net.runelite.api.Client
import net.runelite.api.MenuEntry
import net.runelite.api.TileObject
import net.runelite.api.events.ClientTick
import net.runelite.api.events.GameTick
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.Widget
import net.runelite.client.eventbus.EventBus
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.input.MouseManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDependency
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.ui.overlay.OverlayManager
import net.runelite.client.ui.overlay.tooltip.Tooltip
import net.runelite.client.ui.overlay.tooltip.TooltipManager
import net.runelite.client.util.ColorUtil
import nulled.plugins.ezclick.hunter.net.CanifisNetHuntingEZClick
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
    lateinit var tooltipManager: TooltipManager

    @Inject
    lateinit var overlayManager: OverlayManager

    @Inject
    lateinit var eventBus: EventBus

    val mouseListener = EZClickMouseListener(this)

    var header = Tooltip(ColorUtil.prependColorTag("EZClick", Color.CYAN))
    var missingBonesTooltip = Tooltip(ColorUtil.prependColorTag("Missing: Bones to offer", Color.RED))
    var boneToOfferTooltip = Tooltip("")

    var ezClicks = ArrayList<EZClick>()
    var validEZClicks = ArrayList<EZClick>()
    var runningEZClick: EZClick? = null

    var notRunningColor = Color(82, 28, 24)
    var runningColor = Color(24, 82, 24)

    init {
        ezClicks.add(CanifisNetHuntingEZClick(this))
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

    @Subscribe(priority = Float.MAX_VALUE)
    fun onClientTick(clientTick: ClientTick) {
        runningEZClick?.onClientTick()
    }

    fun handleOverlayClick(ezClick: EZClick) {
        if (ezClick.isRunning) {
            ezClickActive = false
            eventBus.unregister(ezClick)
            ezClick.onStop()
            ezClick.isRunning = false
            ezClick.overlay.panelComponent.backgroundColor = notRunningColor
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

    fun processHouseAltarTooltips(menu: MenuEntry): Boolean {
        var foundHouseAlter = false
        if (menu.option == "Pray" && menu.target == "<col=ffff>Altar") {
            foundHouseAlter = true
            addIfMissing(headerTooltips, header)
            val bonesToOffer = Inventory.search().nameContains("bones").withAction("Bury").result()
            val bonesToOfferSize = bonesToOffer.size
            if (bonesToOfferSize == 0) {
                addIfMissing(problemTooltips, missingBonesTooltip)
            } else {
                boneToOffer = bonesToOffer[0]
                boneToOfferTooltip.text = ColorUtil.prependColorTag(boneToOffer!!.name, Color.GREEN)
                addIfMissing(validTooltips, boneToOfferTooltip)
                altar = TileObjects.search().nameContains("Altar").withAction("Pray").result()[0]
            }
        }
        return foundHouseAlter
    }

    @Subscribe
    fun onMenuOptionClicked(menuOptionClicked: MenuOptionClicked) {
        println("Option: " + menuOptionClicked.menuOption)
        println("Target: " + menuOptionClicked.menuTarget)
        if (ezClickActive) menuOptionClicked.consume()
        runningEZClick?.handleAction()
        if (ezHouseAltar) {
            if (boneToOffer != null) {
                MousePackets.queueClickPacket()
                ObjectPackets.queueWidgetOnTileObject(boneToOffer, altar)
            }
        }
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
        var ezHouseAltar = false
    }
}
