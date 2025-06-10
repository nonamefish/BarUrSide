package com.mingyuwu.barurside

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.util.Util
import com.mingyuwu.barurside.util.Util.getDiffHour
import java.sql.Timestamp

class BarUrSideService : LifecycleService() {

    private var notifications = MutableLiveData<List<com.mingyuwu.barurside.data.Notification>>()
    private val channel = Util.getString(R.string.app_name)
    private lateinit var notificationManager: NotificationManager
    var id = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val repository = (applicationContext as BarUrSideApplication).repository
        notifications = repository.getNotificationChange(UserManager.user.value!!.id)

        createNotificationChannel()

        notifications.observe(this) {
            it?.let { notifications ->
                for (notification in notifications) {

                    when (notification.type) {
                        getString(R.string.activity) -> {
                            if (getDiffHour(notification.timestamp?: Timestamp(0)) < 24) {
                                notify(notification)
                            }
                        }
                        else -> {
                            notify(notification)
                        }
                    }
                }
            }
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        val mChannel = NotificationChannel(channel, channel, NotificationManager.IMPORTANCE_HIGH)
        mChannel.description = Util.getString(R.string.app_name)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    private fun notify(notify: com.mingyuwu.barurside.data.Notification) {

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

        val notification: Notification =
            Notification.Builder(this, channel)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(notify.content.replace("<b>", "").replace("</b>", ""))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build()

        notificationManager.notify(id, notification)
        id += 1
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
    }
}
