package org.desperu.realestatemanager.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Class witch provides a model for image
 */
@Entity(foreignKeys = [ForeignKey(entity = Estate::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("estateId"))])
data class Image(@PrimaryKey(autoGenerate = true)
                 var id: Long,
                 var estateId: Long,
                 var imageUri: String,
                 val isPrimary: Boolean = false,
                 var description: String)