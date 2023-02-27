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
package fr.onsea.aeisonlauncher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import fr.onsea.aeisonlauncher.git.GitManager;
import fr.onsea.aeisonlauncher.settings.AeisonLauncherSettings;
import fr.onsea.aeisonlauncher.utils.FileUtils;
import fr.onsea.aeisonlauncher.utils.JarBuilder;
import fr.onsea.aeisonlauncher.utils.ProcessRun;
import fr.onsea.aeisonlauncher.utils.StringUtils;
import lombok.AllArgsConstructor;

/**
 * @Organization Onsea
 * @author Seynax
 *
 */
public class AeisonLauncher
{
	private final static int	MAJOR	= 1;
	private final static int	MINOR	= 0;
	private final static String	VERSION	= AeisonLauncher.MAJOR + "." + AeisonLauncher.MINOR;

	public final static void main(final String[] argsIn)
	{
		System.out.println("AeisonLauncher version " + AeisonLauncher.VERSION);

		final List<String> translatedArguments = new ArrayList<>();
		for (final String arg : argsIn)
		{
			final var splitted = arg.split(" ");
			if (splitted != null && splitted.length > 0)
			{
				Collections.addAll(translatedArguments, splitted);
			}
			else
			{
				translatedArguments.add(arg);
			}
		}

		final List<String>	arguments		= new ArrayList<>();

		var					settingFilePath	= "settings\\aeisonlauncher";
		var					i				= 0;
		while (i < translatedArguments.size())
		{
			var arg = translatedArguments.get(i);
			System.out.println("Receveid arg : " + arg);
			if (arg.contentEquals("--settings-filepath"))
			{
				i++;
				if (i < translatedArguments.size())
				{
					arg = translatedArguments.get(i);
					System.out.println("Receveid settings filepath arg : " + arg);
					settingFilePath = arg;
				}
			}
			else
			{
				arguments.add(arg);
			}

			i++;
		}

		System.out.println(settingFilePath);
		AeisonLauncher.launch(arguments, settingFilePath);
	}

