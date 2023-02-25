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

import java.io.File;

import fr.onsea.aeisonlauncher.git.GitManager;
import fr.onsea.aeisonlauncher.utils.JarBuilder;
import fr.onsea.aeisonlauncher.utils.ProcessRun;

/**
 * @Organization Onsea
 * @author Seynax
 *
 */
public class AeisonLauncher
{
	private static final String	REMOTE_URL		= "https://github.com/seynax/Onsiea";
	private static final String	LOCAL_PATH		= "J:\\Programmations\\java\\git\\OnsieaEngine";

	private final static String	MODULES_PATH	= "\"J:\\Programmations\\java\\eclipse\\moteurs\\OnsieaEngine2\\common;J:\\Programmations\\java\\eclipse\\tools\\OnseaLogger\\bin;J:\\Programmations\\java\\librairies\\archives\\Lombok\\lombok-1.18.26.jar;J:\\Programmations\\java\\librairies\\mslinks-1.1.0.jar;J:\\Programmations\\java\\librairies\\JNA\\jna-5.5.0.jar;J:\\Programmations\\java\\librairies\\JNA\\jna-platform-5.5.0.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl3-awt-0.1.8.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-freetype.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-jemalloc.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjglx-debug-1.0.0.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-nanovg.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-egl.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-vma.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-assimp.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-harfbuzz.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-par.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-stb.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-openal.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-zstd.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-vulkan.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-yoga.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-glfw.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-openxr.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-llvm.jar;J:\\Programmations\\java\\librairies\\LWJGL\\steamworks4j-1.9.0.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-cuda.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-meshoptimizer.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-ovr.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-lmdb.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-remotery.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-opus.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-xxhash.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-nuklear.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-spvc.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-sse.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-fmod.jar;J:\\Programmations\\java\\librairies\\LWJGL\\steamworks4j-server-1.9.0.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-odbc.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-bgfx.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-jawt.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-shaderc.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-tootle.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-tinyfd.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-opencl.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-opengl.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-lz4.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-libdivide.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-openvr.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-opengles.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-tinyexr.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-nfd.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-meow.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-ktx.jar;J:\\Programmations\\java\\librairies\\LWJGL\\joml-1.10.5.jar;J:\\Programmations\\java\\librairies\\LWJGL\\lwjgl-rpmalloc.jar\"";
	private final static String	CLASSES_PATH	= "\"J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-vma-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-opengl-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-nuklear-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-glfw-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-tootle-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-ovr-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-stb-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-opus-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-assimp-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-opengles-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-nanovg-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-xxhash-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-remotery-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-meshoptimizer-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-tinyfd-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-yoga-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-lmdb-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-nfd-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-meow-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-openvr-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-lz4-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-rpmalloc-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-spvc-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-freetype-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-llvm-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-openal-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-harfbuzz-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-shaderc-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-bgfx-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-tinyexr-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-openxr-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-jemalloc-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-sse-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-zstd-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-ktx-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-libdivide-natives-windows.jar;J:\\Programmations\\java\\librairies\\LWJGL\\natives\\lwjgl-par-natives-windows.jar\"";
	private final static String	SOURCES_PATH	= "\"sources\\client;sources\\common;sources\\core;sources\\game;sources\\server;sources\\utils;\"";

	public final static void main(final String[] argsIn)
	{
		// then clone

		try
		{
			final var	gitManager		= new GitManager(AeisonLauncher.REMOTE_URL, AeisonLauncher.LOCAL_PATH);

			final var	javacCommand	= new JarBuilder("sources\\game\\fr\\onsiea\\engine\\game\\GameTest.java",
					"C:\\Program Files\\Java\\jdk-19\\bin\\").additions("--target 19 -encoding UTF-8 -verbose -g")
					.implicitClass()
					.processor("J:\\Programmations\\java\\librairies\\archives\\Lombok\\lombok-1.18.26.jar")
					.modules("jdk.incubator.vector").modulesPath(AeisonLauncher.MODULES_PATH)
					.classesPath(AeisonLauncher.CLASSES_PATH).sourcesPath(AeisonLauncher.SOURCES_PATH)
					.outputPath("bin2\\").buildJavacCommand();

			System.out.println("Launch javac command : ");
			System.out.println(javacCommand);
			ProcessRun.run(new ProcessBuilder(javacCommand).directory(new File(AeisonLauncher.LOCAL_PATH)));
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

}