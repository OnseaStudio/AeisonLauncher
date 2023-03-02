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
package fr.onsea.launcher.utils.file;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Seynax
 *
 */
public class FilesManager
{
	private final Map<String, File>	FILES_DELETION_FORBIDDEN;

	private final Map<String, File>	FILES_DELETION_ALLOWED_WITH_CONFIRMATION;

	private final Map<String, File>	FILES_INSTANCES;

	public FilesManager()
	{
		this.FILES_DELETION_FORBIDDEN					= new HashMap<>();
		this.FILES_DELETION_ALLOWED_WITH_CONFIRMATION	= new HashMap<>();
		this.FILES_INSTANCES							= new HashMap<>();
	}

	public final FilesManager addFileDeletionForbidden(final String filePathIn)
	{
		return this.addFileDeletionForbidden(new File(filePathIn));
	}

	public final FilesManager addFileDeletionForbidden(final File fileIn)
	{
		this.FILES_DELETION_FORBIDDEN.put(fileIn.getAbsolutePath(), fileIn);

		return this;
	}

	public final FilesManager addFileDeletionAllowedWithConfirmation(final String filePathIn)
	{
		return this.addFileDeletionAllowedWithConfirmation(new File(filePathIn));
	}

	public final FilesManager addFileDeletionAllowedWithConfirmation(final File fileIn)
	{
		this.FILES_DELETION_ALLOWED_WITH_CONFIRMATION.put(fileIn.getAbsolutePath(), fileIn);

		return this;
	}

	/**
	 * @param filePathIn
	 * @return
	 * @throws IOException
	 */
	public FilesManager newFolders(final String... filesPathIn) throws IOException
	{
		FileUtils.newFolders(filesPathIn);

		return this;
	}

	/**
	 * @param fromFilePathIn
	 * @param toFilePathIn
	 * @return
	 * @throws Exception
	 */
	public FilesManager copy(final String fromFilePathIn, final String toFilePathIn) throws Exception
	{
		FileUtils.copy(fromFilePathIn, toFilePathIn);

		return this;
	}

	/**
	 * @param fromIn
	 * @param toIn
	 * @return
	 * @throws Exception
	 */
	public FilesManager copy(final File fromIn, final File toIn) throws Exception
	{
		FileUtils.copy(fromIn, toIn);

		return this;
	}

	/**
	 * @param rootFilePathIn
	 * @param destinationFilePathIn
	 * @return
	 * @throws Exception
	 */
	public FilesManager writeFilesList(final String rootFilePathIn, final String destinationFilePathIn) throws Exception
	{
		FileUtils.writeFilesList(rootFilePathIn, destinationFilePathIn);

		return this;
	}

	public FilesManager extractJarExcluded(final File fileIn, final String destinationFilePathIn,
			final String excludedFilesPathIn)
	{
		FileUtils.extractJarExcluded(fileIn, destinationFilePathIn, excludedFilesPathIn);

		return this;
	}

	/**
	 * @param stringIn
	 * @return
	 * @throws Exception
	 */
	public File createNewFile(final String filePathIn) throws Exception
	{
		return FileUtils.createNewFile(filePathIn);
	}

	/**
	 * @param fileIn
	 * @return
	 * @throws Exception
	 */
	public File createNewFile(final File fileIn) throws Exception
	{
		return FileUtils.createNewFile(fileIn);
	}

	/**
	 * @param destinationFileIn
	 * @param contentIn
	 * @return
	 */
	public FilesManager write(final File destinationFileIn, final String contentIn)
	{
		FileUtils.write(destinationFileIn, contentIn);

		return this;
	}

	/**
	 * @param fileIn
	 * @param valueIn
	 * @return
	 */
	public FilesManager makeJar(final File fileIn, final String destinationFilePathIn)
	{
		FileUtils.makeJar(fileIn, destinationFilePathIn);

		return this;
	}

	/**
	 * @param fromFilePathIn
	 * @param toFilePathIn
	 * @return
	 * @throws Exception
	 */
	public FilesManager createSymbolicLink(final String fromFilePathIn, final String toFilePathIn) throws Exception
	{
		FileUtils.createSymbolicLink(fromFilePathIn, toFilePathIn);

		return this;
	}

	public final FilesManager delete(final String filePathIn) throws Exception
	{
		return this.delete(new File(filePathIn), false);
	}

	public final FilesManager delete(final String filePathIn, final boolean forceDeletionIn) throws Exception
	{
		return this.delete(new File(filePathIn), forceDeletionIn);
	}

	public final FilesManager delete(final File fileIn) throws Exception
	{
		return this.delete(fileIn, false);
	}

	public final FilesManager delete(final File fileIn, final boolean forceDeletionIn) throws Exception
	{
		if (this.FILES_DELETION_FORBIDDEN.containsKey(fileIn.getAbsolutePath()))
		{
			throw new Exception(
					"[ERROR] Deletion of \"" + fileIn.getAbsolutePath() + "\" file or directory is forbidden !");
		}

		if (this.FILES_DELETION_ALLOWED_WITH_CONFIRMATION.containsKey(fileIn.getAbsolutePath()))
		{
			throw new Exception("[ERROR] Deletion of \"" + fileIn.getAbsolutePath()
					+ "\" file or directory is forbidden without confirmation (force deletion parameter) !");
		}

		FileUtils.deleteDirectory(fileIn.getAbsolutePath());

		return this;
	}

	public final FilesManager deleteWithFileInstance(final String keyIn) throws Exception
	{
		final var file = this.get(keyIn);

		if (file != null)
		{
			this.delete(file);
		}

		return this;
	}

	public final FilesManager addFileInstance(final String keyIn, final File fileIn)
	{
		this.FILES_INSTANCES.put(keyIn, fileIn);

		return this;
	}

	public final FilesManager addFileInstance(final File fileIn)
	{
		this.FILES_INSTANCES.put(fileIn.getAbsolutePath(), fileIn);

		return this;
	}

	public final File get(final String keyIn)
	{
		return this.FILES_INSTANCES.get(keyIn);
	}

	public final FilesManager removeFileInstance(final String keyIn)
	{
		this.FILES_INSTANCES.remove(keyIn);

		return this;
	}

	public final File popFileInstance(final String keyIn)
	{
		return this.FILES_INSTANCES.remove(keyIn);
	}
}