package lars_lion.dev.o_harid.ui.book.fullScreen

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentFullScreenBookBinding
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class FullScreenBookFragment : BaseFragment<FragmentFullScreenBookBinding>() {
    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFullScreenBookBinding = FragmentFullScreenBookBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}