/*
 * Copyright (C) 2020 panpf <panpfpanpf@outlook.com>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package com.github.panpf.activity.monitor;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ActivityMonitor {

    @NonNull
    private static final Object LIST_EDIT_LOCK = new Object();
    private static final Object COUNT_EDIT_LOCK = new Object();

    @NonNull
    private static final ActivityMonitor INSTANCE = new ActivityMonitor();

    @NonNull
    private final List<WeakReference<Activity>> createdActivityReverseList = new LinkedList<>();
    @NonNull
    private final List<WeakReference<Activity>> startedActivityReverseList = new LinkedList<>();
    @NonNull
    private final List<WeakReference<Activity>> resumedActivityReverseList = new LinkedList<>();

    @Nullable
    private LinkedList<OnActivityCreatedListener> createdListeners;
    @Nullable
    private LinkedList<OnActivityStartedListener> startedListeners;
    @Nullable
    private LinkedList<OnActivityResumedListener> resumedListeners;
    @Nullable
    private LinkedList<OnActivityPausedListener> pausedListeners;
    @Nullable
    private LinkedList<OnActivityStoppedListener> stoppedListeners;
    @Nullable
    private LinkedList<OnActivityDestroyedListener> destroyedListeners;
    @Nullable
    private LinkedList<OnActivitySaveInstanceStateListener> saveInstanceStateListeners;
    @Nullable
    private LinkedList<OnActivityLifecycleChangedListener> lifecycleChangedListeners;

    private ActivityMonitor() {
    }

    static void init(@NonNull Application application) {
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksImpl(INSTANCE));
    }


    public static boolean isRunningForeground() {
        return getStartedActivityCount() > 0;
    }

    public static int getCreatedActivityCount() {
        synchronized (COUNT_EDIT_LOCK) {
            return INSTANCE.createdActivityReverseList.size();
        }
    }

    public static int getStartedActivityCount() {
        synchronized (COUNT_EDIT_LOCK) {
            return INSTANCE.startedActivityReverseList.size();
        }
    }

    public static int getResumedActivityCount() {
        synchronized (COUNT_EDIT_LOCK) {
            return INSTANCE.resumedActivityReverseList.size();
        }
    }


    @NonNull
    public static List<Activity> getCreatedActivityList() {
        synchronized (COUNT_EDIT_LOCK) {
            List<WeakReference<Activity>> createdActivityStack = INSTANCE.createdActivityReverseList;
            if (createdActivityStack.isEmpty()) {
                return new ArrayList<>(0);
            }
            List<Activity> activityList = new ArrayList<>(createdActivityStack.size());
            for (WeakReference<Activity> reference : createdActivityStack) {
                Activity activity = reference.get();
                if (activity != null) {
                    activityList.add(activity);
                }
            }
            Collections.reverse(activityList);
            return activityList;
        }
    }

    @NonNull
    public static List<Activity> getStartedActivityList() {
        synchronized (COUNT_EDIT_LOCK) {
            List<WeakReference<Activity>> startedActivityStack = INSTANCE.startedActivityReverseList;
            if (startedActivityStack.isEmpty()) {
                return new ArrayList<>(0);
            }
            List<Activity> activityList = new ArrayList<>(startedActivityStack.size());
            for (WeakReference<Activity> reference : startedActivityStack) {
                Activity activity = reference.get();
                if (activity != null) {
                    activityList.add(activity);
                }
            }
            Collections.reverse(activityList);
            return activityList;
        }
    }

    @NonNull
    public static List<Activity> getResumedActivityList() {
        synchronized (COUNT_EDIT_LOCK) {
            List<WeakReference<Activity>> resumedActivityStack = INSTANCE.resumedActivityReverseList;
            if (resumedActivityStack.isEmpty()) {
                return new ArrayList<>(0);
            }
            List<Activity> activityList = new ArrayList<>(resumedActivityStack.size());
            for (WeakReference<Activity> reference : resumedActivityStack) {
                Activity activity = reference.get();
                if (activity != null) {
                    activityList.add(activity);
                }
            }
            Collections.reverse(activityList);
            return activityList;
        }
    }

    @Nullable
    public static Activity getLastCreatedActivity() {
        synchronized (COUNT_EDIT_LOCK) {
            List<WeakReference<Activity>> createdActivityStack = INSTANCE.createdActivityReverseList;
            WeakReference<Activity> lastCreatedActivityRef = !createdActivityStack.isEmpty() ? createdActivityStack.get(0) : null;
            return lastCreatedActivityRef != null ? lastCreatedActivityRef.get() : null;
        }
    }

    @Nullable
    public static Activity getLastStartedActivity() {
        synchronized (COUNT_EDIT_LOCK) {
            List<WeakReference<Activity>> startedActivityStack = INSTANCE.startedActivityReverseList;
            WeakReference<Activity> lastStartedActivityRef = !startedActivityStack.isEmpty() ? startedActivityStack.get(0) : null;
            return lastStartedActivityRef != null ? lastStartedActivityRef.get() : null;
        }
    }

    @Nullable
    public static Activity getLastResumedActivity() {
        synchronized (COUNT_EDIT_LOCK) {
            List<WeakReference<Activity>> resumedActivityStack = INSTANCE.resumedActivityReverseList;
            WeakReference<Activity> lastResumedActivityRef = !resumedActivityStack.isEmpty() ? resumedActivityStack.get(0) : null;
            return lastResumedActivityRef != null ? lastResumedActivityRef.get() : null;
        }
    }

    @Nullable
    public static Activity getFirstCreatedActivity() {
        synchronized (COUNT_EDIT_LOCK) {
            List<WeakReference<Activity>> createdActivityStack = INSTANCE.createdActivityReverseList;
            WeakReference<Activity> lastCreatedActivityRef = !createdActivityStack.isEmpty() ? createdActivityStack.get(createdActivityStack.size() - 1) : null;
            return lastCreatedActivityRef != null ? lastCreatedActivityRef.get() : null;
        }
    }

    @Nullable
    public static Activity getFirstStartedActivity() {
        synchronized (COUNT_EDIT_LOCK) {
            List<WeakReference<Activity>> startedActivityStack = INSTANCE.startedActivityReverseList;
            WeakReference<Activity> lastStartedActivityRef = !startedActivityStack.isEmpty() ? startedActivityStack.get(startedActivityStack.size() - 1) : null;
            return lastStartedActivityRef != null ? lastStartedActivityRef.get() : null;
        }
    }

    @Nullable
    public static Activity getFirstResumedActivity() {
        synchronized (COUNT_EDIT_LOCK) {
            List<WeakReference<Activity>> resumedActivityStack = INSTANCE.resumedActivityReverseList;
            WeakReference<Activity> lastResumedActivityRef = !resumedActivityStack.isEmpty() ? resumedActivityStack.get(resumedActivityStack.size() - 1) : null;
            return lastResumedActivityRef != null ? lastResumedActivityRef.get() : null;
        }
    }


    public static void registerActivityCreatedListener(@NonNull OnActivityCreatedListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivityCreatedListener> createdListeners = INSTANCE.createdListeners;
            if (createdListeners != null) {
                createdListeners.add(listener);
            } else {
                LinkedList<OnActivityCreatedListener> newCreatedListeners = new LinkedList<>();
                newCreatedListeners.add(listener);
                INSTANCE.createdListeners = newCreatedListeners;
            }
        }
    }

    public static void unregisterActivityCreatedListener(@NonNull OnActivityCreatedListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivityCreatedListener> createdListeners = INSTANCE.createdListeners;
            if (createdListeners != null) {
                createdListeners.remove(listener);
            }
        }
    }

    public static void observeActivityCreated(@NonNull LifecycleOwner owner, @NonNull final OnActivityCreatedListener listener) {
        if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            throw new IllegalStateException("LifecycleOwner state is DESTROYED");
        }
        registerActivityCreatedListener(listener);
        owner.getLifecycle().addObserver(new CreatedAutoUnregisterObserver(listener));
    }

    public static void registerActivityStartedListener(@NonNull OnActivityStartedListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivityStartedListener> startedListeners = INSTANCE.startedListeners;
            if (startedListeners != null) {
                startedListeners.add(listener);
            } else {
                LinkedList<OnActivityStartedListener> newStartedListeners = new LinkedList<>();
                newStartedListeners.add(listener);
                INSTANCE.startedListeners = newStartedListeners;
            }
        }
    }

    public static void unregisterActivityStartedListener(@NonNull OnActivityStartedListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivityStartedListener> startedListeners = INSTANCE.startedListeners;
            if (startedListeners != null) {
                startedListeners.remove(listener);
            }
        }
    }

    public static void observeActivityStarted(@NonNull LifecycleOwner owner, @NonNull final OnActivityStartedListener listener) {
        if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            throw new IllegalStateException("LifecycleOwner state is DESTROYED");
        }
        registerActivityStartedListener(listener);
        owner.getLifecycle().addObserver(new StartedAutoUnregisterObserver(listener));
    }

    public static void registerActivityResumedListener(@NonNull OnActivityResumedListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivityResumedListener> resumedListeners = INSTANCE.resumedListeners;
            if (resumedListeners != null) {
                resumedListeners.add(listener);
            } else {
                LinkedList<OnActivityResumedListener> newResumedListeners = new LinkedList<>();
                newResumedListeners.add(listener);
                INSTANCE.resumedListeners = newResumedListeners;
            }
        }
    }

    public static void unregisterActivityResumedListener(@NonNull OnActivityResumedListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivityResumedListener> resumedListeners = INSTANCE.resumedListeners;
            if (resumedListeners != null) {
                resumedListeners.remove(listener);
            }
        }
    }

    public static void observeActivityResumed(@NonNull LifecycleOwner owner, @NonNull final OnActivityResumedListener listener) {
        if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            throw new IllegalStateException("LifecycleOwner state is DESTROYED");
        }
        registerActivityResumedListener(listener);
        owner.getLifecycle().addObserver(new ResumedAutoUnregisterObserver(listener));
    }

    public static void registerActivityPausedListener(@NonNull OnActivityPausedListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivityPausedListener> pausedListeners = INSTANCE.pausedListeners;
            if (pausedListeners != null) {
                pausedListeners.add(listener);
            } else {
                LinkedList<OnActivityPausedListener> newPausedListeners = new LinkedList<>();
                newPausedListeners.add(listener);
                INSTANCE.pausedListeners = newPausedListeners;
            }
        }
    }

    public static void unregisterActivityPausedListener(@NonNull OnActivityPausedListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivityPausedListener> pausedListeners = INSTANCE.pausedListeners;
            if (pausedListeners != null) {
                pausedListeners.remove(listener);
            }
        }
    }

    public static void observeActivityPaused(@NonNull LifecycleOwner owner, @NonNull final OnActivityPausedListener listener) {
        if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            throw new IllegalStateException("LifecycleOwner state is DESTROYED");
        }
        registerActivityPausedListener(listener);
        owner.getLifecycle().addObserver(new PausedAutoUnregisterObserver(listener));
    }

    public static void registerActivityStoppedListener(@NonNull OnActivityStoppedListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivityStoppedListener> stoppedListeners = INSTANCE.stoppedListeners;
            if (stoppedListeners != null) {
                stoppedListeners.add(listener);
            } else {
                LinkedList<OnActivityStoppedListener> newStoppedListeners = new LinkedList<>();
                newStoppedListeners.add(listener);
                INSTANCE.stoppedListeners = newStoppedListeners;
            }
        }
    }

    public static void unregisterActivityStoppedListener(@NonNull OnActivityStoppedListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivityStoppedListener> stoppedListeners = INSTANCE.stoppedListeners;
            if (stoppedListeners != null) {
                stoppedListeners.remove(listener);
            }
        }
    }

    public static void observeActivityStopped(@NonNull LifecycleOwner owner, @NonNull final OnActivityStoppedListener listener) {
        if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            throw new IllegalStateException("LifecycleOwner state is DESTROYED");
        }
        registerActivityStoppedListener(listener);
        owner.getLifecycle().addObserver(new StoppedAutoUnregisterObserver(listener));
    }

    public static void registerActivityDestroyedListener(@NonNull OnActivityDestroyedListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivityDestroyedListener> destroyedListeners = INSTANCE.destroyedListeners;
            if (destroyedListeners != null) {
                destroyedListeners.add(listener);
            } else {
                LinkedList<OnActivityDestroyedListener> newDestroyedListeners = new LinkedList<>();
                newDestroyedListeners.add(listener);
                INSTANCE.destroyedListeners = newDestroyedListeners;
            }
        }
    }

    public static void unregisterActivityDestroyedListener(@NonNull OnActivityDestroyedListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivityDestroyedListener> destroyedListeners = INSTANCE.destroyedListeners;
            if (destroyedListeners != null) {
                destroyedListeners.remove(listener);
            }
        }
    }

    public static void observeActivityDestroyed(@NonNull LifecycleOwner owner, @NonNull final OnActivityDestroyedListener listener) {
        if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            throw new IllegalStateException("LifecycleOwner state is DESTROYED");
        }
        registerActivityDestroyedListener(listener);
        owner.getLifecycle().addObserver(new DestroyedAutoUnregisterObserver(listener));
    }

    public static void registerActivitySaveInstanceStateListener(@NonNull OnActivitySaveInstanceStateListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivitySaveInstanceStateListener> saveInstanceStateListeners = INSTANCE.saveInstanceStateListeners;
            if (saveInstanceStateListeners != null) {
                saveInstanceStateListeners.add(listener);
            } else {
                LinkedList<OnActivitySaveInstanceStateListener> newSaveInstanceStateListeners = new LinkedList<>();
                newSaveInstanceStateListeners.add(listener);
                INSTANCE.saveInstanceStateListeners = newSaveInstanceStateListeners;
            }
        }
    }

    public static void unregisterActivitySaveInstanceStateListener(@NonNull OnActivitySaveInstanceStateListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivitySaveInstanceStateListener> saveInstanceStateListeners = INSTANCE.saveInstanceStateListeners;
            if (saveInstanceStateListeners != null) {
                saveInstanceStateListeners.remove(listener);
            }
        }
    }

    public static void observeActivitySaveInstanceState(@NonNull LifecycleOwner owner, @NonNull final OnActivitySaveInstanceStateListener listener) {
        if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            throw new IllegalStateException("LifecycleOwner state is DESTROYED");
        }
        registerActivitySaveInstanceStateListener(listener);
        owner.getLifecycle().addObserver(new SaveInstanceStateAutoUnregisterObserver(listener));
    }

    public static void registerActivityLifecycleChangedListener(@NonNull OnActivityLifecycleChangedListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivityLifecycleChangedListener> lifecycleChangedListeners = INSTANCE.lifecycleChangedListeners;
            if (lifecycleChangedListeners != null) {
                lifecycleChangedListeners.add(listener);
            } else {
                LinkedList<OnActivityLifecycleChangedListener> newLifecycleChangedListeners = new LinkedList<>();
                newLifecycleChangedListeners.add(listener);
                INSTANCE.lifecycleChangedListeners = newLifecycleChangedListeners;
            }
        }
    }

    public static void unregisterActivityLifecycleChangedListener(@NonNull OnActivityLifecycleChangedListener listener) {
        synchronized (LIST_EDIT_LOCK) {
            LinkedList<OnActivityLifecycleChangedListener> lifecycleChangedListeners = INSTANCE.lifecycleChangedListeners;
            if (lifecycleChangedListeners != null) {
                lifecycleChangedListeners.remove(listener);
            }
        }
    }

    public static void observeActivityLifecycleChanged(@NonNull LifecycleOwner owner, @NonNull final OnActivityLifecycleChangedListener listener) {
        if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            throw new IllegalStateException("LifecycleOwner state is DESTROYED");
        }
        registerActivityLifecycleChangedListener(listener);
        owner.getLifecycle().addObserver(new LifecycleChangedAutoUnregisterObserver(listener));
    }


    private static class ActivityLifecycleCallbacksImpl implements Application.ActivityLifecycleCallbacks {

        @NonNull
        private final ActivityMonitor monitor;

        ActivityLifecycleCallbacksImpl(@NonNull ActivityMonitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            final int createdCount;
            synchronized (COUNT_EDIT_LOCK) {
                monitor.createdActivityReverseList.add(0, new WeakReference<>(activity));
                createdCount = monitor.createdActivityReverseList.size();
            }

            synchronized (LIST_EDIT_LOCK) {
                LinkedList<OnActivityCreatedListener> createdListeners = monitor.createdListeners;
                if (createdListeners != null && !createdListeners.isEmpty()) {
                    for (OnActivityCreatedListener listener : createdListeners) {
                        listener.onActivityCreated(activity, savedInstanceState, createdCount == 1);
                    }
                }

                LinkedList<OnActivityLifecycleChangedListener> lifecycleChangedListeners = monitor.lifecycleChangedListeners;
                if (lifecycleChangedListeners != null && !lifecycleChangedListeners.isEmpty()) {
                    for (OnActivityLifecycleChangedListener listener : lifecycleChangedListeners) {
                        listener.onActivityLifecycleChanged(activity, LifecycleEvent.CREATED);
                    }
                }
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            final int startedCount;
            synchronized (COUNT_EDIT_LOCK) {
                monitor.startedActivityReverseList.add(0, new WeakReference<>(activity));
                startedCount = monitor.startedActivityReverseList.size();
            }

            synchronized (LIST_EDIT_LOCK) {
                LinkedList<OnActivityStartedListener> startedListeners = monitor.startedListeners;
                if (startedListeners != null && !startedListeners.isEmpty()) {
                    for (OnActivityStartedListener listener : startedListeners) {
                        listener.onActivityStarted(activity, startedCount == 1);
                    }
                }

                LinkedList<OnActivityLifecycleChangedListener> lifecycleChangedListeners = monitor.lifecycleChangedListeners;
                if (lifecycleChangedListeners != null && !lifecycleChangedListeners.isEmpty()) {
                    for (OnActivityLifecycleChangedListener listener : lifecycleChangedListeners) {
                        listener.onActivityLifecycleChanged(activity, LifecycleEvent.STARTED);
                    }
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            final int resumedCount;
            synchronized (COUNT_EDIT_LOCK) {
                monitor.resumedActivityReverseList.add(0, new WeakReference<>(activity));
                resumedCount = monitor.resumedActivityReverseList.size();
            }

            synchronized (LIST_EDIT_LOCK) {
                LinkedList<OnActivityResumedListener> resumedListeners = monitor.resumedListeners;
                if (resumedListeners != null && !resumedListeners.isEmpty()) {
                    for (OnActivityResumedListener listener : resumedListeners) {
                        listener.onActivityResumed(activity, resumedCount == 1);
                    }
                }

                LinkedList<OnActivityLifecycleChangedListener> lifecycleChangedListeners = monitor.lifecycleChangedListeners;
                if (lifecycleChangedListeners != null && !lifecycleChangedListeners.isEmpty()) {
                    for (OnActivityLifecycleChangedListener listener : lifecycleChangedListeners) {
                        listener.onActivityLifecycleChanged(activity, LifecycleEvent.RESUMED);
                    }
                }
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            final int resumedCount;
            synchronized (COUNT_EDIT_LOCK) {
                Iterator<WeakReference<Activity>> iterator = monitor.resumedActivityReverseList.iterator();
                while (iterator.hasNext()) {
                    WeakReference<Activity> activityWeakReference = iterator.next();
                    Activity cacheActivity = activityWeakReference.get();
                    if (cacheActivity == null || cacheActivity == activity) {
                        iterator.remove();
                    }
                }

                resumedCount = monitor.resumedActivityReverseList.size();
            }

            synchronized (LIST_EDIT_LOCK) {
                LinkedList<OnActivityPausedListener> pausedListeners = monitor.pausedListeners;
                if (pausedListeners != null && !pausedListeners.isEmpty()) {
                    for (OnActivityPausedListener listener : pausedListeners) {
                        listener.onActivityPaused(activity, resumedCount <= 0);
                    }
                }

                LinkedList<OnActivityLifecycleChangedListener> lifecycleChangedListeners = monitor.lifecycleChangedListeners;
                if (lifecycleChangedListeners != null && !lifecycleChangedListeners.isEmpty()) {
                    for (OnActivityLifecycleChangedListener listener : lifecycleChangedListeners) {
                        listener.onActivityLifecycleChanged(activity, LifecycleEvent.PAUSED);
                    }
                }
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            final int startedCount;
            synchronized (COUNT_EDIT_LOCK) {
                Iterator<WeakReference<Activity>> iterator = monitor.startedActivityReverseList.iterator();
                while (iterator.hasNext()) {
                    WeakReference<Activity> activityWeakReference = iterator.next();
                    Activity cacheActivity = activityWeakReference.get();
                    if (cacheActivity == null || cacheActivity == activity) {
                        iterator.remove();
                    }
                }

                startedCount = monitor.startedActivityReverseList.size();
            }

            synchronized (LIST_EDIT_LOCK) {
                LinkedList<OnActivityStoppedListener> stoppedListeners = monitor.stoppedListeners;
                if (stoppedListeners != null && !stoppedListeners.isEmpty()) {
                    for (OnActivityStoppedListener listener : stoppedListeners) {
                        listener.onActivityStopped(activity, startedCount <= 0);
                    }
                }

                LinkedList<OnActivityLifecycleChangedListener> lifecycleChangedListeners = monitor.lifecycleChangedListeners;
                if (lifecycleChangedListeners != null && !lifecycleChangedListeners.isEmpty()) {
                    for (OnActivityLifecycleChangedListener listener : lifecycleChangedListeners) {
                        listener.onActivityLifecycleChanged(activity, LifecycleEvent.STOPPED);
                    }
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            synchronized (LIST_EDIT_LOCK) {
                LinkedList<OnActivitySaveInstanceStateListener> saveInstanceStateListeners = monitor.saveInstanceStateListeners;
                if (saveInstanceStateListeners != null && !saveInstanceStateListeners.isEmpty()) {
                    for (OnActivitySaveInstanceStateListener listener : saveInstanceStateListeners) {
                        listener.onActivitySaveInstanceState(activity, outState);
                    }
                }

                LinkedList<OnActivityLifecycleChangedListener> lifecycleChangedListeners = monitor.lifecycleChangedListeners;
                if (lifecycleChangedListeners != null && !lifecycleChangedListeners.isEmpty()) {
                    for (OnActivityLifecycleChangedListener listener : lifecycleChangedListeners) {
                        listener.onActivityLifecycleChanged(activity, LifecycleEvent.SAVE_INSTANCE_STATE);
                    }
                }
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            final int createdCount;
            synchronized (COUNT_EDIT_LOCK) {
                Iterator<WeakReference<Activity>> iterator = monitor.createdActivityReverseList.iterator();
                while (iterator.hasNext()) {
                    WeakReference<Activity> activityWeakReference = iterator.next();
                    Activity cacheActivity = activityWeakReference.get();
                    if (cacheActivity == null || cacheActivity == activity) {
                        iterator.remove();
                    }
                }

                createdCount = monitor.createdActivityReverseList.size();
            }

            synchronized (LIST_EDIT_LOCK) {
                LinkedList<OnActivityDestroyedListener> destroyedListeners = monitor.destroyedListeners;
                if (destroyedListeners != null && !destroyedListeners.isEmpty()) {
                    for (OnActivityDestroyedListener listener : destroyedListeners) {
                        listener.onActivityDestroyed(activity, createdCount <= 0);
                    }
                }

                LinkedList<OnActivityLifecycleChangedListener> lifecycleChangedListeners = monitor.lifecycleChangedListeners;
                if (lifecycleChangedListeners != null && !lifecycleChangedListeners.isEmpty()) {
                    for (OnActivityLifecycleChangedListener listener : lifecycleChangedListeners) {
                        listener.onActivityLifecycleChanged(activity, LifecycleEvent.DESTROYED);
                    }
                }
            }
        }
    }

    private static class CreatedAutoUnregisterObserver implements LifecycleEventObserver {
        @NonNull
        private final OnActivityCreatedListener listener;

        CreatedAutoUnregisterObserver(@NonNull OnActivityCreatedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                unregisterActivityCreatedListener(listener);
                source.getLifecycle().removeObserver(this);
            }
        }
    }

    private static class StartedAutoUnregisterObserver implements LifecycleEventObserver {
        @NonNull
        private final OnActivityStartedListener listener;

        StartedAutoUnregisterObserver(@NonNull OnActivityStartedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                unregisterActivityStartedListener(listener);
                source.getLifecycle().removeObserver(this);
            }
        }
    }

    private static class ResumedAutoUnregisterObserver implements LifecycleEventObserver {
        @NonNull
        private final OnActivityResumedListener listener;

        ResumedAutoUnregisterObserver(@NonNull OnActivityResumedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                unregisterActivityResumedListener(listener);
                source.getLifecycle().removeObserver(this);
            }
        }
    }

    private static class PausedAutoUnregisterObserver implements LifecycleEventObserver {
        @NonNull
        private final OnActivityPausedListener listener;

        PausedAutoUnregisterObserver(@NonNull OnActivityPausedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                unregisterActivityPausedListener(listener);
                source.getLifecycle().removeObserver(this);
            }
        }
    }

    private static class StoppedAutoUnregisterObserver implements LifecycleEventObserver {
        @NonNull
        private final OnActivityStoppedListener listener;

        StoppedAutoUnregisterObserver(@NonNull OnActivityStoppedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                unregisterActivityStoppedListener(listener);
                source.getLifecycle().removeObserver(this);
            }
        }
    }

    private static class DestroyedAutoUnregisterObserver implements LifecycleEventObserver {
        @NonNull
        private final OnActivityDestroyedListener listener;

        DestroyedAutoUnregisterObserver(@NonNull OnActivityDestroyedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                unregisterActivityDestroyedListener(listener);
                source.getLifecycle().removeObserver(this);
            }
        }
    }

    private static class SaveInstanceStateAutoUnregisterObserver implements LifecycleEventObserver {
        @NonNull
        private final OnActivitySaveInstanceStateListener listener;

        SaveInstanceStateAutoUnregisterObserver(@NonNull OnActivitySaveInstanceStateListener listener) {
            this.listener = listener;
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                unregisterActivitySaveInstanceStateListener(listener);
                source.getLifecycle().removeObserver(this);
            }
        }
    }

    private static class LifecycleChangedAutoUnregisterObserver implements LifecycleEventObserver {
        @NonNull
        private final OnActivityLifecycleChangedListener listener;

        LifecycleChangedAutoUnregisterObserver(@NonNull OnActivityLifecycleChangedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                unregisterActivityLifecycleChangedListener(listener);
                source.getLifecycle().removeObserver(this);
            }
        }
    }
}
