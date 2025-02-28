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

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

/**
 * Wraps objects for native variant constituents (OS + ARCH={CPU + INSTRUCT_SET} + VM).
 *
 * <p>
 * Use the following list to build your platform predicates:
 * <ul>
 * <li> x86: 32-bit x86 architecture </li>
 * <li> x86_64: 64-bit x86 architecture, often referred to as amd64 </li>
 * <li> amd64: Another name for x86_64 </li>
 * <li> i386: Another designation for 32-bit x86 architecture </li>
 * <li> arm: ARM architecture </li>
 * <li> aarch64: 64-bit ARM architecture </li>
 * <li> sparc: SPARC architecture </li>
 * <li> sparcv9: 64-bit SPARC architecture </li>
 * <li> ppc: PowerPC architecture </li>
 * <li> ppc64: 64-bit PowerPC architecture </li>
 * <li> ppc64le: 64-bit PowerPC architecture, little-endian </li>
 * <li> s390: IBM System/390 architecture </li>
 * <li> s390x: 64-bit IBM System/390 architecture </li>
 * <li> riscv32: 32-bit RISC-V architecture </li>
 * <li> riscv64: 64-bit RISC-V architecture </li>
 * </ul>
 *
 * @author pavl_g
 */
public enum NativeVariant {
    
    /**
     * The Operating system name property for this variant.
     */
    OS_NAME(System.getProperty("os.name")),
    
    /**
     * The Operating system architecture.
     */
    OS_ARCH(System.getProperty("os.arch")),
    
    /**
     * The current java virtual machine.
     */
    JVM(System.getProperty("java.vm.name"));
    
    private static final String Linux = "Linux";
    private static final String Windows = "Windows";
    private static final String Mac = "Mac";
    private static final String Dalvik = "Dalvik";

    private final String property;

    NativeVariant(final String property) {
        this.property = property;
    }

    /**
     * A namespace class exposing the Operating System Propositions.
     */
    public static final class Os {
        private Os() {
        }
        /**
         * Tests whether the current system is a Linux.
         *
         * @return true if the current OS is a Linux, false otherwise.
         */
        public static boolean isLinux() {
            return NativeVariant.OS_NAME.getProperty().contains(NativeVariant.Linux);
        }

        /**
         * Tests whether the current system is a Windows.
         *
         * @return true if the current OS is a Windows, false otherwise.
         */
        public static boolean isWindows() {
            return NativeVariant.OS_NAME.getProperty().contains(NativeVariant.Windows);
        }

        /**
         * Tests whether the current system is a Mac.
         *
         * @return true if the current OS is a Mac, false otherwise.
         */
        public static boolean isMac() {
            return NativeVariant.OS_NAME.getProperty().contains(NativeVariant.Mac);
        }

        /**
         * Tests whether the current system is an Android.
         *
         * @return true if the current OS is an Android, false otherwise.
         */
        public static boolean isAndroid() {
            return JVM.getProperty().contains(NativeVariant.Dalvik);
        }
    }

    /**
     * A namespace class exposing the CPU propositions.
     */
    public static final class Cpu {
        private Cpu() {
        }

        /**
         * Tests whether the current environment is running
         * on a RISC-V (reduced instruction-set) CPU, typically plain RISC-V means 32-bit, and
         * with the added predicate {@link Cpu#is64()} adds the 64-bit
         * predicate.
         *
         * @return true if the current runtime is operating on a RISC-V
         */
        public static boolean isRiscV() {
            return JVM.getProperty().contains("riscv");
        }

        /**
         * Tests whether the current runtime environment is operating
         * on a Sparc CPU, typically plain Sparc means 32-bit.
         *
         * @return true if the current runtime is operating on a Sparc.
         * @see Cpu#isSparcV9()
         */
        public static boolean isSparc() {
            return JVM.getProperty().contains("sparc");
        }

        /**
         * Tests whether the current runtime environment is operating
         * on a SparcV9 CPU, the typical 64-bit version of the Sparc CPU.
         *
         * @return true if the current runtime environment is running on a SparcV9.
         * @see Cpu#isSparc()
         */
        public static boolean isSparcV9() {
            return JVM.getProperty().contains("sparcv9");
        }

        /**
         * Tests whether the current runtime environment is operating
         * on a PowerPc CPU, typically Ppc only means 32-bit.
         *
         * @return true if the current runtime environment is operating on a PPC.
         * @see Cpu#isPpc64le()
         */
        public static boolean isPpc() {
            return JVM.getProperty().contains("ppc");
        }

