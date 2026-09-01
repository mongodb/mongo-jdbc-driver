/*
 * Copyright 2026-present MongoDB, Inc.
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

package com.mongodb.jdbc.sqlinterface.exception;

/** An exception caused by a marker that is otherwise valid, but is explicitly disabled. */
public class SQLInterfaceStatusDisabledException extends Exception {
    public SQLInterfaceStatusDisabledException() {
        super(
                "SQL Interface is disabled for this cluster. Enable the SQL Interface for your cluster on Atlas and retry. Contact your admin if necessary.");
    }
}
