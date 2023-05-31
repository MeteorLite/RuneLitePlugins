package nulled.ext

import com.example.InteractionApi.InventoryInteraction
import com.example.Packets.MousePackets
import com.example.Packets.NPCPackets
import net.runelite.api.Item
import net.runelite.api.NPC
import nulled.core.API

object Item {
    val Item.definition
        get() = API.client.getItemDefinition(id)
    val Item.name : String
        get() = definition.name
    val widgetIDMap = HashMap<Item, Int>()

    var Item.widgetID : Int
        get() = widgetIDMap[this] ?: -1
        set(value) { widgetIDMap[this] = value }

    val slotMap = HashMap<Item, Int>()

    var Item.slot : Int
        get() = slotMap[this] ?: -1
        set(value) { slotMap[this] = value }

    fun Item.hasAction(action : String) : Boolean {
        for (invAction in definition.inventoryActions)
            if (action == invAction)
                return true
        return false
    }

    fun Item.interact(action: String) : Boolean {
        InventoryInteraction.useItem(id, action)
        return true
    }

    fun Item.isNoted() : Boolean {
        return definition.note == 799
    }

    fun Item.useOn(npc: NPC) {
        MousePackets.queueClickPacket()
        NPCPackets.queueWidgetOnNPC(npc.index, id, slot, widgetID, false)
    }
}