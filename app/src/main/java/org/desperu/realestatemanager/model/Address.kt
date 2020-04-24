package org.desperu.realestatemanager.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Class witch provides a model for address.
 * @param estateId Unique identifier of the estate.
 * @param streetNumber Street number of the address.
 * @param streetName Street name of the address.
 * @param flatBuilding Flat, building complementary information's.
 * @param postalCode Postal code of the address.
 * @param city City of the address.
 * @param country Country of the address.
 * @param latitude the latitude of the address.
 * @param longitude the longitude of the address.
 */
@Parcelize
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
                   var country: String = "",
                   var latitude: Double = 0.0,
                   var longitude: Double = 0.0): Parcelable