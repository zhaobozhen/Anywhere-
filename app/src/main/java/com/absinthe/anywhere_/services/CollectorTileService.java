package com.absinthe.anywhere_.services;

import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.annotation.RequiresApi;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.ToastUtil;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CollectorTileService extends TileService {
    private static CollectorTileService instance;

    public CollectorTileService() {
        instance = this;
    }

    @Override
    public void onClick() {
        Tile tile = getQsTile(); // Get Tile
        if (tile.getState() == Tile.STATE_ACTIVE) {
            Intent intent = new Intent(this, CollectorService.class);
            intent.putExtra(CollectorService.COMMAND, CollectorService.COMMAND_CLOSE);
            startService(intent);
            tile.setState(Tile.STATE_INACTIVE);
            tile.setLabel(getString(R.string.tile_collector_on));
        } else {
            Intent intent = new Intent(this, CollectorService.class);
            intent.putExtra(CollectorService.COMMAND, CollectorService.COMMAND_OPEN);
            ToastUtil.makeText(R.string.toast_collector_opened);

            startService(intent);
            tile.setState(Tile.STATE_ACTIVE);
            tile.setLabel(getString(R.string.tile_collector_off));
        }

        sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        tile.updateTile();
    }

    public static CollectorTileService getInstance() {
        return instance;
    }
}
