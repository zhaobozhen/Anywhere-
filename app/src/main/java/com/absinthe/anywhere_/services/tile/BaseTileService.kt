package com.absinthe.anywhere_.services.tile

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.ui.shortcuts.ShortcutsActivity

const val TILE_LABEL = "Label"
const val TILE_ACTIVE_STATE = "ActiveState"

@RequiresApi(api = Build.VERSION_CODES.N)
abstract class BaseTileService : TileService() {

  private val prefLabel: String = javaClass.simpleName + TILE_LABEL
  private val prefTile: String = javaClass.simpleName
  private val prefGlobalValues: Boolean =
    GlobalValues.mmkv.decodeBool(javaClass.simpleName + TILE_ACTIVE_STATE, false)

  override fun onStartListening() {
    super.onStartListening()

    qsTile?.let {
      val label = GlobalValues.mmkv.decodeString(prefLabel)
      if (label?.isNotEmpty() == true) {
        it.label = label
      }
      it.state = if (prefGlobalValues) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE

      it.updateTile()
    }
  }

  override fun onClick() {
    val id = GlobalValues.mmkv.decodeString(prefTile)
    val intent = Intent(this, ShortcutsActivity::class.java).apply {
      action = ShortcutsActivity.ACTION_START_ENTITY
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      putExtra(Const.INTENT_EXTRA_SHORTCUTS_ID, id)
      putExtra(Const.INTENT_EXTRA_FROM_TILE, prefTile)
    }

    if (id?.isNotEmpty() == true) {
      startActivity(intent)
    }
    sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
    qsTile?.updateTile()
  }
}
