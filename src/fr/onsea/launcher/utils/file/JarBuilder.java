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

import java.io.File;

/**
 * @Organization Onsea
 * @author Seynax
 *
 */
public class JarBuilder
{
	private String	javaPath;

	private String	modules;
	private String	modulesPath;
	private String	classesPath;
	private String	sourcesPath;

	private String	processor;

	private String	outputPath;

	private String	additions;
	private boolean	implicitClass;

	private String	sourceInputs;

	public JarBuilder(final String sourceInputsIn)
	{
		this.defineSources(sourceInputsIn);
	}

	public JarBuilder(final String sourceInputsIn, final String javaPathIn)
	{
		this.defineSources(sourceInputsIn);
		this.javaPath = javaPathIn;
	}

	public final JarBuilder defineSources(final String sourceInputsIn)
	{
		if (sourceInputsIn.endsWith(".java") || sourceInputsIn.startsWith("@"))
		{
			this.sourceInputs = sourceInputsIn;
		}
		else
		{
			this.sourceInputs = "";
			this.addAllSources(new File(sourceInputsIn));

			if (this.sourceInputs.endsWith(" "))
			{
				this.sourceInputs = this.sourceInputs.substring(0, this.sourceInputs.length() - 2);
			}
		}

		return this;
	}

	public final JarBuilder addAllSources(final File rootIn)
	{
		FileUtils.recurse(rootIn, fileIn -> this.sourceInputs += "\"" + fileIn.getAbsolutePath() + "\" ");

		return this;
	}

	public final JarBuilder argument(final String keyIn, final String contentIn)
	{
		if (this.additions != null)
		{
			this.additions += " " + keyIn + " " + contentIn;
		}
		else
		{
			this.additions = keyIn + " " + contentIn;
		}

		return this;
	}

	public final JarBuilder javaPath(final String javaPathIn)
	{
		this.javaPath = javaPathIn;

		return this;
	}

	public final JarBuilder processor(final String processorPathIn)
	{
		this.processor = "-proc:only -processorpath \"" + processorPathIn + "\"";

		return this;
	}

	public final JarBuilder sourceInputs(final String sourceInputsIn)
	{
		this.sourceInputs = sourceInputsIn;

		return this;
	}

	public final JarBuilder modules(final String modulesIn)
	{
		this.modules = modulesIn;

		return this;
	}

	public final JarBuilder modulesPath(final String modulesPathIn)
	{
		this.modulesPath = modulesPathIn;

		return this;
	}

	public final JarBuilder classesPath(final String classesPathIn)
	{
		this.classesPath = classesPathIn;

		return this;
	}

	public final JarBuilder sourcesPath(final String sourcesPathIn)
	{
		this.sourcesPath = sourcesPathIn;

		return this;
	}

	public final JarBuilder outputPath(final String outputPathIn)
	{
		this.outputPath = outputPathIn;

		return this;
	}

	public final JarBuilder additions(final String additionsIn)
	{
		this.additions = additionsIn;

		return this;
	}

	public final JarBuilder implicitClass()
	{
		this.implicitClass = true;

		return this;
	}

	public final JarBuilder implicitNone()
	{
		this.implicitClass = false;

		return this;
	}

	public final String buildJavacCommand() throws Exception
	{
		if (this.sourceInputs == null || this.sourceInputs.isEmpty() || this.sourceInputs.isBlank()
				|| this.sourceInputs.matches("\\s+"))
		{
			throw new Exception("[ERROR] JarInfoBuilder : main path is necessary !");
		}
		String javacCommand;
		if (this.javaPath == null || this.javaPath.isEmpty() || this.javaPath.isBlank()
				|| this.javaPath.matches("\\s+"))
		{
			javacCommand = "javac";
		}
		else
		{
			javacCommand = "\"" + this.javaPath + "\\" + "javac.exe\"";
		}

		javacCommand += " " + this.sourceInputs;

		if (this.additions != null)
		{
			javacCommand += " " + this.additions;
		}

		if (this.implicitClass)
		{
			javacCommand += " -implicit:class";
		}
		else
		{
			javacCommand += " -implicit:none";
		}

		if (this.processor != null)
		{
			javacCommand += " " + this.processor;
		}

		if (this.modules != null)
		{
			javacCommand += " --add-modules " + this.modules;
		}

		if (this.modulesPath != null)
		{
			javacCommand += " -p " + this.modulesPath;
		}

		if (this.classesPath != null)
		{
			javacCommand += " -classpath " + this.classesPath;
		}

		if (this.sourcesPath != null)
		{
			javacCommand += " -sourcepath " + this.sourcesPath;
		}

		if (this.outputPath != null)
		{
			javacCommand += " -d \"" + this.outputPath + "\"";
		}

		return javacCommand;
	}

	public final static String buildJarCommand()
	{
		return null;
	}
}