package nulled.plugins.ezclick.plugins.hunter.nettrap.canifis

import net.runelite.api.Tile
import net.runelite.api.coords.WorldPoint
import nulled.ext.runelite.WorldPoint.getTile
import nulled.plugins.ezclick.plugins.hunter.nettrap.NetTrap

class CanifisNetTrap(trapPoint: WorldPoint,
                     lootPoint: WorldPoint) : NetTrap(trapPoint, lootPoint) {

    companion object {
        val TRAP_ONE = CanifisNetTrap(
                WorldPoint(3536, 3451, 0),
                WorldPoint(3537, 3451, 0))
        val TRAP_TWO = CanifisNetTrap(
                WorldPoint(3538, 3445, 0),
                WorldPoint(3537, 3445, 0))
        val TRAP_THREE = CanifisNetTrap(
                WorldPoint(3532, 3447, 0),
                WorldPoint(3532, 3446, 0))

        fun fromTile(tile: Tile) : CanifisNetTrap? {
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