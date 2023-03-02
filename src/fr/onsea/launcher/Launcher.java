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

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import fr.onsea.launcher.git.GitManager;
import fr.onsea.launcher.settings.GenericSettings;
import fr.onsea.launcher.settings.arguments.ArgumentsLoader;
import fr.onsea.launcher.utils.ConnectivityUtils;
import fr.onsea.launcher.utils.ProcessRun;
import fr.onsea.launcher.utils.StringUtils;
import fr.onsea.launcher.utils.file.FileUtils;
import fr.onsea.launcher.utils.file.FilesManager;
import fr.onsea.launcher.utils.file.JarBuilder;

/**
 * @author Seynax
 *
 */
public class Launcher implements ILauncher
{
	public final static void start(final ILauncher launcherIn, final String... argsIn) throws Exception
	{
		launcherIn.initialization();

		var	updateAvailable		= false;
		var	updateDownloaded	= false;

		if (launcherIn.canCheckUpdate() && launcherIn.updateAvailable())
		{
			if (launcherIn.canUpdate())
			{
				launcherIn.preUpdading().updating().postUpdading();

				updateDownloaded = true;
			}

			updateAvailable = true;
		}

		if (launcherIn.canAssemble(updateAvailable, updateDownloaded))
		{
			launcherIn.preAssemble().assemble().postAssemble();
		}

		if (launcherIn.canLaunch())
		{
			launcherIn.preLaunch().launch().postLaunch();
		}

		launcherIn.cleanup();
	}

	public final static void start(final String... argsIn) throws Exception
	{
		final var			filesManager	= new FilesManager();

		final List<String>	othersArguments	= new ArrayList<>();
		final List<String>	passArguments	= new ArrayList<>();

		// If receive --pass, copy this .jar into --pass value (to move new launcher in correct directory)
		var					pass			= false;
		for (final String arg : argsIn)
		{
			System.out.println(arg);
			if (pass)
			{
				passArguments.add(arg);

				continue;
			}

			if (arg.contentEquals("--pass") || arg.contentEquals("-ps"))
			{
				pass = true;
			}
			else
			{
				othersArguments.add(arg);
			}
		}

		if (pass)
		{
			final var	javaexepath			= passArguments.get(0);
			final var	from				= passArguments.get(1);
			final var	to					= passArguments.get(2);
			final var	settingsFilePath	= passArguments.get(3);
			System.out.println("Pass settings : " + javaexepath + " " + to + " " + from + " " + settingsFilePath);

			// LOAD ALL SETTINGS

			System.out.println("Settings loading.");
			System.out.println("Settings file path : " + settingsFilePath);
			final var settings = new GenericSettings(settingsFilePath);

			filesManager.addFileDeletionAllowedWithConfirmation(settings.localPath().value());

			final var	fromFile	= new File(from);
			final var	toFile		= new File(to + "\\" + fromFile.getName());
			if (toFile.exists() && toFile.isFile())
			{
				filesManager.delete(toFile.getAbsolutePath());
			}

			System.out.println("Copy : " + fromFile.getAbsolutePath() + " into " + toFile.getAbsolutePath());
			filesManager.copy(fromFile, toFile);

			Launcher.newOutputsFiles(filesManager, settings);

			System.out.println("Launch !");
			Launcher.launch("", javaexepath, toFile, othersArguments, toFile.getParent(), Launcher.errorsFile,
					Launcher.outputsFile, Launcher.inputsFile);
		}

		// COMPILE ARGUMENTS

		System.out.println("Compile all command line arguments");

		final var	argumentsLoader			= new ArgumentsLoader().addKeyValues("--pass", "-ps")
				.addKeysWithAliases("--force-recompiling", "-fp", "--force-download", "-fd")
				.addKeysValuesWithAliases("--settings-filepath", "-sf").compile(argsIn);

		final var	settingsFilePathSetting	= argumentsLoader.values().get("--settings-filepath");
		var			settingsFilePath		= "settings\\main";
		if (settingsFilePathSetting != null && settingsFilePathSetting.value() != null)
		{
			settingsFilePath = settingsFilePathSetting.value();
			argumentsLoader.values().remove("--settings-filepath");
		}

		// LOAD ALL SETTINGS

		System.out.println("Settings loading.");
		System.out.println("Settings file path : " + settingsFilePath);
		final var settings = new GenericSettings(settingsFilePath, argumentsLoader);

		Launcher.start(new Launcher(settings, filesManager), argsIn);
	}

