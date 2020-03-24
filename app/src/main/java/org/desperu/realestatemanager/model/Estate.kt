package org.desperu.realestatemanager.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Class witch provides a model for estate
 */
@Entity
data class Estate(@PrimaryKey(autoGenerate = true)
                  var id: Long,
                  var type: String, // TODO use val instead var
                  var price: Long,
                  var surfaceArea: Int,
                  var rooms: Int,
                  var description: String,
                  var interestPlaces: String, // Create other table ??
                  var state: String, // 0 to sale, 1 sold
                  var saleDate: String, // Convert function Date to String and String to Date
                  var soldDate: String,
                  var realEstateAgent: String) {
    @Ignore lateinit var images: List<Image>
    @Ignore lateinit var address: Address
}