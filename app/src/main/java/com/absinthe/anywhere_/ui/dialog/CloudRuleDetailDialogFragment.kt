package com.absinthe.anywhere_.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.viewModels
import com.absinthe.anywhere_.databinding.LayoutCloudRuleDetailBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel

const val EXTRA_ENTITY = "EXTRA_ENTITY"

class CloudRuleDetailDialogFragment : AnywhereDialogFragment() {

    private val entity by lazy { arguments?.getParcelable(EXTRA_ENTITY) as? AnywhereEntity }
    private val viewModel by viewModels<AnywhereViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = LayoutCloudRuleDetailBinding.inflate(layoutInflater)
        entity?.let { et ->
            binding.tvName.text = et.appName
            binding.btnAdd.setOnClickListener {
                viewModel.insert(et)
            }
        }

        return AnywhereDialogBuilder(requireContext()).setView(binding.root).create()
    }

}