	private static File				errorsFile;
	private static File				outputsFile;
	private static File				inputsFile;

	private final GenericSettings	SETTINGS;
	private GitManager				gitManager;
	private boolean					hasBeenRecompiled;
	private final FilesManager		FILES_MANAGER;

	public Launcher(final GenericSettings settingsLoaderIn, final FilesManager filesManagerIn)
	{
		this.SETTINGS		= settingsLoaderIn;
		this.FILES_MANAGER	= filesManagerIn;
	}

	@Override
	public ILauncher initialization() throws Exception
	{
		ILauncher.super.initialization();

		return this;
	}

	@Override
	public boolean canCheckUpdate() throws SocketException
	{
		System.out.println("Test connection");
		if (!ConnectivityUtils.isConnected())
		{
			System.err.println("is not connected to the internet !");

			return false;
		}

		if (this.SETTINGS.isEnabled("NO_UPDATE_CHECK"))
		{
			System.out.println("No update checking is enabled !");

			return false;
		}

		return ILauncher.super.canCheckUpdate();
	}

	@Override
	public boolean canUpdate()
	{
		if (this.SETTINGS.isEnabled("NO_UPDATE"))
		{
			System.out.println("No updating is enabled !");

			return false;
		}

		return ILauncher.super.canUpdate();
	}

	@Override
	public boolean updateAvailable() throws Exception
	{
		// Git runtime (get local repo, if exists pull else clone the remote repo)

		// Remove local git repository to force re download if enabled

		if (this.SETTINGS.isEnabled("FORCE_DOWNLOAD"))
		{
			System.out.println("Force download is enabled !");

			this.FILES_MANAGER.delete(this.SETTINGS.localPath().value(), true);
		}

		System.out.println("Initialization of git repository (get local repo or clone if not exists and pull)");

		this.gitManager = new GitManager(this.SETTINGS.remoteUrl().value(), this.SETTINGS.localPath().value());

		if (this.gitManager.lastPullResult() != null && this.gitManager.lastPullResult().getMergeResult() != null)
		{
			switch (this.gitManager.lastPullResult().getMergeResult().getMergeStatus())
			{
				case ABORTED:
					throw new Exception("[ERROR] ABORTED !");
				case ALREADY_UP_TO_DATE:
					System.out.println("Is already up to date !");
					break;
				case CHECKOUT_CONFLICT:
					throw new Exception("[ERROR] CHECKOUT_CONFLICT !");
				case CONFLICTING:
					throw new Exception("[ERROR] CONFLICTING !");
				case FAILED:
					throw new Exception("[ERROR] FAILED !");
				case FAST_FORWARD:
					System.out.println("FAST_FORWARD !");
					return true;
				case FAST_FORWARD_SQUASHED:
					System.out.println("FAST_FORWARD_SQUASHED !");
					return true;
				case MERGED:
					System.out.println("MERGED !");
					return true;
				case MERGED_NOT_COMMITTED:
					System.out.println("MERGED_NOT_COMMITTED !");
					return true;
				case MERGED_SQUASHED:
					System.out.println("MERGED_SQUASHED !");
					return true;
				case MERGED_SQUASHED_NOT_COMMITTED:
					System.out.println("MERGED_SQUASHED_NOT_COMMITTED !");
					return true;
				case NOT_SUPPORTED:
					throw new Exception("[ERROR] Git pull not supported !");
			}
		}

		return false;
	}

	@Override
	public ILauncher updating()
	{
		return this;
	}

	@Override
	public boolean canAssemble(final boolean updateAvailableIn, final boolean updateDownloadedIn)
	{
		if (this.SETTINGS.isEnabled("FORCE_RECOMPILING"))
		{
			System.out.println("Force recompiling is enabled !");

			return true;
		}

		return ILauncher.super.canAssemble(updateAvailableIn, updateDownloadedIn)
				|| !new File(this.SETTINGS.jarOutputPath().value()).exists();
	}

