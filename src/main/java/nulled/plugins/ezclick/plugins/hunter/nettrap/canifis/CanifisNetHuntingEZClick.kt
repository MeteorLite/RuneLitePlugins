package nulled.plugins.ezclick.plugins.hunter.nettrap.canifis

import net.runelite.api.*
import net.runelite.api.events.GameTick
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.ui.overlay.tooltip.Tooltip
import nulled.core.Items
import nulled.ext.Item.interact
import nulled.ext.Objects.interact
import nulled.ext.runelite.Tile.getObject
import nulled.ext.runelite.Tile.hasLoot
import nulled.ext.runelite.Tile.hasObject
import nulled.ext.runelite.Tile.lootAny
import nulled.ext.runelite.WorldPoint.getTile
import nulled.plugins.ezclick.EZClick
import nulled.plugins.ezclick.EZClickPlugin
import nulled.plugins.ezclick.plugins.hunter.nettrap.NetHuntingOverlay
import nulled.plugins.ezclick.plugins.hunter.nettrap.NetTrap
import nulled.plugins.ezclick.plugins.hunter.nettrap.NetTrap.Companion.TRAP_ONE
import nulled.plugins.ezclick.plugins.hunter.nettrap.NetTrap.Companion.TRAP_THREE
import nulled.plugins.ezclick.plugins.hunter.nettrap.NetTrap.Companion.TRAP_TWO
import nulled.plugins.ezclick.plugins.hunter.nettrap.NetTrapStatus
import nulled.plugins.ezclick.plugins.hunter.nettrap.canifis.CanifisNetTrap.Companion.fromTile
import nulled.plugins.ezclick.overlay.EZClickOverlayPanel

class CanifisNetHuntingEZClick(var plugin: EZClickPlugin) : EZClick() {

    init {
        validChecks.add(this::locationCheck)
        validChecks.add(this::itemsCheck)
    }

    override var overlay: EZClickOverlayPanel = NetHuntingOverlay(plugin, this, "Canifis")

    private var unsetTrapId = 9341
    private var setTrapId = 9257
    private var caughtTrapId = 9004

    private var trapOneStatus = NetTrapStatus.UNINITIALIZED
    private var trapTwoStatus = NetTrapStatus.UNINITIALIZED
    private var trapThreeStatus = NetTrapStatus.UNINITIALIZED

    private var trapsNeedingAttention = ArrayList<CanifisNetTrap>()
    private var lastNearestTrap: NetTrap? = null

    private var releaseLizard = Tooltip("Release Swamp lizard")
    private var setTrap = Tooltip("Set Trap")
    private var checkTrap = Tooltip("Check Trap")
    private var loot = Tooltip("Pickup loot")

    private var waiting = Tooltip("Waiting")

    fun locationCheck() : Boolean {
        return plugin.client.localPlayer.worldLocation.regionID == 14133
    }

    fun itemsCheck() : Boolean {
        if (Items.inventoryContains(ItemID.ROPE))
            if (Items.inventoryContains(ItemID.SMALL_FISHING_NET))
                return true
        return false
    }

    override fun onStart() {
        EZClickPlugin.ezClickActive = true
    }

    @Subscribe
    fun onGameTick(gameTick: GameTick) {
        trapsNeedingAttention.clear()

        trapOneStatus = TRAP_ONE.trapPoint.getTile()!!.updateTrapStatus()
        trapTwoStatus =  TRAP_TWO.trapPoint.getTile()!!.updateTrapStatus()
        trapThreeStatus = TRAP_THREE.trapPoint.getTile()!!.updateTrapStatus()

        lastNearestTrap = getNearestTrap()

        plugin.reset()
        plugin.addIfMissing(EZClickPlugin.headerTooltips, plugin.header)
        if (lastNearestTrap == null)
            plugin.addIfMissing(EZClickPlugin.problemTooltips, waiting)
        else {
            if (Items.getFirst("Swamp lizard") != null)
                plugin.addIfMissing(EZClickPlugin.validTooltips, releaseLizard)
            else if (lastNearestTrap!!.lootPoint.getTile()?.hasLoot(ItemID.ROPE, ItemID.SMALL_FISHING_NET) == true) {
                plugin.addIfMissing(EZClickPlugin.validTooltips, loot)
            }
            else {
                val status = lastNearestTrap!!.trapPoint.getTile()!!.getTrapStatus()
                when (status) {
                    0 -> plugin.addIfMissing(EZClickPlugin.validTooltips, setTrap)
                    2 -> plugin.addIfMissing(EZClickPlugin.validTooltips, checkTrap)
                }
            }
        }
    }

    override fun handleAction() {
        if (releaseLizards())
            return

        handleNearestTrap()
    }

    fun releaseLizards() : Boolean {
        return Items.getFirst("Swamp lizard")?.interact("Release") == true
    }

    fun getNearestTrap() : NetTrap? {
        return trapsNeedingAttention.minByOrNull {
            it.trapPoint.getTile()!!.localLocation.distanceTo(plugin.client.localPlayer.localLocation)
        }
    }

    fun handleNearestTrap() : Boolean {
        lastNearestTrap?.let {
            if (handleTrap(it))
                return true
        }
        return false
    }

    fun handleTrap(netTrap: NetTrap) : Boolean {
        if (netTrap.lootPoint.getTile()?.lootAny(ItemID.ROPE, ItemID.SMALL_FISHING_NET) == true)
            return true
        netTrap.trapPoint.getTile()?.getObject(unsetTrapId)?.let {
            it.interact("Set-trap")
            return true
        }
        netTrap.trapPoint.getTile()?.getObject(caughtTrapId)?.let {
            it.interact("Check")
            return true
        }
        return false
    }

    private fun Tile.updateTrapStatus() : NetTrapStatus {
        if (!hasObject(setTrapId)) {
            if (hasObject(unsetTrapId)) {
                trapsNeedingAttention.add(fromTile(this)!!)
                return NetTrapStatus.EMPTY
            }
            if (hasObject(caughtTrapId)) {
                trapsNeedingAttention.add(fromTile(this)!!)
                return NetTrapStatus.CAUGHT
            }
        }
        return NetTrapStatus.UNINITIALIZED
    }

    private fun Tile.getTrapStatus() : Int {
        if (!hasObject(setTrapId)) {
            if (hasObject(unsetTrapId)) {
                return 0
            }
            if (hasObject(caughtTrapId)) {
                return 2
            }
        }
        return -1
    }
}