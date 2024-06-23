package com.aksar.dungru.ui.profile.profilefragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aksar.dungru.adapters.ProfileFilesAdapter
import com.aksar.dungru.databinding.FragmentProfileVideoListBinding
import com.aksar.dungru.models.response.ProfilePostData
import com.aksar.dungru.ui.home.feedOperation.PreviewActivity
import com.aksar.dungru.utils.Constance.VIDEO

private const val UserName ="UserName"
private const val UserImage ="UserImage"
class ProfileVideoListFragment(private val itemList: List<ProfilePostData>) : Fragment() {
    private lateinit var binding: FragmentProfileVideoListBinding
    private lateinit var adapter: ProfileFilesAdapter
    private var userName: String = ""
    private var userImage: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileVideoListBinding.inflate(layoutInflater, container, false)
        arguments?.let {
            userName = it.getString(UserName).toString()
            userImage = it.getString(UserImage).toString()
        }

        if (itemList.isNotEmpty()) {
            binding.noDataView.visibility = View.GONE
            val videoProfileListRecycler = binding.videoProfileListRecycler
            adapter = ProfileFilesAdapter(requireContext(), itemList, VIDEO) {item->
                Toast.makeText(requireContext(), "Video", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), PreviewActivity::class.java)
                intent.putExtra("videoUrl", item.post_url)
                intent.putExtra("postTime",item.uploaded_on)
                intent.putExtra("caption",item.caption)
                intent.putExtra("user_name",userName)
                intent.putExtra("user_profile",userImage)
                intent.putExtra("like_count",item.like_count)
                intent.putExtra("comment_count",item.comment_count)
                intent.putExtra("share_count",item.share_count)
                intent.putExtra("post_id",item.post_id)
                intent.putExtra("Like_flag",item.isLiked)
                startActivity(intent)
            }
            videoProfileListRecycler.adapter = adapter

        } else  binding.noDataView.visibility = View.VISIBLE

        return binding.root
    }


    companion object {
        fun newInstance(itemList: List<ProfilePostData>, username: String, image: String) = ProfileVideoListFragment(itemList).apply {
            arguments = Bundle().apply {
                putString(UserName, username)
                putString(UserImage, image)
            }
        }
    }
}