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

package electrostatic4j.snaploader.platform.util;

/**
 * Wraps a platform-specific predicate; that if all of its propositions evaluated
 * as true, the respective platform library will be assigned to be extracted
 * and loaded by the loader object in-command.
 *
 * @author pavl_g
 */
public final class PlatformPredicate {

    /**
     * Alias object for Linux on X86 Desktop Chipset.
     */
    public static final PlatformPredicate LINUX_X86 = new PlatformPredicate(NativeVariant.Os.isDesktop() &&
            NativeVariant.Os.isLinux() && NativeVariant.Cpu.isX86());

    /**
     * Alias object for Android all variants (i.e., x86, AARCH64, ARM32)
     * when using {@link Runtime#loadLibrary(String)}.
     */
    public static final PlatformPredicate ANDROID = new PlatformPredicate(NativeVariant.Os.isAndroid());

    /**
     * Alias object for Linux on X86-64 Desktop Chipset.
     */
    public static final PlatformPredicate LINUX_X86_64 = new PlatformPredicate(NativeVariant.Os.isDesktop() &&
            NativeVariant.Os.isLinux() && NativeVariant.Cpu.isAMD() && NativeVariant.Cpu.is64());

    /**
     * Alias object for Linux on arm-32 Desktop Chipset.
     */
    public static final PlatformPredicate LINUX_ARM_32 = new PlatformPredicate(NativeVariant.Os.isDesktop() &&
            NativeVariant.Os.isLinux() && NativeVariant.Cpu.isARM());

    /**
     * Alias object for Linux on arm-64 Desktop Chipset.
     */
    public static final PlatformPredicate LINUX_ARM_64 = new PlatformPredicate(NativeVariant.Os.isDesktop() &&
            NativeVariant.Os.isLinux() && NativeVariant.Cpu.isARM() && NativeVariant.Cpu.is64());

    /**
     * Alias object for Linux on RiscV-32 Chipset.
     */
    public static final PlatformPredicate LINUX_RISC_V_32 = new PlatformPredicate(NativeVariant.Os.isLinux() && NativeVariant.Cpu.isRiscV());

    /**
     * Alias object for Linux on RiscV-64 Chipset.
     */
    public static final PlatformPredicate LINUX_RISC_V_64 = new PlatformPredicate(NativeVariant.Os.isLinux() && NativeVariant.Cpu.isRiscV() && NativeVariant.Cpu.is64());

    /**
     * Alias object for MacOSX on X86 Chipset.
     */
    public static final PlatformPredicate MACOS_X86 = new PlatformPredicate(NativeVariant.Os.isMac() && NativeVariant.Cpu.isX86());

    /**
     * Alias object for MacOSX on X86-64 Chipset.
     */
    public static final PlatformPredicate MACOS_X86_64 = new PlatformPredicate(NativeVariant.Os.isMac() && NativeVariant.Cpu.isX86() && NativeVariant.Cpu.is64());

    /**
     * Alias object for MacOSX on arm-32 Chipset.
     */
    public static final PlatformPredicate MACOS_ARM_32 = new PlatformPredicate(NativeVariant.Os.isMac() && NativeVariant.Cpu.isARM());

    /**
     * Alias object for MacOSX on arm-64 Chipset.
     */
    public static final PlatformPredicate MACOS_ARM_64 = new PlatformPredicate(NativeVariant.Os.isMac() && NativeVariant.Cpu.isARM() && NativeVariant.Cpu.is64());

    /**
     * Alias object for Windows on X86 Chipset.
     */
    public static final PlatformPredicate WIN_X86 = new PlatformPredicate(NativeVariant.Os.isWindows() && NativeVariant.Cpu.isX86());

    /**
     * Alias object for Windows on X86-64 Chipset.
     */
    public static final PlatformPredicate WIN_X86_64 = new PlatformPredicate(NativeVariant.Os.isWindows() && NativeVariant.Cpu.isAMD() && NativeVariant.Cpu.is64());

    /**
     * Alias object for Windows on arm-32 Chipset.
     */
    public static final PlatformPredicate WIN_ARM_32 = new PlatformPredicate(NativeVariant.Os.isWindows() && NativeVariant.Cpu.isARM());

    /**
     * Alias object for Windows on arm-64 Chipset.
     */
    public static final PlatformPredicate WIN_ARM_64 = new PlatformPredicate(NativeVariant.Os.isWindows() && NativeVariant.Cpu.isARM() && NativeVariant.Cpu.is64());

    /**
     * Alias object for Windows on RiscV-32 Chipset.
     */
    public static final PlatformPredicate WIN_RISC_V_32 = new PlatformPredicate(NativeVariant.Os.isWindows() && NativeVariant.Cpu.isRiscV());

    /**
     * Alias object for Windows on RiscV-64 Chipset.
     */
    public static final PlatformPredicate WIN_RISC_V_64 = new PlatformPredicate(NativeVariant.Os.isWindows() && NativeVariant.Cpu.isRiscV());

    private final boolean predicate;

    /**
     * Instantiates a platform-specific predicate object
     * that wraps a predicate composed of multiple
     * propositions appended by logical operations.
     *
     * @param predicate a raw boolean predicate to evaluate against
     */
    public PlatformPredicate(boolean predicate) {
        this.predicate = predicate;
    }

    /**
     * Instantiates a predicate object that combines a pre-existing predicate
     * with one or more instruction-set extensions.  The result is true if and
     * only if the base predicate is true and all named extensions are present.
     *
     * @param base          a pre-existing predicate (not null)
     * @param isaExtensions names of required ISA extensions
     */
    public PlatformPredicate(PlatformPredicate base, String... isaExtensions) {
        this.predicate = base.evaluatePredicate()
                && NativeVariant.Cpu.hasExtensions(isaExtensions);
    }

    /**
     * Evaluate the propositions of the predefined platform-predicate.
     *
     * @return true if the
     */
    public boolean evaluatePredicate() {
        return predicate;
    }
}
