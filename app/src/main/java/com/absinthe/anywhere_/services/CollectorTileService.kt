package com.absinthe.anywhere_.services

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.absinthe.anywhere_.R

@RequiresApi(api = Build.VERSION_CODES.N)
class CollectorTileService : TileService() {

    private var isBound = false
    private var collectorService: CollectorService? = null
    private val conn = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isBound = true
            collectorService = (service as CollectorService.CollectorBinder).service
            collectorService?.startCollector()
        }

    }

    override fun onClick() {
        qsTile?.let {
            if (isBound) {
                collectorService?.startCollector()
            } else {
                bindService(Intent(this, CollectorService::class.java), conn, Context.BIND_AUTO_CREATE)
            }

            it.state = Tile.STATE_ACTIVE
            it.label = getString(R.string.tile_collector_off)
            sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
            it.updateTile()
        }
    }
}