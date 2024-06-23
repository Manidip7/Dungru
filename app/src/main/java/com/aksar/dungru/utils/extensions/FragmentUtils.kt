package com.aksar.dungru.utils.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

// Extension function to perform fragment transaction  within an activity
fun FragmentActivity.replaceFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    tag: String? = null
) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(containerId, fragment, tag)
    if (addToBackStack) {
        transaction.addToBackStack(tag)
      }
    transaction.commit()
}

// Extension function to perform fragment transaction from within a fragment
fun Fragment.replaceFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    tag: String? = null


) {
    activity?.replaceFragment(containerId, fragment, addToBackStack, tag)
}

// Extension function to add fragment within an activity
fun FragmentActivity.addFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    tag: String? = null
) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.add(containerId, fragment, tag)
    if (addToBackStack) {
        transaction.addToBackStack(tag)
    }
    transaction.commit()
}

// Extension function to add fragment from within a fragment
fun Fragment.addFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    tag: String? = null
) {
    activity?.addFragment(containerId, fragment, addToBackStack, tag)
}


// Extension function to clear backstack from within a fragment
fun Fragment.clearFragmentBackstack() {
    val count = parentFragmentManager.backStackEntryCount
    for (i in 0 until count) { // Change to until count
        parentFragmentManager.popBackStack()
    }
}

// Extension function to clear backstack from activity
fun FragmentActivity.clearFragmentBackstack() {
    val count = supportFragmentManager.backStackEntryCount
    for (i in 0 until count) { // Change to until count
        supportFragmentManager.popBackStack()
    }
}

