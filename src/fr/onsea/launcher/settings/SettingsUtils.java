/**
*	Copyright 2021 Onsiea All rights reserved.
*
*	This file is part of Onsiea Engine. (https://github.com/Onsiea/OnsieaEngine)
*
*	Unless noted in license (https://github.com/Onsiea/OnsieaEngine/blob/main/LICENSE.md) notice file (https://github.com/Onsiea/OnsieaEngine/blob/main/LICENSE_NOTICE.md), Onsiea engine and all parts herein is licensed under the terms of the LGPL-3.0 (https://www.gnu.org/licenses/lgpl-3.0.html)  found here https://www.gnu.org/licenses/lgpl-3.0.html and copied below the license file.
*
*	Onsiea Engine is libre software: you can redistribute it and/or modify
*	it under the terms of the GNU Lesser General Public License as published by
*	the Free Software Foundation, either version 3.0 of the License, or
*	(at your option) any later version.
*
*	Onsiea Engine is distributed in the hope that it will be useful,
*	but WITHOUT ANY WARRANTY; without even the implied warranty of
*	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*	GNU Lesser General Public License for more details.
*
*	You should have received a copy of the GNU Lesser General Public License
*	along with Onsiea Engine.  If not, see <https://www.gnu.org/licenses/> <https://www.gnu.org/licenses/lgpl-3.0.html>.
*
*	Neither the name "Onsiea", "Onsiea Engine", or any derivative name or the names of its authors / contributors may be used to endorse or promote products derived from this software and even less to name another project or other work without clear and precise permissions written in advance.
*
*	(more details on https://github.com/Onsiea/OnsieaEngine/wiki/License)
*
*	@author Seynax
*/
package fr.onsea.launcher.settings;

import java.util.Scanner;
import java.util.regex.Pattern;

import fr.onsea.launcher.settings.setting.Setting;
import fr.onsea.launcher.utils.Pair;
import fr.onsea.launcher.utils.StringUtils;
import fr.onsea.launcher.utils.file.FileUtils;

/**
 * @author Seynax
 *
 */
public class SettingsUtils
{
	final static Scanner		SCANNER		= new Scanner(System.in);
	public final static Pattern	KEY_PATTERN	= Pattern.compile("<[^=^<^>]*>");

	public final static void load(final String filePathIn, final Settings settingsIn) throws Exception
	{
		FileUtils.linesFunction(filePathIn, line -> {
			final var pair = SettingsUtils.separateAndCompile(line, settingsIn);

			if (pair != null)
			{
				final var key = StringUtils.removeUnusedBlank(pair.s1());
				settingsIn.strings().add(key, pair.s2());
			}
		});
	}

	public final static Setting<String> replaceKeys(final Setting<String> settingIn, final Settings SettingsLoaderIn)
	{
		settingIn.value(SettingsUtils.replaceKeys(settingIn.value(), SettingsLoaderIn));

		return settingIn;
	}

	public final static String replaceKeys(final String contentIn, final Settings SettingsLoaderIn)
	{
		var			finalValue	= contentIn;

		final var	matcher		= SettingsUtils.KEY_PATTERN.matcher(contentIn);

		while (matcher.find())
		{
			final var group = matcher.group();
			if (group != null)
			{
				final var replacement = SettingsLoaderIn.strings().toString(group.substring(1, group.length() - 1));

				if (replacement != null)
				{
					finalValue = finalValue.replace(group, replacement);
				}
			}
		}

		return finalValue;
	}

	public final static String valueCorrections(final String fromValueIn, final Settings SettingsLoaderIn)
	{
		var			finalValue	= fromValueIn;
		final var	splitted	= finalValue.split("\\\".*\\\"");
		for (final var split : splitted)
		{
			if (split.isBlank() || split.isEmpty() || split.matches("\s+"))
			{
				continue;
			}
			finalValue = finalValue.replace(split, "");
		}

		finalValue = SettingsUtils.replaceKeys(finalValue, SettingsLoaderIn);

		return StringUtils.removeUnusedBlank(finalValue);
	}

	public final static Pair<String, String> separateAndCompile(final String contentIn, final Settings SettingsLoaderIn)
	{
		final var pair = StringUtils.splitWithFirst(contentIn, "=");

		if (pair != null)
		{
			return pair.s1(StringUtils.removeUnusedBlank(pair.s1()))
					.s2(SettingsUtils.valueCorrections(pair.s2(), SettingsLoaderIn));
		}

		return null;
	}

	public final static Pair<String, String> compile(final String contentIn, final Settings SettingsLoaderIn)
	{
		if (contentIn.contains("="))
		{
			final var pair = SettingsUtils.separateAndCompile(contentIn, SettingsLoaderIn);

			if (pair != null)
			{
				return pair;
			}
		}

		return new Pair<>(contentIn.replace("--", ""));
	}

	public final static String convertIntoSettingKey(final String argumentKeyIn)
	{
		return argumentKeyIn.replaceFirst("-", "").replaceFirst("-", "").replace("-", "_").toUpperCase();
	}
}