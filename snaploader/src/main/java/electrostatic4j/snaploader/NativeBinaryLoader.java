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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Level;
import electrostatic4j.snaploader.filesystem.FileExtractionListener;
import electrostatic4j.snaploader.filesystem.FileExtractor;
import electrostatic4j.snaploader.filesystem.FileLocalizingListener;
import electrostatic4j.snaploader.filesystem.FileLocator;
import electrostatic4j.snaploader.library.LibraryExtractor;
import electrostatic4j.snaploader.library.LibraryLocator;
import electrostatic4j.snaploader.platform.NativeDynamicLibrary;
import electrostatic4j.snaploader.platform.util.NativeVariant;
import electrostatic4j.snaploader.throwable.LoadingRetryExhaustionException;
import electrostatic4j.snaploader.throwable.UnSupportedSystemError;
import electrostatic4j.snaploader.util.SnapLoaderLogger;

/**
 * A cross-platform utility for extracting and loading native binaries based on
 * the variant properties (OS + ARCH + VM).
 *
 * @author pavl_g.
 */
public class NativeBinaryLoader {

    protected final LibraryInfo libraryInfo;

    protected List<NativeDynamicLibrary> registeredLibraries;

    protected NativeBinaryLoadingListener nativeBinaryLoadingListener;

    protected SystemDetectionListener systemDetectionListener;

    protected FileLocalizingListener libraryLocalizingListener;

    protected FileExtractionListener libraryExtractionListener;

    /**
     * An Output stream concrete provider for library extraction.
     */
    protected FileExtractor libraryExtractor;

    /**
     * The native dynamic library object representing the library to extract and load.
     */
    protected NativeDynamicLibrary nativeDynamicLibrary;

    /**
     * Flag for retry loading with clean extract if UnSatisfiedLinkError is thrown.
     */
    protected boolean retryWithCleanExtraction;

    protected int maxNumberOfLoadingFailure = 2;

    protected int numberOfLoadingFailure = 0;

    /**
     * Instantiates a native dynamic library loader to extract and load a system-specific native dynamic library.
     */
    public NativeBinaryLoader(final LibraryInfo libraryInfo) {
        this.libraryInfo = libraryInfo;
    }

    /**
     * Instantiates a native dynamic library loader to extract and load a system-specific native dynamic library.
     */
    public NativeBinaryLoader(final List<NativeDynamicLibrary> registeredLibraries, final LibraryInfo libraryInfo) {
        this(libraryInfo);
        this.registeredLibraries = registeredLibraries;
    }

    public NativeBinaryLoader registerNativeLibraries(NativeDynamicLibrary[] nativeDynamicLibraries) {
        this.registeredLibraries = Arrays.asList(nativeDynamicLibraries);
        return this;
    }

    /**
     * Initializes the platform-dependent native dynamic library.
     *
     * @return this instance for chained invocations
     * @throws UnSupportedSystemError if the OS is not supported by jSnapLoader
     */
    public NativeBinaryLoader initPlatformLibrary() throws UnSupportedSystemError {
        final boolean[] isSystemFound = new boolean[]{false};
        // search for the compatible library using the predefined predicate
        // a predicate is a conditional statement composed of multiple propositions
        // representing the complete system variant (OS + ARCH + VM).
        registeredLibraries.forEach(nativeDynamicLibrary -> {
            if (isSystemFound[0]) {
                return;
            }
            // re-evaluate the library info part
            if (libraryInfo != null) {
                nativeDynamicLibrary.initWithLibraryInfo(libraryInfo);
            }
            if (nativeDynamicLibrary.getPlatformPredicate().evaluatePredicate()) {
                this.nativeDynamicLibrary = nativeDynamicLibrary;
                isSystemFound[0] = true;
            }
        });

        // execute a system found listeners
        if (isSystemFound[0]) {
            if (systemDetectionListener != null) {
                systemDetectionListener.onSystemFound(this, nativeDynamicLibrary);
            }
        } else {
            if (systemDetectionListener != null) {
                systemDetectionListener.onSystemNotFound(this);
            }
            throw new UnSupportedSystemError(NativeVariant.OS_NAME.getProperty(),
                    NativeVariant.OS_ARCH.getProperty());
        }

        return this;
    }

