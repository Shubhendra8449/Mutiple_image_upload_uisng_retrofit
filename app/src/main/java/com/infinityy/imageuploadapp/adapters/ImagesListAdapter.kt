package com.infinityy.imageuploadapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.infinityy.imageuploadapp.R
import com.infinityy.imageuploadapp.databinding.LayoutDetailsListBinding
import com.infinityy.imageuploadapp.domain.model.DBDataModel
import com.infinityy.imageuploadapp.domain.model.ImageUploadStatus
import javax.inject.Inject

class ImagesListAdapter @Inject constructor() :
    RecyclerView.Adapter<ImagesListAdapter.ImagesListViewHolder>() {

    val imageData = ArrayList<DBDataModel>()
    private var callBack: (Int) -> Unit = { }


    //Retry click
    fun onRetryClicked(listener: (Int) -> Unit) {
        callBack = listener
    }

    //set list data
    fun setList(data: ArrayList<DBDataModel>) {
        calculateDifferenceAndUpdate(data)
    }

    inner class ImagesListViewHolder(private val mBinding: LayoutDetailsListBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(data: DBDataModel) {

            setImage(data.imageUrl)
            setProgress()

            /**
             * Managing status like uploading,failed and completed.
             */
            mBinding.ivProgress.visibility = if (data.status == ImageUploadStatus.UPLOADING.toString()) View.VISIBLE else View.INVISIBLE
            mBinding.ivRetry.visibility = if (data.status == ImageUploadStatus.FAILED.toString()) View.VISIBLE else View.INVISIBLE
            mBinding.ivIsUpload.visibility = if (data.isUpload) View.VISIBLE else View.INVISIBLE


            mBinding.tvStatus.text =
                if (data.status == ImageUploadStatus.NOT_START.toString())
                    "InQueue"
                else data.status


            mBinding.ivRetry.setOnClickListener {
                callBack.invoke(adapterPosition)
            }


        }

        private fun setProgress() {
            Glide.with(itemView).load(R.drawable.uploading)
                .into(mBinding.ivProgress)
        }

        private fun setImage(url: String) {
            Glide.with(itemView).load(url)
                .into(mBinding.ivImage)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesListViewHolder {
        return ImagesListViewHolder(
            LayoutDetailsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {

        return imageData.size
    }

    override fun onBindViewHolder(holder: ImagesListViewHolder, position: Int) {
        holder.bind(imageData[position])
    }

    //DiffUtils
    private fun calculateDifferenceAndUpdate(newList: ArrayList<DBDataModel>) {
        val differCallback = DiffUtilCallback(newList, this.imageData)
        val diffResult = DiffUtil.calculateDiff(differCallback)
        this.imageData.clear()
        this.imageData.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

}