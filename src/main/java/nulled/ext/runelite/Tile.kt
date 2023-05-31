package nulled.ext.runelite

import net.runelite.api.ItemID
import net.runelite.api.Tile
import net.runelite.api.TileObject
import nulled.ext.Loots.interact

object Tile {
    fun Tile.hasObject(id: Int) : Boolean {
        return gameObjects.any { it != null && it.id == id }
    }

    fun Tile.getObject(id: Int) : TileObject? {
        return gameObjects?.firstOrNull { it != null && it.id == id }
    }

    fun Tile.lootAny(vararg id: Int): Boolean {
        groundItems?.forEach { loot ->
            if (id.contains(loot.id)) {
                loot.interact(false)
                return true
            }
        }
        return false
    }

    fun Tile.hasLoot(vararg id: Int): Boolean {
        groundItems?.forEach { loot ->
            if (id.contains(loot.id))
                return true
        }
        return false
    }
}