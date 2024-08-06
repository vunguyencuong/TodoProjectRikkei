package com.lazyman.todo.ui.workspace

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.lazyman.todo.R
import com.lazyman.todo.adapter.ChooseWorkspaceAdapter
import com.lazyman.todo.data.Workspace
import com.lazyman.todo.data.repository.TodoRepo
import com.lazyman.todo.databinding.DialogWorkspacePickerBinding
import com.lazyman.todo.interfaces.BaseBottomDialogFragment
import com.lazyman.todo.others.constants.BundleKeys
import com.lazyman.todo.others.utilities.toast
import com.lazyman.todo.viewmodels.WorkspaceViewModel
import com.lazyman.todo.viewmodels.WorkspaceViewModelFactory

class WorkspacePickerDialog(
    private val repo: TodoRepo,
    private val onChooseWorkspaceSubmit: (String) -> Unit
) :
    BaseBottomDialogFragment(R.layout.dialog_workspace_picker) {

    private lateinit var binding: DialogWorkspacePickerBinding
    private lateinit var viewModel: WorkspaceViewModel
    private lateinit var adapter: ChooseWorkspaceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = DialogWorkspacePickerBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observing()
    }

    private fun setupViewModel() {

        viewModel = ViewModelProvider(
            this,
            WorkspaceViewModelFactory(repo)
        )[WorkspaceViewModel::class.java]
    }

    private fun setupClickListeners() {
        binding.apply {
            btAdd.setOnClickListener { addClicked() }
            btEdit.setOnClickListener { editClicked() }
        }
    }

    private fun setupRecyclerView() {
        adapter = ChooseWorkspaceAdapter(onWorkspaceSelected, onWorkspaceLongClicked)
        binding.rvWorkspace.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        binding.rvWorkspace.adapter = adapter
    }

    private fun observing() {
        viewModel.workspaceLiveData.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }
    }

    private fun editClicked() {
        requireActivity().toast(getString(R.string.edit_workspace_helper))
    }

    private fun addClicked() {
        val workspaceNames = viewModel.workspaceLiveData.value!!.map { it.workspaceName }
        val editWorkspaceDialog = EditWorkspaceDialog(onAddWorkspaceSubmit, workspaceNames)
        editWorkspaceDialog.show(childFragmentManager, EditWorkspaceDialog.TAG)
    }

    private val onWorkspaceSelected = { workspaceName: String ->
        onChooseWorkspaceSubmit(workspaceName)
        dismiss()
    }

    private val onAddWorkspaceSubmit = { workspace: Workspace, _: Boolean ->
        viewModel.addWorkspace(workspace)
    }

    private val onWorkspaceLongClicked = { workspace: Workspace ->
        val workspaceNames = viewModel.workspaceLiveData.value!!.map { it.workspaceName }
        val editWorkspaceDialog = EditWorkspaceDialog(onEditWorkspaceDialog, workspaceNames)

        editWorkspaceDialog.arguments = Bundle().apply {
            putSerializable(BundleKeys.WORKSPACE_OBJ, workspace)
        }
        editWorkspaceDialog.show(childFragmentManager, EditWorkspaceDialog.TAG)
    }
    private val onEditWorkspaceDialog = { workspace: Workspace, isDeleted: Boolean ->
        if (isDeleted) {
            viewModel.deleteWorkspace(workspace)
        } else {
            viewModel.editWorkspace(workspace)
        }
    }

    companion object {
        const val TAG = "DialogWorkspacePicker"
    }
}