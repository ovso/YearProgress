package io.github.ovso.yearprogress

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
      R.layout.fragment_progress,
      container,
      false
    )

  @Suppress("UNCHECKED_CAST")
  private fun provideViewModel() =
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
      override fun <T : ViewModel?> create(modelClass: Class<T>) =
        ProgressViewModel(context!!, 0) as T
    }).get(ProgressViewModel::class.java)

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
  }
}
