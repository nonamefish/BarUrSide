package com.mingyuwu.barurside

import android.app.*
import android.content.Intent
import android.util.Log
import com.mingyuwu.barurside.login.UserManager
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.mingyuwu.barurside.util.Util.getDiffHour

class BarUrSideService : LifecycleService() {

    private var notifications = MutableLiveData<List<com.mingyuwu.barurside.data.Notification>>()
    private val channel = "BarUrSide"
    private lateinit var notificationManager: NotificationManager
    var id = 0

    override fun onCreate() {
        super.onCreate()
        Log.d("Ming", "Service onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val repository = (applicationContext as BarUrSideApplication).repository
        notifications = repository.getNotificationChange(UserManager.user.value!!.id)

        createNotificationChannel()

        notifications.observe(this) {
            Log.d("Ming", "Service notifications: $it")
            it?.let { notifications ->
                for (notification in notifications) {
                    when(notification.type){
                        "activity" -> {
                            if(getDiffHour(notification.timestamp!!) < 24){
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

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        val mChannel = NotificationChannel(channel, channel, NotificationManager.IMPORTANCE_HIGH)
        mChannel.description = "BarUrSide"
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    private fun notify(notify: com.mingyuwu.barurside.data.Notification) {

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
            }

        val notification: Notification = Notification.Builder(this, channel)
            .setContentTitle("BarUrSide")
            .setContentText(notify.content.replace("<b>","").replace("</b>",""))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id, notification)
        id += 1
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("Ming", "Service onDestroy")
    }
}