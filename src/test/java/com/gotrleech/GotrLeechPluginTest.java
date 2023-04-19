package com.gotrleech;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GotrLeechPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GotrLeechPlugin.class);
		RuneLite.main(args);
	}
}