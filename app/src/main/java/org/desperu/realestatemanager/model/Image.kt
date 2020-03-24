package org.desperu.realestatemanager.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Class witch provides a model for image
 */
@Entity(foreignKeys = [ForeignKey(entity = Estate::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("estateId"))],
        indices = [Index(name = "estateId_index", value = ["estateId"])])
data class Image(@PrimaryKey(autoGenerate = true)
                 val id: Long,
                 val estateId: Long,
                 val imageUri: String,
                 val isPrimary: Boolean = false,
                 val description: String)