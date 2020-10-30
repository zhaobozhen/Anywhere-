package com.absinthe.anywhere_.services.tile

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.ui.shortcuts.ShortcutsActivity
import com.absinthe.anywhere_.utils.SPUtils

@RequiresApi(api = Build.VERSION_CODES.N)
abstract class BaseTileService : TileService() {

    protected abstract val prefLabel: String
    protected abstract val prefTile: String
    protected abstract val prefGlobalValues: Boolean

    override fun onStartListening() {
        super.onStartListening()

        qsTile?.let {
            val label = SPUtils.getString(this, prefLabel)
            if (label.isNotEmpty()) {
                it.label = label
            }
            it.state = if (prefGlobalValues) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE

            it.updateTile()
        }
    }

    override fun onClick() {
        val id = SPUtils.getString(this, prefTile)
        val intent = Intent(this, ShortcutsActivity::class.java).apply {
            action = ShortcutsActivity.ACTION_START_ENTITY
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(Const.INTENT_EXTRA_SHORTCUTS_ID, id)
            putExtra(Const.INTENT_EXTRA_FROM_TILE, prefTile)
        }

        if (id.isNotEmpty()) {
            startActivity(intent)
        }
        sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        qsTile?.updateTile()
    }
}