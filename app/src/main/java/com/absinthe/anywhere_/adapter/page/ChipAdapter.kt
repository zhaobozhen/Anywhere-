package com.absinthe.anywhere_.adapter.page

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.utils.CommandUtils
import com.absinthe.anywhere_.utils.TextUtils
import com.absinthe.anywhere_.utils.UiUtils
import com.absinthe.anywhere_.utils.manager.ActivityStackManager
import com.absinthe.anywhere_.utils.manager.DialogManager.showImageDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showShellResultDialog
import com.blankj.utilcode.util.Utils
import com.google.android.material.chip.Chip
import java.util.*

class ChipAdapter internal constructor(category: String) : RecyclerView.Adapter<ChipAdapter.ViewHolder>() {

    private val mList: MutableList<AnywhereEntity> = ArrayList()

    init {
        AnywhereApplication.sRepository.allAnywhereEntities.value?.let {
            for (item in it) {
                if (TextUtils.isEmpty(item.category) && category == AnywhereType.DEFAULT_CATEGORY
                        || item.category == category) {
                    mList.add(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chip, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])

        holder.chip.setOnClickListener {
            val ae = mList[position]

            when (ae.anywhereType) {
                AnywhereType.IMAGE -> {
                    ActivityStackManager.topActivity?.let {
                        showImageDialog(it, ae)
                    }
                }
                AnywhereType.SHELL -> {
                    val result = CommandUtils.execAdbCmd(ae.param1)
                    ActivityStackManager.topActivity?.let {
                        showShellResultDialog(it, result, null, null)
                    }
                }
                else -> {
                    CommandUtils.execCmd(TextUtils.getItemCommand(ae))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val chip: Chip = itemView.findViewById(R.id.chip)

        fun bind(item: AnywhereEntity) {
            chip.apply {
                text = item.appName
                chipIcon = UiUtils.getAppIconByPackageName(Utils.getApp(), item)
            }
        }

    }
}