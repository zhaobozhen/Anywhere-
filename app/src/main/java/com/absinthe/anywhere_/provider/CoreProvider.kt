package com.absinthe.anywhere_.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.database.AnywhereDao
import com.absinthe.anywhere_.database.AnywhereRoomDatabase


class CoreProvider : ContentProvider() {

  companion object {
    const val AUTHORITY = "${BuildConfig.APPLICATION_ID}.coreprovider"
    const val AE_TABLE = "anywhere_table"
    const val CODE_AE_DIR = 1
    const val CODE_AE_ITEM = 2
    val URI_ANYWHERE_ENTITY = Uri.parse("content://$AUTHORITY/$AE_TABLE")
    val MATCHER = UriMatcher(UriMatcher.NO_MATCH)
  }

  init {
    MATCHER.addURI(AUTHORITY, AE_TABLE, CODE_AE_DIR)
    MATCHER.addURI(AUTHORITY, "$AE_TABLE/*", CODE_AE_ITEM)
  }

  override fun onCreate(): Boolean {
    return true
  }

  override fun query(
    uri: Uri,
    projection: Array<out String>?,
    selection: String?,
    selectionArgs: Array<out String>?,
    sortOrder: String?
  ): Cursor? {
    val code = MATCHER.match(uri)
    return if (code == CODE_AE_DIR || code == CODE_AE_ITEM) {
      val context = context ?: return null
      val aeDao: AnywhereDao = AnywhereRoomDatabase.getDatabase(context).anywhereDao()
      val cursor: Cursor? = if (code == CODE_AE_DIR) {
        aeDao.selectAll()
      } else {
        aeDao.selectById(ContentUris.parseId(uri))
      }
      cursor?.setNotificationUri(context.contentResolver, uri)
      cursor
    } else {
      throw java.lang.IllegalArgumentException("Unknown URI: $uri")
    }
  }

  override fun getType(uri: Uri): String {
    return when (MATCHER.match(uri)) {
      CODE_AE_DIR -> "vnd.android.cursor.dir/$AUTHORITY.$AE_TABLE"
      CODE_AE_ITEM -> "vnd.android.cursor.item/$AUTHORITY.$AE_TABLE"
      else -> throw IllegalArgumentException("Unknown URI: $uri")
    }
  }

  override fun insert(uri: Uri, values: ContentValues?): Uri? {
    return null
  }

  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
    return 0
  }

  override fun update(
    uri: Uri,
    values: ContentValues?,
    selection: String?,
    selectionArgs: Array<out String>?
  ): Int {
    return 0
  }
}