	public final static void launch(final List<String> argumentsIn, final String settingsFilepathIn)
	{
		final List<String>	arguments			= new ArrayList<>();
		var					forceRecompiling	= false;
		var					forceReDownload		= false;
		var					settingsParameters	= false;
		var					i					= 0;

		while (i < argumentsIn.size())
		{
			final var argument = argumentsIn.get(i);
			if (settingsParameters)
			{
				arguments.add(argument);

				continue;
			}

			if (argument.contentEquals("--setting"))
			{
				i++;
				settingsParameters = true;
			}
			else if (argument.contentEquals("--force-recompiling"))
			{
				forceRecompiling = true;
			}
			else if (argument.contentEquals("--force-download"))
			{
				forceReDownload = true;
			}

			i++;
		}
		if (forceRecompiling)
		{
			System.out.println("Force recompiling enable");
		}
		if (forceReDownload)
		{
			System.out.println("Force re download enable");
		}

		try
		{
			// LOAD ALL SETTINGS

			System.out.println("Settings loading.");
			final var	settingsLoader	= new AeisonLauncherSettings(settingsFilepathIn, arguments);

			var			hasChanged		= false;
			// Git runtime (get local repo, if exists pull else clone the remote repo)
			{
				// Remove local git repository to force re download if enabled
				{
					if (forceReDownload)
					{
						FileUtils.deleteDirectory(Paths.get(settingsLoader.localPath().value()));
					}
				}

				System.out.println("Initialization of git repository (get local repo or clone if not exists and pull)");

				final var gitManager = new GitManager(settingsLoader.remoteUrl().value(),
						settingsLoader.localPath().value());

				if (gitManager.lastPullResult() != null && gitManager.lastPullResult().getMergeResult() != null)
				{
					switch (gitManager.lastPullResult().getMergeResult().getMergeStatus())
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
							hasChanged = true;
							break;
						case FAST_FORWARD_SQUASHED:
							System.out.println("FAST_FORWARD_SQUASHED !");
							hasChanged = true;
							break;
						case MERGED:
							System.out.println("MERGED !");
							hasChanged = true;
							break;
						case MERGED_NOT_COMMITTED:
							System.out.println("MERGED_NOT_COMMITTED !");
							hasChanged = true;
							break;
						case MERGED_SQUASHED:
							System.out.println("MERGED_SQUASHED !");
							hasChanged = true;
							break;
						case MERGED_SQUASHED_NOT_COMMITTED:
							System.out.println("MERGED_SQUASHED_NOT_COMMITTED !");
							hasChanged = true;
							break;
						case NOT_SUPPORTED:
							throw new Exception("[ERROR] Git pull not supported !");
					}

				}
			}

			if (hasChanged)
			{
				System.out.println("Update available, launch recompilation process !");
			}
			else
			{
				final var outputJarFile = new File(settingsLoader.jarOutputPath().value());
				if (!outputJarFile.exists())
				{
					hasChanged = true;
					System.out.println("\"" + outputJarFile.getAbsolutePath()
							+ "\" jar file not exists, launch recompilation process !");
				}
			}

			if (forceRecompiling)
			{
				System.out.println("Recompiling forced !");
			}
			if (hasChanged || forceRecompiling)
			{
				// BACKUP JAR FILE IF SPECIFIED
				{
					final var	jarBackupSetting	= settingsLoader.settings().get("BACKUP_LAST_JAR");
					String		jarBackupPath		= null;
					var			validJarBackupPath	= false;
					if (jarBackupSetting != null)
					{
						jarBackupPath		= jarBackupSetting.value();
						validJarBackupPath	= jarBackupPath != null && !jarBackupPath.isBlank()
								&& !jarBackupPath.isEmpty() && !jarBackupPath.matches("\s+");
					}

					final var	resourcesBackupSetting		= settingsLoader.settings().get("BACKUP_LAST_RESOURCES");
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
						final var jarOutputFile = new File(settingsLoader.jarOutputPath().value());
						if (jarOutputFile.exists())
						{
							final var	date				= new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss")
									.format(new Date());
							var			finalJarBackupPath	= jarBackupPath.replace("\"", "");
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

							System.out.println("Backup jar from " + settingsLoader.jarOutputPath().value() + " into "
									+ finalJarBackupPath + " with name \"" + name + "\"");

							FileUtils.copy(settingsLoader.jarOutputPath().value(), finalJarBackupPath + "\\" + name);
							FileUtils.deleteDirectory(jarOutputFile.getAbsolutePath());
						}
					}
					else
					{
						final var jarOutputFile = new File(settingsLoader.jarOutputPath().value());
						if (jarOutputFile.exists())
						{
							FileUtils.deleteDirectory(jarOutputFile.getAbsolutePath());
						}
					}

					if (validResourcesBackupPath)
					{
						final var resourcesOutputFile = new File(settingsLoader.outputFolder().value());
						if (resourcesOutputFile.exists())
						{
							final var	date						= new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss")
									.format(new Date());
							var			finalResourcesBackupPath	= resourcesBackupPath.replace("\"", "");
							if (validJarBackupPath)
							{
								finalResourcesBackupPath += "\\" + date;
							}
							FileUtils.mkdirs(finalResourcesBackupPath);

							System.out.println("Backup all files except jar from \"" + settingsLoader.outputFolder()
									+ "\" into " + finalResourcesBackupPath);

							FileUtils.copy(settingsLoader.outputFolder().value(), finalResourcesBackupPath);
						}
					}

				}

				// BACKUP ALL FILES EXCEPT JAR FILE
				{
					System.out.println("Backup all files except jar into " + settingsLoader.backupFolder());
					final var jarOutputFile = new File(settingsLoader.jarOutputPath().value());
					if (jarOutputFile.exists())
					{
						FileUtils.deleteDirectory(jarOutputFile.getAbsolutePath());
					}
					FileUtils.newFolders(settingsLoader.backupFolder().value());
					FileUtils.copy(settingsLoader.outputFolder().value(), settingsLoader.backupFolder().value());
				}

				// DELETE and CREATE operations folders
				{
					System.out.println("Delete and create operations folders :");
					System.out.println("	parent folder - " + settingsLoader.operationFolder() + "\\");
					System.out.println("		temp folder - " + settingsLoader.tempFolder() + "\\");
					System.out.println("		compilation folder - " + settingsLoader.compilationFolder() + "\\");
					System.out.println("		grouping folder - " + settingsLoader.groupingFolder() + "\\");

					FileUtils.mkdirs(settingsLoader.operationFolder().value());
					FileUtils.newFolders(settingsLoader.tempFolder().value(),
							settingsLoader.compilationFolder().value(), settingsLoader.groupingFolder().value(),
							settingsLoader.outputFolder().value());
				}

				// Write all source inputs absolute path separate by newline into sources.txt file
				final var sourceInputsPath = new File(settingsLoader.tempFolder().value(), "\\sources.txt")
						.getAbsolutePath();
				{
					System.out.println("Writing of all .java sources files into \"" + sourceInputsPath + "\"");
					AeisonLauncher.writeSourceInputsFile(settingsLoader, new File(sourceInputsPath));
				}

				// Define all sources inputs with * character on linux, with source inputs file on windows

				var sourceInputs = settingsLoader.relativeSourcesFolder() + "\\*.java";

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
				System.out.println(
						"Loading of modules path and extraction into \"" + settingsLoader.groupingFolder() + "\"");
				settingsLoader.modulesPath().value("");
				{
					final var	excludedLibrairies			= settingsLoader.excludedLibrairiesForExtraction().value();
					final var	excludedLibsFilesSetting	= settingsLoader.settings().get("EXCLUDED_LIBS_FILES");
					final var	excludedLibsFiles			= excludedLibsFilesSetting != null
							&& excludedLibsFilesSetting.value() != null ? excludedLibsFilesSetting.value() : "";

					FileUtils.recurse(new File(settingsLoader.libPath().value()), fileIn -> {
						if (fileIn.getAbsolutePath().endsWith(".jar")
								&& (settingsLoader.classesPath() == null || settingsLoader.classesPath().value() == null
										|| !settingsLoader.classesPath().value().contains(fileIn.getName()))
								|| fileIn.getName().contains("lombok"))
						{
							settingsLoader.modulesPath()
									.value(settingsLoader.modulesPath().value() + fileIn.getAbsolutePath() + ";");
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

								FileUtils.extractJarExcluded(fileIn, settingsLoader.groupingFolder().value(),
										excludedLibsFiles);
							}
						}
					});

					if (settingsLoader.modulesPath().value().endsWith(";"))
					{
						settingsLoader.modulesPath().value(settingsLoader.modulesPath().value().substring(0,
								settingsLoader.modulesPath().value().length() - 1));
					}

					settingsLoader.modulesPath().value("\"" + settingsLoader.modulesPath().value() + "\"");
				}

