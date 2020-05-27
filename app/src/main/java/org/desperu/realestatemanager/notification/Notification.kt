package org.desperu.realestatemanager.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_LIGHTS
import androidx.core.app.NotificationCompat.DEFAULT_SOUND
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.extension.rotate
import org.desperu.realestatemanager.extension.toBitmap
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.MainActivity
import org.desperu.realestatemanager.ui.main.ESTATE_NOTIFICATION
import org.desperu.realestatemanager.utils.Utils.convertPriceToPatternPrice


// FOR NOTIFICATION
private const val CHANNEL_ID = "RealEstateManagerNotification"
private const val NOTIFICATION_NAME = "CreatedEstate"
private const val NOTIFICATION_ID = 1
private const val CHANNEL_DESCRIPTION = "Real Estate Manager notifications channel"

/**
 * Class to create and sent a notification when an estate was created or updated.
 */
class Notification {

    /**
     * Create notification, and set on click.
     * @param context the context from this function is called.
     * @param estate the created or updated estate.
     */
    internal fun createNotification(context: Context, estate: Estate, isNew: Boolean) {
        // Create notification.
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo_outlined)
                .setContentTitle(context.getString(if (isNew) R.string.notification_title_create
                                                   else R.string.notification_title_update))
                .setContentText(getContentText(context, estate))
                .setLargeIcon(getPrimaryImage(context, estate))
                .setDefaults(DEFAULT_LIGHTS or DEFAULT_SOUND)
                .setAutoCancel(true)

        // Create intent for notification click.
        val resultIntent: Intent = Intent(context, MainActivity::class.java)
                .putExtra(ESTATE_NOTIFICATION, true)

        // Add activity to the top of stack.
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(resultIntent)

        // Adds the intent that starts the activity.
        val resultPendingIntent: PendingIntent? = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(resultPendingIntent)

        // Notification Manager instance.
        val notificationManagerCompat = NotificationManagerCompat.from(context)

        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, NOTIFICATION_NAME, importance)
            mChannel.description = CHANNEL_DESCRIPTION
            notificationManagerCompat.createNotificationChannel(mChannel)
        }

        // Show notification.
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
    }

    /**
     * Get the content text for the notification.
     * @param context the context from this function is called.
     * @param estate the created or updated estate.
     * @return the content text to show.
     */
    private fun getContentText(context: Context, estate: Estate): String = when {

        estate.type.isNotBlank() && estate.address.city.isNotBlank() && estate.price != 0L ->
            context.resources.getString(R.string.notification_full_content_text,
                    estate.type,
                    estate.address.city,
                    convertPriceToPatternPrice(estate.price.toString(), true))

        estate.type.isNotBlank() && estate.address.city.isNotBlank() ->
            context.resources.getString(R.string.fragment_estate_detail_text_title_at,
                    estate.type,
                    estate.address.city)

        estate.type.isNotBlank() -> estate.type

        estate.address.city.isNotBlank() -> estate.address.city

        else -> context.resources.getString(R.string.fragment_estate_detail_text_no_data)
    }

    /**
     * Get the primary image of the estate, if not set, use the first image in list, if no image, use the apk logo.
     * @param context the context from this function is called.
     * @param estate the created or updated estate.
     * @return the bitmap to show.
     */
    @Suppress("deprecation")
    private fun getPrimaryImage(context: Context, estate: Estate): Bitmap? =
        if (!estate.imageList.isNullOrEmpty()) {
            val estateImage = (estate.imageList.find { it.isPrimary } ?: estate.imageList[0])
            estateImage.imageUri.toUri().toBitmap(context.contentResolver!!)?.rotate(estateImage.rotation)
        } else
            (context.resources.getDrawable(R.drawable.app_logo) as BitmapDrawable).bitmap
}