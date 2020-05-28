package org.desperu.realestatemanager.model

import android.content.ContentValues
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.desperu.realestatemanager.utils.EQUALS
import org.desperu.realestatemanager.utils.NOT_EQUALS

/**
 * Class witch provides a model for image
 * @param id Unique identifier of the image.
 * @param estateId Unique identifier of the estate.
 * @param imageUri Uri of the image.
 * @param isPrimary True if this image is the primary image of the estate.
 * @param description Description of the image.
 * @param rotation the image rotation.
 */
@Parcelize
@Entity(foreignKeys = [ForeignKey(entity = Estate::class,
        parentColumns = ["id"],
        childColumns = ["estateId"])],
        indices = [Index(name = "estateId_index", value = ["estateId"])])
data class Image(@PrimaryKey(autoGenerate = true)
                 val id: Long = 0L,
                 var estateId: Long = 0L,
                 var imageUri: String = "",
                 var isPrimary: Boolean = false,
                 var description: String = "",
                 var rotation: Float = 0F
): Parcelable, Comparable<Image> {

    /**
     * Compare this image to another image. Compare fields one to one,
     * if one field is different with the other one, return 1,
     * else the two images are equals, return 0.
     *
     * @param other the other image to compare with.
     *
     * @return {@code 0} if equals, {@code 1} otherwise.
     */
    override fun compareTo(other: Image): Int = when {

        id != other.id -> NOT_EQUALS
        estateId != other.estateId -> NOT_EQUALS
        imageUri != other.imageUri -> NOT_EQUALS
        isPrimary != other.isPrimary -> NOT_EQUALS
        description != other.description -> NOT_EQUALS
        rotation != other.rotation -> NOT_EQUALS

        else -> EQUALS
    }

    /**
     * Get image data from content values.
     * @param values the content value to get data from.
     * @return the Image object created from content values.
     */
    fun fromContentValues(values: ContentValues?): Image {
        val image = values?.getAsLong("id")?.let { Image(id = it) } ?: Image()
        if (values != null) {
            if (values.containsKey("estateId")) image.estateId = values.getAsLong("estateId")
            if (values.containsKey("imageUri")) image.imageUri = values.getAsString("imageUri")
            if (values.containsKey("isPrimary")) image.isPrimary = values.getAsBoolean("isPrimary")
            if (values.containsKey("description")) image.description = values.getAsString("description")
            if (values.containsKey("rotation")) image.rotation = values.getAsFloat("rotation")
        }
        return image
    }
}