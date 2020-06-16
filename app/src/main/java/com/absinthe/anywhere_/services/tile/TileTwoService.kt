package com.absinthe.anywhere_.services.tile

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.ui.shortcuts.ShortcutsActivity
import com.absinthe.anywhere_.utils.SPUtils

@RequiresApi(api = Build.VERSION_CODES.N)
class TileTwoService : TileService() {

    override fun onStartListening() {
        super.onStartListening()

        qsTile?.let {
            val label = SPUtils.getString(this, Const.PREF_TILE_TWO_LABEL)
            if (label.isNotEmpty()) {
                it.label = label
            }
            it.updateTile()
        }
    }

    override fun onClick() {
        val id = SPUtils.getString(this, Const.PREF_TILE_ONE)
        val intent = Intent(this, ShortcutsActivity::class.java).apply {
            action = ShortcutsActivity.ACTION_START_ENTITY
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(Const.INTENT_EXTRA_SHORTCUTS_ID, id)
        }

        if (id.isNotEmpty()) {
            startActivity(intent)
        }
        sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        qsTile?.updateTile()
    }
}