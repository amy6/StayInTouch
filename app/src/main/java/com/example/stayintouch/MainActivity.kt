package com.example.stayintouch

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.CursorAdapter
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader

private val FROM_COLUMNS = arrayOf(
    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
//    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
)

private val TO_IDS = intArrayOf(
    R.id.contact_name
//    R.id.contact_image
)

private val PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
)

private val SELECTION: String =
    "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"

// Defines a variable for the search string
private val searchString: String = ""
// Defines the array to hold values that replace the ?
private val selectionArgs = arrayOf(searchString)


// The column index for the _ID column
private const val CONTACT_ID_INDEX: Int = 0
// The column index for the CONTACT_KEY column
private const val CONTACT_KEY_INDEX: Int = 1

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {


    lateinit var contactList: ListView
    var contactId: Long = 0
    var contactKey: String? = null
    var contactUri: Uri? = null
    private var cursorAdapter: SimpleCursorAdapter? = null

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // Get the Cursor
        val cursor: Cursor? = (parent?.adapter as? CursorAdapter)?.cursor?.apply {
            // Move to the selected contact
            moveToPosition(position)
            // Get the _ID value
            contactId = getLong(CONTACT_ID_INDEX)
            // Get the selected LOOKUP KEY
            contactKey = getString(CONTACT_KEY_INDEX)
            // Create the contact's content Uri
            contactUri = ContactsContract.Contacts.getLookupUri(contactId, contactKey)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {/*
         * Makes search string into pattern and
         * stores it in the selection array
         */
        selectionArgs[0] = "%$searchString%"
        // Starts the query
        return CursorLoader(
            this,
            ContactsContract.Contacts.CONTENT_URI,
            PROJECTION,
            null,
            null,
            null
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (data?.count!! > 0) {
            Log.i(this.localClassName, "$data.count")
        }
        cursorAdapter?.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        cursorAdapter?.swapCursor(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contactList = findViewById(R.id.contact_list)
        cursorAdapter = SimpleCursorAdapter(
            this, R.layout.layout_contacts_list_item, null, FROM_COLUMNS, TO_IDS, 0
        )
        contactList.adapter = cursorAdapter

        contactList.onItemClickListener = this

        supportLoaderManager.initLoader(0, null, this)
    }
}
