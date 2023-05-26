package com.example;

import com.example.AutoTele.AutoTele;
import com.example.CalvarionHelper.CalvarionHelper;
import com.example.E3t4g.e3t4g;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.LavaRunecrafter.LavaRunecrafterPlugin;
import com.example.NightmareHelper.NightmareHelperPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.PrayerFlicker.EthanPrayerFlickerPlugin;
import com.example.UpkeepPlugin.UpkeepPlugin;
import com.example.gauntletFlicker.gauntletFlicker;
import com.example.harpoon2ticker.SwordFish2Tick;
import com.example.superglass.SuperGlassMakerPlugin;
import nulled.core.ExtPlugin;
import nulled.ezclick.EZClickPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import nulled.tooltips.TooltipsPlugin;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(
				//Ethan
				EthanApiPlugin.class, PacketUtilsPlugin.class,

				AutoTele.class, CalvarionHelper.class, e3t4g.class,
				gauntletFlicker.class, SwordFish2Tick.class, LavaRunecrafterPlugin.class,
				NightmareHelperPlugin.class, EthanPrayerFlickerPlugin.class,
				SuperGlassMakerPlugin.class, UpkeepPlugin.class,

				//Null
				ExtPlugin.class,

				TooltipsPlugin.class, EZClickPlugin.class
				);
		RuneLite.main(args);
	}
}