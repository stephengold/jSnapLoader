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

import java.nio.file.FileSystems;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * Controls a Java System property via a property provider.
 *
 * @author pavl_g.
 */
public interface PropertiesController {

    /**
     * Retrieves the associated properties provider.
     *
     * @return the associated properties provider object (non-null).
     */
    PropertiesProvider getProvider();

    /**
     * Retrieves the key for the properties provider object.
     *
     * @return the key of the properties provider object (non-null).
     */
    String getKey();

    /**
     * Retrieves the delimiter for the properties provider object.
     * <p>
     * A delimiter is utilized by the provider to separate values
     * (e.g., the colon character ':').
     * </p>
     *
     * @return the delimiter in String format (non-null).
     */
    char getDelimiter();

    /**
     * Initializes this property controller with the proper values.
     */
    default void initialize() {
        if (getProvider() == null || getDelimiter() == '\0'
                || getKey() == null) {
            throw new IllegalStateException("Properties Controller corrupted state!");
        }
        if (getProvider().getSystemProperty() != null) {
            return ;
        }
        System.setProperty(getKey(), "");
    }

    /**
     * De-init this property controller to NULL.
     */
    default void deInitialize() {
        if (getProvider() == null || getDelimiter() == '\0'
                || getKey() == null) {
            throw new IllegalStateException("Properties Controller corrupted state!");
        }
        if (getProvider().getSystemProperty() == null) {
            return ;
        }
        System.clearProperty(getKey());
    }

    /**
     * Iterates over the available delimited paths from this property.
     * <p>
     * Note: the return of this inscribed function should provide a
     * post-processing modifications to the 'word' literal; in most
     * cases its an empty string, but could be otherwise a meaningful
     * prefix for the next word (e.g., forward or back slash).
     * </p>
     *
     * @param function a function to execute on each time the iteration
     *                 samples (i.e., when a delimited path is found).
     * @param <T> the type of the path object.
     */
    default <T extends Path>
    void iterate(Function<T, String> function) {
        if (getProvider() == null || getDelimiter() == '\0'
                || getKey() == null || function == null) {
            throw new IllegalStateException("Properties Controller corrupted state!");
        }

        final String value = System.getProperty(getKey());
        String word = "";
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != getDelimiter()) {
                word += value.charAt(i);
                // sample on the last item
                if ((i + 1) != value.length()) {
                    continue;
                }
            }
            word = function.apply((T) FileSystems.getDefault().getPath(word));
        }
    }

    /**
     * Converts paths from this property controller concrete object
     * to a contiguous buffer of paths.
     *
     * @return a contiguous buffer of paths.
     * @throws IllegalStateException thrown if the controller has corrupted dependencies.
     */
    default Path[] toList() throws IllegalStateException {
        if (getProvider() == null || getDelimiter() == '\0' || getKey() == null) {
            throw new IllegalStateException("Properties Controller corrupted state!");
        }

        final ArrayList<Path> paths = new ArrayList<>();
        final String value = System.getProperty(getKey());
        String word = "";
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != getDelimiter()) {
                word += value.charAt(i);
                // sample on the last item
                if ((i + 1) != value.length()){
                    continue;
                }
            }
            paths.add(FileSystems.getDefault().getPath(word));
            // post-processing increment count
            word = "";
        }
        return paths.toArray(new Path[paths.size()]);
    }

    /**
     * Adds a path value to a system property.
     *
     * @param path a platform-independent wrapper object for a path (absolute or relative).
     * @throws IllegalArgumentException thrown if the path object is corrupted.
     * @throws IllegalStateException thrown if the controller has corrupted dependencies.
     */
    default void addSystemPath(Path path) throws IllegalArgumentException,
                                                 IllegalStateException {
        // preprocessing automata -- Input Validation States
        if (path == null) {
            throw new IllegalArgumentException("Cannot add a null path!");
        }
        if (getProvider() == null || getDelimiter() == '\0' || getKey() == null) {
            throw new IllegalStateException("Properties Controller corrupted state!");
        }
        // preprocessing automata -- Automatically allocate new path appendices States
        final String value = System.getProperty(getKey());
        final String newValue = value + getDelimiter() + path;
        // processing automata -- Set the new value State
        System.setProperty(getKey(), newValue);
    }

    /**
     * Retrieves a path from a path list of this property or throws
     * {@link NoSuchFileException}.
     *
     * @param index the index of the required path.
     * @return the required path or NULL if the path is not found.
     * @throws IllegalStateException thrown if the controller has corrupted dependencies.
     * @throws NoSuchFileException thrown if the Path queried for wasn't found.
     */
    default Path get(int index) throws IllegalStateException, NoSuchFileException {
        if (getProvider() == null || getDelimiter() == '\0' || getKey() == null) {
            throw new IllegalStateException("Properties Controller corrupted state!");
        }
        // preprocessing automata -- Substring delimited values
        final String value = System.getProperty(getKey());
        String word = "";
        int pathCount = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != getDelimiter()) {
                word += value.charAt(i);
                // sample on the last item
                if ((i + 1) != value.length()){
                    continue;
                }
            }
            if (pathCount == index) {
                return FileSystems.getDefault().getPath(word);
            }
            // post-processing increment count
            word = "";
            pathCount++;
        }
        throw new NoSuchFileException("Cannot find the required file!s");
    }

    /**
     * Searches and removes a path from the system paths adjusting the system property.
     *
     * @param path the path to query and remove.
     * @throws IllegalArgumentException thrown if the path object is corrupted.
     * @throws IllegalStateException thrown if the controller has corrupted dependencies.
     * @throws NoSuchFileException thrown if the Path queried wasn't found.
     */
    default void removeSystemPath(Path path) throws IllegalArgumentException,
                                                    IllegalStateException,
                                                    NoSuchFileException {
        // preprocessing automata -- Input Validation States
        if (path == null) {
            throw new IllegalArgumentException("Cannot remove a null path!");
        }
        if (getProvider() == null || getDelimiter() == '\0' || getKey() == null) {
            throw new IllegalStateException("Properties Controller corrupted state!");
        }
        // preprocessing automata -- Initializing local variables
        final String value = System.getProperty(getKey());
        String newValue = ""; // initialize the new value automatically here
        String word = "";
        int count = 0;

        // processing automata -- Exclude the path from the list of paths
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != getDelimiter()) {
                word += value.charAt(i);
                // sample on the last item
                if ((i + 1) != value.length()){
                    continue;
                }
            }
            // test equality for removal
            if (word.equals(path.toString())) {
                word = "";
                count++;
                continue;
            }
            // test to append a delimiter
            if (!newValue.isEmpty()) {
                newValue += getDelimiter();
            }
            newValue += word;
            word = ""; // reset the intermediate
        }
        // post-processing automata -- Setting the new value State
        if (count > 0) {
            System.setProperty(getKey(), newValue);
            return ;
        }
        // post-processing automata -- Failure signaling
        throw new NoSuchFileException("Cannot find the required file!");
    }
}
