package nulled.core

import net.runelite.api.events.ItemSpawned
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import nulled.ext.Loots.tile

@PluginDescriptor(
        name = "Ext",
        tags = ["null"],
        enabledByDefault = true,
        hidden = true)
/**
 * This helps us set some information for our extension functions
 */
class ExtPlugin : Plugin() {
    @Subscribe(priority = Float.MAX_VALUE)
    fun onItemSpawned(itemSpawned: ItemSpawned) {
        itemSpawned.item.tile = itemSpawned.tile
    }
}