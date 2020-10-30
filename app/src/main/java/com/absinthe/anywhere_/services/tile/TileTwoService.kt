package com.absinthe.anywhere_.services.tile

import android.os.Build
import androidx.annotation.RequiresApi
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues

@RequiresApi(api = Build.VERSION_CODES.N)
class TileTwoService : BaseTileService() {
    override val prefLabel: String
        get() = Const.PREF_TILE_TWO_LABEL
    override val prefTile: String
        get() = Const.PREF_TILE_TWO
    override val prefGlobalValues: Boolean
        get() = GlobalValues.tileTwoActive
}