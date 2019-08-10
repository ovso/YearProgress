package io.github.ovso.yearprogress.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.github.ovso.yearprogress.R.layout
import io.github.ovso.yearprogress.databinding.FragmentProgressBinding

class ProgressFragment : Fragment() {

  companion object {
    fun newInstance(position: Int) = ProgressFragment().apply {
      this.arguments = Bundle().apply { putInt("position", position) }
    }
  }

  private lateinit var viewModel: ProgressViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel = provideViewModel()
    val inflate = getDataBindingInflate(inflater, container)
    inflate.viewModel = viewModel
    return inflate.root
  }

  private fun getDataBindingInflate(inflater: LayoutInflater, container: ViewGroup?) =
    DataBindingUtil.inflate<FragmentProgressBinding>(
      inflater,
      layout.fragment_progress,
      container,
      false
    )

  @Suppress("UNCHECKED_CAST")
  private fun provideViewModel() =
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
      override fun <T : ViewModel?> create(modelClass: Class<T>) =
        ProgressViewModel(
          context!!,
          arguments?.getInt("position")!!
        ) as T
    }).get(ProgressViewModel::class.java)

}
