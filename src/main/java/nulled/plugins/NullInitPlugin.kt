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
        name = "Null Init",
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
 * We also hide core apis here
 */
class NullInitPlugin : Plugin() {

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

    override fun startUp() {
        //Make sure core apis are running
        pluginManager.forceStart(ethanApiPlugin)
        pluginManager.forceStart(packetUtilsPlugin)

        //Hide RuneLite plugins we replace
        pluginManager.stopAndRemove(rlAgility)
        pluginManager.stopAndRemove(rlTooltips)

        //Remove core apis to prevent disabling
        pluginManager.remove(ethanApiPlugin)
        pluginManager.remove(packetUtilsPlugin)
        pluginManager.remove(this)

        //Reload plugin list
        eventBus.post(ExternalPluginsChanged(null))
    }

    private fun PluginManager.stopAndRemove(orig: Plugin) {
        if (isPluginEnabled(orig))
            stopPlugin(orig)
        remove(orig)
    }

    private fun PluginManager.forceStart(plugin: Plugin) {
        setPluginEnabled(plugin, true)
        startPlugin(plugin)
    }
}