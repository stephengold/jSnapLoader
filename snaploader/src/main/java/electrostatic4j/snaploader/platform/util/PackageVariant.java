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

/**
 * A namespace class exposing checks for the native window
 * currently in runtime; this could give some more insights
 * about the type of the framework the application is running
 * on.
 *
 * @author pavl_g.
 */
public final class PackageVariant {

    private static final String FLUTTER_ACTIVITY_PATH = "io.flutter.embedding.android.FlutterActivity";
    private static final String JME_CONTEXT_PATH = "com.jme3.system.JmeContext";
    private static final String ANDROID_CONTEXT_PATH = "android.content.Context";
    private static final String JAVA_FX_CONTEXT_PATH = "javafx.application.Application";
    private static final String JAVA_AWT_CONTEXT_PATH = "java.awt.Frame";
    private static final String GWT_CONTEXT_PATH = "com.google.gwt.user.client.Window";

    private PackageVariant() {
    }

    public static boolean hasJavaFxWindow() {
        try {
            return ClassLoader.getSystemClassLoader().loadClass(PackageVariant.JAVA_FX_CONTEXT_PATH) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean hasJavaAWTWindow() {
        try {
            return ClassLoader.getSystemClassLoader().loadClass(PackageVariant.JAVA_AWT_CONTEXT_PATH) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean hasAndroidActivity() {
        try {
            return ClassLoader.getSystemClassLoader().loadClass(PackageVariant.ANDROID_CONTEXT_PATH) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean hasGWTContext() {
        try {
            return ClassLoader.getSystemClassLoader().loadClass(PackageVariant.GWT_CONTEXT_PATH) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean hasJmeContext() {
        try {
            return ClassLoader.getSystemClassLoader().loadClass(PackageVariant.JME_CONTEXT_PATH) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean hasFlutterActivity() {
        try {
            return ClassLoader.getSystemClassLoader().loadClass(PackageVariant.FLUTTER_ACTIVITY_PATH) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean hasAndroidActivityOnly() {
        return hasAndroidActivity() && !hasFlutterActivity();
    }

    public static boolean hasJmeAndroidContext() {
        return hasAndroidActivity() && hasJmeContext();
    }

    public static boolean hasJmeFlutterContext() {
        return hasFlutterActivity() && hasJmeContext();
    }
}