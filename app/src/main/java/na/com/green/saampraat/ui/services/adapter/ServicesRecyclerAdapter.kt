package na.com.green.saampraat.ui.services.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import na.com.green.saampraat.databinding.LayoutItemServiceBinding
import na.com.green.saampraat.ui.services.model.Service

class ServicesRecyclerAdapter(
    private val listener: (String) -> Unit
) : RecyclerView.Adapter<ServicesRecyclerAdapter.CustomViewHolder>() {

    private var serviceList = emptyList<Service>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(
            LayoutItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) = holder.bind(serviceList[position], listener)

    override fun getItemCount(): Int { return serviceList.size}

    inner class CustomViewHolder(private val binding: LayoutItemServiceBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(service: Service, listener: (String) -> Unit) {

            binding.textViewName.text = service.name
            binding.imageViewFranchiseImage.setImageResource(service.icon)

            binding.root.setOnClickListener {
                listener(service.name)
            }
        }
    }

    fun updateServices(newServices: List<Service>) {
        val diffCallback = ServiceDiff(serviceList, newServices)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        serviceList = newServices
        diffResult.dispatchUpdatesTo(this)
    }

}