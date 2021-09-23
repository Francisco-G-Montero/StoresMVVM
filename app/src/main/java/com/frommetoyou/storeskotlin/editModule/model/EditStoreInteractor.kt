package com.frommetoyou.storeskotlin.editModule.model

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import com.frommetoyou.storeskotlin.StoreApplication
import com.frommetoyou.storeskotlin.common.entities.StoreEntity
import com.frommetoyou.storeskotlin.common.utils.ErrorType
import com.frommetoyou.storeskotlin.common.utils.StoresException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EditStoreInteractor {

    fun getStoreById(storeId: Long): LiveData<StoreEntity> {
        return StoreApplication.database.storeDao().getStoreById(storeId)
    }

    suspend fun saveStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        try {
            StoreApplication.database.storeDao().addStore(storeEntity)
        }catch (e: SQLiteConstraintException){
            throw StoresException(errorType = ErrorType.INSERT)
        }
    }

    suspend fun updateStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        try {
            val result = StoreApplication.database.storeDao().updateStore(storeEntity)
            if (result == 0) throw StoresException(ErrorType.UPDATE)
        }catch (e: SQLiteConstraintException){
            throw StoresException(errorType = ErrorType.UPDATE)
        }
    }
}