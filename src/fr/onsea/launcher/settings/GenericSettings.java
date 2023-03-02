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
package fr.onsea.launcher.settings;

import java.io.File;

import fr.onsea.launcher.settings.arguments.ArgumentsLoader;
import fr.onsea.launcher.settings.setting.StringSetting;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Seynax
 *
 */
@Getter
public class GenericSettings extends Settings
{
	private StringSetting			remoteUrl;
	protected StringSetting			relativeLocalPath;
	protected StringSetting			localPath;

	private StringSetting			libPath;
	private StringSetting			relativeSourcesFolder;
	private StringSetting			sourcesFolder;

	private StringSetting			classesPath;
	private StringSetting			sourcesPath;
	private @Setter StringSetting	modulesPath;

	protected StringSetting			javaPath;
	protected StringSetting			javaBinPath;
	protected StringSetting			javaExePath;

	protected StringSetting			mainClass;

	private StringSetting			operationFolder;
	private StringSetting			backupFolder;
	private StringSetting			tempFolder;
	private StringSetting			compilationFolder;
	private StringSetting			groupingFolder;
	private StringSetting			outputFolder;

	private StringSetting			jarOutputPath;
	private StringSetting			excludedLibrairiesForExtraction;	// separate by ;

	public GenericSettings(final String filePathIn, final ArgumentsLoader argumentsLoaderIn) throws Exception
	{
		super(filePathIn, argumentsLoaderIn);
	}

	public GenericSettings(final String filePathIn) throws Exception
	{
		super(filePathIn);
	}

	@Override
	public Settings reload() throws Exception
	{
		this.relativeLocalPath					= this.strings().load("RELATIVE_LOCAL_PATH");
		this.localPath							= this.strings().load("LOCAL_PATH",
				new File(this.relativeLocalPath.value()).getAbsolutePath());
		this.remoteUrl							= this.strings().load("REMOTE_URL");

		this.libPath							= this.strings().load("LIB_PATH", this.localPath + "\\librairies");
		this.relativeSourcesFolder				= this.strings().load("RELATIVE_SOURCES_FOLDER",
				this.localPath + "\\sources");
		this.sourcesFolder						= this.strings().load("SOURCES_FOLDER",
				this.localPath + "\\" + this.relativeSourcesFolder);
		this.modulesPath						= new StringSetting("MODULES_PATH", null);
		this.classesPath						= this.strings().load("CLASSES_PATH", null);
		this.sourcesPath						= this.strings().load("SOURCES_PATH", null);
		this.javaPath							= this.strings().load("JAVA_PATH", null);
		this.javaBinPath						= this.strings().load("JAVA_BIN_PATH",
				this.javaPath.value() != null ? this.javaPath.value() + "\\bin" : null);
		this.javaExePath						= this.strings().load("JAVA_EXE_PATH",
				this.javaBinPath != null ? this.javaBinPath.value() + "\\java.exe" : "java");
		this.mainClass							= this.strings().load("MAIN_CLASS", null);
		this.operationFolder					= this.strings().load("OPERATION_FOLDER",
				this.localPath + "\\target\\generated_sources");
		this.backupFolder						= this.strings().load("BACKUP_FOLDER",
				this.operationFolder + "\\backup");
		this.tempFolder							= this.strings().load("TEMP_FOLDER", this.operationFolder + "\\tmp");
		this.compilationFolder					= this.strings().load("COMPILATION_FOLDER",
				this.operationFolder + "\\compiled");
		this.groupingFolder						= this.strings().load("GROUPING_FOLDER",
				this.operationFolder + "\\grouped");
		this.outputFolder						= this.strings().load("OUTPUT_FOLDER",
				this.operationFolder + "\\output");
		this.jarOutputPath						= this.strings().load("JAR_OUTPUT_PATH",
				this.outputFolder + "\\Output.jar");
		this.excludedLibrairiesForExtraction	= this.strings().load("EXCLUDED_LIBRAIRIES_FOR_EXTRACTION", "");

		this.updateTags();

		return this;
	}
}