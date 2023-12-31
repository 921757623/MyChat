package com.hust.homepage.ui.addressBook

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hust.chat.ChatActivity
import com.hust.database.BaseApplication
import com.hust.homepage.HomePageActivityViewModel
import com.hust.homepage.databinding.FragmentAddressBookBinding
import com.hust.netbase.ChatUnit
import com.hust.resbase.SpaceItemDecoration
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddressBookFragment : Fragment() {

    private var _binding: FragmentAddressBookBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddressBookFragmentViewModel by viewModels()

    private val parentViewModel: HomePageActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressBookBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    override fun onResume() {
        initData()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initData() {
        viewModel.getLocalAddressList()
    }

    private fun initView() {
        val adapter = AddressBookRecycleViewAdapter()
        adapter.setOnItemClickListener(object : AddressBookRecycleViewAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int, data: ChatUnit) {
                if(position < 7) {
                    viewModel.tip.value = "功能还在开发中"
                }else {
                    val intent = Intent(this@AddressBookFragment.requireContext(), ChatActivity::class.java)
                    intent.putExtra("chatUnit", data)
                    BaseApplication.certainFriendPicPath = data.profilePicPath
                    this@AddressBookFragment.requireContext().startActivity(intent)
                }
            }
        })
        binding.rvAddressBookList.apply {
            this.adapter = adapter
            this.addItemDecoration(object : SpaceItemDecoration(0, 0) {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val layoutManager: LinearLayoutManager =
                        parent.layoutManager as LinearLayoutManager
                    if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                        //最后一项需要 bottom
                        if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {
                            outRect.bottom = topBottom
                        }
                        if (parent.getChildAdapterPosition(view) == 5 || parent.getChildAdapterPosition(
                                view
                            ) == 7
                        ) {
                            outRect.top = topBottom + 15
                        } else {
                            outRect.top = topBottom
                        }
                        outRect.left = leftRight
                        outRect.right = leftRight
                    } else {
                        //最后一项需要right
                        if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {
                            outRect.right = leftRight
                        }
                        outRect.top = topBottom
                        outRect.left = leftRight
                        outRect.bottom = topBottom
                    }
                }
            })
        }
        viewModel.apply {
            tip.observe(viewLifecycleOwner) {
                it?.let {
                    if (it.isNotEmpty()) {
                        Toast.makeText(
                            this@AddressBookFragment.requireContext(),
                            it,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        doneShowingTip()
                    }
                }
            }
            parentViewModel.isRefresh.observe(viewLifecycleOwner) {
                initData()
            }
        }

        lifecycleScope.launch {
            viewModel.addressBookList.collectLatest {
                adapter.submitList(it)
            }
        }
    }
}