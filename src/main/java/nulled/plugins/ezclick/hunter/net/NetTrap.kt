package nulled.plugins.ezclick.hunter.net

import net.runelite.api.Tile
import net.runelite.api.coords.WorldPoint
import nulled.ext.runelite.WorldPoint.getTile

class NetTrap(var trapPoint: WorldPoint,
              var lootPoint: WorldPoint) {

    companion object {
        val TRAP_ONE = NetTrap(
                WorldPoint(3536, 3451, 0),
                WorldPoint(3537, 3451, 0))
        val TRAP_TWO = NetTrap(
                WorldPoint(3538, 3445, 0),
                WorldPoint(3537, 3445, 0))
        val TRAP_THREE = NetTrap(
                WorldPoint(3532, 3447, 0),
                WorldPoint(3532, 3446, 0))

        fun fromTile(tile: Tile) : NetTrap? {
            if (TRAP_ONE.trapPoint.getTile() == tile)
                return TRAP_ONE
            if (TRAP_TWO.trapPoint.getTile() == tile)
                return TRAP_TWO
            if (TRAP_THREE.trapPoint.getTile() == tile)
                return TRAP_THREE
            return null
        }
    }
}