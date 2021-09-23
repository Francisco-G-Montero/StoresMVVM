package com.frommetoyou.storeskotlin.editModule

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.frommetoyou.storeskotlin.R
import com.frommetoyou.storeskotlin.common.entities.StoreEntity
import com.frommetoyou.storeskotlin.common.utils.ErrorType
import com.frommetoyou.storeskotlin.databinding.FragmentEditStoreBinding
import com.frommetoyou.storeskotlin.editModule.viewModel.EditStoreViewModel
import com.frommetoyou.storeskotlin.mainModule.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout


class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private lateinit var mStoreEntity: StoreEntity
    private lateinit var mEditStoreViewModel: EditStoreViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEditStoreViewModel =
            ViewModelProvider(requireActivity())[EditStoreViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentEditStoreBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupTextFields()
    }

    private fun setupViewModel() {
        mEditStoreViewModel.getStoreSelected().observe(viewLifecycleOwner) {
            mStoreEntity = it ?: StoreEntity()
            if (it != null) {
                mIsEditMode = true
                setUiStore(it)
            } else {
                mIsEditMode = false
            }
            setupActionBar()
        }
        mEditStoreViewModel.getResult().observe(viewLifecycleOwner) { result ->
            hideKeyboard()
            when (result) {
                is StoreEntity -> {
                    val messageRes =
                        if (result.id == 0L)  R.string.edit_store_message_added_success
                        else R.string.edit_store_message_update_success

                    mEditStoreViewModel.setStoreSelected(mStoreEntity)
                    Snackbar.make(
                        mBinding.root,
                        messageRes,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    mActivity?.onBackPressed()
                }
            }
        }
        mEditStoreViewModel.getErrorType().observe(viewLifecycleOwner){ errorType ->
            if ( errorType != ErrorType.NONE){
                val msgRes = when(errorType){
                    ErrorType.GET -> R.string.error_get_store
                    ErrorType.INSERT -> R.string.error_add_store
                    ErrorType.UPDATE -> R.string.error_update_store
                    ErrorType.DELETE -> R.string.error_delete_store
                    else -> R.string.error_unknown_store
                }
                Snackbar.make(mBinding.root, msgRes, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title =
            if (mIsEditMode) getString(R.string.edit_store_title_edit)
            else getString(R.string.edit_store_title_add)
        setHasOptionsMenu(true)
    }

    private fun loadImage(url: String) {
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.ivPhoto)
    }

    private fun setupTextFields() {
        with(mBinding) {
            etPhotoUrl.addTextChangedListener {
                validateFields(tilPhotoUrl)
                loadImage(it.toString().trim())
            }
            etPhone.addTextChangedListener { validateFields(tilPhone) }
            etStoreName.addTextChangedListener { validateFields(tilStoreName) }
        }
    }

    private fun setUiStore(storeEntity: StoreEntity) {
        with(mBinding) {
            etStoreName.setText(storeEntity.name)
            etPhone.setText(storeEntity.phone)
            etWebsite.setText(storeEntity.website)
            etPhotoUrl.setText(storeEntity.photoUrl)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save -> {
                if (validateFields(
                        mBinding.tilPhotoUrl,
                        mBinding.tilPhone,
                        mBinding.tilStoreName
                    )
                ) {
                    with(mStoreEntity) {
                        name = mBinding.etStoreName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }
                    if (mIsEditMode) mEditStoreViewModel.updateStore(mStoreEntity)
                    else mEditStoreViewModel.saveStore(mStoreEntity)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean {
        var isValid = true
        for (textField in textFields) {
            if (textField.editText?.text.toString().trim().isEmpty()) {
                textField.error = getString(R.string.common_helper_required)
                textField.editText?.requestFocus()
                isValid = false
            } else textField.error = null
        }
        if (!isValid) Snackbar.make(
            mBinding.root,
            R.string.edit_store_message_validate,
            Snackbar.LENGTH_SHORT
        ).show()
        return isValid
    }

    private fun hideKeyboard() {
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        if (view != null) {
            imm?.hideSoftInputFromWindow(requireView().windowToken, 0)
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mEditStoreViewModel.setShowFab(true)
        mEditStoreViewModel.setResult(Any())
        mEditStoreViewModel.setErrorType(ErrorType.NONE)
        setHasOptionsMenu(false)
        super.onDestroy()
    }
}