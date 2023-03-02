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
public class SettingChannel<T>
{
	private final Map<String, Setting<T>> settings;

	public SettingChannel()
	{
		this.settings = new LinkedHashMap<>();
	}

	public SettingChannel<T> replace(final String keyIn, final T valueIn)
	{
		this.settings.put(keyIn, new Setting<>(keyIn, valueIn));

		return this;
	}

	public SettingChannel<T> add(final String keyIn, final T valueIn)
	{
		final var setting = this.of(keyIn);

		if (setting != null)
		{
			setting.value(valueIn);
		}
		else
		{
			this.settings.put(keyIn, new Setting<>(keyIn, valueIn));
		}

		return this;
	}

	public Setting<T> load(final String keyIn) throws Exception
	{
		final var setting = this.of(keyIn);

		if (setting == null)
		{
			throw new Exception("\"" + keyIn + "\" setting isn't defined and cannot be loaded !");
		}

		return setting;
	}

	public Setting<T> load(final String keyIn, final T defaultValueIn)
	{
		var setting = this.of(keyIn);

		if (setting == null)
		{
			System.out.println("\"" + keyIn + "\" setting isn't defined, using default value : \""
					+ defaultValueIn.toString() + "\"");
			setting = new Setting<>(keyIn, defaultValueIn);

			this.settings.put(keyIn, setting);
		}

		return setting;
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

	public Setting<T> of(final String keyIn)
	{
		return this.settings.get(keyIn);
	}

	public T valueOf(final String keyIn)
	{
		final var setting = this.of(keyIn);

		if (setting != null)
		{
			return setting.value;
		}

		return null;
	}

	public boolean contains(final String keyIn)
	{
		return this.settings.containsKey(keyIn);
	}

	public boolean containsValue(final T valueIn)
	{
		return this.settings.containsValue(valueIn);
	}

	public SettingChannel<T> remove(final String keyIn)
	{
		this.settings.remove(keyIn);

		return this;
	}

	public Set<Entry<String, Setting<T>>> entrySet()
	{
		return this.settings.entrySet();
	}

	public Collection<Setting<T>> settings()
	{
		return this.settings.values();
	}

	public Collection<String> keys()
	{
		return this.settings.keySet();
	}

	public SettingChannel<T> clear()
	{
		this.settings.clear();

		return this;
	}
}