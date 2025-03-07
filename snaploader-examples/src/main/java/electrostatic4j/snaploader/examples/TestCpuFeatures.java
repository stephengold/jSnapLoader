/*
 * Copyright (c) 2025, The Electrostatic-Sandbox Distributed Simulation Framework, jSnapLoader
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'AvrSandbox' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package electrostatic4j.snaploader.examples;

import com.github.stephengold.joltjni.Jolt;
import electrostatic4j.snaploader.LibraryInfo;
import electrostatic4j.snaploader.LoadingCriterion;
import electrostatic4j.snaploader.NativeBinaryLoader;
import electrostatic4j.snaploader.filesystem.DirectoryPath;
import electrostatic4j.snaploader.platform.NativeDynamicLibrary;
import electrostatic4j.snaploader.platform.util.NativeVariant;
import electrostatic4j.snaploader.platform.util.PlatformPredicate;

/**
 * Tests selection between native libraries based on CPU features.
 *
 * @author Stephen Gold sgold@sonic.net
 */
public final class TestCpuFeatures {

    public static void main(String[] argv) {
        // Test for each of the relevant CPU features:
        System.out.println("avx    = " + NativeVariant.Cpu.hasExtensions("avx"));
        System.out.println("avx2   = " + NativeVariant.Cpu.hasExtensions("avx2"));
        System.out.println("bmi1   = " + NativeVariant.Cpu.hasExtensions("bmi1"));
        System.out.println("f16c   = " + NativeVariant.Cpu.hasExtensions("f16c"));
        System.out.println("fma    = " + NativeVariant.Cpu.hasExtensions("fma"));
        System.out.println("sse4_1 = " + NativeVariant.Cpu.hasExtensions("sse4_1"));
        System.out.println("sse4_2 = " + NativeVariant.Cpu.hasExtensions("sse4_2"));

        // Define a custom predicate for Linux with all 7 CPU features:
        PlatformPredicate linuxWithFma = new PlatformPredicate(
                PlatformPredicate.LINUX_X86_64,
                "avx", "avx2", "bmi1", "f16c", "fma", "sse4_1", "sse4_2");
        System.out.println("linuxWithFma    = " + linuxWithFma.evaluatePredicate());

        // Define a custom predicate for Windows with 4 CPU features:
        PlatformPredicate windowsWithAvx2 = new PlatformPredicate(
                PlatformPredicate.WIN_X86_64,
                "avx", "avx2", "sse4_1", "sse4_2");
        System.out.println("windowsWithAvx2 = " + windowsWithAvx2.evaluatePredicate());
        System.out.flush();

        LibraryInfo info = new LibraryInfo(
                new DirectoryPath("linux/x86-64/com/github/stephengold"),
                "joltjni", DirectoryPath.USER_DIR);
        NativeBinaryLoader loader = new NativeBinaryLoader(info);
        NativeDynamicLibrary[] libraries = {
            new NativeDynamicLibrary("linux/x86-64-fma/com/github/stephengold", linuxWithFma), // must precede vanilla LINUX_X86_64
            new NativeDynamicLibrary("linux/x86-64/com/github/stephengold", PlatformPredicate.LINUX_X86_64),
            new NativeDynamicLibrary("windows/x86-64-avx2/com/github/stephengold", windowsWithAvx2), // must precede vanilla WIN_X86_64
            new NativeDynamicLibrary("windows/x86-64/com/github/stephengold", PlatformPredicate.WIN_X86_64)
        };
        loader.registerNativeLibraries(libraries).initPlatformLibrary();
        loader.setLoggingEnabled(true);
        loader.setRetryWithCleanExtraction(true);
        try {
            loader.loadLibrary(LoadingCriterion.INCREMENTAL_LOADING);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load the joltjni library!");
        }
        System.err.flush();

        // Invoke native code to obtain the configuration of the native library.
        String configuration = Jolt.getConfigurationString();
        /*
         * Depending which native library was loaded, the configuration string
         * should be one of the following:
         *
         * On LINUX_X86_64 platforms, either
         *  Single precision x86 64-bit with instructions: SSE2 SSE4.1 SSE4.2 AVX AVX2 F16C LZCNT TZCNT FMADD (Debug Renderer) (16-bit ObjectLayer) (Assertions) (ObjectStream) (Debug) (C++ RTTI) (C++ Exceptions)
         * or
         *  Single precision x86 64-bit with instructions: SSE2 (Debug Renderer) (16-bit ObjectLayer) (Assertions) (ObjectStream) (Debug) (C++ RTTI) (C++ Exceptions)
         *
         * On WIN_X86_64 platforms, either
         *  Single precision x86 64-bit with instructions: SSE2 SSE4.1 SSE4.2 AVX AVX2 F16C LZCNT TZCNT (FP Exceptions) (Debug Renderer) (16-bit ObjectLayer) (Assertions) (ObjectStream) (Debug) (C++ RTTI) (C++ Exceptions)
         * or
         *  Single precision x86 64-bit with instructions: SSE2 (FP Exceptions) (Debug Renderer) (16-bit ObjectLayer) (Assertions) (ObjectStream) (Debug) (C++ RTTI) (C++ Exceptions)
         */
        System.out.println(configuration);
    }
}
