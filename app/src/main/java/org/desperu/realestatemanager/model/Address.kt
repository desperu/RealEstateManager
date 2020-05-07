package org.desperu.realestatemanager.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.desperu.realestatemanager.utils.EQUALS
import org.desperu.realestatemanager.utils.NOT_EQUALS

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
                   var longitude: Double = 0.0
): Parcelable, Comparable<Address> {

    /**
     * Compare this address to another address. Compare fields one to one,
     * if one field is different with the other one, return 1,
     * else the two addresses are equals, return 0
     *
     * @param other the other address to compare with.
     *
     * @return {@code 0} if equals, {@code 1} otherwise.
     */
    override fun compareTo(other: Address): Int = when {

        estateId != other.estateId -> NOT_EQUALS
        streetNumber != other.streetNumber -> NOT_EQUALS
        streetName != other.streetName -> NOT_EQUALS
        flatBuilding != other.flatBuilding -> NOT_EQUALS
        postalCode != other.postalCode -> NOT_EQUALS
        city != other.city -> NOT_EQUALS
        country != other.country -> NOT_EQUALS
        latitude != other.latitude -> NOT_EQUALS
        longitude != other.longitude -> NOT_EQUALS

        else -> EQUALS
    }
}