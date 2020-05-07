package org.desperu.realestatemanager.utils

import android.Manifest

// FOR INTENT

const val RC_ESTATE = 10


// FOR EXCHANGE

const val exchangeRate = 0.812


// FOR VIEW PAGER

const val numberOfPage = 4

// Fragment identifier
const val ESTATE_DATA = 0
const val ESTATE_IMAGE = 1
const val ESTATE_ADDRESS = 2
const val ESTATE_SALE = 3


// FOR GOOGLE MAPS

// TO REPLACE BUTTON

// Map Toolbar Tag
const val GOOGLE_MAP_TOOLBAR = "GoogleMapToolbar"

// Zoom button Tag
const val GOOGLE_MAP_ZOOM_OUT_BUTTON = "GoogleMapZoomOutButton"

// FOR MAP ANIMATION MARKER

const val LITTLE_SIZE = "littleSize"
const val FULL_SIZE = "fullSize"


// FOR PERMISSIONS

// Images
const val RC_PERMS_STORAGE = 100
const val RC_CHOOSE_PHOTO = 200
const val RC_PERMS_PHOTO = 300
const val RC_TAKE_PHOTO = 400

// Location
const val PERM_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
const val PERM_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
const val RC_PERM_LOCATION = 500


// FOR SAVE IMAGE

const val FOLDER_NAME = "EstateImages"


// FOR COMPARISON RESULT


const val EQUALS = 0
const val NOT_EQUALS = 1