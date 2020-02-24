package com.absinthe.anywhere_.services;

import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.annotation.RequiresApi;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.PermissionUtils;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CollectorTileService extends TileService {
    private static CollectorTileService sInstance;

    public CollectorTileService() {
        sInstance = this;
    }

    @Override
    public void onClick() {
        Tile tile = getQsTile(); // Get Tile
        if (tile.getState() == Tile.STATE_ACTIVE) {
            CollectorService.closeCollector(this);

            tile.setState(Tile.STATE_INACTIVE);
            tile.setLabel(getString(R.string.tile_collector_on));
        } else {
            if (PermissionUtils.checkOverlayPermission(this)) {
                CollectorService.startCollector(this);
            }

            tile.setState(Tile.STATE_ACTIVE);
            tile.setLabel(getString(R.string.tile_collector_off));
        }

        sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        tile.updateTile();
    }

    public static CollectorTileService getInstance() {
        return sInstance;
    }
}
