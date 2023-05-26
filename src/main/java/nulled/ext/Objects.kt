package nulled.ext

import net.runelite.api.*
import net.runelite.api.Item
import net.runelite.api.coords.WorldPoint
import nulled.core.API
import nulled.ext.Item.composition
import nulled.ext.NPCs.transformedId
import nulled.ext.Objects.name

object Objects {
    val TileObject.composition
        get() = API.client.getItemDefinition(id)

    val TileObject.name : String?
        get() = composition.name

    fun TileObject.hasAction(action : String) : Boolean {
        for (invAction in composition.inventoryActions)
            if (action == invAction)
                return true
        return false
    }

    val tilesMap = HashMap<TileItem, Tile>()

    var TileItem.tile : Tile?
        get() = tilesMap[this]
        set(value) { tilesMap[this] = value!! }

    fun TileItem.getWorldLocation() : WorldPoint {
        return tile!!.worldLocation
    }

    fun TileItem.getName() : String {
        return API.client.getItemDefinition(id).name
    }
    val TileItem.notedId
        get() = API.client.getItemDefinition(id).linkedNoteId
}