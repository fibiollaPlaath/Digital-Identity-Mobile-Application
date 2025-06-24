package na.com.green.saampraat.ui.services.adapter

import androidx.recyclerview.widget.DiffUtil
import na.com.green.saampraat.ui.services.model.Service

class ServiceDiff(
    private val oldList: List<Service>,
    private val newList: List<Service>

) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return try {
            oldList[oldItemPosition].id == newList[newItemPosition].id
        } catch (ex: Exception) {
            false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return try {
            return when {

                oldList[oldItemPosition].name != newList[oldItemPosition].name -> {
                    false
                }

                else -> true
            }
        } catch (ex: Exception) {
            false
        }
    }
}