				// Generate javac command

				System.out.println("Javac command generation : ");
				final var javacCommand = new JarBuilder(sourceInputs, settingsLoader.javaBinPath().value())
						.additions("-verbose -g -encoding UTF-8").implicitNone().modules("jdk.incubator.vector")
						.modulesPath(settingsLoader.modulesPath().value())
						.classesPath(settingsLoader.classesPath().value())
						.sourcesPath(settingsLoader.sourcesPath().value())
						.outputPath(new File(settingsLoader.compilationFolder().value()).getAbsolutePath())
						.buildJavacCommand();
				System.out.println(javacCommand);

				// Create errors, outputs and inputs logs files

				System.out.println("New errors, outputs and inputs files creation");
				final var	errorsFile	= FileUtils.createNewFile(settingsLoader.tempFolder().value() + "\\errors.txt");
				final var	outputsFile	= FileUtils
						.createNewFile(settingsLoader.tempFolder().value() + "\\outputs.txt");
				final var	inputsFile	= FileUtils.createNewFile(settingsLoader.tempFolder().value() + "\\inputs.txt");

				// Run javac command with redirection of outputs into logs files
				System.out.println("Launching of javac command");
				ProcessRun.runWithoutOutput(new ProcessBuilder(javacCommand)
						.directory(new File(settingsLoader.localPath().value())).redirectErrorStream(true)
						.redirectError(errorsFile).redirectOutput(outputsFile).redirectInput(inputsFile));

				// Copy compiled sources into grouped path

				System.out.println("Copy of generated .class files from \"" + settingsLoader.compilationFolder()
						+ "\" into \"" + settingsLoader.groupingFolder() + "\"");
				FileUtils.copy(settingsLoader.compilationFolder().value(), settingsLoader.groupingFolder().value());

