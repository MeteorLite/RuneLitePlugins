package nulled.ext

import net.runelite.api.Item
import net.runelite.api.NPC
import net.runelite.api.Tile
import net.runelite.api.TileItem
import net.runelite.api.coords.WorldPoint
import nulled.core.API
import nulled.ext.NPCs.transformedId

object NPCs {
    val NPC.transformedId
        get() = composition.transform().id
    val NPC.transformedName
        get() = composition.transform().name

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