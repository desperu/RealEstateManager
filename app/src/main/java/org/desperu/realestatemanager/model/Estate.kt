package org.desperu.realestatemanager.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

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
@Entity
data class Estate(@PrimaryKey(autoGenerate = true)
                  val id: Long = 0,
                  var type: String = "", // TODO use val instead var
                  var price: Long = 0,
                  var surfaceArea: Int = 0,
                  var roomNumber: Int = 0,
                  var description: String = "",
                  var interestPlaces: String = "", // Create other table ??
                  var state: String = "", // 0 to sale, 1 sold
                  var saleDate: String = "", // Convert function Date to String and String to Date
                  var soldDate: String = "",
                  var realEstateAgent: String = "") {

    // Image list of the estate.
    @Ignore lateinit var imageList: ArrayList<Image>

    // Address of the estate.
    @Ignore lateinit var address: Address
}