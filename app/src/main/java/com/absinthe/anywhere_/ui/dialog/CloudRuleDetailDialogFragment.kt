package com.absinthe.anywhere_.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.LayoutCloudRuleDetailBinding
import com.absinthe.anywhere_.model.cloud.RuleEntity
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.CipherUtils
import com.absinthe.anywhere_.utils.manager.CardTypeIconGenerator
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.libraries.utils.extensions.dp
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val EXTRA_ENTITY = "EXTRA_ENTITY"

class CloudRuleDetailDialogFragment : AnywhereDialogFragment() {

  private val rule by lazy { arguments?.getParcelable(EXTRA_ENTITY) as? RuleEntity }
  private var entity: AnywhereEntity? = null
  private lateinit var binding: LayoutCloudRuleDetailBinding

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    binding = LayoutCloudRuleDetailBinding.inflate(layoutInflater)
    init()
    return AnywhereDialogBuilder(requireContext()).setView(binding.root).create()
  }

  private fun init() {
    rule?.let { rule ->
      val decrypted = CipherUtils.decrypt(rule.content)
      entity = Gson().fromJson(decrypted, AnywhereEntity::class.java)
      binding.tvName.text = rule.name
      binding.tvContributor.text = rule.contributor
      binding.tvDesc.text = rule.desc
      entity?.let {
        if (context == null) {
          return
        }
        binding.ivType.setImageDrawable(
          CardTypeIconGenerator.getAdvancedIcon(
            requireContext(),
            it.type,
            36.dp
          )
        )
        binding.tvType.text =
          getString(AnywhereType.Card.TYPE_STRINGRES_MAP[it.type] ?: R.string.btn_activity)
        binding.tvNeedRoot.isGone = !AppUtils.isAnywhereEntityNeedRoot(it)
      }
      binding.btnAdd.setOnClickListener {
        entity?.let {
          lifecycleScope.launch(Dispatchers.IO) {
            it.category = GlobalValues.category
            AnywhereApplication.sRepository.insert(it)
          }
        }
        dismiss()
      }
    } ?: run {
      dismiss()
    }
  }
}