				// Create Meta-Inf

				System.out.println("Meta-Info.MF file content generation");
				final var metaInfContent = new StringBuilder("Manifest-Version: 1.0\r\n")
						.append("Created-By: Onsea Studio with Java 19.0.2 (Oracle Corporation)");
				if (settingsLoader.mainClass() != null)
				{
					metaInfContent.append("\r\nMain-Class: ")
							.append(settingsLoader.mainClass().value().replace("\"", ""));
				}
				metaInfContent.append("\r\n").append("\r\n").append("");

				System.out.println("MANIFEST.MF file creation");
				final var metaInf = new File(settingsLoader.groupingFolder().value(), "META-INF\\MANIFEST.MF");

				FileUtils.createNewFile(metaInf);

				FileUtils.write(metaInf, metaInfContent.toString());

				System.out.println("Assembly all with MANIFEST.MF into .jar");
				FileUtils.makeJar(new File(settingsLoader.groupingFolder().value()),
						settingsLoader.jarOutputPath().value());

				// Delete temp directory

				System.out.println("deleting temp directory");
				FileUtils.deleteDirectory(settingsLoader.tempFolder().value());

				// Copies into output path

				final var copies = settingsLoader.settings().get("COPIES_INTO_OUTPUT_PATH");
				if (copies != null)
				{
					System.out.println("Copy all specified files " + copies + " into output folder \""
							+ settingsLoader.outputFolder() + "\"");

					final var	copiesFilesPath	= copies.value().replace("\"", "");
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
										FileUtils.copy(file,
												new File(settingsLoader.outputFolder().value(), file.getName()));
									}