	@Override
	public ILauncher assemble() throws Exception
	{
		this.hasBeenRecompiled = true;
		// BACKUP JAR FILE IF SPECIFIED
		{
			final var	jarBackupSetting	= this.SETTINGS.strings().of("BACKUP_LAST_JAR");
			String		jarBackupPath		= null;
			var			validJarBackupPath	= false;
			if (jarBackupSetting != null)
			{
				jarBackupPath		= jarBackupSetting.value();
				validJarBackupPath	= jarBackupPath != null && !jarBackupPath.isBlank() && !jarBackupPath.isEmpty()
						&& !jarBackupPath.matches("\s+");
			}

			final var	resourcesBackupSetting		= this.SETTINGS.strings().of("BACKUP_LAST_RESOURCES");
			String		resourcesBackupPath			= null;
			var			validResourcesBackupPath	= false;
			if (resourcesBackupSetting != null)
			{
				resourcesBackupPath			= resourcesBackupSetting.value();
				validResourcesBackupPath	= resourcesBackupPath != null && !resourcesBackupPath.isBlank()
						&& !resourcesBackupPath.isEmpty() && !resourcesBackupPath.matches("\s+");
			}

			if (validJarBackupPath)
			{
				final var jarOutputFile = new File(this.SETTINGS.jarOutputPath().value());
				if (jarOutputFile.exists())
				{
					final var	date				= new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
					var			finalJarBackupPath	= jarBackupPath;
					if (validResourcesBackupPath)
					{
						finalJarBackupPath += "\\" + date;
					}
					FileUtils.mkdirs(finalJarBackupPath);

					var name = jarOutputFile.getName();
					if (name.endsWith(".jar"))
					{
						name = name.substring(0, name.length() - 4);
					}
					name = name + "_" + date + ".jar";

					System.out.println("Backup jar from " + this.SETTINGS.jarOutputPath().value() + " into "
							+ finalJarBackupPath + " with name \"" + name + "\"");

					this.FILES_MANAGER.copy(this.SETTINGS.jarOutputPath().value(), finalJarBackupPath + "\\" + name);
					this.FILES_MANAGER.delete(jarOutputFile.getAbsolutePath());
				}
			}
			else
			{
				final var jarOutputFile = new File(this.SETTINGS.jarOutputPath().value());
				if (jarOutputFile.exists())
				{
					this.FILES_MANAGER.delete(jarOutputFile.getAbsolutePath());
				}
			}

			if (validResourcesBackupPath)
			{
				final var resourcesOutputFile = new File(this.SETTINGS.outputFolder().value());
				if (resourcesOutputFile.exists())
				{
					final var	date						= new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss")
							.format(new Date());
					var			finalResourcesBackupPath	= resourcesBackupPath;
					if (validJarBackupPath)
					{
						finalResourcesBackupPath += "\\" + date;
					}
					FileUtils.mkdirs(finalResourcesBackupPath);

					System.out.println("Backup all files except jar from \"" + this.SETTINGS.outputFolder() + "\" into "
							+ finalResourcesBackupPath);

					this.FILES_MANAGER.copy(this.SETTINGS.outputFolder().value(), finalResourcesBackupPath);
				}
			}

		}

		// BACKUP ALL FILES EXCEPT JAR FILE
		{
			System.out.println("Backup all files except jar into " + this.SETTINGS.backupFolder());
			final var jarOutputFile = new File(this.SETTINGS.jarOutputPath().value());
			if (jarOutputFile.exists())
			{
				this.FILES_MANAGER.delete(jarOutputFile.getAbsolutePath());
			}
			this.FILES_MANAGER.newFolders(this.SETTINGS.backupFolder().value());
			this.FILES_MANAGER.copy(this.SETTINGS.outputFolder().value(), this.SETTINGS.backupFolder().value());
		}

		// DELETE and CREATE operations folders
		{
			System.out.println("Delete and create operations folders :");
			System.out.println("	parent folder - " + this.SETTINGS.operationFolder() + "\\");
			System.out.println("		temp folder - " + this.SETTINGS.tempFolder() + "\\");
			System.out.println("		compilation folder - " + this.SETTINGS.compilationFolder() + "\\");
			System.out.println("		grouping folder - " + this.SETTINGS.groupingFolder() + "\\");

			FileUtils.mkdirs(this.SETTINGS.operationFolder().value());
			this.FILES_MANAGER.newFolders(this.SETTINGS.tempFolder().value(), this.SETTINGS.compilationFolder().value(),
					this.SETTINGS.groupingFolder().value(), this.SETTINGS.outputFolder().value());
		}

		// Write all source inputs absolute path separate by newline into sources.txt file
		final var sourceInputsPath = new File(this.SETTINGS.tempFolder().value(), "\\sources.txt").getAbsolutePath();
		{
			System.out.println("Writing of all .java sources files into \"" + sourceInputsPath + "\"");

			this.FILES_MANAGER.writeFilesList(this.SETTINGS.sourcesFolder().value(), sourceInputsPath);
		}

		final var projectName = this.SETTINGS.strings().of("PROJECT_NAME");
		if (projectName != null && projectName.value() != null && projectName.value().contentEquals("Aeison"))
		{
			System.exit(0);
		}

		// Define all sources inputs with * character on linux, with source inputs file on windows

		var sourceInputs = this.SETTINGS.relativeSourcesFolder() + "\\*.java";

		if (System.getProperty("os.name").contains("Windows"))
		{
			sourceInputs = "@" + sourceInputsPath;
			System.out.println("Source inputs defined by \"" + sourceInputs + "\" on windows");
		}
		else
		{
			System.out.println("Source inputs defined by \"" + sourceInputs + "\" if not on windows");
		}

		// Define modules_path string and extract all .class files from all librairies jar into grouped folder
		System.out.println("Loading of modules path and extraction into \"" + this.SETTINGS.groupingFolder() + "\"");
		this.SETTINGS.modulesPath().value("");
		{
			final var	excludedLibrairies			= this.SETTINGS.excludedLibrairiesForExtraction().value();
			final var	excludedLibsFilesSetting	= this.SETTINGS.strings().of("EXCLUDED_LIBS_FILES");
			final var	excludedLibsFiles			= excludedLibsFilesSetting != null
					&& excludedLibsFilesSetting.value() != null ? excludedLibsFilesSetting.value() : "";

			FileUtils.recurse(new File(this.SETTINGS.libPath().value()), fileIn -> {
				if (fileIn.getAbsolutePath().endsWith(".jar")
						&& (this.SETTINGS.classesPath() == null || this.SETTINGS.classesPath().value() == null
								|| !this.SETTINGS.classesPath().value().contains(fileIn.getName()))
						|| fileIn.getName().contains("lombok"))
				{
					this.SETTINGS.modulesPath()
							.value(this.SETTINGS.modulesPath().value() + fileIn.getAbsolutePath() + ";");
					var excluded = false;

					if (excludedLibrairies.contains(fileIn.getName()))
					{
						System.out.println(fileIn.getAbsolutePath() + " is excluded by " + excludedLibrairies);

						excluded = true;
					}
					if (excludedLibsFiles.contains(fileIn.getName()))
					{
						System.out.println(fileIn.getAbsolutePath() + " is excluded by " + excludedLibsFiles);

						excluded = true;
					}

					if (!excluded)
					{
						System.out.println("Extracting of jar : " + fileIn.getAbsolutePath());

						this.FILES_MANAGER.extractJarExcluded(fileIn, this.SETTINGS.groupingFolder().value(),
								excludedLibsFiles);
					}
				}
			});

			if (this.SETTINGS.modulesPath().value().endsWith(";"))
			{
				this.SETTINGS.modulesPath().value(this.SETTINGS.modulesPath().value().substring(0,
						this.SETTINGS.modulesPath().value().length() - 1));
			}

			this.SETTINGS.modulesPath().value("\"" + this.SETTINGS.modulesPath().value() + "\"");
		}

		// Generate javac command

		System.out.println("Javac command generation : ");
		final var javacCommand = new JarBuilder(sourceInputs, this.SETTINGS.javaBinPath().value())
				.additions("-verbose -g -encoding UTF-8").implicitNone().modules("jdk.incubator.vector")
				.modulesPath(this.SETTINGS.modulesPath().value()).classesPath(this.SETTINGS.classesPath().value())
				.sourcesPath(this.SETTINGS.sourcesPath().value())
				.outputPath(new File(this.SETTINGS.compilationFolder().value()).getAbsolutePath()).buildJavacCommand();
		System.out.println(javacCommand);

		// Create errors, outputs and inputs logs files

		Launcher.newOutputsFiles(this.FILES_MANAGER, this.SETTINGS);

		// Run javac command with redirection of outputs into logs files
		System.out.println("Launching of javac command");
		ProcessRun.runWithoutOutput(
				new ProcessBuilder(javacCommand).directory(new File(this.SETTINGS.localPath().value()))
						.redirectErrorStream(true).redirectError(Launcher.errorsFile)
						.redirectOutput(Launcher.outputsFile).redirectInput(Launcher.inputsFile));

		// Copy compiled sources into grouped path

		System.out.println("Copy of generated .class files from \"" + this.SETTINGS.compilationFolder() + "\" into \""
				+ this.SETTINGS.groupingFolder() + "\"");
		this.FILES_MANAGER.copy(this.SETTINGS.compilationFolder().value(), this.SETTINGS.groupingFolder().value());

		// Create Meta-Inf

		System.out.println("Meta-Info.MF file content generation");
		final var metaInfContent = new StringBuilder("Manifest-Version: 1.0\r\n")
				.append("Created-By: Onsea Studio with Java 19.0.2 (Oracle Corporation)");
		if (this.SETTINGS.mainClass() != null)
		{
			metaInfContent.append("\r\nMain-Class: ").append(this.SETTINGS.mainClass().value());
		}
		metaInfContent.append("\r\n").append("\r\n").append("");

		System.out.println("MANIFEST.MF file creation");
		final var metaInf = new File(this.SETTINGS.groupingFolder().value(), "META-INF\\MANIFEST.MF");

		this.FILES_MANAGER.createNewFile(metaInf);

		this.FILES_MANAGER.write(metaInf, metaInfContent.toString());

		System.out.println("Assembly all with MANIFEST.MF into " + this.SETTINGS.jarOutputPath().quotedValue());
		this.FILES_MANAGER.makeJar(new File(this.SETTINGS.groupingFolder().value()),
				this.SETTINGS.jarOutputPath().value());

		// Delete temp directory

		System.out.println("deleting temp directory");
		this.FILES_MANAGER.delete(this.SETTINGS.tempFolder().value());

		// Copies into output path

		final var copies = this.SETTINGS.strings().of("COPIES_INTO_OUTPUT_PATH");
		if (copies != null)
		{
			System.out.println("Copy all specified files " + copies + " into output folder \""
					+ this.SETTINGS.outputFolder() + "\"");

			final var	copiesFilesPath	= copies.value();
			final var	splitted		= copiesFilesPath.split(";");

			if (splitted != null && splitted.length > 0)
			{
				for (final var split : splitted)
				{
					var from = new File(split);
					if (split.endsWith("\\*"))
					{
						from = new File(split.substring(0, split.length() - 2));
						final var files = from.listFiles();
						if (files != null)
						{
							for (final var file : files)
							{
								this.FILES_MANAGER.copy(file,
										new File(this.SETTINGS.outputFolder().value(), file.getName()));
							}

							continue;
						}
					}

					System.out.println("Copie of " + from.getAbsolutePath() + " into "
							+ new File(this.SETTINGS.outputFolder().value(), from.getName()).getAbsolutePath());
					this.FILES_MANAGER.copy(from, new File(this.SETTINGS.outputFolder().value(), from.getName()));
				}
			}
			else
			{
				final var from = new File(copiesFilesPath);
				if (copiesFilesPath.endsWith("*"))
				{
					final var files = from.listFiles();
					if (files != null)
					{
						for (final var file : files)
						{
							this.FILES_MANAGER.copy(file,
									new File(this.SETTINGS.outputFolder().value(), file.getName()));
						}
					}
				}
				else
				{
					this.FILES_MANAGER.copy(from, new File(this.SETTINGS.outputFolder().value(), from.getName()));
				}
			}
		}

		final var links = this.SETTINGS.strings().of("LINKS_INTO_OUTPUT_PATH");
		if (links != null)
		{
			System.out.println("Links all specified files " + links + " into output folder \""
					+ this.SETTINGS.outputFolder() + "\"");

			final var	linksFilesPath	= links.value();
			final var	splitted		= linksFilesPath.split(";");

			if (splitted != null && splitted.length > 0)
			{
				for (final var split : splitted)
				{
					final var from = new File(split);
					this.FILES_MANAGER.createSymbolicLink(from.getAbsolutePath(),
							this.SETTINGS.outputFolder() + "\\" + from.getName());
				}
			}
			else
			{
				final var from = new File(linksFilesPath);
				this.FILES_MANAGER.createSymbolicLink(from.getAbsolutePath(),
						this.SETTINGS.outputFolder() + "\\" + from.getName());
			}
		}

		return this;
	}

