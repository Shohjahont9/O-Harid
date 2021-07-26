package lars_lion.dev.o_harid.ui.book

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import lars_lion.dev.o_harid.R
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentBookBinding

class BookFragment : BaseFragment<FragmentBookBinding>() {
    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentBookBinding = FragmentBookBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}