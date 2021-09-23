package com.frommetoyou.storeskotlin.mainModule.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.frommetoyou.storeskotlin.StoreApplication
import com.frommetoyou.storeskotlin.common.entities.StoreEntity
import com.frommetoyou.storeskotlin.common.utils.ErrorType
import com.frommetoyou.storeskotlin.common.utils.StoresException
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainInteractor {
    //https://storesfran.free.beeceptor.com
    val stores: LiveData<MutableList<StoreEntity>> = liveData {
        //delay(500) //el 1_000 no hace nada, es para ver mejor los numeros
        val storesLiveData = StoreApplication.database.storeDao().getAllStores()

        emitSource(storesLiveData.map { stores ->
            stores.sortedBy { it.name }.toMutableList()
        })
    }

suspend fun deleteStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO) {
    val result = StoreApplication.database.storeDao().deleteStore(storeEntity)
    if (result == 0) throw StoresException(ErrorType.DELETE)
}

suspend fun updateStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO) {
    val result = StoreApplication.database.storeDao().updateStore(storeEntity)
    if (result == 0) throw StoresException(ErrorType.UPDATE)
}
}