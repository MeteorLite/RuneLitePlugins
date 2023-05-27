package nulled.plugins

import net.runelite.client.eventbus.EventBus
import net.runelite.client.events.ExternalPluginsChanged
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDependency
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginManager
import net.runelite.client.plugins.agility.AgilityPlugin
import net.runelite.client.plugins.mousehighlight.MouseHighlightPlugin
import net.runelite.client.plugins.xptracker.XpTrackerPlugin
import javax.inject.Inject

@PluginDescriptor(
        name = "Replacer",
        tags = ["null"],
        enabledByDefault = true,
        hidden = true)
@PluginDependency(XpTrackerPlugin::class)
@PluginDependency(AgilityPlugin::class)
@PluginDependency(MouseHighlightPlugin::class)
/**
 * We rebuild some modified runelite plugins
 * We remove the original plugins from the list to prevent confusion
 */
class ReplacerPlugin : Plugin() {

    @Inject
    lateinit var pluginManager: PluginManager

    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var rlAgility: AgilityPlugin

    @Inject
    lateinit var rlTooltips: MouseHighlightPlugin

    private var complete = false

    override fun startUp() {
        if (!complete) {
            removeAll(rlAgility, rlTooltips)

            //Reload plugin list
            eventBus.post(ExternalPluginsChanged(null))

            complete = true
        }
    }

    private fun removeAll(vararg plugins: Plugin) {
        plugins.forEach { pluginManager.remove(it) }
    }
}