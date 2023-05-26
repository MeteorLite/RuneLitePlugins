package nulled.ext

import net.runelite.api.NPC

object NPCs {
    val NPC.transformedId
        get() = composition.transform().id
    val NPC.transformedName
        get() = composition.transform().name
}