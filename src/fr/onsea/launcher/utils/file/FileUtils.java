/**
*	Copyright 2021-2023 Onsea Studio All rights reserved.
*
*	This file is part of Onsiea Engine. (https://github.com/Onsea Studio/Onsea StudioEngine)
*
*	Unless noted in license (https://github.com/Onsea Studio/Onsea StudioEngine/blob/main/LICENSE.md) notice file (https://github.com/Onsea Studio/Onsea StudioEngine/blob/main/LICENSE_NOTICE.md), Onsea Studio engine and all parts herein is licensed under the terms of the LGPL-3.0 (https://www.gnu.org/licenses/lgpl-3.0.html)  found here https://www.gnu.org/licenses/lgpl-3.0.html and copied below the license file.
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
*	Neither the name "Onsea Studio", "Onsiea Engine", or any derivative name or the names of its authors / contributors may be used to endorse or promote products derived from this software and even less to name another project or other work without clear and precise permissions written in advance.
*
*	(more details on https://github.com/OnseaStudio/OnsieaEngine/wiki/License)
*
*	@author Seynax
*/
package fr.onsea.launcher.utils.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import fr.onsea.launcher.utils.IFunction;

/**
 * @Organization Onsea
 * @author Seynax
 *
 */
public class FileUtils
{
	private final static String ZIP_FILE_PATH_SEPARATOR = "/";

	final static void createSymbolicLink(final String fromIn, final String toIn) throws Exception
	{
		final var	regularFile	= Paths.get(fromIn);
		final var	link		= Paths.get(toIn);

		if (Files.exists(link))
		{
			Files.delete(link);
		}
		Files.createSymbolicLink(link, regularFile);
	}

	final static File createNewFile(final String filePathIn) throws Exception
	{
		return FileUtils.createNewFile(new File(filePathIn));
	}

	final static File createNewFile(final File fileIn) throws Exception
	{
		if (!fileIn.getParentFile().exists())
		{
			fileIn.getParentFile().mkdirs();
		}

		if (fileIn.exists() && !fileIn.delete())
		{
			throw new Exception("[ERROR] Deletion of \"" + fileIn.getAbsolutePath() + "\" from \""
					+ fileIn.getParentFile().getAbsolutePath() + "\" failed !");
		}

		if (!fileIn.createNewFile())
		{
			throw new Exception("[ERROR] Creation of \"" + fileIn.getAbsolutePath() + "\" into \""
					+ fileIn.getParentFile().getAbsolutePath() + "\"failed !");
		}

		return fileIn;
	}

	final static void writeFilesList(final String rootIn, final String destinationFileIn) throws Exception
	{
		FileUtils.writeFilesList(new File(rootIn), new File(destinationFileIn));
	}

	final static void writeFilesList(final File rootIn, final File destinationFileIn) throws Exception
	{
		final var	file	= FileUtils.createNewFile(destinationFileIn);

		final var	writer	= new BufferedWriter(new FileWriter(file));

		FileUtils.recurse(rootIn, fileIn -> {
			if (fileIn.getAbsolutePath().endsWith(".java"))
			{
				FileUtils.appendLn(writer, fileIn.getAbsolutePath());
			}
		});

		writer.close();
	}

	final static void copy(final String fromIn, final String toIn) throws Exception
	{
		FileUtils.copy(new File(fromIn), new File(toIn));
	}

	final static void copy(final File fromIn, final File toIn) throws Exception
	{
		if (!fromIn.exists())
		{
			return;
		}

		if (fromIn.isFile())
		{
			FileUtils.copyFile(fromIn, toIn);
		}
		else
		{
			final var files = fromIn.listFiles();
			if (files != null)
			{
				for (final var file : files)
				{
					FileUtils.copy(file, new File(toIn, file.getName()));
				}
			}
		}
	}

	static void copyFile(final File sourceFileIn, final File destFileIn) throws Exception
	{
		if (!destFileIn.getParentFile().exists())
		{
			destFileIn.getParentFile().mkdirs();
		}
		if (destFileIn.exists() && !destFileIn.isFile())
		{
			throw new Exception(
					"[ERROR] Destination file exists and is directory + \"" + destFileIn.getAbsolutePath() + "\"");
		}
		if (sourceFileIn.exists() && !sourceFileIn.isFile())
		{
			throw new Exception(
					"[ERROR] Source file exists and is directory + \"" + destFileIn.getAbsolutePath() + "\"");
		}

		final var	fileInputStream		= new FileInputStream(sourceFileIn);
		final var	fileOutputStream	= new FileOutputStream(destFileIn);

		int			bufferSize;
		final var	bufffer				= new byte[512];
		while ((bufferSize = fileInputStream.read(bufffer)) > 0)
		{
			fileOutputStream.write(bufffer, 0, bufferSize);
		}
		fileInputStream.close();
		fileOutputStream.close();
	}

