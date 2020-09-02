package com.github.panpf.activity.monitor.sample

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.github.panpf.activity.monitor.ActivityMonitor
import com.github.panpf.activity.monitor.OnActivityDestroyedListener
import com.github.panpf.activity.monitor.OnActivityLifecycleChangedListener

class StatService : Service() {

    private var listener: MyListener? = null
    private var autoDismissListener = AutoDismissListener(this)

    private var notificationBuilder: NotificationCompat.Builder? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val notificationManager: NotificationManager = baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel("ACTIVITY_LIFECYCLE_STAT", "Activity Lifecycle Stat", NotificationManager.IMPORTANCE_DEFAULT))
        }
        notificationBuilder = NotificationCompat.Builder(applicationContext, "ACTIVITY_LIFECYCLE_STAT")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(false)
        refreshNotification()

        val newListener = MyListener(this)
        listener = newListener
        ActivityMonitor.registerActivityLifecycleChangedListener(newListener)
        ActivityMonitor.registerActivityDestroyedListener(autoDismissListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        listener?.let { ActivityMonitor.unregisterActivityLifecycleChangedListener(it) }
        ActivityMonitor.unregisterActivityDestroyedListener(autoDismissListener)
    }

    fun refreshNotification() {
        val notificationBuilder: NotificationCompat.Builder = this.notificationBuilder ?: return
        notificationBuilder.setContentTitle("ActivityCount: ${ActivityMonitor.getCreatedActivityCount()}/${ActivityMonitor.getStartedActivityCount()}/${ActivityMonitor.getResumedActivityCount()}")
        notificationBuilder.setContentText("LastActivity: ${ActivityMonitor.getLastCreatedActivity()?.hashCode().toHexString()}" +
                "/${ActivityMonitor.getLastStartedActivity()?.hashCode().toHexString()}" +
                "/${ActivityMonitor.getLastResumedActivity()?.hashCode().toHexString()}")
        startForeground(101, notificationBuilder.build())
    }

    class MyListener(private val statService: StatService) : OnActivityLifecycleChangedListener {
        override fun onActivityLifecycleChanged(activity: Activity, newState: Int) {
            statService.refreshNotification()
        }
    }

    class AutoDismissListener(private val statService: StatService) : OnActivityDestroyedListener {
        override fun onActivityDestroyed(activity: Activity, last: Boolean) {
            if (last) {
                statService.stopSelf()
            }
        }
    }
}