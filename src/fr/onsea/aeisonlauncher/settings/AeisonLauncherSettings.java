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
package fr.onsea.aeisonlauncher.settings;

import java.io.File;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Seynax
 *
 */
@Getter
public class AeisonLauncherSettings extends SettingsLoader
{
	private Setting			remoteUrl;
	private Setting			relativeLocalPath;
	private Setting			localPath;

	private Setting			libPath;
	private Setting			relativeSourcesFolder;
	private Setting			sourcesFolder;

	private Setting			classesPath;
	private Setting			sourcesPath;
	private @Setter Setting	modulesPath;

	private Setting			javaPath;
	private Setting			javaBinPath;

	private Setting			operationFolder;
	private Setting			backupFolder;
	private Setting			tempFolder;
	private Setting			compilationFolder;
	private Setting			groupingFolder;
	private Setting			outputFolder;

	private Setting			jarOutputPath;
	private Setting			mainClass;
	private Setting			excludedLibrairiesForExtraction;	// separate by ;

	public AeisonLauncherSettings(final String filePathIn, final List<String> argumentsIn) throws Exception
	{
		super(filePathIn);
		this.addArguments(argumentsIn);
		this.modulesPath = new Setting("MODULES_PATH", null);
	}

	@Override
	public SettingsLoader reload() throws Exception
	{
		this.remoteUrl							= this.loadAndReplaceQuotes("REMOTE_URL", true);
		this.relativeLocalPath					= this.loadAndReplaceQuotes("RELATIVE_LOCAL_PATH", true);
		this.localPath							= this.loadAndReplaceQuotes("LOCAL_PATH",
				new File(this.relativeLocalPath.value()).getAbsolutePath());
		this.libPath							= this.loadAndReplaceQuotes("LIB_PATH",
				this.localPath + "\\librairies");
		this.relativeSourcesFolder				= this.loadAndReplaceQuotes("RELATIVE_SOURCES_FOLDER",
				this.localPath + "\\librairies");
		this.sourcesFolder						= this.loadAndReplaceQuotes("SOURCES_FOLDER",
				this.localPath + "\\" + this.relativeSourcesFolder);
		this.classesPath						= this.load("CLASSES_PATH", null);
		this.sourcesPath						= this.load("SOURCES_PATH", null);
		this.javaPath							= this.loadAndReplaceQuotes("JAVA_PATH", null);
		this.javaBinPath						= this.loadAndReplaceQuotes("JAVA_BIN_PATH",
				this.javaPath.value() != null ? this.javaPath.value() + "\\bin" : null);
		this.operationFolder					= this.loadAndReplaceQuotes("OPERATION_FOLDER",
				this.localPath + "\\target\\generated_sources");
		this.backupFolder						= this.loadAndReplaceQuotes("BACKUP_FOLDER",
				this.operationFolder + "\\backup");
		this.tempFolder							= this.loadAndReplaceQuotes("TEMP_FOLDER",
				this.operationFolder + "\\tmp");
		this.compilationFolder					= this.loadAndReplaceQuotes("COMPILATION_FOLDER",
				this.operationFolder + "\\compiled");
		this.groupingFolder						= this.loadAndReplaceQuotes("GROUPING_FOLDER",
				this.operationFolder + "\\grouped");
		this.outputFolder						= this.loadAndReplaceQuotes("OUTPUT_FOLDER",
				this.operationFolder + "\\output");
		this.jarOutputPath						= this.loadAndReplaceQuotes("JAR_OUTPUT_PATH",
				this.outputFolder + "\\Output.jar");
		this.mainClass							= this.load("MAIN_CLASS", null);
		this.excludedLibrairiesForExtraction	= this.loadAndReplaceQuotes("EXCLUDED_LIBRAIRIES_FOR_EXTRACTION", "");

		this.updateTags();

		return this;
	}

	private final Setting loadAndReplaceQuotes(final String keyIn, final boolean canAskUserIn) throws Exception
	{
		final var setting = this.load(keyIn, canAskUserIn);

		if (setting.value() != null)
		{
			setting.value(setting.value().replace("\"", ""));
		}

		return setting;
	}

	private final Setting loadAndReplaceQuotes(final String keyIn, final String defaultValueIn) throws Exception
	{
		final var setting = this.load(keyIn, defaultValueIn);

		if (setting.value() != null)
		{
			setting.value(setting.value().replace("\"", ""));
		}

		return setting;
	}

	public final void updateTags()
	{
		for (final var entry : this.settings.entrySet())
		{
			SettingsLoader.replaceKeys(entry.getValue(), this.settings);
		}
	}

	@Override
	public String toString()
	{
		return "AeisonLauncherSettings [remoteUrl=" + this.remoteUrl + ", localPath=" + this.localPath
				+ ", classesPath=" + this.classesPath + ", sourcesPath=" + this.sourcesPath + ", modulesPath="
				+ this.modulesPath + ", javaPath=" + this.javaPath + ", javaBinPath=" + this.javaBinPath
				+ ", operationFolder=" + this.operationFolder + ", tempFolder=" + this.tempFolder
				+ ", compilationFolder=" + this.compilationFolder + ", groupingFolder=" + this.groupingFolder
				+ ", outputFolder=" + this.outputFolder + ", jarOutputPath=" + this.jarOutputPath + ", mainClass="
				+ this.mainClass + ", excludedLibrairiesForExtraction=" + this.excludedLibrairiesForExtraction + "]";
	}
}