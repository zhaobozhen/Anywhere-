package com.absinthe.anywhere_.model

import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.database.PageEntity

data class BackupBean(
        val anywhereList: List<AnywhereEntity>,
        val pageList: List<PageEntity>
)