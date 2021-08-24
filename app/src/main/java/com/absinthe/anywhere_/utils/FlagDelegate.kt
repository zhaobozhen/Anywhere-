package com.absinthe.anywhere_.utils

import android.app.PendingIntent

object FlagDelegate {
  val PENDING_INTENT_FLAG_MUTABLE = if (AppUtils.atLeastS()) {
    PendingIntent.FLAG_MUTABLE
  } else {
    0
  }
}
