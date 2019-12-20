package com.absinthe.anywhere_.services;

import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.annotation.RequiresApi;

import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.QREntity;
import com.absinthe.anywhere_.ui.shortcuts.ShortcutsActivity;
import com.absinthe.anywhere_.utils.SPUtils;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TileThreeService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();

        Tile tile = getQsTile(); // Get Tile

        if (tile != null) {
            String label = SPUtils.getString(this, Const.PREF_TILE_THREE_LABEL);
            if (!label.isEmpty()) {
                tile.setLabel(label);
            }

            tile.updateTile();
        }
    }

    @Override
    public void onClick() {
        Tile tile = getQsTile(); // Get Tile

        String cmd = SPUtils.getString(this, Const.PREF_TILE_THREE_CMD);
        Intent intent = new Intent(this, ShortcutsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (cmd.contains(QREntity.PREFIX)) {
            intent.setAction(ShortcutsActivity.ACTION_START_QR_CODE);
        } else {
            intent.setAction(ShortcutsActivity.ACTION_START_COMMAND);
        }
        intent.putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, cmd);
        if (!cmd.isEmpty()) {
            startActivity(intent);
        }
        sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        if (tile != null) {
            tile.updateTile();
        }
    }

}
