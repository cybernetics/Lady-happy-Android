package com.egoriku.ladyhappy.presentation.fragment.allgoods

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.egoriku.corelib_kt.dsl.hide
import com.egoriku.corelib_kt.dsl.show
import com.egoriku.ladyhappy.R
import com.egoriku.ladyhappy.presentation.activity.main.MainActivity
import com.egoriku.ladyhappy.presentation.adapter.animator.DefaultItemAnimator
import com.egoriku.ladyhappy.presentation.base.BaseInjectableFragment
import com.egoriku.ladyhappy.presentation.fragment.allgoods.recycler.controller.CategoriesController
import com.egoriku.ladyhappy.presentation.fragment.allgoods.recycler.controller.ErrorStateController
import com.egoriku.ladyhappy.presentation.fragment.allgoods.recycler.controller.NewsController
import com.egoriku.ladyhappy.presentation.fragment.allgoods.recycler.controller.NewsHeaderController
import kotlinx.android.synthetic.main.fragment_all_goods.*
import org.jetbrains.anko.support.v4.toast
import ru.surfstudio.easyadapter.recycler.EasyAdapter
import ru.surfstudio.easyadapter.recycler.ItemList
import javax.inject.Inject

class AllGoodsFragment : BaseInjectableFragment<AllGoodsContract.View, AllGoodsContract.Presenter>(), AllGoodsContract.View {

    @Inject
    lateinit var allGoodsPresenter: AllGoodsContract.Presenter

    private lateinit var categoriesController: CategoriesController
    private lateinit var errorStateController: ErrorStateController
    private lateinit var newsHeaderController: NewsHeaderController
    private lateinit var newsController: NewsController

    private val allGoodsAdapter = EasyAdapter()

    companion object {
        fun newInstance() = AllGoodsFragment()
    }

    override fun initPresenter() = allGoodsPresenter

    override fun provideLayout(): Int = R.layout.fragment_all_goods

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showTitle(R.string.navigation_drawer_all_goods)

        initRecyclerView()

        presenter.loadData()
    }

    private fun initRecyclerView() {
        recyclerViewAllGoods.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = allGoodsAdapter
        }

        categoriesController = CategoriesController(onClickListener = {
            toast("on ${it.title} click")
        })

        errorStateController = ErrorStateController(onReloadClickListener = {
            presenter.loadData()
        })

        newsHeaderController = NewsHeaderController()
        newsController = NewsController()
    }

    override fun render(screenModel: AllGoodsScreenModel) {
        val itemList = ItemList.create()
                .addAll(screenModel.categories, categoriesController)
                .addIf(!screenModel.newsEmpty(), newsHeaderController)
                .addAll(screenModel.news, newsController)
                .addIf(screenModel.isEmpty(), errorStateController)

        allGoodsAdapter.setItems(itemList)
    }

    override fun showTitle(@StringRes title: Int) {
        (activity as MainActivity).setUpToolbar(title)
    }

    override fun showLoading() {
        progressView.show()
    }

    override fun hideLoading() {
        progressView.hide()
    }
}