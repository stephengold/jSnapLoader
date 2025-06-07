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

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Provides a namespace grouping properties controllers {@link PropertiesController} concrete objects.
 *
 * @author pavl_g.
 */
public final class PropertiesControllerNamespace {

    /**
     * Cannot instantiate; Utility Class.
     */
    private PropertiesControllerNamespace() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * A factorized singleton implementation for the Java path system controller to
     * control the Java System loading path for native DLL (i.e., adds and removes paths).
     */
    public static final PropertiesController systemDirectoryController = new PropertiesController() {

        @Override
        public PropertiesProvider getProvider() {
            return DefaultPropertiesProvider.SYSTEM_DIR;
        }

        @Override
        public String getKey() {
            return "jsnaploader.library.path";
        }

        @Override
        public char getDelimiter() {
            return ':';
        }

        @Override
        public void initialize() {
            if (getProvider().getSystemProperty() != null) {
                return ;
            }
            System.setProperty(getKey(), System.getProperty("java.library.path"));
        }

        @Override
        public void deInitialize() {
            PropertiesController.super.deInitialize();
        }

        @Override
        public void addSystemPath(Path path) throws IllegalArgumentException, IllegalStateException {
            PropertiesController.super.addSystemPath(path);
        }

        @Override
        public void removeSystemPath(Path path) throws IllegalArgumentException, IllegalStateException, NoSuchFileException {
            PropertiesController.super.removeSystemPath(path);
        }

        @Override
        public String toString() {
            return System.getProperty(getKey());
        }
    };
}
