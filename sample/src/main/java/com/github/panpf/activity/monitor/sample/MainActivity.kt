package com.github.panpf.activity.monitor.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.panpf.activity.monitor.ActivityMonitor
import com.github.panpf.activity.monitor.OnActivityLifecycleChangedListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        main_finishButton.setOnClickListener {
            finish()
        }

        main_startButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        ActivityMonitor.observeActivityLifecycleChanged(this, OnActivityLifecycleChangedListener { _, _ -> refreshTest() })

        startService(Intent(this, StatService::class.java))
    }

    private fun refreshTest() {
        main_text.text = buildString {
            append("CurrentPage: ${this@MainActivity.toSimpleString()}")
            append("\n")
            append("\n")
            append("CreatedCount: ${ActivityMonitor.getCreatedActivityCount()}")
            append("\n")
            append("StartedCount: ${ActivityMonitor.getStartedActivityCount()}")
            append("\n")
            append("ResumedCount: ${ActivityMonitor.getResumedActivityCount()}")
            append("\n")
            append("\n")
            append("FirstCreated: ${ActivityMonitor.getFirstCreatedActivity().toSimpleString()}")
            append("\n")
            append("FirstStarted: ${ActivityMonitor.getFirstStartedActivity().toSimpleString()}")
            append("\n")
            append("FirstResumed: ${ActivityMonitor.getFirstResumedActivity().toSimpleString()}")
            append("\n")
            append("\n")
            append("LastCreated: ${ActivityMonitor.getLastCreatedActivity().toSimpleString()}")
            append("\n")
            append("LastStarted: ${ActivityMonitor.getLastStartedActivity().toSimpleString()}")
            append("\n")
            append("LastResumed: ${ActivityMonitor.getLastResumedActivity().toSimpleString()}")
            append("\n")
            append("\n")
            append("CreatedList: ${ActivityMonitor.getCreatedActivityList()?.joinToString(separator = " -> ") { it.toSimpleString() }}")
            append("\n")
            append("StartedList: ${ActivityMonitor.getStartedActivityList()?.joinToString(separator = " -> ") { it.toSimpleString() }}")
            append("\n")
            append("ResumedList: ${ActivityMonitor.getResumedActivityList()?.joinToString(separator = " -> ") { it.toSimpleString() }}")
        }
    }
}