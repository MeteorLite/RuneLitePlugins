package nulled.ext

import com.example.InteractionApi.InventoryInteraction
import net.runelite.api.Item
import nulled.core.API

object Item {
    val Item.composition
        get() = API.client.getItemDefinition(id)
    val Item.name : String
        get() = composition.name
    val widgetIDMap = HashMap<Item, Int>()

    var Item.widgetID : Int
        get() = widgetIDMap[this] ?: -1
        set(value) { widgetIDMap[this] = value }

    val slotMap = HashMap<Item, Int>()

    var Item.slot : Int
        get() = slotMap[this] ?: -1
        set(value) { slotMap[this] = value }

    fun Item.hasAction(action : String) : Boolean {
        for (invAction in composition.inventoryActions)
            if (action == invAction)
                return true
        return false
    }

    fun Item.interact(action: String) : Boolean {
        InventoryInteraction.useItem(id, action)
        return true
    }
}