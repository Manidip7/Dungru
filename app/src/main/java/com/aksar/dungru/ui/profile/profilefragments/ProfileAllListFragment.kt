package com.aksar.dungru.ui.profile.profilefragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aksar.dungru.adapters.ProfileFilesAdapter
import com.aksar.dungru.databinding.FragmentProfileAllListBinding
import com.aksar.dungru.models.response.ProfilePostData
import com.aksar.dungru.ui.home.feedOperation.PreviewActivity
import com.aksar.dungru.utils.Constance.ALL

private const val UserName ="UserName"
private const val UserImage ="UserImage"
class ProfileAllListFragment(private val itemList: List<ProfilePostData>) : Fragment() {

    private lateinit var binding: FragmentProfileAllListBinding
    private lateinit var adapter: ProfileFilesAdapter
    private var userName: String = ""
    private var userImage: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileAllListBinding.inflate(layoutInflater, container, false)

        arguments?.let {
            userName = it.getString(UserName).toString()
            userImage = it.getString(UserImage).toString()
        }

        Log.d("All", itemList.size.toString())
        if (itemList.isNotEmpty()) {
            binding.noDataView.visibility =View.GONE
            val profileFilesRecycler = binding.allProfileListRecycler
            adapter = ProfileFilesAdapter(requireContext(), itemList, ALL) { item->
                val intent = Intent(requireContext(), PreviewActivity::class.java)
                intent.putExtra("postTime", item.uploaded_on)
                intent.putExtra(
                    if (item.post_type == "video") "videoUrl" else "imageUrl",
                    item.post_url
                )
                intent.putExtra("postTime",item.uploaded_on)
                intent.putExtra("caption",item.caption)
                intent.putExtra("user_name",userName)
                intent.putExtra("user_profile",userImage)
                intent.putExtra("like_count",item.like_count)
                intent.putExtra("comment_count",item.comment_count)
                intent.putExtra("share_count",item.share_count)
                intent.putExtra("post_id",item.post_id)
                intent.putExtra("Like_flag",item.isLiked)
                Log.d( "onCreateView: ",item.isLiked.toString())
                startActivity(intent)
            }
            profileFilesRecycler.adapter = adapter

        } else binding.noDataView.visibility =View.VISIBLE
        
        return binding.root
    }

    companion object {
        fun newInstance(itemList: List<ProfilePostData>, username: String, image: String) = ProfileAllListFragment(itemList).apply{
            arguments = Bundle().apply {
                putString(UserName, username)
                putString(UserImage, image)
            }
        }
    }
}