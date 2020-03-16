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
        if (qsTile.state == Tile.STATE_ACTIVE) {
            CollectorService.closeCollector(this)
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.label = getString(R.string.tile_collector_on)
        } else {
            CollectorService.startCollector(this)
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.label = getString(R.string.tile_collector_off)
        }
        sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        qsTile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()

        if (ServiceUtils.isServiceRunning(CollectorService::class.java)) {
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.label = getString(R.string.tile_collector_off)
        } else {
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.label = getString(R.string.tile_collector_on)
        }
    }
}