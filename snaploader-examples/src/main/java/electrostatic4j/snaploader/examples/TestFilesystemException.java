package electrostatic4j.snaploader.examples;

import electrostatic4j.snaploader.LibraryInfo;
import electrostatic4j.snaploader.LoadingCriterion;
import electrostatic4j.snaploader.NativeBinaryLoader;
import electrostatic4j.snaploader.filesystem.DirectoryPath;
import electrostatic4j.snaploader.platform.NativeDynamicLibrary;
import electrostatic4j.snaploader.platform.util.DefaultDynamicLibraries;
import electrostatic4j.snaploader.platform.util.NativeVariant;
import electrostatic4j.snaploader.platform.util.PlatformPredicate;
import electrostatic4j.snaploader.platform.util.DefaultPropertiesProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TestFilesystemException {
    public static void main(String[] args) throws Exception {
        final Path compressionPath = Paths.get(DefaultPropertiesProvider.USER_DIR.getSystemProperty(), "libs", TestBasicFeatures.getJarFile());
        final Path extractionPath = Files.createDirectories(Paths.get(DefaultPropertiesProvider.USER_DIR.getSystemProperty(), "libs",
                NativeVariant.OS_NAME.getProperty(), NativeVariant.OS_ARCH.getProperty()));

        final LibraryInfo libraryInfo = new LibraryInfo(new DirectoryPath(compressionPath.toString()), new DirectoryPath("lib/placeholder"),
                "jme3alloc", new DirectoryPath(extractionPath.toString()));

        final NativeDynamicLibrary[] libraries = new NativeDynamicLibrary[] {
                DefaultDynamicLibraries.LINUX_X86,
                DefaultDynamicLibraries.LINUX_X86_64,
                new NativeDynamicLibrary("lib/windows/x86", "libjme3alloc.dll", PlatformPredicate.WIN_X86),
                new NativeDynamicLibrary("lib/windows/x86-64", "libjme3alloc.dll", PlatformPredicate.WIN_X86_64),
                DefaultDynamicLibraries.MAC_X86,
                DefaultDynamicLibraries.MAC_X86_64,
        };

        final NativeBinaryLoader loader = new NativeBinaryLoader(libraryInfo);

        loader.registerNativeLibraries(libraries).initPlatformLibrary();

        loader.setLoggingEnabled(true);
        loader.setRetryWithCleanExtraction(true);
        /* Native dynamic library properties */
        printDetails(loader);
        loader.loadLibrary(LoadingCriterion.INCREMENTAL_LOADING);
    }

    public static void printDetails(NativeBinaryLoader loader) {
        System.out.println("--------------------------------------------------------------");
        System.out.println("OS: " + NativeVariant.OS_NAME.getProperty());
        System.out.println("ARCH: " + NativeVariant.OS_ARCH.getProperty());
        System.out.println("VM: " + NativeVariant.JVM.getProperty());
        System.out.println("--------------------------------------------------------------");
        System.out.println("Jar Path: " + loader.getNativeDynamicLibrary().getJarPath());
        System.out.println("Library Directory: " + loader.getNativeDynamicLibrary().getPlatformDirectory());
        System.out.println("Compressed library path: " + loader.getNativeDynamicLibrary().getCompressedLibrary());
        System.out.println("Extracted library absolute path: " + loader.getNativeDynamicLibrary().getExtractedLibrary());
        System.out.println("Is Extracted: " + loader.getNativeDynamicLibrary().isExtracted());
        System.out.println("--------------------------------------------------------------");
    }
}