    /**
     * Extracts and loads the system and the architecture-specific library from the output jar to a specified directory
     * according to a loading criterion (incremental-load or clean-extract). The directory is determined by the selected
     * {@link NativeDynamicLibrary} from the registered platform libraries.
     *
     * <p>
     * Note: The default loading action for Android Systems is loading the libraries from the System directories (i.e., /lib/ABI inside the APK
     * packages), while the default loading action for other desktop systems is determined by the user through this parameterized function.
     * </p>
     *
     * <p>
     * Fallback loading routines can be implemented as needed via {@link NativeBinaryLoadingListener#onLoadingFailure(NativeBinaryLoader)}
     * and are left for the user applications.
     * </p>
     *
     * @param criterion the initial loading criterion, either {@link LoadingCriterion#INCREMENTAL_LOADING}, {@link LoadingCriterion#CLEAN_EXTRACTION}
     *                  or {@link LoadingCriterion#SYSTEM_LOAD} for loading native dlls from system directories.
     * @return this instance for chained invocations
     * @throws IOException if the library to extract is not present in the jar filesystem
     */
    public NativeBinaryLoader loadLibrary(LoadingCriterion criterion) throws Exception {
        if (nativeDynamicLibrary == null || libraryInfo == null) {
            throw new IllegalArgumentException("Native library data structures cannot be null!");
        }
        // commands and loads the library from the system directories
        if (NativeVariant.Os.isAndroid() || criterion == LoadingCriterion.SYSTEM_LOAD) {
            loadSystemBinary();
            return this;
        }
        if (criterion == LoadingCriterion.INCREMENTAL_LOADING && nativeDynamicLibrary.isExtracted()) {
            loadBinary(nativeDynamicLibrary);
            return this;
        }
        cleanExtractBinary(nativeDynamicLibrary);
        return this;
    }

    /**
     * Retrieves the native dynamic library object representing the library to extract and load.
     *
     * @return an object representing the platform-dependent native dynamic library
     */
    public NativeDynamicLibrary getNativeDynamicLibrary() {
        return nativeDynamicLibrary;
    }

    /**
     * Enables the logging for this object, default value is false.
     *
     * @param loggingEnabled true to enable logging, false otherwise
     */
    public void setLoggingEnabled(boolean loggingEnabled) {
        SnapLoaderLogger.setLoggingEnabled(loggingEnabled);
    }

    /**
     * Tests the retry with clean extraction flag, default value is false.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isRetryWithCleanExtraction() {
        return retryWithCleanExtraction;
    }

    /**
     * Enables the retry with clean extraction after a load failure, default value is false.
     *
     * @param retryWithCleanExtraction true to enable the flag, false otherwise
     */
    public void setRetryWithCleanExtraction(boolean retryWithCleanExtraction) {
        this.retryWithCleanExtraction = retryWithCleanExtraction;
    }

    public List<NativeDynamicLibrary> getRegisteredLibraries() {
        return registeredLibraries;
    }

    public NativeBinaryLoadingListener getNativeBinaryLoadingListener() {
        return nativeBinaryLoadingListener;
    }

    public void setNativeBinaryLoadingListener(NativeBinaryLoadingListener nativeBinaryLoadingListener) {
        this.nativeBinaryLoadingListener = nativeBinaryLoadingListener;
    }

    public SystemDetectionListener getSystemDetectionListener() {
        return systemDetectionListener;
    }

    public void setSystemDetectionListener(SystemDetectionListener systemDetectionListener) {
        this.systemDetectionListener = systemDetectionListener;
    }

    public FileExtractionListener getLibraryExtractionListener() {
        return libraryExtractionListener;
    }

    public void setLibraryExtractionListener(FileExtractionListener libraryExtractionListener) {
        this.libraryExtractionListener = libraryExtractionListener;
    }

    public FileLocalizingListener getLibraryLocalizingListener() {
        return libraryLocalizingListener;
    }

