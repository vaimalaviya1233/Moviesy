package com.kpstv.yts.ui.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kpstv.navigation.*
import com.kpstv.yts.extensions.MovieBase
import com.kpstv.yts.ui.activities.StartActivity
import com.kpstv.yts.ui.fragments.DetailFragment
import com.kpstv.yts.ui.fragments.MoreFragment
import kotlin.reflect.KClass

class StartViewModel : ViewModel() {
    private val _navigation = MutableLiveData<NavigationOption>()
    val navigation: LiveData<NavigationOption> = _navigation

    private val _errors = MutableLiveData<Exception?>(null)
    val errors: LiveData<Exception?> = _errors

    fun navigateTo(
        screen: StartActivity.Screen,
        args: BaseArgs? = null,
        transactionType: Navigator.TransactionType = Navigator.TransactionType.REPLACE,
        animation: NavAnimation = AnimationDefinition.None,
        remember: Boolean = false,
        clearAllHistory: Boolean = false
    ) {
        val options = Navigator.NavOptions(
            args = args,
            transaction = transactionType,
            animation = animation,
            remember = remember,
            clearAllHistory = clearAllHistory
        )

        _navigation.value = NavigationOption(screen.clazz, options)
    }

    fun goToSearch(payload: AnimationDefinition.Shared? = null) {
        navigateTo(
            screen = StartActivity.Screen.SEARCH,
            remember = true,
            animation = payload ?: AnimationDefinition.None
        )
    }

    fun goToDetail(ytsId: Int? = null, tmDbId: String? = null, movieUrl: String? = null, add: Boolean = false) {
        navigateTo(
            screen = StartActivity.Screen.DETAIL,
            args = DetailFragment.Args(tmDbId = tmDbId, ytsId = ytsId, movieUrl = movieUrl),
            remember = true,
            transactionType = if (add) Navigator.TransactionType.ADD else Navigator.TransactionType.REPLACE,
            animation = AnimationDefinition.Fade,
        )
    }

    fun goToMore(
        titleText: String,
        queryMap: Map<String, String>,
        base: MovieBase = MovieBase.YTS,
        add: Boolean = false
    ) {
        navigateTo(
            screen = StartActivity.Screen.MORE,
            animation = AnimationDefinition.Fade,
            transactionType = if (add) Navigator.TransactionType.ADD else Navigator.TransactionType.REPLACE,
            remember = true,
            args = MoreFragment.Args(
                title = titleText,
                movieBaseString = base.toString(),
                keyArrayList = ArrayList(queryMap.keys),
                valueArrayList = ArrayList(queryMap.values)
            )
        )
    }

    fun propagateError(ex: Exception) {
        _errors.value = ex
    }

    data class NavigationOption(
        val clazz: KClass<out Fragment>,
        val options: Navigator.NavOptions
    )
}