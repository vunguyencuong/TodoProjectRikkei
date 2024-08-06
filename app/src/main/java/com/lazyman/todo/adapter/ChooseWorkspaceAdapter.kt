package com.lazyman.todo.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lazyman.todo.data.Workspace
import com.lazyman.todo.databinding.ItemCardWorkspaceBinding
import com.lazyman.todo.others.utilities.ColorUtils

class ChooseWorkspaceAdapter(
    private val onWorkspaceClickListener: (String) -> Unit,
    private val onWorkspaceLongClickListener: (Workspace) -> Unit
) :
    ListAdapter<Workspace, ChooseWorkspaceAdapter.ChooseWorkspaceViewHolder>(
        DIFF_CALLBACK
    ) {

    fun updateList(workspaces: List<Workspace>) {
        submitList(workspaces.toList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseWorkspaceViewHolder {
        val binding =
            ItemCardWorkspaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChooseWorkspaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChooseWorkspaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChooseWorkspaceViewHolder(private val binding: ItemCardWorkspaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(workspace: Workspace) {
            binding.tvWorkspaceName.backgroundTintList =
                ColorStateList.valueOf(workspace.workspaceColor)
            binding.tvWorkspaceName.text = workspace.workspaceName
            binding.tvWorkspaceName.setTextColor(ColorUtils.getContrastColor(workspace.workspaceColor))
            binding.root.setOnClickListener {
                onWorkspaceClickListener.invoke(workspace.workspaceName)
            }
            binding.root.setOnLongClickListener {
                onWorkspaceLongClickListener.invoke(workspace)
                true
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Workspace>() {
            override fun areItemsTheSame(oldItem: Workspace, newItem: Workspace): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Workspace, newItem: Workspace): Boolean {
                return oldItem.workspaceName == newItem.workspaceName
            }
        }
    }
}