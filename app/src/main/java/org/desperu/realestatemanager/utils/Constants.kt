package org.desperu.realestatemanager.utils

import android.Manifest

// FOR INTENT

const val RC_ESTATE = 10


// FOR EXCHANGE

const val exchangeRate = 0.812


// FOR MAIN FRAGMENTS

// Fragment identifier
const val FRAG_ESTATE_LIST = 0
const val FRAG_ESTATE_MAP = 1
const val FRAG_ESTATE_DETAIL = 2
const val FRAG_ESTATE_FILTER = 3

// FOR VIEW PAGER

const val numberOfPage = 4

// Fragment identifier
const val ESTATE_DATA = 0
const val ESTATE_IMAGE = 1
const val ESTATE_ADDRESS = 2
const val ESTATE_SALE = 3


// FOR GOOGLE MAPS

// To replace button

// Map Toolbar Tag
const val GOOGLE_MAP_TOOLBAR = "GoogleMapToolbar"

// Zoom button Tag
const val GOOGLE_MAP_ZOOM_OUT_BUTTON = "GoogleMapZoomOutButton"

// For the map mode
const val LITTLE_MODE = 0
const val FULL_MODE = 1

// For map animation marker
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


// FOR SAVE IMAGE IN STORAGE

const val FOLDER_NAME = "EstateImages"


// FOR MODELS COMPARISON RESULT

const val EQUALS = 0
const val NOT_EQUALS = 1


// FOR SETTINGS ACTIVITY

// For alert dialog
const val ZOOM_DIALOG = 0
const val RESET_DIALOG = 1


// FOR SHARED PREFERENCES

// Shared preferences keys
const val NOTIFICATION_ENABLED = "notificationEnabled"
const val DISABLE_UPDATE_NOTIFICATION = "disableUpdateNotification"
const val MAP_ZOOM_LEVEL = "mapZoomLevel"
const val MAP_ZOOM_BUTTON = "mapZoomButton"


// Settings default value
const val NOTIFICATION_DEFAULT = true
const val UPDATE_NOTIFICATION_DEFAULT = true
const val ZOOM_LEVEL_DEFAULT = 13
const val ZOOM_BUTTON_DEFAULT = true