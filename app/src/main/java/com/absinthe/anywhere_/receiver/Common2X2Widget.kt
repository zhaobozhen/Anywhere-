package com.absinthe.anywhere_.receiver

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

class Common2X2Widget : GlanceAppWidgetReceiver() {
  override val glanceAppWidget = MyGlanceWidget()

  class MyGlanceWidget : GlanceAppWidget() {
    @Composable
    override fun Content() {
      Column(
        modifier = GlanceModifier
          .fillMaxSize()
          .background(android.R.color.system_neutral1_400)
          .cornerRadius(10.dp)
          .padding(8.dp)
      ) {
        Text(
          text = "First Glance widget",
          modifier = GlanceModifier.fillMaxWidth(),
          style = TextStyle(fontWeight = FontWeight.Bold),
        )
      }
    }
  }
}
