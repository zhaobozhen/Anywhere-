package com.absinthe.anywhere_.model.viewholder

import com.absinthe.anywhere_.model.database.AnywhereEntity

data class FlowStepBean(
        var entity: AnywhereEntity? = null,
        var delay: Long = 0
)