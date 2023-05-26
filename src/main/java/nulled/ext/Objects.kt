package nulled.ext

import net.runelite.api.*
import nulled.core.API

object Objects {
    val TileObject.composition
        get() = API.client.getObjectDefinition(id)

    val TileObject.name : String?
        get() = composition.name

    fun TileObject.hasAction(action : String) : Boolean {
        for (objAction in composition.actions)
            if (action == objAction)
                return true
        return false
    }
}