package com.aksar.dungru.ui.profile.profilefragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aksar.dungru.adapters.ProfileFilesAdapter
import com.aksar.dungru.databinding.FragmentProfilePhotoListBinding
import com.aksar.dungru.models.response.ProfilePostData
import com.aksar.dungru.ui.home.feedOperation.PreviewActivity
import com.aksar.dungru.utils.Constance.IMAGE

private const val UserName ="UserName"
private const val UserImage ="UserImage"
class ProfilePhotoListFragment(private val itemList:List<ProfilePostData>) : Fragment() {
    private lateinit var binding:FragmentProfilePhotoListBinding
    private lateinit var adapter: ProfileFilesAdapter

    private var userName: String = ""
    private var userImage: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfilePhotoListBinding.inflate(layoutInflater,container,false)
        arguments?.let {
            userName = it.getString(UserName).toString()
            userImage = it.getString(UserImage).toString()
        }

        if(itemList.isNotEmpty()){
            binding.noDataView.visibility = View.GONE
            val photoProfileListRecycler = binding.photoProfileListRecycler
            adapter = ProfileFilesAdapter(requireContext(), itemList, IMAGE) {item->
                val intent = Intent(requireContext(), PreviewActivity::class.java)
                intent.putExtra("imageUrl", item.post_url)
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
            photoProfileListRecycler.adapter = adapter

        }else binding.noDataView.visibility = View.VISIBLE

        return binding.root
    }
    companion object {
        fun newInstance(itemList: List<ProfilePostData>, username: String, image: String)= ProfilePhotoListFragment(itemList).apply{
            arguments = Bundle().apply {
                putString(UserName, username)
                putString(UserImage, image)
            }
        }
    }
}