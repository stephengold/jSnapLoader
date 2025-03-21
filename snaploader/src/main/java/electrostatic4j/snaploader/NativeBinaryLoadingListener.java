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

import electrostatic4j.snaploader.platform.NativeDynamicLibrary;
import electrostatic4j.snaploader.util.CallingStackMetaData;

/**
 * Provides executable functions binding the user applications to
 * the loading lifecycle.
 * <p>
 * Note: All the functions on this interface are dispatched
 * by the {@link NativeBinaryLoader#loadBinary(NativeDynamicLibrary, LoadingCriterion)}
 *
 * @author pavl_g
 */
public interface NativeBinaryLoadingListener {

    /**
     * Dispatched when loading the system-specific binary has succeeded.
     *
     * @param nativeBinaryLoader the dispatching loader.
     * @param callingStackMetaData a data structure representing the meta data of the calling stack.
     */
    void onLoadingSuccess(NativeBinaryLoader nativeBinaryLoader, CallingStackMetaData callingStackMetaData);

    /**
     * Dispatched when loading the system-specific binary has failed.
     *
     * @param nativeBinaryLoader the dispatching loader.
     * @param callingStackMetaData a data structure representing the meta data of the calling stack.
     */
    void onLoadingFailure(NativeBinaryLoader nativeBinaryLoader,
                          CallingStackMetaData callingStackMetaData);

    /**
     * Dispatched when loading the system-specific binary has failed,
     * and the retry criterion has been executed.
     * <p>
     * Note: this dispatching function could be overridden to add
     * your own anti-failure mechanisms (i.e., Retry Criterion).
     *
     * @param nativeBinaryLoader the dispatching loader.
     * @param callingStackMetaData a data structure representing the meta data of the calling stack.
     */
    void onRetryCriterionExecution(NativeBinaryLoader nativeBinaryLoader, CallingStackMetaData callingStackMetaData);
}
