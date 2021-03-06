package com.media.dmitry68.callrecorder.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.media.dmitry68.callrecorder.MainActivity
import com.media.dmitry68.callrecorder.R
import com.media.dmitry68.callrecorder.preferences.ManagerPref

class NotifyManager(private val context: Context) {
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val contentTitle: String = context.getString(com.media.dmitry68.callrecorder.R.string.notification_title)
    private var channelName: String = context.getString(com.media.dmitry68.callrecorder.R.string.channel_name)//TODO: make manager resource
    private val managerPref = ManagerPref(context)
    private val modeOfWorkString = managerPref.getStringModeOfWorkInSharedPref()
    var contentText: String = StringBuilder().apply{
            append(context.getString(R.string.notification_text_mode_of_work))
            append(": ")
            append(modeOfWorkString)
           // append(context.getString(R.string.notification_text_recorder_status))
        }.toString()

    fun builder() : NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel()
        }
        notificationBuilder=
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))

        val resultIntent = Intent(context, MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(context).apply {
            addParentStack(MainActivity::class.java)
            addNextIntent(resultIntent)
        }
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(resultPendingIntent)
        return notificationBuilder
    }

    fun addAction(actionIntent: String, actionText: String, actionIcon: Int){
        val intentForBroadcast = Intent(actionIntent)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intentForBroadcast, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.addAction(NotificationCompat.Action(actionIcon, actionText, pendingIntent))
        updateNotification()
    }

    fun removeAction(){
        notificationBuilder.mActions.clear()
        updateNotification()
    }

    fun isEmptyOfNotificationActions() : Boolean = notificationBuilder.mActions.isEmpty()

    fun removeNotification() = notificationManager.cancel(NOTIFICATION_ID)

    fun addText(text: String){
        notificationBuilder.setContentText(text)
        updateNotification()
    }

    private fun updateNotification() = notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(){
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(CHANNEL_ID, channelName, importance)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "100"
    }
}