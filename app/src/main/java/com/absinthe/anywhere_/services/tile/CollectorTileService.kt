package com.absinthe.anywhere_.services.tile

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
import com.absinthe.anywhere_.services.overlay.CollectorService
import com.absinthe.anywhere_.services.overlay.ICollectorService

@RequiresApi(api = Build.VERSION_CODES.N)
class CollectorTileService : TileService() {

    private var isBound = false
    private var collectorService: ICollectorService? = null
    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            collectorService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isBound = true
            collectorService = ICollectorService.Stub.asInterface(service)
            collectorService?.startCollector()
        }

    }

    override fun onClick() {
        qsTile?.let {
            if (isBound) {
                collectorService?.startCollector()
            } else {
                bindService(Intent(this, CollectorService::class.java), connection, Context.BIND_AUTO_CREATE)
            }

            it.state = Tile.STATE_ACTIVE
            it.label = getString(R.string.tile_collector_off)
            sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
            it.updateTile()
        }
    }
}