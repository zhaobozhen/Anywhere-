package com.absinthe.anywhere_.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.controls.Control
import android.service.controls.ControlsProviderService
import android.service.controls.DeviceTypes
import android.service.controls.actions.ControlAction
import androidx.annotation.RequiresApi
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.ui.settings.SettingsActivity
import io.reactivex.Flowable
import io.reactivex.processors.ReplayProcessor
import org.reactivestreams.FlowAdapters
import java.util.concurrent.Flow
import java.util.function.Consumer

/**
 * <pre>
 * author : Absinthe
 * time : 2020/08/12
 * </pre>
 */

@RequiresApi(Build.VERSION_CODES.R)
class AnywhereControlsProviderService : ControlsProviderService() {

    private lateinit var updatePublisher: ReplayProcessor<Control>

    override fun createPublisherForAllAvailable(): Flow.Publisher<Control> {
        val context: Context = baseContext
        val i = Intent()
        val pi =
                PendingIntent.getActivity(
                        context, Const.REQUEST_CODE_R_CONTROL, i,
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
        val controls = mutableListOf<Control>()
        val control =
                Control.StatelessBuilder("MY-UNIQUE-DEVICE-ID", pi)
                        // Required: The name of the control
                        .setTitle("MY-CONTROL-TITLE")
                        // Required: Usually the room where the control is located
                        .setSubtitle("MY-CONTROL-SUBTITLE")
                        // Optional: Structure where the control is located, an example would be a house
                        .setStructure("MY-CONTROL-STRUCTURE")
                        // Required: Type of device, i.e., thermostat, light, switch
                        .setDeviceType(DeviceTypes.TYPE_FAN) // For example, DeviceTypes.TYPE_THERMOSTAT
                        .build()
        controls.add(control)
        // Create more controls here if needed and add it to the ArrayList

        // Uses the RxJava 2 library
        return FlowAdapters.toFlowPublisher(Flowable.fromIterable(controls))
    }

    override fun performControlAction(controlId: String, action: ControlAction, consumer: Consumer<Int>) {
        TODO("Not yet implemented")
    }

    override fun createPublisherFor(controlIds: MutableList<String>): Flow.Publisher<Control> {
        val context: Context = baseContext
        /* Fill in details for the activity related to this device. On long press,
         * this Intent will be launched in a bottomsheet. Please design the activity
         * accordingly to fit a more limited space (about 2/3 screen height).
         */
        val i = Intent(this, SettingsActivity::class.java)
        val pi =
                PendingIntent.getActivity(context, Const.REQUEST_CODE_R_CONTROL, i, PendingIntent.FLAG_UPDATE_CURRENT)
        updatePublisher = ReplayProcessor.create()

        if (controlIds.contains("MY-UNIQUE-DEVICE-ID")) {
            val control =
                    Control.StatefulBuilder("MY-UNIQUE-DEVICE-ID", pi)
                            // Required: The name of the control
                            .setTitle("MY-CONTROL-TITLE")
                            // Required: Usually the room where the control is located
                            .setSubtitle("MY -CONTROL-SUBTITLE")
                            // Optional: Structure where the control is located, an example would be a house
                            .setStructure("MY-CONTROL-STRUCTURE")
                            // Required: Type of device, i.e., thermostat, light, switch
                            .setDeviceType(DeviceTypes.TYPE_FAN) // For example, DeviceTypes.TYPE_THERMOSTAT
                            // Required: Current status of the device
                            .setStatus(Control.STATUS_OK) // For example, Control.STATUS_OK
                            .build()

            updatePublisher.onNext(control)
        }

        // If you have other controls, check that they have been selected here

        // Uses the Reactive Streams API

        return updatePublisher as Flow.Publisher<Control>
    }
}