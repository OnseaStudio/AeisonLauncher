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
package fr.onsea.aeisonlauncher.settings;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import fr.onsea.aeisonlauncher.utils.FileUtils;
import fr.onsea.aeisonlauncher.utils.Pair;
import fr.onsea.aeisonlauncher.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Seynax
 *
 */
@Getter
public abstract class SettingsLoader
{
	@Getter
	@AllArgsConstructor
	public final static class Setting
	{
		private final String	key;
		private @Setter String	value;

		@Override
		public String toString()
		{
			return this.value;
		}
	}

	private final static Scanner	SCANNER		= new Scanner(System.in);
	public final static Pattern		KEY_PATTERN	= Pattern.compile("<[^=^<^>]*>");

	public final static Map<String, Setting> load(final String filePathIn) throws Exception
	{
		final var settings = new LinkedHashMap<String, Setting>();

		FileUtils.linesFunction(filePathIn, line -> {
			final var pair = SettingsLoader.separateAndCompile(line, settings);

			if (pair != null)
			{
				final var key = StringUtils.removeUnusedBlank(pair.s1());
				settings.put(key, new Setting(key, pair.s2()));
			}
		});

		return settings;
	}

	public final static Setting replaceKeys(final Setting settingIn, final Map<String, Setting> settingsIn)
	{
		settingIn.value(SettingsLoader.replaceKeys(settingIn.value, settingsIn));

		return settingIn;
	}

	public final static String replaceKeys(final String contentIn, final Map<String, Setting> settingsIn)
	{
		var			finalValue	= contentIn;

		final var	matcher		= SettingsLoader.KEY_PATTERN.matcher(contentIn);

		while (matcher.find())
		{
			final var group = matcher.group();
			if (group != null)
			{
				final var setting = settingsIn.get(group.subSequence(1, group.length() - 1));
				if (setting != null)
				{
					var replacement = setting.value();

					if (replacement != null)
					{
						if (replacement.startsWith("\""))
						{
							replacement = replacement.substring(1);
						}
						if (replacement.endsWith("\""))
						{
							replacement = replacement.substring(0, replacement.length() - 1);
						}
						finalValue = finalValue.replace(group, replacement);
					}
				}
			}
		}

		return finalValue;
	}

	public final static String valueCorrections(final String fromValueIn, final Map<String, Setting> settingsIn)
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

		finalValue = SettingsLoader.replaceKeys(finalValue, settingsIn);

		return StringUtils.removeUnusedBlank(finalValue);
	}

	public final static Pair<String, String> separateAndCompile(final String contentIn,
			final Map<String, Setting> settingsIn)
	{
		final var pair = StringUtils.splitWithFirst(contentIn, "=");

		if (pair != null)
		{
			return pair.s1(StringUtils.removeUnusedBlank(pair.s1()))
					.s2(SettingsLoader.valueCorrections(pair.s2(), settingsIn));
		}

		return null;
	}

	public final static Pair<String, String> compile(final String contentIn, final Map<String, Setting> settingsIn)
	{
		if (contentIn.contains("="))
		{
			final var pair = SettingsLoader.separateAndCompile(contentIn, settingsIn);

			if (pair != null)
			{
				return pair;
			}
		}

		return new Pair<>(contentIn.replace("--", ""));
	}

	protected final Map<String, Setting> settings;

	public SettingsLoader(final String filePathIn) throws Exception
	{
		this.settings = SettingsLoader.load(filePathIn);
		this.reload();
	}

	public abstract SettingsLoader reload() throws Exception;

	public SettingsLoader addWithoutReload(final Setting settingIn) throws Exception
	{
		this.settings.put(settingIn.key(), settingIn);

		return this;
	}

	public SettingsLoader add(final Setting settingIn) throws Exception
	{
		this.addWithoutReload(settingIn);
		this.reload();

		return this;
	}

	public SettingsLoader addWithoutReload(final String keyIn, final String valueIn) throws Exception
	{
		final var setting = this.settings.get(keyIn);
		if (setting != null)
		{
			setting.value(valueIn);
		}
		else
		{
			this.settings.put(keyIn, new Setting(keyIn, valueIn));
		}
		return this;
	}

	public SettingsLoader add(final String keyIn, final String valueIn) throws Exception
	{
		this.addWithoutReload(keyIn, valueIn);
		this.reload();

		return this;
	}

	public SettingsLoader addArguments(final List<String> argumentsIn) throws Exception
	{
		if (argumentsIn == null || argumentsIn.size() <= 0)
		{
			return this;
		}

		var i = 0;
		while (i < argumentsIn.size())
		{
			final var arg = argumentsIn.get(i);
			i++;

			final var pair = SettingsLoader.compile(arg, this.settings);

			if (pair.s2() == null && i < argumentsIn.size())
			{
				this.addWithoutReload(pair.s1(), SettingsLoader.valueCorrections(argumentsIn.get(i), this.settings));
				i++;
			}
			else
			{
				this.addWithoutReload(pair.s1(), pair.s2());
			}
		}
		this.reload();

		return this;
	}

	public Setting load(final String keyIn, final String defaultValueIn) throws Exception
	{
		final var value = this.settings.get(keyIn);

		if (value == null)
		{
			final var setting = new Setting(keyIn, defaultValueIn);

			this.addWithoutReload(setting);

			return setting;
		}

		return value;
	}

	public Setting load(final String keyIn, final boolean canAskUserIn) throws Exception
	{
		final var value = this.settings.get(keyIn);

		if (value == null)
		{
			if (!canAskUserIn)
			{
				throw new Exception("[ERROR] \"" + keyIn
						+ "\" paramter isn't defined, doesn't had default value and ask user is disallowed.");
			}

			System.out.println("\"" + keyIn
					+ "\" parameter isn't defined and doesn't had default value. Can you define the value ? (exit to stop the program, skip to skip this demand)");

			final var anwser = SettingsLoader.SCANNER.next();

			if (anwser.contentEquals("exit"))
			{
				System.exit(0);
			}
			else if (anwser.contentEquals("skip"))
			{
				return null;
			}
			else
			{
				final var setting = new Setting(keyIn, anwser);

				this.addWithoutReload(setting);

				return setting;
			}
		}

		return value;
	}
}