									continue;
								}
							}

							System.out.println("Copie of " + from.getAbsolutePath() + " into "
									+ new File(settingsLoader.outputFolder().value(), from.getName())
											.getAbsolutePath());
							FileUtils.copy(from, new File(settingsLoader.outputFolder().value(), from.getName()));
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
									FileUtils.copy(file,
											new File(settingsLoader.outputFolder().value(), file.getName()));
								}
							}
						}
						else
						{
							FileUtils.copy(from, new File(settingsLoader.outputFolder().value(), from.getName()));
						}
					}
				}

				final var links = settingsLoader.settings().get("LINKS_INTO_OUTPUT_PATH");
				if (links != null)
				{
					System.out.println("Links all specified files " + links + " into output folder \""
							+ settingsLoader.outputFolder() + "\"");

					final var	linksFilesPath	= links.value();
					final var	splitted		= linksFilesPath.split(";");

					if (splitted != null && splitted.length > 0)
					{
						for (final var split : splitted)
						{
							final var from = new File(split);
							FileUtils.createSymbolicLink(from.getAbsolutePath(),
									settingsLoader.outputFolder() + "\\" + from.getName());
						}
					}
					else
					{
						final var from = new File(linksFilesPath);
						FileUtils.createSymbolicLink(from.getAbsolutePath(),
								settingsLoader.outputFolder() + "\\" + from.getName());
					}
				}
			}

			final var launchSetting = settingsLoader.settings().get("LAUNCH");
			if (launchSetting != null)
			{
				final var launchPath = launchSetting.value();
				if (launchPath != null)
				{
					System.out.println("Compile launch command " + launchPath);
					final var	splitted	= launchPath.replace("\"", "").split(" from ");
					var			jarPath		= launchPath;
					var			workPath	= settingsLoader.localPath().value();
					if (splitted != null && splitted.length > 1)
					{
						jarPath		= StringUtils.removeUnusedBlank(splitted[0]);
						workPath	= StringUtils.removeUnusedBlank(splitted[1]);
					}

					final var launchFile = new File(jarPath);
					if (launchFile.exists())
					{
						System.out.println("New errors, outputs and inputs files creation");
						final var	errorsFile	= FileUtils.createNewFile(settingsLoader.tempFolder() + "\\errors.txt");
						final var	outputsFile	= FileUtils
								.createNewFile(settingsLoader.tempFolder() + "\\outputs.txt");
						final var	inputsFile	= FileUtils.createNewFile(settingsLoader.tempFolder() + "\\inputs.txt");

						System.out.println("Launch \"" + jarPath + "\" from work path \"" + workPath + "\"");

						var			title			= "";
						final var	titleSetting	= settingsLoader.settings().get("NEW_CMD_PROCESS_TITLE");
						if (titleSetting != null && titleSetting.value() != null)
						{
							title = titleSetting.value().replace("\"", "");
							System.out.println("Title of new CMD process : " + title);
						}

						var			args					= "";
						final var	nextSettingsFilepath	= settingsLoader.settings().get("NEXT_SETTINGS_FILEPATH");
						if (nextSettingsFilepath != null && nextSettingsFilepath.value() != null)
						{
							args += "--settings-filepath " + nextSettingsFilepath.value().replace("\"", "");
							System.out.println("Next settings filepath : " + nextSettingsFilepath.value());
						}
						final var nextProcessArgs = settingsLoader.settings().get("NEXT_PROCESS_ARGS");
						if (nextProcessArgs != null && nextProcessArgs.value() != null)
						{
							final var splittedArgs = nextProcessArgs.value().replace("\"", "").split(";");
							if (splittedArgs != null && splitted.length > 0)
							{
								var i0 = 0;
								for (final var split : splitted)
								{
									if (i0 > 0)
									{
										args += " ";
									}
									args += split;
									System.out.println(split + " added to next process arguments");
									i0++;
								}
							}
							else
							{
								args += (nextSettingsFilepath != null ? " " : "")
										+ nextProcessArgs.value().replace("\"", "");
								System.out.println(nextProcessArgs + " added to next process arguments");
							}
						}

						System.out.println("cmd /C start \"" + title + "\" \"" + settingsLoader.javaBinPath().value()
								+ "\\java.exe\" -jar " + launchFile.getAbsolutePath() + " "
								+ settingsLoader.mainClass().value() + (args == "" ? "" : " " + args));

						new Launcher(settingsLoader.javaBinPath().value(), launchFile, workPath, errorsFile,
								outputsFile, inputsFile, settingsLoader.mainClass().value(), title,
								args == "" ? null : args).run();
						System.exit(0);
					}
					else
					{
						System.err.println("\"" + jarPath + "\" not found !");
					}
				}
			}

		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	@AllArgsConstructor
	public final static class Launcher implements Runnable
	{
		private final String	javaBinPath;
		private final File		launchFile;
		private final String	workPath;
		private final File		errorsFile;
		private final File		outputsFile;
		private final File		inputsFile;
		private final String	main;
		private final String	title;
		private final String	args;

		@Override
		public void run()
		{
			try
			{
				if (this.args != null)
				{
					new ProcessBuilder("cmd", "/C", "start", "\"" + this.title + "\"",
							"\"" + this.javaBinPath + "\\java.exe\"", "-jar", this.launchFile.getAbsolutePath(),
							this.main, this.args).inheritIO()
							.directory(new File(new File(this.workPath).getAbsolutePath())).redirectErrorStream(true)
							.redirectError(this.errorsFile).redirectOutput(this.outputsFile)
							.redirectInput(this.inputsFile).start();
				}
				else
				{
					new ProcessBuilder("cmd", "/C", "start", "\"" + this.title + "\"",
							"\"" + this.javaBinPath + "\\java.exe\"", "-jar", this.launchFile.getAbsolutePath(),
							this.main).inheritIO().directory(new File(new File(this.workPath).getAbsolutePath()))
							.redirectErrorStream(true).redirectError(this.errorsFile).redirectOutput(this.outputsFile)
							.redirectInput(this.inputsFile).start();
				}
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private final static void writeSourceInputsFile(final AeisonLauncherSettings settingsLoaderIn,
			final File sourceInputsFileIn) throws Exception
	{
		FileUtils.createNewFile(sourceInputsFileIn);

		final var writer = new BufferedWriter(new FileWriter(sourceInputsFileIn));

		FileUtils.recurse(new File(settingsLoaderIn.sourcesFolder().value()), fileIn -> {
			if (fileIn.getAbsolutePath().endsWith(".java"))
			{
				FileUtils.appendLn(writer, fileIn.getAbsolutePath());
			}
		});

		writer.close();
	}
}
