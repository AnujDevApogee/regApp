package com.apogee.registration.utils


import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.apogee.registration.databinding.ActionBarLayoutBinding

//R.drawable.ic_back_arrow
fun Fragment.displayActionBar(
    srcTitle: String,
    binding: ActionBarLayoutBinding,
    menu: Int = -1,
    bckIc: Int = -1,
    onMenuItem: OnItemClickListener? = null
) {
    if (bckIc != -1) {
        binding.toolBar.setNavigationIcon(bckIc)
    }else{
//        binding.scrName.updateLayoutParams<ConstraintLayout.LayoutParams> {
//            setMargins(100, 0, 5, 0)
//        }
    }
    if (menu != -1) {
        binding.toolBar.inflateMenu(menu)
        binding.toolBar.setOnMenuItemClickListener {
            onMenuItem?.onClickListener(it)
            return@setOnMenuItemClickListener true
        }
    }
    binding.scrName.text = srcTitle

    binding.toolBar.setNavigationOnClickListener {
        findNavController().popBackStack()
    }
}
