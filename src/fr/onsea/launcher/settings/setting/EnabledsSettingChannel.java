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
package fr.onsea.launcher.settings.setting;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Seynax
 *
 */
public class EnabledsSettingChannel
{
	private final Map<String, KeySetting> settings;

	public EnabledsSettingChannel()
	{
		this.settings = new LinkedHashMap<>();
	}

	public EnabledsSettingChannel enable(final KeySetting keySettingIn)
	{
		if (!this.settings.containsKey(keySettingIn.key()))
		{
			this.settings.put(keySettingIn.key(), keySettingIn);
		}

		return this;
	}

	public EnabledsSettingChannel enable(final String keyIn)
	{
		if (!this.settings.containsKey(keyIn))
		{
			this.settings.put(keyIn, new KeySetting(keyIn));
		}

		return this;
	}

	public EnabledsSettingChannel set(final String keyIn, final boolean valueIn)
	{
		if (valueIn)
		{
			this.isEnabled(keyIn);
		}
		else
		{
			this.disable(keyIn);
		}

		return this;
	}

	public boolean load(final String keyIn, final boolean defaultValueIn)
	{
		final var setting = this.of(keyIn);

		if (setting == null)
		{
			System.out.println(
					"\"" + keyIn + "\" setting isn't defined, using default value : \"" + defaultValueIn + "\"");

			this.set(keyIn, defaultValueIn);

			return defaultValueIn;
		}

		return true;
	}

	public String toString(final String keyIn)
	{
		final var setting = this.of(keyIn);

		if (setting != null)
		{
			return setting.toString();
		}

		return null;
	}

	public KeySetting of(final String keyIn)
	{
		return this.settings.get(keyIn);
	}

	public boolean isEnabled(final String keyIn)
	{
		return this.settings.containsKey(keyIn);
	}

	public EnabledsSettingChannel disable(final KeySetting keySettingIn)
	{
		this.settings.remove(keySettingIn.key());

		return this;
	}

	public EnabledsSettingChannel disable(final String keyIn)
	{
		this.settings.remove(keyIn);

		return this;
	}

	public Set<Entry<String, KeySetting>> entrySet()
	{
		return this.settings.entrySet();
	}

	public Collection<KeySetting> settings()
	{
		return this.settings.values();
	}

	public Collection<String> keys()
	{
		return this.settings.keySet();
	}

	public EnabledsSettingChannel clear()
	{
		this.settings.clear();

		return this;
	}
}