	@Override
	public ILauncher launch() throws Exception
	{
		final var launchSetting = this.SETTINGS.strings().of("LAUNCH");
		if (launchSetting != null)
		{
			final var launchPath = launchSetting.value();
			if (launchPath != null)
			{
				System.out.println("Compile launch command " + launchPath);
				final var	splitted	= launchPath.split(" from ");
				var			jarPath		= launchPath;
				var			workPath	= this.SETTINGS.localPath().value();
				if (splitted != null && splitted.length > 1)
				{
					jarPath		= StringUtils.removeUnusedBlank(splitted[0]);
					workPath	= StringUtils.removeUnusedBlank(splitted[1]);
				}

				final var launchFile = new File(jarPath);
				if (launchFile.exists())
				{
					Launcher.newOutputsFiles(this.FILES_MANAGER, this.SETTINGS);

					System.out.println("Launch \"" + jarPath + "\" from work path \"" + workPath + "\"");

					var			title			= "";
					final var	titleSetting	= this.SETTINGS.strings().of("NEW_CMD_PROCESS_TITLE");
					if (titleSetting != null && titleSetting.value() != null)
					{
						title = titleSetting.value();
						System.out.println("Title of new CMD process : " + title);
					}

					final var	args					= new ArrayList<String>();

					final var	nextSettingsFilepath	= this.SETTINGS.strings().of("NEXT_SETTINGS_FILEPATH");
					if (nextSettingsFilepath != null && nextSettingsFilepath.value() != null)
					{
						args.add("--settings-filepath");
						args.add(nextSettingsFilepath.quotedValue());
						System.out.println("Next settings filepath : " + nextSettingsFilepath.value());
					}
					final var nextProcessArgsSetting = this.SETTINGS.strings().of("NEXT_PROCESS_ARGS");
					if (nextProcessArgsSetting != null && nextProcessArgsSetting.value() != null)
					{
						final var nextProcessArgs = nextProcessArgsSetting.value();
						if (nextProcessArgs != null)
						{
							args.add(nextProcessArgs);

							System.out.println("Next args : " + nextProcessArgs);
						}
					}

					if (this.hasBeenRecompiled)
					{
						args.add("--pass");
						args.add(this.SETTINGS.javaExePath().value());
						args.add(launchFile.getAbsolutePath());
						args.add(new File(workPath).getAbsolutePath());
						args.add(this.SETTINGS.filePath());
					}

					Launcher.launch("", this.SETTINGS.javaExePath().quotedValue(), launchFile, args, workPath,
							Launcher.errorsFile, Launcher.outputsFile, Launcher.inputsFile);
				}
				else
				{
					System.err.println("\"" + jarPath + "\" not found !");
				}
			}
		}

		return this;
	}

