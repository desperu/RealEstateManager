package org.desperu.realestatemanager.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Class witch provides a model for estate.
 * @constructor Sets all properties of the estate.
 * @param id Unique identifier of the estate.
 * @param type Type of the estate.
 * @param price Price of the esate.
 * @param surfaceArea Surface area of the estate.
 * @param roomNumber Rooms number of the estate.
 * @param description Complete description of the estate.
 * @param interestPlaces Interest places near the estate.
 * @param state Sale state of the estate.
 * @param saleDate Sale date of the estate.
 * @param soldDate Sold date of the estate.
 * @param realEstateAgent Real Estate Agent witch manage estate sale.
 */
@Parcelize
@Entity
data class Estate(@PrimaryKey(autoGenerate = true)
                  var id: Long = 0,
                  var type: String = "",
                  var price: Long = 0,
                  var surfaceArea: Int = 0,
                  var roomNumber: Int = 0,
                  var description: String = "",
                  var interestPlaces: String = "",
                  var state: String = "",
                  var saleDate: String = "",
                  var soldDate: String = "",
                  var realEstateAgent: String = "",
                  @Ignore
                  var imageList: MutableList<Image> = mutableListOf(),
                  @Ignore
                  var address: Address = Address()
): Parcelable