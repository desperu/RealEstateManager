package org.desperu.realestatemanager.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Class witch provides a model for address.
 * @param estateId Unique identifier of the estate.
 * @param streetNumber Street number of the address.
 * @param streetName Street name of the address.
 * @param flatBuilding Flat, building complementary information's.
 * @param postalCode Postal code of the address.
 * @param city City of the address.
 * @param country Country of the address.
 */
@Entity(foreignKeys = [ForeignKey(entity = Estate::class,
        parentColumns = ["id"],
        childColumns = ["estateId"])])
data class Address(@PrimaryKey(autoGenerate = false)
                   var estateId: Long = 0,
                   var streetNumber: Int = 0,
                   var streetName: String = "",
                   var flatBuilding: String = "",
                   var postalCode: Int = 0,
                   var city: String = "",
                   var country: String = "")