        /**
         * Tests whether the current runtime environment is operating
         * on a PowerPc CPU, typically the 64-bit version with the little
         * endian byte-order (i.e., le architecture).
         *
         * @return true if the current runtime environment is operating on a PPC-64-bit-le.
         * @see Cpu#isPpc()
         */
        public static boolean isPpc64le() {
            return JVM.getProperty().contains("ppc64le");
        }

        /**
         * Tests whether the current runtime environment is operating on an
         * IBM System/390 CPU, typically plain s390 means the 32-bit version.
         *
         * @return true if the current runtime environment is operating on an IBM System/390.
         * @see Cpu#isS390x()
         */
        public static boolean isS390() {
            return JVM.getProperty().contains("s390");
        }

        /**
         * Tests whether the current runtime environment is operating on an
         * IBM System/390 CPU, typically the 64-bit version.
         *
         * @return true if the current runtime environment is operating on an IBM System/390-64-bit.
         * @see Cpu#isS390()
         */
        public static boolean isS390x() {
            return JVM.getProperty().contains("s390x");
        }

        /**
         * Tests whether the current system architecture is a 64-bit chipset
         * (NOT APPLICABLE TO ALL CPUs).
         *
         * @return true if the current OS architecture is a 64-bit chipset, false otherwise.
         */
        public static boolean is64() {
            return OS_ARCH.getProperty().contains("64");
        }

        /**
         * Tests whether the current system architecture is of an x86 chipset, typically 32-bit intel chipsets.
         *
         * @return true if the current OS architecture is of an x86 chipset, false otherwise.
         */
        public static boolean isX86() {
            return OS_ARCH.getProperty().contains("x86") || OS_ARCH.getProperty().contains("i386");
        }

        /**
         * Tests whether the current CPU vendor is an AMD vendor (e.g., x86_64 Intel Chipset).
         *
         * @return true if the current CPU vendor is an AMD vendor.
         */
        public static boolean isAMD() {
            return OS_ARCH.getProperty().contains("amd");
        }

        /**
         * Tests whether the current CPU vendor is an ARM vendor (e.g., Broadcom Chipset).
         *
         * @return true if the current CPU vendor is an ARM vendor.
         */
        public static boolean isARM() {
            return OS_ARCH.getProperty().contains("arm") || OS_ARCH.getProperty().contains("aarch");
        }

        /**
         * Cache the names of instruction-set architecture (ISA) extensions that
         * are present.
         */
        private static final Collection<String> extNameCache = new TreeSet<>();

        /**
         * Lazily initializes the collection of ISA extensions that are present.
         */
        private static synchronized void initializeExtNameCache() {
            if (extNameCache.isEmpty()) {
                // Obtain the list of CPU features from OSHI:
                SystemInfo si = new SystemInfo();
                HardwareAbstractionLayer hal = si.getHardware();
                CentralProcessor cpu = hal.getProcessor();
                List<String> strings = cpu.getFeatureFlags();

                for (String string : strings) {
                    String lcString = string.toLowerCase(Locale.ROOT);

                    // Matches to test for interesting X86 ISA extensions:
                    if (lcString.matches(".*\\bavx\\b.*")) {
                        extNameCache.add("avx");
                    }
                    if (lcString.matches(".*\\bavx2\\b.*")) {
                        extNameCache.add("avx2");
                    }
                    if (lcString.matches(".*\\bbmi1\\b.*")) {
                        extNameCache.add("bmi1");
                    }
                    if (lcString.matches(".*\\bf16c\\b.*")) {
                        extNameCache.add("f16c");
                    }
                    if (lcString.matches(".*\\bfma\\b.*")) {
                        extNameCache.add("fma");
                    }
                    if (lcString.matches(".*\\bsse4_1\\b.*")) {
                        extNameCache.add("sse4_1");
                    }
                    if (lcString.matches(".*\\bsse4_2\\b.*")) {
                        extNameCache.add("sse4_2");
                    }
                }
                /*
                 * Add an empty string so that the name cache
                 * is guaranteed to no longer be empty. This ensures
                 * that getFeatureFlags() is invoked at most once.
                 */
                extNameCache.add("");
            }
        }

        /**
         * Tests whether the named ISA extensions are all present.
         *
         * @param names the names of the extensions to test for
         * @return {@code true} if all are present, otherwise {@code false}
         */
        public static boolean hasExtensions(String... names) {
            initializeExtNameCache();

            for (String name : names) {
                String lcName = name.toLowerCase(Locale.ROOT);
                boolean isPresent = extNameCache.contains(lcName);
                if (!isPresent) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Retrieves the data of this native variant property.
     * 
     * @return the specified property object in a string format.
     */
    public String getProperty() {
        return property;
    }
}
