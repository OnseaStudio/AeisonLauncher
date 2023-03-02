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
package fr.onsea.launcher.settings.arguments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.onsea.launcher.utils.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Seynax
 *
 */
public class ArgumentsLoader
{
	private @Getter ArgumentKeyValues					lastValues;
	private final Map<String, ArgumentKeyValues>		existingKeysWithValues;
	private final Map<String, ArgumentKey>				existingKeys;
	private final Map<String, ArgumentKeyValue>			existingKeysWithValue;

	private @Getter final Map<String, ArgumentKey>		enableds;

	private @Getter final Map<String, ArgumentKeyValue>	values;

	private @Getter final List<String>					remainingArguments;

	public ArgumentsLoader()
	{
		this.existingKeysWithValues	= new LinkedHashMap<>();
		this.existingKeys			= new LinkedHashMap<>();
		this.existingKeysWithValue	= new LinkedHashMap<>();

		this.enableds				= new LinkedHashMap<>();
		this.values					= new LinkedHashMap<>();
		this.remainingArguments		= new LinkedList<>();
	}

	public List<String> all()
	{
		final var arguments = new ArrayList<String>();

		for (final var enabled : this.enableds.values())
		{
			arguments.add(enabled.name);
		}

		for (final var value : this.values.values())
		{
			arguments.add(value.name());
			arguments.add(value.value());
		}

		arguments.addAll(this.remainingArguments);

		for (final var values : this.existingKeysWithValues.values())
		{
			arguments.addAll(values.values);
		}

		return arguments;
	}

	public final ArgumentsLoader compile(final String[] argsIn)
	{
		final var translateds = new ArrayList<String>();
		for (final String arg : argsIn)
		{
			final var splitteds = StringUtils.splits(arg, " ", ";");

			translateds.addAll(splitteds);
		}

		var i = 0;
		while (i < translateds.size())
		{
			final var			arg		= translateds.get(i);

			final List<String>	values	= new ArrayList<>();
			if (StringUtils.split(arg, "=", values) && values.size() > 1)
			{
				for (var i0 = 0; i0 < values.size(); i0 += 2)
				{
					final var	key		= values.get(i0);
					final var	value	= values.get(i0 + 1);

					if (this.valueRuntimeWithLog(key, value) <= -1)
					{
						System.out.println("Receive non existing value : " + key + " -> " + value);

						this.values.put(key, new ArgumentKeyValue(key, value));
					}

				}

				continue;
			}
			i = this.argumentRuntime(arg, translateds, i);

			i++;
		}

		return this;
	}

	private final int valueRuntime(final String nameIn, final String valueIn)
	{
		final var key = this.existingKeys.get(nameIn);

		if (key != null)
		{
			if (valueIn.contentEquals("true") || valueIn.contains("enable"))
			{
				this.existingKeys.remove(key.name());
				this.enableds.put(key.name(), key);
			}

			return 0;
		}
		if (valueIn.contentEquals("true") || valueIn.contains("enable"))
		{
			this.enableds.put(nameIn, key);

			return 1;
		}

		final var keyValue = this.existingKeysWithValue.get(nameIn);

		if (keyValue != null)
		{
			this.existingKeysWithValue.remove(keyValue.name());
			keyValue.value = valueIn;
			this.values.put(keyValue.name(), keyValue);

			return 2;
		}

		return -1;
	}

	private final int valueRuntimeWithLog(final String nameIn, final String valueIn)
	{
		final var exitCode = this.valueRuntime(nameIn, valueIn);

		switch (exitCode)
		{
			case 0:
				System.out.println("Receive supposed existing key : " + nameIn + " -> " + valueIn);
				break;
			case 1:
				System.out.println("Receive supposed key : " + nameIn + " -> " + valueIn);
				break;
			case 2:
				System.out.println("Receive supposed value : " + nameIn + " -> " + valueIn);
				break;
			default:
				break;
		}

		return exitCode;
	}

