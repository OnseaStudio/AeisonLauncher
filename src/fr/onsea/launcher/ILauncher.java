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
package fr.onsea.launcher;

import java.io.IOException;
import java.net.SocketException;

/**
 * @author Seynax
 *
 */
public interface ILauncher
{
	default ILauncher initialization() throws Exception
	{
		return this;
	}

	// Updating part

	/*
	 *  If return false, updateChecking isn't executed
	 */
	default boolean canCheckUpdate() throws SocketException
	{
		return true;
	}

	boolean updateAvailable() throws Exception; // If return false preUpdating, updating and postUpdating isn't executing

	/**
	 * If return false, updating process isn't executed even if update is available
	 */
	default boolean canUpdate()
	{
		return true;
	}

	default ILauncher preUpdading()
	{
		return this;
	}

	ILauncher updating();

	default ILauncher postUpdading()
	{
		return this;
	}

	// Make final jar and resources operations part

	default boolean canAssemble(final boolean updateAvailableIn, final boolean updateDownloadedIn)
	{
		return updateAvailableIn && updateDownloadedIn;
	} // If return false, assembly isn't executed even if update is available

	default ILauncher preAssemble()
	{
		return this;
	}

	ILauncher assemble() throws IOException, Exception;

	default ILauncher postAssemble()
	{
		return this;
	}

	// Launching

	default boolean canLaunch()
	{
		return true;
	} // If return false, assembly isn't executed even if update is available

	default ILauncher preLaunch()
	{
		return this;
	}

	ILauncher launch() throws IOException, Exception;

	default ILauncher postLaunch()
	{
		return this;
	}

	default ILauncher cleanup()
	{
		return this;
	}
}