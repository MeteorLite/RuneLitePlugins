package nulled.plugins

import com.example.EthanApiPlugin.EthanApiPlugin
import com.example.PacketUtils.PacketUtilsPlugin
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
@PluginDependency(EthanApiPlugin::class)
@PluginDependency(PacketUtilsPlugin::class)
/**
 * We rebuild some modified runelite plugins
 * We remove the original plugins from the list to prevent confusion
 */
class HiderPlugin : Plugin() {

    @Inject
    lateinit var pluginManager: PluginManager

    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var rlAgility: AgilityPlugin

    @Inject
    lateinit var rlTooltips: MouseHighlightPlugin

    @Inject
    lateinit var ethanApiPlugin: EthanApiPlugin

    @Inject
    lateinit var packetUtilsPlugin: PacketUtilsPlugin

    private var complete = false

    override fun startUp() {
        if (!complete) {
            pluginManager.hidePlugin(rlAgility)
            pluginManager.hidePlugin(rlTooltips)
            pluginManager.forceStart(ethanApiPlugin)
            pluginManager.forceStart(packetUtilsPlugin)
            removeAll(rlAgility, rlTooltips, ethanApiPlugin, packetUtilsPlugin)

            //Reload plugin list
            eventBus.post(ExternalPluginsChanged(null))

            complete = true
        }
    }

    private fun PluginManager.hidePlugin(orig: Plugin) {
        if (isPluginEnabled(orig))
            stopPlugin(orig)
        remove(orig)
    }

    private fun PluginManager.forceStart(plugin: Plugin) {
        setPluginEnabled(plugin, true)
        startPlugin(plugin)
    }

    private fun removeAll(vararg plugins: Plugin) {
        plugins.forEach { pluginManager.remove(it) }
    }
}