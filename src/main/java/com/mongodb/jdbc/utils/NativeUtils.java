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
import java.util.regex.Pattern;
import org.apache.commons.lang3.SystemUtils;

public class NativeUtils {
    private static final Pattern X86_64_ARCH_PATTERN =
            Pattern.compile("^(x8664|amd64|ia32e|em64t|x64)$");
    private static final String ARM_ARCH = "aarch64";

    private static final String ARM = "arm";
    private static final String X86_64 = "x86_64";

    private static final String MACOS = "macos";
    private static final String LINUX = "linux";
    private static final String WINDOWS = "windows";

    public static String normalizeOS() throws MongoException {
        if (SystemUtils.IS_OS_LINUX) {
            return LINUX;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return WINDOWS;
        } else if (SystemUtils.IS_OS_MAC) {
            return MACOS;
        }

        // Unsupported OS
        throw new MongoException("Unsuported OS : " + SystemUtils.OS_NAME);
    }

    public static String normalizeArch() throws MongoException {
        String arch = SystemUtils.OS_ARCH.toLowerCase();
        if (X86_64_ARCH_PATTERN.matcher(arch).matches()) {
            return X86_64;
        } else if (ARM_ARCH.equals(arch)) {
            return ARM;
        }

        // Unsupported architecture
        throw new MongoException("Unsuported architecture : " + arch);
    }
}