	private final int argumentRuntime(final String argumentIn, final ArrayList<String> translatedsIn, final int indexIn)
	{
		if (this.lastValues != null)
		{
			this.lastValues.values().add(argumentIn);

			return indexIn;
		}

		this.lastValues = this.existingKeysWithValues.get(argumentIn);
		if (this.lastValues != null)
		{
			System.out.println(argumentIn + ":  STOP PARSING");

			return indexIn;
		}

		String		next		= null;
		final var	nextIndex	= indexIn + 1;
		if (nextIndex < translatedsIn.size())
		{
			next = translatedsIn.get(nextIndex);

			if (!next.startsWith("--") && !next.startsWith("-"))
			{
				if (this.valueRuntimeWithLog(argumentIn, next) >= 0)
				{
					return nextIndex;
				}
				System.out.println("Receive supposed non existing value : " + argumentIn + " -> " + next);
				this.values.put(argumentIn, new ArgumentKeyValue(argumentIn, next));
				return nextIndex;
			}
		}

		// Key
		{
			final var key = this.existingKeys.get(argumentIn);

			if (key != null)
			{
				System.out.println("Received existing key : " + key.name());
				this.enableds.remove(key.name);
				this.enableds.put(key.name, key);

				return indexIn;
			}
		}

		// Key with value
		{
			final var key = this.existingKeysWithValue.get(argumentIn);

			if (key != null)
			{
				if (nextIndex < translatedsIn.size())
				{
					this.valueRuntimeWithLog(argumentIn, translatedsIn.get(nextIndex));

					return nextIndex;
				}
			}
			else if (argumentIn.startsWith("--") || argumentIn.startsWith("-"))
			{
				System.out.println("Receive non existing key : " + argumentIn);
				this.enableds.put(argumentIn, new ArgumentKey(argumentIn));
			}
			else
			{
				System.out.println("Receive argument : " + argumentIn);
				this.remainingArguments.add(argumentIn);
			}
		}

		return indexIn;
	}

	public final ArgumentsLoader addKeysWithAliases(final String... elementsIn)
	{
		for (var i = 0; i < elementsIn.length; i += 2)
		{
			final var	name	= elementsIn[i];
			final var	alias	= elementsIn[i + 1];

			this.addKey(name, alias);
		}

		return this;
	}

	public final ArgumentsLoader addKeysValuesWithAliases(final String... elementsIn)
	{
		for (var i = 0; i < elementsIn.length; i += 2)
		{
			final var	name	= elementsIn[i];
			final var	alias	= elementsIn[i + 1];

			System.out.println("Adding of " + name);
			this.addKeyValue(name, alias);
		}

		return this;
	}

	public final ArgumentsLoader addKeyValues(final String nameIn, final String aliasIn)
	{
		this.existingKeysWithValues.put(nameIn, new ArgumentKeyValues(nameIn, aliasIn));

		return this;
	}

	public final ArgumentsLoader addKey(final String nameIn, final String aliasIn)
	{
		this.existingKeys.put(nameIn, new ArgumentKey(nameIn));

		return this;
	}

	public final ArgumentsLoader addKeyValue(final String nameIn, final String aliasIn)
	{
		this.existingKeysWithValue.put(nameIn, new ArgumentKeyValue(nameIn));

		return this;
	}

	@Getter
	@EqualsAndHashCode
	public static class ArgumentKey
	{
		private final String	name;
		private final String	alias;

		public ArgumentKey(final String keyIn)
		{
			this.name	= keyIn;
			this.alias	= this.name;
		}

		public ArgumentKey(final String keyIn, final String aliasIn)
		{
			this.name	= keyIn;
			this.alias	= aliasIn;
		}

		public boolean is(final String nameIn)
		{
			if (nameIn.startsWith("--"))
			{
				return nameIn.contentEquals("--" + this.name);
			}
			if (nameIn.startsWith("-"))
			{
				return nameIn.contentEquals("-" + this.alias);
			}

			return false;
		}
	}

	@Getter
	public final static class ArgumentKeyValue extends ArgumentKey
	{
		private String value;

		public ArgumentKeyValue(final String nameIn)
		{
			super(nameIn);
		}

		public ArgumentKeyValue(final String nameIn, final String valueIn)
		{
			super(nameIn);

			this.value = valueIn;
		}
	}

	@Getter
	public final static class ArgumentKeyValues extends ArgumentKey
	{
		private List<String> values;

		public ArgumentKeyValues(final String nameIn)
		{
			super(nameIn);
		}

		public ArgumentKeyValues(final String nameIn, final String aliasIn, final String... valuesIn)
		{
			super(nameIn, aliasIn);

			this.values = new ArrayList<>();
			Collections.addAll(this.values, valuesIn);
		}
	}
}