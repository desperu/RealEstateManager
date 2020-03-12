package org.desperu.realestatemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Class witch provides a model for estate
 */
@Entity
data class Estate(@PrimaryKey(autoGenerate = true)
                  var id: Long,
                  var type: String, // TODO create other table?
                  var price: Int,
                  var surfaceArea: Int,
                  var rooms: Int,
                  var description: String,
                  var address: String, // Create other table ??
                  var interestPlaces: String, // Create other table ??
                  var state: Int, // 0 to sale, 1 sold
                  var saleDate: String, // Convert function Date to String and String to Date
                  var soldDate: String,
                  var realtor: String)