package com.absinthe.anywhere_.services.tile

import android.os.Build
import androidx.annotation.RequiresApi
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues

@RequiresApi(api = Build.VERSION_CODES.N)
class TileThreeService : BaseTileService() {
    override val prefLabel: String
        get() = Const.PREF_TILE_THREE_LABEL
    override val prefTile: String
        get() = Const.PREF_TILE_THREE
    override val prefGlobalValues: Boolean
        get() = GlobalValues.tileThreeActive
}