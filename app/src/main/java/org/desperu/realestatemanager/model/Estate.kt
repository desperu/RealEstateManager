package org.desperu.realestatemanager.model

import android.content.ContentValues
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.desperu.realestatemanager.utils.EQUALS
import org.desperu.realestatemanager.utils.NOT_EQUALS


/**
 * Class witch provides a model for estate.
 *
 * @constructor Sets all properties of the estate.
 *
 * @param id Unique identifier of the estate.
 * @param type Type of the estate.
 * @param price Price of the estate.
 * @param surfaceArea Surface area of the estate.
 * @param roomNumber Rooms number of the estate.
 * @param description Complete description of the estate.
 * @param interestPlaces Interest places near the estate.
 * @param state Sale state of the estate.
 * @param saleDate Sale date of the estate.
 * @param soldDate Sold date of the estate.
 * @param realEstateAgent Real Estate Agent witch manage estate sale.
 * @param createdTime the time in millis at witch the estate was created.
 * @param imageList the list of images associates with this estate.
 * @param address the address of this estate.
 */
@Parcelize
@Entity
data class Estate(@PrimaryKey(autoGenerate = true)
                  var id: Long = 0L,
                  var type: String = "",
                  var price: Long = 0L,
                  var surfaceArea: Int = 0,
                  var roomNumber: Int = 0,
                  var description: String = "",
                  var interestPlaces: String = "",
                  var state: String = "",
                  var saleDate: String = "",
                  var soldDate: String = "",
                  var realEstateAgent: String = "",
                  var createdTime: Long = 0L,
                  @Ignore
                  var imageList: MutableList<Image> = mutableListOf(),
                  @Ignore
                  var address: Address = Address()
): Parcelable, Comparable<Estate> {

    /**
     * Compare this estate to another estate. Compare fields one to one,
     * if one field is different with the other, return 1,
     * else the two estates are equals, return 0
     *
     * @param other the other estate to compare with.
     *
     * @return {@code 0} if equals, {@code 1} otherwise.
     */
    override fun compareTo(other: Estate) = when {

        id != other.id -> NOT_EQUALS
        type != other.type -> NOT_EQUALS
        price != other.price -> NOT_EQUALS
        surfaceArea != other.surfaceArea -> NOT_EQUALS
        roomNumber != other.roomNumber -> NOT_EQUALS
        description != other.description -> NOT_EQUALS
        interestPlaces != other.interestPlaces -> NOT_EQUALS
        state != other.state -> NOT_EQUALS
        saleDate != other.saleDate -> NOT_EQUALS
        soldDate != other.soldDate -> NOT_EQUALS
        realEstateAgent != other.realEstateAgent -> NOT_EQUALS

        // Use compareTo functions of the child objects.
        imageList.size != other.imageList.size -> NOT_EQUALS
        imageList.withIndex().find { it.value.compareTo(other.imageList[it.index]) != EQUALS }  != null -> NOT_EQUALS

        address.compareTo(other.address) != EQUALS -> NOT_EQUALS

        else -> EQUALS
    }

    /**
     * Get estate data from content values.
     * @param values the content value to get data from.
     * @return the Estate object created from content values.
     */
    fun fromContentValues(values: ContentValues?): Estate {
        val estate = Estate()
        if (values != null) {
            if (values.containsKey("id")) estate.id = values.getAsLong("id")
            if (values.containsKey("type")) estate.type = values.getAsString("type")
            if (values.containsKey("price")) estate.price = values.getAsLong("price")
            if (values.containsKey("surfaceArea")) estate.surfaceArea = values.getAsInteger("surfaceArea")
            if (values.containsKey("roomNumber")) estate.roomNumber = values.getAsInteger("roomNumber")
            if (values.containsKey("description")) estate.description = values.getAsString("description")
            if (values.containsKey("interestPlaces")) estate.interestPlaces = values.getAsString("interestPlaces")
            if (values.containsKey("state")) estate.state = values.getAsString("state")
            if (values.containsKey("saleDate")) estate.saleDate = values.getAsString("saleDate")
            if (values.containsKey("soldDate")) estate.soldDate = values.getAsString("soldDate")
            if (values.containsKey("realEstateAgent")) estate.realEstateAgent = values.getAsString("realEstateAgent")
            if (values.containsKey("createdTime")) estate.createdTime = values.getAsLong("createdTime")
        }
        return estate
    }
}