package nulled.core

import net.runelite.api.Client
import net.runelite.client.RuneLite

object API {
    val client = RuneLite.getInjector().getInstance(Client::class.java);
}