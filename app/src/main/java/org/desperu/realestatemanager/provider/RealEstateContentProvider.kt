package org.desperu.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import kotlinx.coroutines.runBlocking
import org.desperu.realestatemanager.model.Address
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image
import org.desperu.realestatemanager.repositories.AddressRepository
import org.desperu.realestatemanager.repositories.EstateRepository
import org.desperu.realestatemanager.repositories.ImageRepository
import org.koin.android.ext.android.inject

/**
 * Content provider class that allow database access for estate, image and address tables from others applications.
 *
 * @constructor Instantiate a new EstateContentProvider.
 */
class RealEstateContentProvider : ContentProvider() {

    private var estateRepo = inject<EstateRepository>()
    private var imageRepo = inject<ImageRepository>()
    private var addressRepo = inject<AddressRepository>()

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, ESTATE_TABLE_NAME, ESTATE)
        addURI(AUTHORITY, "$ESTATE_TABLE_NAME/#", ESTATE)
        addURI(AUTHORITY, IMAGE_TABLE_NAME, IMAGE)
        addURI(AUTHORITY, "$IMAGE_TABLE_NAME/#", IMAGE)
        addURI(AUTHORITY, ADDRESS_TABLE_NAME, ADDRESS)
        addURI(AUTHORITY, "$ADDRESS_TABLE_NAME/#", ADDRESS)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(uri: Uri, projection: Array<String?>?, selection: String?,
                       selectionArgs: Array<String?>?, sortOrder: String?): Cursor? {
        if (context != null) {
            val id = ContentUris.parseId(uri)
            val cursor: Cursor? = when (uriMatcher.match(uri)) {
                ESTATE -> estateRepo.value.getEstateWithCursor(id)
                IMAGE -> imageRepo.value.getImageWithCursor(id)
                ADDRESS -> addressRepo.value.getAddressWithCursor(id)
                else -> null
            }
            cursor?.setNotificationUri(context!!.contentResolver, uri)
            return cursor
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String = when (uriMatcher.match(uri)) {
        ESTATE -> "vnd.android.cursor.item/$AUTHORITY.$ESTATE_TABLE_NAME"
        IMAGE -> "vnd.android.cursor.item/$AUTHORITY.$IMAGE_TABLE_NAME"
        ADDRESS -> "vnd.android.cursor.item/$AUTHORITY.$ADDRESS_TABLE_NAME"
        else -> throw IllegalArgumentException("Unsupported URI: $uri")
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri = runBlocking {
        if (context != null) {
            val id: Long = when (uriMatcher.match(uri)) {
                ESTATE -> estateRepo.value.createEstate(Estate().fromContentValues(contentValues))
                IMAGE -> imageRepo.value.createImage(Image().fromContentValues(contentValues))[0]
                ADDRESS -> addressRepo.value.createAddress(Address().fromContentValues(contentValues))
                else -> 0L
            }

            if (id != 0L) {
                context!!.contentResolver.notifyChange(uri, null)
                return@runBlocking ContentUris.withAppendedId(uri, id)
            }
        }
        throw IllegalArgumentException("Failed to insert row into $uri")
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String?>?): Int = runBlocking {
        if (context != null) {
            val count: Int = when (uriMatcher.match(uri)) {
                ESTATE -> estateRepo.value.deleteEstate(ContentUris.parseId(uri))
                IMAGE -> imageRepo.value.deleteImage(ContentUris.parseId(uri))
                ADDRESS -> addressRepo.value.deleteAddress(ContentUris.parseId(uri))
                else -> 0
            }

            context!!.contentResolver.notifyChange(uri, null)
            return@runBlocking count
        }
        throw IllegalArgumentException("Failed to delete row into $uri")
    }

    override fun update(uri: Uri, contentValues: ContentValues?, s: String?, strings: Array<String?>?): Int = runBlocking {
        if (context != null) {
            val count: Int = when (uriMatcher.match(uri)) {
                ESTATE -> estateRepo.value.updateEstate(Estate().fromContentValues(contentValues))
                IMAGE -> imageRepo.value.updateImage(Image().fromContentValues(contentValues))
                ADDRESS -> addressRepo.value.updateAddress(Address().fromContentValues(contentValues))
                else -> 0
            }

            context!!.contentResolver.notifyChange(uri, null)
            return@runBlocking count
        }
        throw IllegalArgumentException("Failed to update row into $uri")
    }

    companion object {
        // FOR DATA
        const val AUTHORITY = "org.desperu.realestatemanager.provider"

        private const val ESTATE = 0
        private const val IMAGE = 1
        private const val ADDRESS = 2

        private val ESTATE_TABLE_NAME = Estate::class.java.simpleName
        private val IMAGE_TABLE_NAME = Image::class.java.simpleName
        private val ADDRESS_TABLE_NAME = Address::class.java.simpleName

        val URI_ESTATE: Uri = Uri.parse("content://$AUTHORITY/$ESTATE_TABLE_NAME")
        val URI_IMAGE: Uri = Uri.parse("content://$AUTHORITY/$IMAGE_TABLE_NAME")
        val URI_ADDRESS: Uri = Uri.parse("content://$AUTHORITY/$ADDRESS_TABLE_NAME")
    }
}