package nulled.ext

import net.runelite.api.Tile
import net.runelite.api.TileItem
import net.runelite.api.coords.WorldPoint
import nulled.core.API

object Loots {
    val Tile.worldX
        get() = worldLocation.x
    val Tile.worldY
        get() = worldLocation.y
    val TileItem.name : String?
        get() = API.client.getItemDefinition(id).name

    val tilesMap = HashMap<TileItem, Tile>()

    var TileItem.tile : Tile?
        get() = tilesMap[this]
        set(value) { tilesMap[this] = value!! }

    fun TileItem.getWorldLocation() : WorldPoint {
        return tile!!.worldLocation
    }

    val TileItem.notedId
        get() = API.client.getItemDefinition(id).linkedNoteId
}