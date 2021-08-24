package com.absinthe.anywhere_.model.database

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.absinthe.anywhere_.constants.AnywhereType

@Keep
@Entity(tableName = "page_table")
data class PageEntity(
  @PrimaryKey
  @ColumnInfo(name = "id")
  var id: String = System.currentTimeMillis().toString(),

  @ColumnInfo(name = "title")
  var title: String = "",

  @ColumnInfo(name = "priority")
  var priority: Int = 0,

  @ColumnInfo(name = "type")
  var type: Int = AnywhereType.Page.CARD_PAGE,

  @ColumnInfo(name = "time_stamp")
  var timeStamp: String = id,

  @ColumnInfo(name = "extra")
  var extra: String? = "",

  @ColumnInfo(name = "backgroundUri")
  var backgroundUri: String? = ""
)
