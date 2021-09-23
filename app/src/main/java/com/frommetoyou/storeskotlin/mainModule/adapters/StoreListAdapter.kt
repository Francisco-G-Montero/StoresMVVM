package com.frommetoyou.storeskotlin.mainModule.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.frommetoyou.storeskotlin.R
import com.frommetoyou.storeskotlin.common.entities.StoreEntity
import com.frommetoyou.storeskotlin.databinding.ItemStoreBinding

class StoreListAdapter(
    private var listener: OnClickListener) :
    ListAdapter<StoreEntity, RecyclerView.ViewHolder>(StoreDiffCallback()) {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val store = getItem(position)
        with(holder as ViewHolder) {
            setListener(store)
            binding.tvName.text = store.name
            binding.cbFavorite.isChecked = store.isFavorite
            Glide.with(mContext)
                .load(store.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.ivPhoto)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemStoreBinding.bind(view)
        fun setListener(storeEntity: StoreEntity) {
            binding.root.setOnClickListener { listener.onClick(storeEntity) }
            binding.cbFavorite.setOnClickListener { listener.onFavoriteStoreClick(storeEntity) }
            binding.root.setOnLongClickListener {
                listener.onDeleteStoreLongClick(storeEntity)
                true
            }
        }
    }

    class StoreDiffCallback: DiffUtil.ItemCallback<StoreEntity>(){
        override fun areItemsTheSame(oldItem: StoreEntity, newItem: StoreEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StoreEntity, newItem: StoreEntity): Boolean {
            return oldItem == newItem
        }
    }
}