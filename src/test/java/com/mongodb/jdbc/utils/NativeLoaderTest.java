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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/**
 * Regression tests for the staging directory and library file handling in {@link NativeLoader}.
 *
 * <p>The driver stages its native library in the shared system temp directory and loads it with
 * {@link System#load}. These tests make sure it never loads a library file that another local user
 * could have planted.
 */
class NativeLoaderTest {

    private static final String LIBRARY_NAME = "mongosqltranslate";

    private final List<File> createdDirs = new ArrayList<>();

    @AfterEach
    void cleanUp() throws Exception {
        setTemporaryLibDir(null);
        for (File dir : createdDirs) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    Files.deleteIfExists(child.toPath());
                }
            }
            Files.deleteIfExists(dir.toPath());
        }
    }

    private File newTempDir() throws IOException {
        File dir = NativeLoader.createTempDirectory();
        createdDirs.add(dir);
        return dir;
    }

    /** Points the loader at an existing staging directory, as if it had already created it. */
    private static void setTemporaryLibDir(File dir) throws Exception {
        Field field = NativeLoader.class.getDeclaredField("temporaryLibDir");
        field.setAccessible(true);
        field.set(null, dir);
    }

    /**
     * Other users must not be able to write into the staging directory, otherwise they could plant
     * a malicious library file before the driver loads it.
     */
    @Test
    void stagingDirectoryIsOwnerOnlyOnPosix() throws IOException {
        Assumptions.assumeTrue(
                FileSystems.getDefault().supportedFileAttributeViews().contains("posix"));

        File dir = newTempDir();

        // Assert that the directory only has read,write,execute path permissions
        // for the user.
        assertEquals(
                PosixFilePermissions.fromString("rwx------"),
                Files.getPosixFilePermissions(dir.toPath()));
    }

    /**
     * A library file that is already in the staging directory was not written by this JVM, so it
     * must never be passed to {@link System#load}.
     */
    @Test
    void refusesToLoadPreExistingLibraryFile() throws Exception {
        // Create a new temporary directory, and place a malicious file named mongosqltranslate to the directory
        File stagingDir = newTempDir();
        File planted = new File(stagingDir, System.mapLibraryName(LIBRARY_NAME));
        byte[] attackerBytes = "not written by the driver".getBytes(StandardCharsets.UTF_8);
        Files.write(planted.toPath(), attackerBytes);
        setTemporaryLibDir(stagingDir);

        Throwable thrown =
                assertThrows(Throwable.class, () -> NativeLoader.loadLibraryFromJar(LIBRARY_NAME));

        // The loader refused the planted file before reaching System.load().
        assertTrue(
                thrown instanceof FileAlreadyExistsException,
                () -> "Expected FileAlreadyExistsException but got: " + thrown);

        // The planted file is neither overwritten nor deleted.
        assertArrayEquals(attackerBytes, Files.readAllBytes(planted.toPath()));
    }
}
