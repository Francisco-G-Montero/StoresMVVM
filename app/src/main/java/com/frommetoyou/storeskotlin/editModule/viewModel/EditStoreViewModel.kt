package com.frommetoyou.storeskotlin.editModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frommetoyou.storeskotlin.common.entities.StoreEntity
import com.frommetoyou.storeskotlin.common.utils.ErrorType
import com.frommetoyou.storeskotlin.common.utils.StoresException
import com.frommetoyou.storeskotlin.editModule.model.EditStoreInteractor
import kotlinx.coroutines.launch

class EditStoreViewModel : ViewModel() {
    private val showFab = MutableLiveData<Boolean>()
    private val result = MutableLiveData<Any>()
    private val interactor: EditStoreInteractor = EditStoreInteractor()
    private var storeId: Long = 0L
    private val errorType: MutableLiveData<ErrorType> = MutableLiveData()

    fun saveStore(storeEntity: StoreEntity) {
        executeAction(storeEntity) { interactor.saveStore(storeEntity) }
    }

    fun updateStore(storeEntity: StoreEntity) {
        executeAction(storeEntity) { interactor.updateStore(storeEntity) }
    }

    fun setStoreSelected(storeEntity: StoreEntity) {
        storeId = storeEntity.id
    }

    fun getStoreSelected(): LiveData<StoreEntity> {
        return interactor.getStoreById(storeId)
    }

    fun setErrorType(errorType: ErrorType){
        this.errorType.value = errorType
    }

    fun setShowFab(isVisible: Boolean) {
        showFab.value = isVisible
    }

    fun getShowFab(): LiveData<Boolean> {
        return showFab
    }

    fun setResult(value: Any) {
        result.value = value
    }

    fun getResult(): LiveData<Any> {
        return result
    }

    fun getErrorType(): MutableLiveData<ErrorType> = errorType

    private fun executeAction(storeEntity: StoreEntity, block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
                result.value = storeEntity
            } catch (e: StoresException) {
                errorType.value = e.errorType
            }
        }
    }
}