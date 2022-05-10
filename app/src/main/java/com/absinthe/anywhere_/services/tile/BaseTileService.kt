package com.absinthe.anywhere_.services.tile

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.ui.editor.impl.SWITCH_OFF
import com.absinthe.anywhere_.ui.shortcuts.ShortcutsActivity
import java.io.File

const val TILE_LABEL = "Label"
const val TILE_ACTIVE_STATE = "ActiveState"

@RequiresApi(api = Build.VERSION_CODES.N)
abstract class BaseTileService : TileService() {

  private val prefLabel: String = javaClass.simpleName + TILE_LABEL
  private val prefTile: String = javaClass.simpleName

  override fun onStartListening() {
    super.onStartListening()

    qsTile?.let {
      val label = GlobalValues.mmkv.decodeString(prefLabel)
      if (label?.isNotEmpty() == true) {
        it.label = label
      }

      val id = GlobalValues.mmkv.decodeString(prefTile, "0")
      AnywhereApplication.sRepository.getEntityById(id.orEmpty())?.let { entity ->
        if (entity.type == AnywhereType.Card.SWITCH_SHELL) {
          it.state = if (entity.param3 == SWITCH_OFF) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE
        } else {
          it.state = Tile.STATE_INACTIVE
        }
      }

      val iconFile = File(
        filesDir,
        "tiles${File.separator}icon${File.separator}${javaClass.simpleName}"
      )
      val icon = BitmapFactory.decodeFile(iconFile.path)
      if (icon != null) {
        it.icon = Icon.createWithBitmap(icon)
      }

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
      startActivityAndCollapse(intent)
    }
    qsTile?.updateTile()
  }
}
