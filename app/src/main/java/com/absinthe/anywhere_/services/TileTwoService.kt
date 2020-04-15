package com.absinthe.anywhere_.services

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.absinthe.anywhere_.model.AnywhereType
import com.absinthe.anywhere_.model.Const
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
        val cmd = SPUtils.getString(this, Const.PREF_TILE_TWO_CMD)
        val intent = Intent(this, ShortcutsActivity::class.java).apply {
            action = if (cmd.startsWith(AnywhereType.QRCODE_PREFIX)) {
                ShortcutsActivity.ACTION_START_QR_CODE
            } else {
                ShortcutsActivity.ACTION_START_COMMAND
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, cmd)
        }

        if (cmd.isNotEmpty()) {
            startActivity(intent)
        }
        sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        qsTile?.updateTile()
    }
}