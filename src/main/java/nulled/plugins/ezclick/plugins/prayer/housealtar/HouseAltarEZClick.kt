package nulled.plugins.ezclick.plugins.prayer.housealtar

import com.example.EthanApiPlugin.Collections.Inventory
import com.example.Packets.MousePackets
import com.example.Packets.ObjectPackets
import com.example.Packets.WidgetPackets
import net.runelite.api.Item
import net.runelite.api.ItemID
import net.runelite.api.events.GameTick
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.ui.overlay.tooltip.Tooltip
import nulled.core.Items
import nulled.core.NPCs
import nulled.core.Objects
import nulled.ext.Item.isNoted
import nulled.ext.Item.name
import nulled.ext.Item.useOn
import nulled.ext.Objects.interact
import nulled.plugins.ezclick.EZClick
import nulled.plugins.ezclick.EZClickPlugin
import nulled.plugins.ezclick.overlay.EZClickOverlayPanel

class HouseAltarEZClick(var plugin: EZClickPlugin) : EZClick() {

    init {
        validChecks.add(this::worldCheck)
        validChecks.add(this::itemsCheck)
    }

    override var overlay: EZClickOverlayPanel = HouseAltarOverlay(plugin, this)

    private var unNoteBones = Tooltip("Un-note bones")
    private var enterHouse = Tooltip("Enter house")
    private var offerBones = Tooltip("Offer bones")
    private var leaveHouse = Tooltip("Leave house")
    private var waiting = Tooltip("Waiting")

    var houseAltarStatus: HouseAltarStatus = HouseAltarStatus.UNINITIALIZED

    fun worldCheck(): Boolean {
        return plugin.client.world == 330
    }

    fun locationCheck() : Boolean {
        return plugin.client.localPlayer.worldLocation.regionID == 14133
    }

    fun itemsCheck() : Boolean {
        if (Items.inventoryContains(ItemID.COINS_995))
            if (hasNotedBones())
                return true
        return false
    }

    fun hasNotedBones() : Boolean {
        Items.getAll()?.let {
            for (item in it) {
                if (item.isNoted())
                    if (item.name.contains(" bones"))
                        return true
            }
        }
        return false
    }

    fun hasUnNotedBones() : Boolean {
        Items.getAll()?.let {
            for (item in it) {
                if (!item.isNoted())
                    if (item.name.contains(" bones"))
                        return true
            }
        }
        return false
    }

    fun getNotedBones() : Item? {
        Items.getAll()?.let {
            for (item in it) {
                if (item.isNoted())
                    if (item.name.contains(" bones"))
                        return item
            }
        }
        return null
    }

    fun getBoneToOffer() : Item? {
        Items.getAll()?.let {
            for (item in it) {
                if (!item.isNoted())
                    if (item.name.contains(" bones"))
                        return item
            }
        }
        return null
    }

    override fun onStart() {
        EZClickPlugin.ezClickActive = true
    }

    @Subscribe
    fun onGameTick(gameTick: GameTick) {
        plugin.reset()
        plugin.addIfMissing(EZClickPlugin.headerTooltips, plugin.header)

        if (plugin.client.localPlayer.worldLocation.regionID == 11826) {
            if (hasNotedBones()) {
                if (!hasUnNotedBones()) {
                    houseAltarStatus = HouseAltarStatus.UNNOTE
                    plugin.addIfMissing(EZClickPlugin.validTooltips, unNoteBones)
                } else {
                    houseAltarStatus = HouseAltarStatus.ENTER_HOUSE
                    plugin.addIfMissing(EZClickPlugin.validTooltips, enterHouse)
                }
            }
        } else if (plugin.client.isInInstancedRegion) {
            if (hasUnNotedBones()) {
                houseAltarStatus = HouseAltarStatus.OFFER_BONES
                plugin.addIfMissing(EZClickPlugin.validTooltips, offerBones)
            } else {
                houseAltarStatus = HouseAltarStatus.LEAVE_HOUSE
                plugin.addIfMissing(EZClickPlugin.validTooltips, leaveHouse)
            }
        }
    }

    override fun handleAction() {
        when (houseAltarStatus) {
            HouseAltarStatus.UNNOTE -> {
                plugin.client.getWidget(WidgetInfo.DIALOG_OPTION_OPTIONS)?.let {
                    if (!it.isHidden) {
                        MousePackets.queueClickPacket()
                        WidgetPackets.queueResumePause(it.id, 3)
                        return
                    }
                }
                NPCs.getFirst("Phials")?.let { phials ->
                    getNotedBones()?.let { bone ->
                        bone.useOn(phials)
                    }
                }
            }
            HouseAltarStatus.ENTER_HOUSE -> {
                Objects.getFirst("House Advertisement")?.interact("Visit-Last")
            }
            HouseAltarStatus.OFFER_BONES -> {
                Objects.getFirstWithAction("Altar", "Pray")?.let { altar ->
                    val bonesToOffer = Inventory.search().nameContains("bones").withAction("Bury").result()
                    if (bonesToOffer.size > 0) {
                        MousePackets.queueClickPacket()
                        ObjectPackets.queueWidgetOnTileObject(bonesToOffer[0], altar)
                    }
                }
            }
            HouseAltarStatus.LEAVE_HOUSE -> {
                Objects.getFirst("Portal")?.interact("Enter")
            }
            else -> {}
        }
    }
}