    public void setLibraryLocalizingListener(FileLocalizingListener libraryLocalizingListener) {
        this.libraryLocalizingListener = libraryLocalizingListener;
    }

    public void setMaxNumberOfLoadingFailure(int maxNumberOfLoadingFailure) {
        this.maxNumberOfLoadingFailure = Math.abs(maxNumberOfLoadingFailure);
    }

    /**
     * Loads a native binary from the system directories into the process virtual
     * address space using the library basename in a platform-dependent way.
     */
    protected void loadSystemBinary() {
        try {
            System.loadLibrary(libraryInfo.getBaseName());
            SnapLoaderLogger.log(Level.INFO, getClass().getName(), "loadSystemBinary", "Successfully loaded library from the system: "
                    + libraryInfo.getBaseName());
            if (nativeBinaryLoadingListener != null) {
                nativeBinaryLoadingListener.onLoadingSuccess(this);
            }
        } catch (UnsatisfiedLinkError e) {
            SnapLoaderLogger.log(Level.SEVERE, getClass().getName(), "loadSystemBinary", "Cannot load the dynamic library from the system: "
                    + libraryInfo.getBaseName(), e);
            // fire failure routine for fallback criteria
            if (nativeBinaryLoadingListener != null) {
                nativeBinaryLoadingListener.onLoadingFailure(this);
            }
        }
    }

    /**
     * Loads a native binary into the virtual process address space from a specified
     * native library data structure defining the directory path.
     *
     * @param library the platform-specific library to load
     * @throws IOException                     in case the binary to be extracted is not found on the specified jar
     * @throws LoadingRetryExhaustionException if the number of loading failure exceeds the specified
     *                                         number.
     */
    protected void loadBinary(NativeDynamicLibrary library) throws Exception {
        try {
            System.load(library.getExtractedLibrary());
            SnapLoaderLogger.log(Level.INFO, getClass().getName(), "loadBinary", "Successfully loaded library: "
                    + library.getExtractedLibrary());
            if (nativeBinaryLoadingListener != null) {
                nativeBinaryLoadingListener.onLoadingSuccess(this);
            }
        } catch (final UnsatisfiedLinkError error) {
            SnapLoaderLogger.log(Level.SEVERE, getClass().getName(), "loadBinary", "Cannot load the dynamic library: "
                    + library.getExtractedLibrary(), error);
            if (nativeBinaryLoadingListener != null) {
                nativeBinaryLoadingListener.onLoadingFailure(this);
            }
            /* Retry with clean extract */
            if (isRetryWithCleanExtraction()) {
                if (nativeBinaryLoadingListener != null) {
                    nativeBinaryLoadingListener.onRetryCriterionExecution(this);
                }
                // limit the number of retries to maxNumberOfLoadingFailure
                if (numberOfLoadingFailure >= maxNumberOfLoadingFailure) {
                    numberOfLoadingFailure = 0; /* reset the number to zero trials */
                    throw new LoadingRetryExhaustionException("Library loading retries exceeded the maximum!");
                }
                ++numberOfLoadingFailure;
                // Jump call -> Possible Recursive Call
                cleanExtractBinary(library);
            }
        }
    }

