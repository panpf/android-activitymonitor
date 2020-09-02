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

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class ActivityMonitorInitProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context != null) {
            ActivityMonitor.init((Application) context.getApplicationContext());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Cursor query(@SuppressWarnings("NullableProblems") Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(@SuppressWarnings("NullableProblems") Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@SuppressWarnings("NullableProblems") Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@SuppressWarnings("NullableProblems") Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@SuppressWarnings("NullableProblems") Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
