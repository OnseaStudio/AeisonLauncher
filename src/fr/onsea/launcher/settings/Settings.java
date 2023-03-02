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

import fr.onsea.launcher.settings.arguments.ArgumentsLoader;
import fr.onsea.launcher.settings.setting.EnabledsSettingChannel;
import fr.onsea.launcher.settings.setting.KeySetting;
import fr.onsea.launcher.settings.setting.SettingChannel;
import fr.onsea.launcher.settings.setting.StringSettingChannel;
import lombok.Getter;

/**
 * @author Seynax
 *
 */
@Getter
public abstract class Settings
{
	private final String					filePath;
	private final SettingChannel<?>			others;
	private final StringSettingChannel		strings;
	private final EnabledsSettingChannel	enableds;

	public Settings(final String filePathIn, final ArgumentsLoader argumentsLoaderIn) throws Exception
	{
		this.filePath	= filePathIn;
		this.others		= new SettingChannel<>();
		this.strings	= new StringSettingChannel();
		this.enableds	= new EnabledsSettingChannel();
		SettingsUtils.load(filePathIn, this);
		this.compileArguments(argumentsLoaderIn);
		this.reload();
	}

	/**
	 * @param filePathIn
	 * @throws Exception
	 */
	public Settings(final String filePathIn) throws Exception
	{
		this.filePath	= filePathIn;
		this.others		= new SettingChannel<>();
		this.strings	= new StringSettingChannel();
		this.enableds	= new EnabledsSettingChannel();
		SettingsUtils.load(filePathIn, this);
		this.reload();
	}

	public abstract Settings reload() throws Exception;

	public Settings compileArguments(final ArgumentsLoader argumentsLoaderIn) throws Exception
	{
		for (final var key : argumentsLoaderIn.enableds().values())
		{
			final var convertedKey = SettingsUtils.convertIntoSettingKey(key.name());
			System.out.println("Converted key : " + convertedKey);
			this.enable(convertedKey);
		}

		for (final var entry : argumentsLoaderIn.values().entrySet())
		{
			final var convertedKey = SettingsUtils.convertIntoSettingKey(entry.getKey());
			System.out.println("Converted key value : " + convertedKey);
			this.strings().add(convertedKey, entry.getValue().value());
		}

		if (argumentsLoaderIn == null || argumentsLoaderIn.remainingArguments().size() <= 0)
		{
			return this;
		}

		var i = 0;
		while (i < argumentsLoaderIn.remainingArguments().size())
		{
			final var arg = argumentsLoaderIn.remainingArguments().get(i);
			i++;

			final var pair = SettingsUtils.compile(arg, this);

			if (pair.s2() == null && i < argumentsLoaderIn.remainingArguments().size())
			{
				this.strings().add(pair.s1(),
						SettingsUtils.valueCorrections(argumentsLoaderIn.remainingArguments().get(i), this));
				i++;
			}
			else
			{
				this.strings().add(pair.s1(), pair.s2());
			}
		}

		return this;
	}

	public Settings compileArguments(final String... argumentsIn) throws Exception
	{
		if (argumentsIn == null || argumentsIn.length <= 0)
		{
			return this;
		}

		var i = 0;
		while (i < argumentsIn.length)
		{
			final var arg = argumentsIn[i];
			i++;

			final var pair = SettingsUtils.compile(arg, this);

			if (pair.s2() == null && i < argumentsIn.length)
			{
				this.strings().add(pair.s1(), SettingsUtils.valueCorrections(argumentsIn[i], this));
				i++;
			}
			else
			{
				this.strings().add(pair.s1(), pair.s2());
			}
		}

		return this;
	}

	public final void updateTags()
	{
		for (final var entry : this.strings().entrySet())
		{
			final var setting = entry.getValue();

			if (setting != null)
			{
				setting.value(SettingsUtils.replaceKeys(setting.toString(), this));
			}
		}
	}

	/**
	 * @param keySettingIn
	 * @return
	 * @see fr.onsea.launcher.settings.setting.EnabledsSettingChannel#enable(fr.onsea.launcher.settings.setting.KeySetting)
	 */
	public EnabledsSettingChannel enable(final KeySetting keySettingIn)
	{
		return this.enableds.enable(keySettingIn);
	}

	/**
	 * @param keyIn
	 * @return
	 * @see fr.onsea.launcher.settings.setting.EnabledsSettingChannel#enable(java.lang.String)
	 */
	public EnabledsSettingChannel enable(final String keyIn)
	{
		return this.enableds.enable(keyIn);
	}

	/**
	 * @param keyIn
	 * @param valueIn
	 * @return
	 * @see fr.onsea.launcher.settings.setting.EnabledsSettingChannel#set(java.lang.String, boolean)
	 */
	public EnabledsSettingChannel set(final String keyIn, final boolean valueIn)
	{
		return this.enableds.set(keyIn, valueIn);
	}

	/**
	 * @param keyIn
	 * @param defaultValueIn
	 * @return
	 * @see fr.onsea.launcher.settings.setting.EnabledsSettingChannel#load(java.lang.String, boolean)
	 */
	public boolean load(final String keyIn, final boolean defaultValueIn)
	{
		return this.enableds.load(keyIn, defaultValueIn);
	}

	/**
	 * @param keyIn
	 * @return
	 * @see fr.onsea.launcher.settings.setting.EnabledsSettingChannel#isEnabled(java.lang.String)
	 */
	public boolean isEnabled(final String keyIn)
	{
		return this.enableds.isEnabled(keyIn);
	}

	/**
	 * @param keySettingIn
	 * @return
	 * @see fr.onsea.launcher.settings.setting.EnabledsSettingChannel#disable(fr.onsea.launcher.settings.setting.KeySetting)
	 */
	public EnabledsSettingChannel disable(final KeySetting keySettingIn)
	{
		return this.enableds.disable(keySettingIn);
	}

	/**
	 * @param keyIn
	 * @return
	 * @see fr.onsea.launcher.settings.setting.EnabledsSettingChannel#disable(java.lang.String)
	 */
	public EnabledsSettingChannel disable(final String keyIn)
	{
		return this.enableds.disable(keyIn);
	}

}