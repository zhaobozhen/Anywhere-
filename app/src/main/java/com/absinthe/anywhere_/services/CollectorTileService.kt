package com.absinthe.anywhere_.services

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.absinthe.anywhere_.R
import com.blankj.utilcode.util.ServiceUtils

@RequiresApi(api = Build.VERSION_CODES.N)
class CollectorTileService : TileService() {

    override fun onClick() {
        qsTile?.let {
            if (it.state == Tile.STATE_ACTIVE) {
                CollectorService.closeCollector(this)
                it.state = Tile.STATE_INACTIVE
                it.label = getString(R.string.tile_collector_on)
            } else {
                CollectorService.startCollector(this)
                it.state = Tile.STATE_ACTIVE
                it.label = getString(R.string.tile_collector_off)
            }
            sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
            it.updateTile()
        }
    }

    override fun onStartListening() {
        super.onStartListening()

        qsTile?.let {
            if (ServiceUtils.isServiceRunning(CollectorService::class.java)) {
                it.state = Tile.STATE_ACTIVE
                it.label = getString(R.string.tile_collector_off)
            } else {
                it.state = Tile.STATE_INACTIVE
                it.label = getString(R.string.tile_collector_on)
            }
        }
    }
}