import Modules.Libraries
import com.egoriku.ext.withLibraries
import com.egoriku.ext.withProjects

plugins {
    id("HappyXPlugin")
    id("com.android.library")
}

happyPlugin {
    viewBindingEnabled = true
}

withProjects(
        Libraries.core,
        Libraries.extensions,
        Libraries.network,
        Libraries.navigation,
        Libraries.ui
)

withLibraries(
        Libs.appcompat,
        Libs.balloon,
        Libs.core,
        Libs.constraintLayout,
        Libs.fragment,
        Libs.koinAndroid,
        Libs.koinViewModel,
        Libs.lifecycleRuntime,
        Libs.liveData,
        Libs.material,
        Libs.playCore,
        Libs.viewBindingDelegates,
        Libs.viewModel
)