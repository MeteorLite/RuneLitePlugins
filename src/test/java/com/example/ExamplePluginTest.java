package com.example;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.PrayerFlicker.EthanPrayerFlickerPlugin;
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

				EthanPrayerFlickerPlugin.class,

				//Null
				TooltipsPlugin.class, EZClickPlugin.class
				);
		RuneLite.main(args);
	}
}