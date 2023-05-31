package nulled.ext.runelite

import net.runelite.api.Tile
import net.runelite.api.coords.WorldPoint
import nulled.core.API

object WorldPoint {
    fun WorldPoint.getTile(): Tile? {
        val x = this.x - API.client.baseX
        val y = this.y - API.client.baseY
        return API.client.scene.tiles[this.plane][x][y]
    }
}