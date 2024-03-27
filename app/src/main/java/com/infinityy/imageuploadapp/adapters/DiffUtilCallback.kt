package com.infinityy.imageuploadapp.adapters

import androidx.recyclerview.widget.DiffUtil
import com.infinityy.imageuploadapp.domain.model.DBDataModel

// it will be automatically called whenever we will update the note
// DiffUtils is used to track changes made to the RecyclerView Adapter.
// DiffUtil notifies the RecyclerView of any changes to the data set using the following methods: notifyItemMoved. notifyItemRangeChanged.
class DiffUtilCallback(
    private val newList: List<DBDataModel>,
    private val oldList: List<DBDataModel>
) : DiffUtil.Callback() {


    /**
     * [getOldListSize] function return the size of the [oldList]
     * */
    override fun getOldListSize() = oldList.size

    /**
     * [getNewListSize] function return the size of the [newList]
     * */
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].imageUrl == newList[newItemPosition].imageUrl
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].imageUrl != newList[newItemPosition].imageUrl
    }
}