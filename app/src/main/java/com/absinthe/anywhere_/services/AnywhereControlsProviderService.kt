package com.absinthe.anywhere_.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.controls.Control
import android.service.controls.ControlsProviderService
import android.service.controls.actions.ControlAction
import androidx.annotation.RequiresApi
import io.reactivex.Flowable
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

    override fun createPublisherForAllAvailable(): Flow.Publisher<Control> {
        val context: Context = baseContext
        val i = Intent()
//        val pi =
//                PendingIntent.getActivity(
//                        context, CONTROL_REQUEST_CODE, i,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                )
        val controls = mutableListOf<Control>()
//        val control =
//                Control.StatelessBuilder(MY-UNIQUE-DEVICE-ID, pi)
//                        // Required: The name of the control
//                        .setTitle(MY-CONTROL-TITLE)
//                        // Required: Usually the room where the control is located
//                        .setSubtitle(MY-CONTROL-SUBTITLE)
//                        // Optional: Structure where the control is located, an example would be a house
//                        .setStructure(MY-CONTROL-STRUCTURE)
//                        // Required: Type of device, i.e., thermostat, light, switch
//                        .setDeviceType(DeviceTypes.DEVICE-TYPE) // For example, DeviceTypes.TYPE_THERMOSTAT
//                        .build()
//        controls.add(control)
        // Create more controls here if needed and add it to the ArrayList

        // Uses the RxJava 2 library
        return FlowAdapters.toFlowPublisher(Flowable.fromIterable(controls))
    }

    override fun performControlAction(controlId: String, action: ControlAction, consumer: Consumer<Int>) {
        TODO("Not yet implemented")
    }

    override fun createPublisherFor(controlIds: MutableList<String>): Flow.Publisher<Control> {
        TODO("Not yet implemented")
    }
}