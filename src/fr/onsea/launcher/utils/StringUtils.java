/**
*	Copyright 2021-2023 Onsiea All rights reserved.
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
package fr.onsea.launcher.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Seynax
 *
 */
public class StringUtils
{
	/**
	 * Split contentIn with separateCharactersIn, if not null and length > 0 add all splitted into splittedsIn and return true, else return false
	 * @param contentIn
	 * @param separateCharactersIn
	 * @param splittedsIn
	 * @return
	 */
	public final static boolean split(final String contentIn, final String separateCharactersIn,
			final List<String> splittedsIn)
	{
		final var splitted = contentIn.split(separateCharactersIn);

		if (splitted != null && splitted.length > 0)
		{
			Collections.addAll(splittedsIn, splitted);

			return true;
		}

		return false;
	}

	/**
	 * Split previous splitteds elements with the next separate charactersIn
	 * @param contentIn
	 * @param separateCharactersIn
	 * @return
	 */
	public final static List<String> splits(final String contentIn, final String... separateCharactersIn)
	{
		final List<String> splitteds0 = new ArrayList<>();
		splitteds0.add(contentIn);
		final List<String> splitteds1 = new ArrayList<>();

		for (final var separateCharacter : separateCharactersIn)
		{
			for (final var lastSplit : splitteds0)
			{
				if (!StringUtils.split(lastSplit, separateCharacter, splitteds1))
				{
					splitteds1.add(lastSplit);
				}
			}

			splitteds0.clear();
			splitteds0.addAll(splitteds1);
			splitteds1.clear();
		}

		return splitteds0;
	}

	/**
	 * Split contentIn String with first index of separateCharacterIn
	 * @param contentIn
	 * @param separateCharacterIn
	 * @return
	 */
	public final static Pair<String, String> splitWithFirst(final String contentIn, final String separateCharacterIn)
	{
		final var separation = contentIn.indexOf(separateCharacterIn);

		if (separation <= 0)
		{
			return null;
		}

		final var	key		= contentIn.substring(0, separation);

		final var	value	= contentIn.substring(separation + 1);

		return new Pair<>(key, value);
	}

	/**
	 * Remove unused start and end blank characters
	 * @param contentIn
	 * @return
	 */
	public final static String removeUnusedBlank(final String contentIn)
	{
		return contentIn.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
	}
}