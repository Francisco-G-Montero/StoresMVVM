package com.frommetoyou.storeskotlin.mainModule

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.frommetoyou.storeskotlin.*
import com.frommetoyou.storeskotlin.common.entities.StoreEntity
import com.frommetoyou.storeskotlin.common.utils.ErrorType
import com.frommetoyou.storeskotlin.databinding.ActivityMainBinding
import com.frommetoyou.storeskotlin.editModule.EditStoreFragment
import com.frommetoyou.storeskotlin.editModule.viewModel.EditStoreViewModel
import com.frommetoyou.storeskotlin.mainModule.adapters.OnClickListener
import com.frommetoyou.storeskotlin.mainModule.adapters.StoreListAdapter
import com.frommetoyou.storeskotlin.mainModule.viewModel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: StoreListAdapter
    private lateinit var mGridLayoutManager: GridLayoutManager

    //MVVM
    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mEditStoreViewModel: EditStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupViewModel()

        mBinding.fab.setOnClickListener {
            launchEditFragment()
        }

        setupRecyclerView()
    }

    private fun setupViewModel() {
        mMainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mMainViewModel.getStores().observe(this, { stores ->
            mBinding.progressBar.visibility = View.GONE
            mAdapter.submitList(stores)
            Log.v("GSOON", Gson().toJson(stores))

        })
        mMainViewModel.isShowProgress().observe(this, { isShowProgress ->
            mBinding.progressBar.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        })
        mMainViewModel.getErrorType().observe(this){ errorType ->
            val msgRes = when(errorType){
                ErrorType.GET -> R.string.error_get_store
                ErrorType.INSERT -> R.string.error_add_store
                ErrorType.UPDATE -> R.string.error_update_store
                ErrorType.DELETE -> R.string.error_delete_store
                else -> R.string.error_unknown_store
            }
            Snackbar.make(mBinding.root, msgRes, Snackbar.LENGTH_SHORT).show()
        }
        mEditStoreViewModel = ViewModelProvider(this)[EditStoreViewModel::class.java]
        mEditStoreViewModel.getShowFab().observe(this){ isVisible ->
            if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
        }
    }

    private fun launchEditFragment(storeEntity: StoreEntity = StoreEntity()) {
        mEditStoreViewModel.setShowFab(false)
        mEditStoreViewModel.setStoreSelected(storeEntity)
        val fragment = EditStoreFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }

    private fun setupRecyclerView() {
        mAdapter = StoreListAdapter(this)
        mGridLayoutManager = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))
        //getAllStores()
        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayoutManager
            adapter = mAdapter
        }
    }

    override fun onClick(storeEntity: StoreEntity) {
        launchEditFragment(storeEntity)
    }

    override fun onFavoriteStoreClick(storeEntity: StoreEntity) {
        mMainViewModel.updateStore(storeEntity)
    }

    override fun onDeleteStoreLongClick(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_options_item)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(items) { _, which ->
                when (which) {
                    0 -> confirmDeleteDialog(storeEntity)
                    1 -> dialPhone(storeEntity.phone)
                    2 -> goToWebsite(storeEntity.website)
                }
            }
            .show()
    }

    private fun confirmDeleteDialog(storeEntity: StoreEntity) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                mMainViewModel.deleteStore(storeEntity)
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }

    private fun dialPhone(phone: String) {
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }
        startIntent(callIntent)
    }

    private fun goToWebsite(website: String) {
        if (website.isEmpty()) {
            Toast.makeText(this, R.string.main_error_website, Toast.LENGTH_SHORT).show()
        } else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }
            startIntent(websiteIntent)
        }
    }

    private fun startIntent(intent: Intent) {
        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)
        else Toast.makeText(this, R.string.main_error_cant_resolve, Toast.LENGTH_SHORT).show()
    }

}