    /**
     * Cleanly extracts and loads the native binary to the current [user.dir].
     *
     * @param library the platform-specific library to extract and load
     * @throws IOException in case the binary to be extracted is not found on the specified jar, or an
     *                     interrupted I/O operation has occurred
     */
    protected void cleanExtractBinary(NativeDynamicLibrary library) throws Exception {
        libraryExtractor = initializeLibraryExtractor(library);
        SnapLoaderLogger.log(Level.INFO, getClass().getName(), "cleanExtractBinary",
                "File extractor handler initialized!");
        /* CLEAR RESOURCES AND RESET OBJECTS ON-EXTRACTION */
        libraryExtractor.setExtractionListener(new FileExtractionListener() {
            @Override
            public void onExtractionCompleted(FileExtractor fileExtractor) {
                try {
                    // free resources
                    // removes file locks on some OS
                    libraryExtractor.close();
                    libraryExtractor = null;
                    SnapLoaderLogger.log(Level.INFO, getClass().getName(), "cleanExtractBinary",
                            "Extracted successfully to " + library.getExtractedLibrary());
                    // load the native binary
                    loadBinary(library);
                } catch (Exception e) {
                    SnapLoaderLogger.log(Level.SEVERE, getClass().getName(), "cleanExtractBinary",
                            "Error while loading the binary!", e);
                }

                // bind the extraction lifecycle to the user application
                if (libraryExtractionListener != null) {
                    libraryExtractionListener.onExtractionCompleted(fileExtractor);
                }
            }

            @Override
            public void onExtractionFailure(FileExtractor fileExtractor, Throwable throwable) {
                SnapLoaderLogger.log(Level.SEVERE, getClass().getName(),
                        "cleanExtractBinary", "Extraction has failed!", throwable);

                // bind the extraction lifecycle to the user application
                if (libraryExtractionListener != null) {
                    libraryExtractionListener.onExtractionFailure(fileExtractor, throwable);
                }
            }

            @Override
            public void onExtractionFinalization(FileExtractor fileExtractor, FileLocator fileLocator) {
                try {
                    if (fileExtractor != null &&
                            fileExtractor.getFileOutputStream() != null) {
                        fileExtractor.close();
                    }
                } catch (Exception e) {
                    SnapLoaderLogger.log(Level.SEVERE, getClass().getName(),
                            "cleanExtractBinary", "Error while closing the resources!", e);
                }

                // bind the extraction lifecycle to the user application
                if (libraryExtractionListener != null) {
                    libraryExtractionListener.onExtractionFinalization(fileExtractor, fileLocator);
                }
            }
        });
        libraryExtractor.extract();
    }

    /**
     * Initializes a filesystem extractor object
     * if the filesystem extractor object associated with this loader isn't defined.
     *
     * @param library the native dynamic library to load
     * @return a new FileExtractor object that represents an output stream provider
     * @throws IOException if the jar filesystem to be located is not found, or if the extraction destination is not found
     */
    protected FileExtractor initializeLibraryExtractor(NativeDynamicLibrary library) throws Exception {
        FileExtractor extractor;
        if (library.getJarPath() != null) {
            // use an extractor with the external jar routine
            extractor = new LibraryExtractor(new JarFile(library.getJarPath()), library.getCompressedLibrary(), library.getExtractedLibrary());
        } else {
            // use an extractor with the classpath routine
            extractor = new LibraryExtractor(library.getCompressedLibrary(), library.getExtractedLibrary());
        }
        extractor.initialize(0);
        final LibraryLocator fileLocator = preInitLibraryLocator(extractor);
        fileLocator.initialize(0);
        return extractor;
    }

    protected LibraryLocator preInitLibraryLocator(FileExtractor extractor) {
        extractor.getFileLocator().setFileLocalizingListener(new FileLocalizingListener() {
            @Override
            public void onFileLocalizationSuccess(FileLocator locator) {
                SnapLoaderLogger.log(Level.INFO, getClass().getName(), "preInitLibraryLocator",
                        "Locating native libraries has succeeded!");

                // bind the library locator lifecycle to the user application
                if (libraryLocalizingListener != null) {
                    libraryLocalizingListener.onFileLocalizationSuccess(locator);
                }
            }

            @Override
            public void onFileLocalizationFailure(FileLocator locator, Throwable throwable) {
                SnapLoaderLogger.log(Level.SEVERE, getClass().getName(), "preInitLibraryLocator",
                        "Locating native libraries has failed!", throwable);
                try {
                    extractor.close();
                } catch (Exception e) {
                    SnapLoaderLogger.log(Level.SEVERE, getClass().getName(),
                            "initializeLibraryExtractor", "File locator closure failed!", e);
                }

                // bind the library locator lifecycle to the user application
                if (libraryLocalizingListener != null) {
                    libraryLocalizingListener.onFileLocalizationFailure(locator, throwable);
                }
            }
        });
        return (LibraryLocator) extractor.getFileLocator();
    }
}