	static void write(final File fileIn, final String contentIn)
	{
		BufferedWriter writer = null;

		try
		{
			writer = new BufferedWriter(new FileWriter(fileIn));

			writer.write(contentIn);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	static void writeAllWithNewLineSeparator(final File fileIn, final String... contentsIn)
	{
		BufferedWriter writer = null;

		try
		{
			writer = new BufferedWriter(new FileWriter(fileIn));

			for (final var content : contentsIn)
			{
				writer.write(content + "\n");
			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public final static void linesFunction(final String filePathIn, final IFunction<String> functionIn) throws Exception
	{
		FileUtils.linesFunction(new File(filePathIn), functionIn);
	}

	public final static void linesFunction(final File fileIn, final IFunction<String> functionIn) throws Exception
	{
		if (!fileIn.exists())
		{
			throw new FileNotFoundException("[ERROR] File \"" + fileIn.getAbsolutePath() + "\" not found !");
		}
		if (fileIn.isDirectory())
		{
			throw new Exception("[ERROR] File \"" + fileIn.getAbsolutePath() + "\" is directory");
		}

		final var	reader	= new BufferedReader(new FileReader(fileIn));

		String		line;
		while ((line = reader.readLine()) != null)
		{
			functionIn.execute(line);
		}

		if (reader != null)
		{
			reader.close();
		}
	}

	public final static List<String> lines(final String filePathIn) throws Exception
	{
		return FileUtils.lines(new File(filePathIn));
	}

	public final static List<String> lines(final File fileIn) throws Exception
	{
		final var lines = new ArrayList<String>();

		FileUtils.linesFunction(fileIn, line -> lines.add(line));

		return lines;
	}

	public final static void recurse(final File rootIn, final IFunction<File> functionIn)
	{
		if (rootIn == null || !rootIn.exists())
		{
			return;
		}

		if (rootIn.isFile())
		{
			functionIn.execute(rootIn);
		}
		else
		{
			final var files = rootIn.listFiles();
			if (files != null)
			{
				for (final var file : files)
				{
					FileUtils.recurse(file, functionIn);
				}
			}
		}
	}

	final static void appendLn(final BufferedWriter writerIn, final String contentIn)
	{
		try
		{
			writerIn.append(contentIn);
			writerIn.newLine();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	final static void newFolders(final String... folderPathsIn) throws IOException
	{
		for (final var folderPath : folderPathsIn)
		{
			final var folderFile = new File(folderPath);

			if (folderFile.exists())
			{
				FileUtils.deleteDirectory(folderPath);
			}

			folderFile.mkdirs();
		}
	}

	public final static void mkdirs(final String... folderPathsIn)
	{
		for (final var folderPath : folderPathsIn)
		{
			final var folderFile = new File(folderPath);
			if (!folderFile.exists())
			{
				folderFile.mkdirs();
			}
		}
	}

	final static void makeJar(final File rootFolderIn, final String destinationPathIn)
	{
		JarOutputStream jar = null;
		try
		{
			jar = new JarOutputStream(new FileOutputStream(new File(destinationPathIn)));
			FileUtils.addIntoJar(rootFolderIn, rootFolderIn, jar);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (jar != null)
			{
				try
				{
					jar.close();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	final static void addIntoJar(final File rootFolderIn, final File fileIn, final JarOutputStream jarFileIn)
			throws IOException
	{
		if (!fileIn.exists())
		{
			throw new FileNotFoundException("[ERROR] File not exist ! \"" + fileIn.getAbsolutePath() + "\"");
		}

		var name = fileIn.getAbsolutePath();
		if (name.startsWith(rootFolderIn.getAbsolutePath()))
		{
			name = name.substring(rootFolderIn.getAbsolutePath().length());
		}
		name = name.replace("\\", "/");
		if (name.startsWith("/"))
		{
			name = name.substring(1);
		}

		if (fileIn.isFile())
		{
			final var entry = new JarEntry(name);
			entry.setTime(fileIn.lastModified());
			jarFileIn.putNextEntry(entry);
			try (var in = new BufferedInputStream(new FileInputStream(fileIn)))
			{
				final var buffer = new byte[1024];
				while (true)
				{
					final var count = in.read(buffer);
					if (count == -1)
					{
						break;
					}
					jarFileIn.write(buffer, 0, count);
				}
				jarFileIn.closeEntry();
			}
		}
		else
		{
			if (!name.isBlank() && !name.isEmpty() && !name.matches("\s+"))
			{
				if (!name.endsWith("/"))
				{
					name += "/";
				}

				final var entry = new JarEntry(name);
				entry.setTime(fileIn.lastModified());
				jarFileIn.putNextEntry(entry);
				jarFileIn.closeEntry();
			}

			final var files = fileIn.listFiles();

			if (files != null)
			{
				for (final var file : files)
				{
					FileUtils.addIntoJar(rootFolderIn, file, jarFileIn);
				}
			}
		}
	}

	/**
	 *
	 * @param fileIn
	 * @param valueIn
	 * @param excludedFilesPathIn all excluded files separate by ;
	 */
	static void extractJarExcluded(final File fileIn, final String destinationPathIn, final String excludedFilesPathIn)
	{
		var dir = destinationPathIn + "\\";
		if (dir.endsWith(".jar"))
		{
			dir = dir.substring(0, dir.length() - 4);
		}

		JarInputStream jar = null;
		try
		{
			jar = new JarInputStream(new FileInputStream(fileIn));
			JarEntry jarEntry = null;
			while ((jarEntry = jar.getNextJarEntry()) != null)
			{
				final var	splitted	= jarEntry.getName().split(FileUtils.ZIP_FILE_PATH_SEPARATOR);
				final var	name		= splitted != null && splitted.length > 0 ? splitted[splitted.length - 1]
						: jarEntry.getName();

				final var	splitted0	= excludedFilesPathIn.split(";");
				if (splitted0 != null && splitted.length > 0)
				{
					var isExcluded = false;
					for (final var split : splitted0)
					{
						if (name.contains(split))
						{
							isExcluded = true;
							continue;
						}
					}
					if (isExcluded)
					{
						continue;
					}
				}
				if (name.contains(excludedFilesPathIn))
				{
					continue;
				}

				final var	jarEntryName	= jarEntry.getName();
				final var	entry			= new File(dir, jarEntryName);

				if (jarEntryName.endsWith(FileUtils.ZIP_FILE_PATH_SEPARATOR))
				{
					entry.mkdirs();
				}
				else
				{
					if (!entry.getParentFile().exists())
					{
						entry.getParentFile().mkdirs();
					}

					entry.createNewFile();

					final var	out			= new FileOutputStream(entry);
					final var	buffer		= new byte[1024];
					var			readCount	= 0;

					while ((readCount = jar.read(buffer)) >= 0)
					{
						out.write(buffer, 0, readCount);
					}

					jar.closeEntry();
					out.flush();
					out.close();
				}
			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (jar != null)
			{
				try
				{
					jar.close();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	final static void extractJar(final File fileIn, final String destinationPathIn)
	{
		var dir = destinationPathIn + "\\";
		if (dir.endsWith(".jar"))
		{
			dir = dir.substring(0, dir.length() - 4);
		}

		JarInputStream jar = null;
		try
		{
			jar = new JarInputStream(new FileInputStream(fileIn));
			JarEntry jarEntry = null;
			while ((jarEntry = jar.getNextJarEntry()) != null)
			{
				final var	jarEntryName	= jarEntry.getName();
				final var	entry			= new File(dir, jarEntryName);

				if (jarEntryName.endsWith(FileUtils.ZIP_FILE_PATH_SEPARATOR))
				{
					entry.mkdirs();
				}
				else
				{
					if (!entry.getParentFile().exists())
					{
						entry.getParentFile().mkdirs();
					}

					entry.createNewFile();

					final var	out			= new FileOutputStream(entry);
					final var	buffer		= new byte[1024];
					var			readCount	= 0;

					while ((readCount = jar.read(buffer)) >= 0)
					{
						out.write(buffer, 0, readCount);
					}

					jar.closeEntry();
					out.flush();
					out.close();
				}
			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (jar != null)
			{
				try
				{
					jar.close();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	final static void deletes(final String... folderPathsIn) throws IOException
	{
		for (final var folderPath : folderPathsIn)
		{
			final var folderFile = new File(folderPath);

			if (folderFile.exists())
			{
				FileUtils.deleteDirectory(folderPath);
			}
		}
	}

	final static void deleteDirectory(final String filepathIn) throws IOException
	{
		FileUtils.deleteDirectory(Paths.get(filepathIn));
	}

	final static void deleteDirectory(final Path filepathIn) throws IOException
	{
		Files.walk(filepathIn).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
	}
}
