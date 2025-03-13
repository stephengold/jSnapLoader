/*
 * Copyright (c) 2023-2025, The Electrostatic-Sandbox Distributed Simulation Framework, jSnapLoader
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
 * * Neither the name of 'Electrostatic-Sandbox' nor the names of its contributors
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

package electrostatic4j.snaploader;

/**
 * Represents an extraction/loading criterion type.
 * 
 * @author pavl_g
 */
public enum LoadingCriterion {
    
    /**
     * Extracts the native binary to the extraction directory, ignoring its existence (the newly extracted binary will replace the current file).
     */
    CLEAN_EXTRACTION, 
    
    /**
     * Extracts the native binary only if the current binary isn't present on the extraction directory.
     */
    INCREMENTAL_LOADING,

    /**
     * Commands to load a native dynamic library from the system directories.
     * <p>
     * This criterion instructs the loader to search for the native library in predefined
     * system locations, relying on the operating system's dynamic linker to resolve the library.
     * The library must be pre-installed and accessible through standard system paths.
     * </p>
     *
     * <h3>System Library Search Paths</h3>
     * <p>The specific directories searched depend on the operating system:</p>
     * <ul>
     *   <li><b>Linux:</b> Searches in:
     *     <ul>
     *       <li>Directories specified in {@code LD_LIBRARY_PATH}.</li>
     *       <li>System-wide library directories:
     *         <ul>
     *           <li>{@code /lib}</li>
     *           <li>{@code /usr/lib}</li>
     *           <li>{@code /usr/local/lib}</li>
     *           <li>Paths configured in {@code /etc/ld.so.conf} and {@code /etc/ld.so.conf.d/}.</li>
     *         </ul>
     *       </li>
     *     </ul>
     *   </li>
     *   <li><b>Windows:</b> Searches in:
     *     <ul>
     *       <li>Directories listed in the {@code PATH} environment variable.</li>
     *       <li>System directories:
     *         <ul>
     *           <li>{@code C:\Windows\System32} (for 64-bit DLLs)</li>
     *           <li>{@code C:\Windows\SysWOW64} (for 32-bit DLLs on 64-bit Windows)</li>
     *           <li>Current working directory of the running process.</li>
     *         </ul>
     *       </li>
     *     </ul>
     *   </li>
     *   <li><b>macOS:</b> Searches in:
     *     <ul>
     *       <li>Directories specified in {@code DYLD_LIBRARY_PATH}.</li>
     *       <li>System library locations:
     *         <ul>
     *           <li>{@code /usr/lib}</li>
     *           <li>{@code /usr/local/lib}</li>
     *           <li>{@code /System/Library/Frameworks/} (for framework-based libraries).</li>
     *         </ul>
     *       </li>
     *     </ul>
     *   </li>
     *   <li><b>Android:</b> Searches in:
     *     <ul>
     *       <li>Application-specific native library directories:
     *         <ul>
     *           <li>{@code /data/data/<package>/lib}</li>
     *           <li>Native library folders inside the APK:
     *             <ul>
     *               <li>{@code /lib/armeabi-v7a/}</li>
     *               <li>{@code /lib/arm64-v8a/}</li>
     *               <li>{@code /lib/x86/}</li>
     *               <li>{@code /lib/x86_64/}</li>
     *             </ul>
     *           </li>
     *         </ul>
     *       </li>
     *       <li>System native library locations:
     *         <ul>
     *           <li>{@code /system/lib} (for 32-bit libraries).</li>
     *           <li>{@code /system/lib64} (for 64-bit libraries).</li>
     *         </ul>
     *       </li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <h3>Usage Considerations</h3>
     * <p>
     * This approach requires the library to be present on the system beforehand.
     * If the library is missing, the loading process will fail with an {@code UnsatisfiedLinkError}.
     * To ensure compatibility across different systems, consider providing a fallback
     * mechanism to extract the library dynamically when needed via {@link NativeBinaryLoadingListener#onLoadingFailure(NativeBinaryLoader)}.
     * </p>
     */
    SYSTEM_LOAD
}
