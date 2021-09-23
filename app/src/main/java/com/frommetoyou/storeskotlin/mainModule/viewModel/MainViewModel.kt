package com.frommetoyou.storeskotlin.mainModule.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frommetoyou.storeskotlin.common.entities.StoreEntity
import com.frommetoyou.storeskotlin.common.utils.Constants
import com.frommetoyou.storeskotlin.common.utils.ErrorType
import com.frommetoyou.storeskotlin.common.utils.StoresException
import com.frommetoyou.storeskotlin.mainModule.model.MainInteractor
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel : ViewModel() {
    private var interactor: MainInteractor = MainInteractor()
    private val showProgress: MutableLiveData<Boolean> = MutableLiveData()
    private val errorType: MutableLiveData<ErrorType> = MutableLiveData()

    private val stores = interactor.stores

    fun getStores(): LiveData<MutableList<StoreEntity>> {
        return stores
    }

    fun getErrorType(): MutableLiveData<ErrorType> = errorType

    fun isShowProgress(): LiveData<Boolean> {
        return showProgress
    }

    fun deleteStore(storeEntity: StoreEntity) {
        excecuteAction { interactor.deleteStore(storeEntity) }
    }

    fun updateStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        excecuteAction { interactor.updateStore(storeEntity) }
    }

    private fun excecuteAction(block: suspend () -> Unit) {
        viewModelScope.launch {
            showProgress.value = Constants.SHOW
            try {
                block()
            } catch (e: StoresException) {
                errorType.value = e.errorType
            } finally {
                showProgress.value = Constants.HIDE
            }
        }
    }
}