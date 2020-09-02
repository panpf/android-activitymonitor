# Android ActivityMonitor

![Platform][platform_image]
[![API][api_image]][api_link]
[![License][license_image]][license_link]

Monitoring the life cycle of all activities through the Application.registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks) method can achieve the following functions:
* Listen to the callback event of the specified state
* Get the first or last Activity in the specified state
* Get the number of activities in a specified state
* Get a list of activities in a specified state

## Getting Started

This library has been published to `jcenter` and private repositories `https://dl.bintray.com/panpf/maven/`, you can freely choose where to download it

Add the following dependencies to your module `build.gradle` file ：

```groovy
implementation "com.github.panpf.activitymonitor:activitymonitor:$LAST_VERSION"
```

Please replace $LAST_VERSION with the latest version：[![Release][version_icon]][version_link]

Dependencies：
* androidx.annotation:annotation: 1.1.0
* androidx.lifecycle:lifecycle-common: 2.2.0

## Use Guide

ActivityMonitor will be automatically initialized by [ActivityMonitorInitProvider], so you can use it directly without worrying about when to initialize

### 1. Listen to the callback event of the specified state

#### Monitor Activity create/start/resume events:

```kotlin
ActivityMonitor.registerActivityCreatedListener(OnActivityCreatedListener { activity: Activity, savedInstanceState: Bundle?, first: Boolean ->
    Log.d("ActivityMonitor", "${activity.getClass().getSimpleName()} created. isFirst=$first")
})
```
The `first` parameter can be used to determine whether it is the first Activity, which is particularly useful for scenarios where you want to do something when the App is created

Other available methods:
* [ActivityMonitor].registerActivityCreatedListener()
* [ActivityMonitor].registerActivityStartedListener()
* [ActivityMonitor].registerActivityResumedListener()
* [ActivityMonitor].unregisterActivityCreatedListener()
* [ActivityMonitor].unregisterActivityStartedListener()
* [ActivityMonitor].unregisterActivityResumedListener()

#### Monitor Activity pause/stop/destroy events:
```kotlin
ActivityMonitor.registerActivityDestroyedListener(OnActivityDestroyedListener { activity: Activity, last: Boolean ->
    Log.d("ActivityMonitor", "${activity.getClass().getSimpleName()} destroyed. isLast=$last")
})
```
The `last` parameter can be used to determine whether it is the last Activity, which is especially useful for scenarios where you want to do something when the App exits

Other available methods:
* [ActivityMonitor].registerActivityPausedListener()
* [ActivityMonitor].registerActivityStoppedListener()
* [ActivityMonitor].registerActivitySaveInstanceStateListener()
* [ActivityMonitor].registerActivityDestroyedListener()
* [ActivityMonitor].unregisterActivityPausedListener()
* [ActivityMonitor].unregisterActivityStoppedListener()
* [ActivityMonitor].unregisterActivitySaveInstanceStateListener()
* [ActivityMonitor].unregisterActivityDestroyedListener()

### Use observe series methods to automatically unregister

When Activity or Fragment is destroyed, observe series methods can be automatically logged out with the help of LifecycleOwner. The available methods are as follows:
* [ActivityMonitor].observeActivityCreatedListener()
* [ActivityMonitor].observeActivityStartedListener()
* [ActivityMonitor].observeActivityResumedListener()
* [ActivityMonitor].observeActivityPausedListener()
* [ActivityMonitor].observeActivityStoppedListener()
* [ActivityMonitor].observeActivitySaveInstanceStateListener()
* [ActivityMonitor].observeActivityDestroyedListener()

### 2. Get the first or last activity in the specified state

In some cases, you need to remind the user through DialogFragment when performing background tasks, and then DialogFragment needs to rely on Activity. In this case, you can get the last available Activity through [ActivityMonitor].getLastCreatedActivity() to display DialogFragment, as follows:
```kotlin
val dialog: DialogFragment = ...
val lastCreatedActivity: Activity = ActivityMonitor.getLastCreatedActivity()
if (lastCreatedActivity is FragmentActivity) {
    dialog.show(lastCreatedActivity.supportFragmentManager, null)
}
```

Other available methods:
* [ActivityMonitor].getFirstCreatedActivity()
* [ActivityMonitor].getFirstStartedActivity()
* [ActivityMonitor].getFirstResumedActivity()
* [ActivityMonitor].getLastCreatedActivity()
* [ActivityMonitor].getLastStartedActivity()
* [ActivityMonitor].getLastResumedActivity()

### 3. Get the number of activities in a specified state

Sometimes we need to determine whether the current application is in the foreground in the background task, this can be achieved through the [ActivityMonitor].getStartedActivityCount() method

```kotlin
val startedActivityCount: Int = ActivityMonitor.getStartedActivityCount()
if (startedActivityCount > 0) {
    Log.d("ActivityMonitor", "Running foreground")
} else {
    Log.d("ActivityMonitor", "Running background")
}
```

Other available methods:
* [ActivityMonitor].getCreatedActivityCount()
* [ActivityMonitor].getStartedActivityCount()
* [ActivityMonitor].getResumedActivityCount()

### 4. Get a list of activities in a specified state

```kotlin
val createdActivityList: List<Activity> = ActivityMonitor.getCreatedActivityList()
for (activity: Activity in createdActivityList) {
    Log.d("ActivityMonitor", activity.getClass().getSimpleName());
}
```

Other available methods:
* [ActivityMonitor].getCreatedActivityList()
* [ActivityMonitor].getStartedActivityList()
* [ActivityMonitor].getResumedActivityList()

### License
    Copyright (C) 2020 panpf <panpfpanpf@outlook.com>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[platform_image]: https://img.shields.io/badge/Platform-Android-brightgreen.svg
[api_image]: https://img.shields.io/badge/API-14%2B-orange.svg
[api_link]: https://android-arsenal.com/api?level=14
[version_icon]: https://api.bintray.com/packages/panpf/maven/activitymonitor/images/download.svg
[version_link]: https://bintray.com/panpf/maven/activitymonitor/_latestVersion#files
[license_image]: https://img.shields.io/badge/License-Apache%202-blue.svg
[license_link]: https://www.apache.org/licenses/LICENSE-2.0

[ActivityMonitor]: activitymonitor/src/main/java/com/github/panpf/activity/monitor/ActivityMonitor.java
[ActivityMonitorInitProvider]: activitymonitor/src/main/java/com/github/panpf/activity/monitor/ActivityMonitorInitProvider.java