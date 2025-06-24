package na.com.green.saampraat.ui.services

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import na.com.green.saampraat.databinding.FragmentAllServicesBinding
import na.com.green.saampraat.ui.services.adapter.ServicesRecyclerAdapter
import na.com.green.saampraat.ui.services.`object`.ServicesData

class AllServicesFragment : Fragment() {

    private var binding: FragmentAllServicesBinding? = null
    private val servicesAdapter by lazy { getServiceRecyclerViewAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllServicesBinding.inflate(layoutInflater, container, false)
        binding?.recyclerViewServices?.adapter = servicesAdapter
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val services = ServicesData.getAllServices()
        servicesAdapter.updateServices(services)
    }

    private fun getServiceRecyclerViewAdapter(): ServicesRecyclerAdapter {

        return ServicesRecyclerAdapter { _ ->
            binding?.apply {
                print("")
            }
        }
    }

}