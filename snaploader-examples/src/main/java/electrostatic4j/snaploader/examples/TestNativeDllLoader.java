package electrostatic4j.snaploader.examples;

import electrostatic4j.snaploader.LibraryInfo;
import electrostatic4j.snaploader.LoadingCriterion;
import electrostatic4j.snaploader.examples.api.NativeDllLoader;
import electrostatic4j.snaploader.filesystem.DirectoryPath;
import electrostatic4j.snaploader.platform.NativeDynamicLibrary;
import electrostatic4j.snaploader.platform.util.DefaultDynamicLibraries;
import electrostatic4j.snaploader.platform.util.PlatformPredicate;

public final class TestNativeDllLoader {
    public static void main(String[] args) throws Exception {
        final NativeDynamicLibrary[] baseLibs = new NativeDynamicLibrary[] {
             DefaultDynamicLibraries.ANDROID_ALL,
             new NativeDynamicLibrary("linux/x86-64/com/github/stephengoldd", PlatformPredicate.LINUX_X86_64),
             new NativeDynamicLibrary("windows/x86-64/com/github/stephengoldd", PlatformPredicate.WIN_X86_64),
        };

        final NativeDynamicLibrary[] cpuEnhancedLibs = new NativeDynamicLibrary[]{
            DefaultDynamicLibraries.ANDROID_ALL,
            new NativeDynamicLibrary("linux/x86-64-fma/com/github/stephengold", new PlatformPredicate(PlatformPredicate.LINUX_X86_64,
                                "avx", "avx2", "bmi1", "f16c", "fma", "sse4_1", "sse4_2")),
            new NativeDynamicLibrary("windows/x86-64-avx2/com/github/stephengold", new PlatformPredicate(PlatformPredicate.WIN_X86_64,
                                "avx", "avx2", "sse4_1", "sse4_2")),
        };
        final LibraryInfo info = new LibraryInfo(new DirectoryPath("linux/x86-64/com/github/stephengold"),
                            "joltjnid", DirectoryPath.USER_DIR);
        final NativeDllLoader nativeDllLoader = new NativeDllLoader(baseLibs, cpuEnhancedLibs, info, true, true);
        nativeDllLoader.loadCpuEnhancedLibs(LoadingCriterion.INCREMENTAL_LOADING);
    }
}
