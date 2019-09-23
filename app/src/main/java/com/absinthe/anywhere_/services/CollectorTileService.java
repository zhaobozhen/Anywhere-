package com.absinthe.anywhere_.services;

import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CollectorTileService extends TileService {
    public CollectorTileService() {
    }

    @Override
    public void onClick() {
        Tile tile = getQsTile(); // 获取 Tile
        if (tile.getState() == Tile.STATE_ACTIVE) {
            tile.setState(Tile.STATE_INACTIVE);
        } else {
            tile.setState(Tile.STATE_ACTIVE);

        }

        tile.updateTile();
    }
}
