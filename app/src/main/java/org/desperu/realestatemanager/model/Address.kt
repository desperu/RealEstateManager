package org.desperu.realestatemanager.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Class witch provides a model for address
 */
@Entity(foreignKeys = [ForeignKey(entity = Estate::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("estateId"))])
data class Address(@PrimaryKey(autoGenerate = false)
                   val estateId: Long,
                   var streetNumber: Int,
                   var streetName: String,
                   var flatBuilding:String,
                   var postalCode: Int,
                   var city: String,
                   var country: String)