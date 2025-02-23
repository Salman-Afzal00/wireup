package com.mani.wirup

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class TaskCompletionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("TASK_TITLE")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification
        val notification = NotificationCompat.Builder(context, "task_channel")
            .setContentTitle("Task Completed")
            .setContentText("Task: $taskTitle")
            .setSmallIcon(R.drawable.app)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Show the notification
        notificationManager.notify(taskTitle.hashCode(), notification)
    }
}