package lars_lion.dev.o_harid.ui.audio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentAudioBinding

class AudioFragment : BaseFragment<FragmentAudioBinding>() {
    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAudioBinding =
        FragmentAudioBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}