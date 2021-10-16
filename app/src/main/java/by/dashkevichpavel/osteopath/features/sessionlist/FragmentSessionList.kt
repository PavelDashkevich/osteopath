package by.dashkevichpavel.osteopath.features.sessionlist

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentSessionListBinding
import by.dashkevichpavel.osteopath.helpers.setupToolbar
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory

class FragmentSessionList : Fragment(R.layout.fragment_session_list) {
    private val viewModel: SessionListViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentSessionListBinding: FragmentSessionListBinding? = null
    private val binding get() = fragmentSessionListBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> binding.dlDrawerLayout.open()
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentSessionListBinding = null
    }

    private fun setupViews(view: View) {
        fragmentSessionListBinding = FragmentSessionListBinding.bind(view)
        setupToolbar(binding.tbActions)
        binding.lNavMenu.nvMain.setupWithNavController(findNavController())
    }

    private fun setupObservers() {
        viewModel.test = 0
    }
}