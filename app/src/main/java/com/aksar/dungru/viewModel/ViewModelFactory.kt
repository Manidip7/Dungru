package com.aksar.dungru.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.repository.Repository
import java.lang.IllegalArgumentException

class ViewModelFactory(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository) as T

            modelClass.isAssignableFrom(OtpViewModel::class.java) -> OtpViewModel(repository) as T

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(repository) as T

            modelClass.isAssignableFrom(PasswordViewModel::class.java) -> PasswordViewModel(repository) as T

            modelClass.isAssignableFrom(SettingViewModel::class.java) -> SettingViewModel(repository) as T

            modelClass.isAssignableFrom(FeedViewModel::class.java) -> FeedViewModel(repository) as T

            modelClass.isAssignableFrom(FollowerFollowingsViewModel::class.java) -> FollowerFollowingsViewModel(repository) as T

            else -> throw IllegalArgumentException("View model not found")
        }
    }
}