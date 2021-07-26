package lars_lion.dev.o_harid.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentProfileBinding


class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        tvKi.setOnClickListener {
//            setNewLocale("ru", "KIRIL")
//            kiril()
//        }
//
//        tvUz.setOnClickListener {
//            setNewLocale("en", "LOTIN")
//            uzbek()
//        }
    }


//    private fun setNewLocale(language: String, country: String) {
//        Lingver.getInstance().setLocale(requireContext(), language, country)
//        restart()
//    }
//
//    private fun restart() {
//        val i = Intent(requireContext(), MainActivity::class.java)
//        requireActivity().overridePendingTransition(0, 0)
//        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
//        requireActivity().overridePendingTransition(
//            android.R.anim.fade_in,
//            android.R.anim.fade_out
//        )
//    }

//    private fun uzbek() {
//        binding!!.tvUz.setTextColor(
//            ResourcesCompat.getColor(
//                resources,
//                R.color.m_color,
//                resources.newTheme()
//            )
//        )
//        binding!!.tvKi.setTextColor(
//            ResourcesCompat.getColor(
//                resources,
//                R.color.n_color,
//                resources.newTheme()
//            )
//        )
//        preferences.language = "en"
//    }

    private fun kiril() {
//        binding!!.tvKi.setTextColor(
//            ResourcesCompat.getColor(
//                resources,
//                R.color.m_color,
//                resources.newTheme()
//            )
//        )
//        binding!!.tvUz.setTextColor(
//            ResourcesCompat.getColor(
//                resources,
//                R.color.n_color,
//                resources.newTheme()
//            )
//        )
//        preferences.language = "ru"
    }

}