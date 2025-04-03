/*
 * Copyright 2024-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.jdbc.utils;

import com.mongodb.MongoException;
import com.mongodb.jdbc.MongoDriver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.lang3.SystemUtils;

/**
 * A helper based on the NativeUtils library of Adam Heinrich:
 *
 * <p>A simple library class which helps with loading dynamic libraries stored in the JAR archive.
 * These libraries usualy contain implementation of some methods in native code (using JNI - Java
 * Native Interface).
 *
 * @see "http://adamheinrich.com/blog/2012/how-to-load-native-jni-library-from-jar"
 * @see "https://github.com/adamheinrich/native-utils"
 */
public class NativeLoader {

    private static final String NATIVE_FOLDER_PATH_PREFIX = "mongosql_native";

    /** Temporary directory which will contain the DLLs. */
    private static File temporaryLibDir;

    // List of libraries loaded using the loader.
    // A library can only be loaded once.
    private static Set<String> loadedLibs = new HashSet<String>();

    // This pattern was constructed using OpenJDK platform keys logic.
    // See https://github.com/openjdk/jtreg/blob/master/make/Platform.gmk#L103
    private static final Pattern X86_64_ARCH_PATTERN =
            Pattern.compile("^(x86_64|amd64|ia32e|em64t|x64|x86-64|8664|intel64)$");
    private static final Pattern ARM_ARCH_PATTERN = Pattern.compile("^(aarch64|arm64)$");

    private static final String ARM = "arm";
    private static final String X86_64 = "x86_64";

    private static final String MACOS = "macos";
    private static final String LINUX = "linux";
    private static final String WINDOWS = "win";

    /** Private constructor - this class will never be instanced. */
    private NativeLoader() {}

    private static String normalizeOS() throws MongoException {
        if (SystemUtils.IS_OS_LINUX) {
            return LINUX;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return WINDOWS;
        } else if (SystemUtils.IS_OS_MAC) {
            return MACOS;
        }

        // Unsupported OS
        throw new MongoException("Unsupported OS : " + SystemUtils.OS_NAME);
    }

    private static String normalizeArch() throws MongoException {
        String arch = SystemUtils.OS_ARCH.toLowerCase();
        if (X86_64_ARCH_PATTERN.matcher(arch).matches()) {
            return X86_64;
        } else if (ARM_ARCH_PATTERN.matcher(arch).matches()) {
            return ARM;
        }

        // Unsupported architecture
        throw new MongoException("Unsupported architecture : " + arch);
    }

    /**
     * Loads library from current JAR archive.
     *
     * <p>The file from JAR is copied into system temporary directory and then loaded. The temporary
     * file is deleted after exiting. Method uses String as filename because the pathname is
     * "abstract", not system-dependent.
     *
     * @param libraryName The name of the library to load.
     * @return the path of the loaded library.
     * @throws IOException If temporary file creation or read/write operation fails
     * @throws IllegalArgumentException If source file (param libPath) does not exist
     * @throws IllegalArgumentException If the libPath is not absolute or if the filename is shorter
     *     than three characters (restriction of {@link File#createTempFile(java.lang.String,
     *     java.lang.String)}).
     * @throws FileNotFoundException If the file could not be found inside the JAR.
     */
    public static String loadLibraryFromJar(String libraryName)
            throws IOException, IllegalArgumentException, FileNotFoundException {

        String libName = System.mapLibraryName(libraryName);
        if (loadedLibs.contains(libName)) {
            // Don't reload.
            return libName;
        }

        // Build the library path using the os and arch information.
        String resourcePath =
                normalizeArch().toLowerCase() + "/" + normalizeOS().toLowerCase() + "/" + libName;

        URL resource =
                MongoDriver.class.getProtectionDomain().getClassLoader().getResource(resourcePath);

        if (resource != null) {

            // Create a temporary directory to copy the library into.
            if (temporaryLibDir == null) {
                temporaryLibDir = createTempDirectory();
                temporaryLibDir.deleteOnExit();
            }

            // Copy the library in the temporary directory.
            File libFile;
            libFile = new File(temporaryLibDir, libName);

            try (InputStream is = resource.openStream()) {
                Files.copy(is, libFile.toPath());
            } catch (FileAlreadyExistsException e) {
                // Do nothing, the library is already there which means that the JVM already loaded it.
            } catch (IOException e) {
                libFile.delete();
                // Unexpected error.
                throw e;
            } catch (NullPointerException e) {
                libFile.delete();
                throw new FileNotFoundException(
                        "Resource " + resourcePath + " was not found inside JAR.");
            }

            try {
                System.load(libFile.getAbsolutePath());
            } finally {
                if (isPosixCompliant()) {
                    // Assume POSIX compliant file system, can be deleted after loading
                    libFile.delete();
                } else {
                    // Assume non-POSIX, and don't delete until last file descriptor closed
                    libFile.deleteOnExit();
                }
            }

            return libFile.getAbsolutePath();
        }
        throw new FileNotFoundException("Resource " + resourcePath + " was not found inside JAR.");
    }

    private static boolean isPosixCompliant() {
        try {
            return FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
        } catch (FileSystemNotFoundException | ProviderNotFoundException | SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a temporary directory under the file system path of a temporary directory for use by
     * the java runtime. The path will look like {java.io.tmpdir}/{prefix}{nanoTime}
     *
     * @return The path to the created directory.
     * @throws IOException If an error occurs.
     */
    private static File createTempDirectory() throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File generatedDir = new File(tempDir, NATIVE_FOLDER_PATH_PREFIX + System.nanoTime());

        if (!generatedDir.mkdir() && !Files.exists(generatedDir.toPath())) {
            throw new IOException("Failed to create temp directory " + generatedDir.getName());
        }
        return generatedDir;
    }
}
