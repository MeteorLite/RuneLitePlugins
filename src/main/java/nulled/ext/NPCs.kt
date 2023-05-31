package nulled.ext

import net.runelite.api.NPC

object NPCs {
    val NPC.transformedId
        get() = composition.id
    val NPC.transformedName
        get() = composition.name
}