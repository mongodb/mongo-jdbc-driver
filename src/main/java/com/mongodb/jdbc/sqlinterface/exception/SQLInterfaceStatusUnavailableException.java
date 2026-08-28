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

/** An exception for clusters which do not have a marker. */
public class SQLInterfaceStatusUnavailableException extends SQLInterfaceStatusException {
    public SQLInterfaceStatusUnavailableException() {
        super(
                "Unable to determine SQL Interface status for this cluster. Verify that the SQL interface is enabled for your cluster and retry.");
    }
}
