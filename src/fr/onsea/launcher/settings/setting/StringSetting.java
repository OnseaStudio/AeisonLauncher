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

import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author Seynax
 *
 */

@Getter(AccessLevel.PUBLIC)
public class StringSetting extends Setting<String> implements ISetting
{
	protected String value;

	/**
	 * @param keyIn
	 * @param valueIn
	 */
	public StringSetting(final String keyIn, final String valueIn)
	{
		super(keyIn);
		this.value(valueIn);
	}

	public String quotedValue()
	{
		return "\"" + this.value + "\"";
	}

	@Override
	public StringSetting value(final String valueIn)
	{
		if (valueIn != null)
		{
			var finalValue = valueIn;
			if (finalValue.startsWith("\""))
			{
				finalValue = finalValue.substring(1);
			}
			if (finalValue.endsWith("\""))
			{
				finalValue = finalValue.substring(0, finalValue.length() - 1);
			}

			this.value = finalValue;
		}

		return this;
	}

	@Override
	public String toString()
	{
		return this.value;
	}
}
