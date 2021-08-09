package by.dashkevichpavel.osteopath.helpers.contacts

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import by.dashkevichpavel.osteopath.model.getIntByColumnName
import by.dashkevichpavel.osteopath.model.getStringByColumnName

class ContactInfoLoader(private val contentResolver: ContentResolver) {
    fun getContactInfo(fromContactUri: Uri): ContactInfo {
        val contactInfo = ContactInfo()

        setupContactFields(fromContactUri, contactInfo)
        setupDataFields(contactInfo)

        return contactInfo
    }

    private fun setupContactFields(uri: Uri, contactInfo: ContactInfo) {
        val projection: Array<String> = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
        )

        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )

        cursor?.let {
            if (cursor.moveToFirst()) {
                contactInfo.id = cursor.getStringByColumnName(
                    ContactsContract.Contacts._ID,
                    contactInfo.id
                )
                contactInfo.name = cursor.getStringByColumnName(
                    ContactsContract.Contacts.DISPLAY_NAME,
                    contactInfo.name
                )
            }

            cursor.close()
        }
    }

    private fun setupDataFields(contactInfo: ContactInfo) {
        val cursor = contentResolver.query(
            ContentUris
                .withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactInfo.id.toLong())
                .buildUpon()
                .appendPath(ContactsContract.Contacts.Entity.CONTENT_DIRECTORY)
                .build(),
            arrayOf(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            null
        )

        cursor?.let {
            if (cursor.count > 0) {
                val phones: MutableList<String> = mutableListOf()
                while (cursor.moveToNext()) {
                    if (cursor.getStringByColumnName(
                            ContactsContract.CommonDataKinds.Phone.MIMETYPE, ""
                        ) == ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) {
                        phones += cursor.getStringByColumnName(
                            ContactsContract.CommonDataKinds.Phone.NUMBER, ""
                        )
                    }
                }

                contactInfo.phones = phones
            }

            cursor.close()
        }
    }
}

data class ContactInfo(
    var id: String = "",
    var name: String = "",
    var phones: List<String> = emptyList()
)