package com.egoriku.ladyhappy.catalog.subcategory.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.egoriku.extensions.gone
import com.egoriku.extensions.logD
import com.egoriku.extensions.visible
import com.egoriku.ladyhappy.catalog.R
import com.egoriku.ladyhappy.catalog.databinding.FragmentCatalogBinding
import com.egoriku.ladyhappy.catalog.subcategory.presentation.SubCategoriesViewModel
import com.egoriku.ladyhappy.catalog.subcategory.presentation.SubcategoryScreenState
import com.egoriku.ladyhappy.catalog.subcategory.presentation.controller.SubCategoryController
import com.egoriku.ladyhappy.catalog.subcategory.presentation.controller.balloon.ViewHolderBalloonFactory
import com.skydoves.balloon.balloon
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import org.koin.core.parameter.parametersOf
import ru.surfstudio.android.easyadapter.EasyAdapter
import ru.surfstudio.android.easyadapter.ItemList
import kotlin.properties.Delegates

private const val INITIAL_PREFETCH_COUNT = 7
const val ARGUMENT_CATEGORY_ID = "category_id"

class SubCategoryFragment : Fragment(R.layout.fragment_catalog) {

    private val binding by viewBinding(FragmentCatalogBinding::bind)

    private val viewHolderBalloon by balloon(ViewHolderBalloonFactory::class)

    private val catalogViewModel: SubCategoriesViewModel by lifecycleScope.viewModel(this) {
        parametersOf(arguments?.getInt(ARGUMENT_CATEGORY_ID))
    }

    private var subcategoryController: SubCategoryController by Delegates.notNull()

    private val catalogAdapter = EasyAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subcategoryController = SubCategoryController(
                onCatalogItemClick = {
                    logD("Item ${it.name} was clicked")
                },
                onTrendingClick = {
                    viewHolderBalloon?.showAlignLeft(it)
                }
        )

        binding.catalogRecyclerView.apply {
            layoutManager = LinearLayoutManager(context).apply {
                initialPrefetchItemCount = INITIAL_PREFETCH_COUNT
            }
            adapter = catalogAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))
        }

        catalogViewModel.subcategoryItems.observe(viewLifecycleOwner) {
            binding.render(it)
        }
    }

    private fun FragmentCatalogBinding.render(screenState: SubcategoryScreenState) {
        when (screenState) {
            is SubcategoryScreenState.Success -> {
                errorView.gone()
                progressBar.gone()
                catalogAdapter.setItems(
                        ItemList.create()
                                .addAll(screenState.screenData, subcategoryController)
                )
            }
            is SubcategoryScreenState.Error -> {
                errorView.visible()
                progressBar.gone()
            }
            is SubcategoryScreenState.Loading -> {
                errorView.gone()
                progressBar.visible()
            }
        }
    }
}