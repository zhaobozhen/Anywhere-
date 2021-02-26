package com.absinthe.anywhere_.services.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.provider.BaseColumns
import android.service.controls.Control
import android.service.controls.ControlsProviderService
import android.service.controls.DeviceTypes
import android.service.controls.actions.BooleanAction
import android.service.controls.actions.ControlAction
import androidx.annotation.RequiresApi
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.provider.CoreProvider.Companion.URI_ANYWHERE_ENTITY
import com.absinthe.anywhere_.ui.main.MainActivity
import com.absinthe.anywhere_.ui.shortcuts.ShortcutsActivity
import io.reactivex.Flowable
import io.reactivex.processors.ReplayProcessor
import org.reactivestreams.FlowAdapters
import java.util.concurrent.Flow
import java.util.function.Consumer

@RequiresApi(Build.VERSION_CODES.R)
class AwControlsProviderService : ControlsProviderService() {

    private val updatePublisher = ReplayProcessor.create<Control>()

    @SuppressLint("Recycle")
    override fun createPublisherForAllAvailable(): Flow.Publisher<Control> {
        val context: Context = baseContext
        val i = Intent()
        val pi =
                PendingIntent.getActivity(
                        context, 2025, i,
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
        val controls = mutableListOf<Control>()
        val cursor: Cursor = context.contentResolver.query(URI_ANYWHERE_ENTITY, null, null, null, null)
                ?: return FlowAdapters.toFlowPublisher(Flowable.fromIterable(controls))

        while (cursor.moveToNext()) {
            val control =
                    Control.StatelessBuilder(cursor.getString(cursor.getColumnIndex(BaseColumns._ID)), pi)
                            // Required: The name of the control
                            .setTitle(cursor.getString(cursor.getColumnIndex(AnywhereEntity.APP_NAME)))
                            // Required: Usually the room where the control is located
                            .setSubtitle(cursor.getString(cursor.getColumnIndex(AnywhereEntity.DESCRIPTION)))
                            // Required: Type of device, i.e., thermostat, light, switch
                            .setDeviceType((DeviceTypes.TYPE_AC_HEATER..DeviceTypes.TYPE_ROUTINE).random())
                            .build()
            controls.add(control)
        }

        cursor.close()

        // Uses the RxJava 2 library
        return FlowAdapters.toFlowPublisher(Flowable.fromIterable(controls))
    }

    @SuppressLint("Recycle")
    override fun createPublisherFor(controlIds: MutableList<String>): Flow.Publisher<Control> {
        val context: Context = baseContext
        /* Fill in details for the activity related to this device. On long press,
         * this Intent will be launched in a bottomsheet. Please design the activity
         * accordingly to fit a more limited space (about 2/3 screen height).
         */
        var i: Intent
        var pi: PendingIntent
        var id: String

        val cursor: Cursor = context.contentResolver.query(URI_ANYWHERE_ENTITY, null, null, null, null)
                ?: return FlowAdapters.toFlowPublisher(updatePublisher)

        while (cursor.moveToNext()) {
            id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID))

            if (controlIds.contains(id)) {
                i = Intent(context, MainActivity::class.java).apply {
                    action = ShortcutsActivity.ACTION_START_DEVICE_CONTROL
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(Const.INTENT_EXTRA_PARAM_1, cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_1)))
                    putExtra(Const.INTENT_EXTRA_PARAM_2, cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_2)))
                    putExtra(Const.INTENT_EXTRA_PARAM_3, cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_3)))
                    putExtra(Const.INTENT_EXTRA_TYPE, cursor.getInt(cursor.getColumnIndex(AnywhereEntity.TYPE)))
                }
                pi = PendingIntent.getActivity(context, 2025, i, PendingIntent.FLAG_UPDATE_CURRENT)
                val control =
                        Control.StatefulBuilder(id, pi)
                                // Required: The name of the control
                                .setTitle(cursor.getString(cursor.getColumnIndex(AnywhereEntity.APP_NAME)))
                                // Required: Usually the room where the control is located
                                .setSubtitle(cursor.getString(cursor.getColumnIndex(AnywhereEntity.DESCRIPTION)))
                                // Required: Type of device, i.e., thermostat, light, switch
                                .setDeviceType((DeviceTypes.TYPE_AC_HEATER..DeviceTypes.TYPE_ROUTINE).random())
                                // Required: Current status of the device
                                .setStatus(Control.STATUS_OK) // For example, Control.STATUS_OK
                                .build()

                updatePublisher.onNext(control)
            }
        }

        return FlowAdapters.toFlowPublisher(updatePublisher)
    }

    override fun performControlAction(controlId: String, action: ControlAction, consumer: Consumer<Int>) {
        /* First, locate the control identified by the controlId. Once it is located, you can
         * interpret the action appropriately for that specific device. For instance, the following
         * assumes that the controlId is associated with a light, and the light can be turned on
         * or off.
         */
        if (action is BooleanAction) {

            // Inform SystemUI that the action has been received and is being processed
            consumer.accept(ControlAction.RESPONSE_OK)
        }
    }
}