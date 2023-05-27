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
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.Widget
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDependency
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.ui.overlay.tooltip.Tooltip
import net.runelite.client.ui.overlay.tooltip.TooltipManager
import net.runelite.client.util.ColorUtil
import java.awt.Color
import javax.inject.Inject

@PluginDescriptor(name = "EZClick", tags = ["null"])
@PluginDependency(PacketUtilsPlugin::class)
@PluginDependency(EthanApiPlugin::class)
class EZClickPlugin : Plugin() {
    @Inject
    lateinit var client: Client

    @Inject
    lateinit var tooltipManager: TooltipManager

    var header = Tooltip(ColorUtil.prependColorTag("EZClick", Color.CYAN))
    var missingBonesTooltip = Tooltip(ColorUtil.prependColorTag("Missing: Bones to offer", Color.RED))
    var boneToOfferTooltip = Tooltip("")

    @Subscribe(priority = Float.MAX_VALUE)
    fun onClientTick(clientTick: ClientTick?) {
        //RL flips entries frequently
        for (entry in client.menuEntries) {
            if (processHouseAltarTooltips(entry)) {
                ezClickActive = true
                ezHouseAltar = true
                return
            }
        }
        reset()
    }

    fun reset() {
        ezClickActive = false
        ezHouseAltar = false
        headerTooltips.clear()
        problemTooltips.clear()
        validTooltips.clear()
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
        if (ezHouseAltar) {
            if (boneToOffer != null) {
                MousePackets.queueClickPacket()
                ObjectPackets.queueWidgetOnTileObject(boneToOffer, altar)
            }
        }
    }

    companion object {
        var headerTooltips = ArrayList<Tooltip>()
        var problemTooltips = ArrayList<Tooltip>()
        var validTooltips = ArrayList<Tooltip>()
        var ezClickActive = false
        var ezHouseAltar = false
    }
}
