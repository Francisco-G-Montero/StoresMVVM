package com.frommetoyou.storeskotlin.mainModule.adapters

import com.frommetoyou.storeskotlin.common.entities.StoreEntity

interface OnClickListener {
    fun onClick(storeEntity: StoreEntity)
    fun onFavoriteStoreClick(storeEntity: StoreEntity)
    fun onDeleteStoreLongClick(storeEntity: StoreEntity)
}