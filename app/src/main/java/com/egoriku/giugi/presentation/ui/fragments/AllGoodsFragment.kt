package com.egoriku.giugi.presentation.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.egoriku.corelib_kt.extensions.fromApi
import com.egoriku.corelib_kt.extensions.inflate
import com.egoriku.giugi.App
import com.egoriku.giugi.R
import com.egoriku.giugi.di.AllGoodsComponent
import com.egoriku.giugi.di.module.AllGoodsModule
import com.egoriku.giugi.presentation.presenters.AllGoodsMVP
import com.egoriku.giugi.presentation.presenters.impl.AllGoodsPresenterNew
import com.egoriku.giugi.presentation.ui.activity.MainActivity
import com.egoriku.giugi.presentation.ui.base.BaseFragment
import ext.adapter.GhostAdapter
import kotlinx.android.synthetic.main.fragment_all_goods.*
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class AllGoodsFragment : BaseFragment(), AllGoodsMVP.View {

    @Inject
    private lateinit var router: Router
    private lateinit var presenter: AllGoodsPresenterNew
    private var component: AllGoodsComponent? = null

    lateinit var allGoodsAdapter: GhostAdapter

    companion object {
        fun newInstance(): AllGoodsFragment {
            return AllGoodsFragment()
        }
    }

    override fun injectDependencies() {
        if (component == null) {
            component = DaggerAllGoodsComponent.builder()
                    .appComponent(App.instance.appComponent)
                    .allGoodsModule(AllGoodsModule(this))
                    .build()
            component?.inject(this)
        }
    }

    override fun attachToPresenter() {
        this.presenter.attachView(this)
    }

    override fun detachFromPresenter() {
        this.presenter.detachView()
        activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.getArgs(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_all_goods, false)
        initRecyclerView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getCategories()
    }

    override fun getArgs(_bundle: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAttach(context: Context?) {
        this.injectDependencies()
        this.attachToPresenter()
        this.showTitle(getString(R.string.navigation_drawer_all_goods))
        super.onAttach(context)
    }

    override fun onDetach() {
        detachFromPresenter()
        super.onDetach()
    }

    override fun showTitle(message: String) {
        (activity as MainActivity).onFragmentStart(message)
    }

    override fun onLandscape() {
    }

    override fun showCategories() {
    }

    override fun onPortrait() {

    }

    override fun showLoading() {

    }

    override fun showNews() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {

    }

    override fun showNoNetwork() {

    }

    private fun initRecyclerView() {
        val adapterToy = GhostAdapter()
        //adapterToy.addItems(list)

        recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = adapterToy
        }

        appbarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {

            internal var scrollRange = -1

            @SuppressLint("NewApi")
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }

                val scale = 1 + verticalOffset / scrollRange.toFloat()

                toolbarRounded.setScale(scale)

                if (scale <= 0) {
                    fromApi(Build.VERSION_CODES.LOLLIPOP, true) {
                        appbarLayout.elevation = 0f
                    }
                } else {
                    appbarLayout.elevation = 0f
                }
            }
        })
    }
}