	public final static void launch(final String titleIn, final String javaExePathIn, final File launchFileIn,
			final List<String> argsIn, final String workPathIn, final File errorsFileIn, final File outputsFileIn,
			final File inputsFileIn) throws IOException
	{
		final var			workAbsolutePath	= new File(workPathIn).getAbsolutePath();
		final List<String>	launchCommand		= new ArrayList<>();

		//
		Collections.addAll(launchCommand, "cmd", "/k", "start", "TITLE !", "/wait", "/b", "/i", javaExePathIn, "-jar",
				launchFileIn.getAbsolutePath());

		launchCommand.addAll(argsIn);

		// Show command
		final var	arguments	= new String[launchCommand.size()];
		var			i			= 0;
		for (final var arg : launchCommand)
		{
			if (i > 0)
			{
				System.out.print(" ");
			}
			arguments[i] = arg;
			System.out.print(arg);

			i++;
		}
		System.out.println();

		new ProcessBuilder(arguments).directory(new File(workAbsolutePath)).inheritIO().redirectErrorStream(true)
				.redirectError(errorsFileIn).redirectOutput(outputsFileIn).redirectInput(inputsFileIn).start();

		System.exit(0);
	}

	private static void newOutputsFiles(final FilesManager filesManagerIn, final GenericSettings settingsIn)
			throws Exception
	{
		System.out.println("New errors, outputs and inputs files creation");
		final var date = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss-SSS").format(new Date());
		Launcher.errorsFile		= filesManagerIn
				.createNewFile(settingsIn.tempFolder() + "\\" + settingsIn.projectName() + "_errors_" + date + ".txt");
		Launcher.outputsFile	= filesManagerIn.createNewFile(
				settingsIn.tempFolder() + "\\" + settingsIn.projectName() + "_outputs_." + date + ".txt");
		Launcher.inputsFile		= filesManagerIn
				.createNewFile(settingsIn.tempFolder() + "\\" + settingsIn.projectName() + "_inputs_" + date + ".txt");
	}
}