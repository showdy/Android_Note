package com.showdy.learnandroid

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

/**
 * Created by <b>Showdy</b> on 2020/10/27 12:37
 *
 * 无注入方式提供Context
 */
class ContextProvider : ContentProvider() {

    companion object {
        @JvmStatic
        lateinit var contentProviderContext: Context
    }


    override fun onCreate(): Boolean {
        contentProviderContext = context!!
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
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

/**
 * 供全局使用的context对象
 */
val providerContext by lazy {
    ContextProvider.contentProviderContext
}
