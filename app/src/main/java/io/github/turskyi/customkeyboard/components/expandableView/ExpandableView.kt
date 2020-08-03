package io.github.turskyi.customkeyboard.components.expandableView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import io.github.turskyi.customkeyboard.components.ResizableRelativeLayout

abstract class ExpandableView(
        context: Context, attr: AttributeSet) :
        ResizableRelativeLayout(context, attr) {

    private var state: ExpandableState? = null
    private val stateListeners = ArrayList<ExpandableStateListener>()

    val isExpanded: Boolean
        get() = state === ExpandableState.EXPANDED

    init {
        /* view is expanded when initially created */
        state = ExpandableState.EXPANDED
        visibility = View.INVISIBLE
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        /* collapse view after initial inflation */
        translateLayout()
    }

    fun registerListener(listener: ExpandableStateListener) = stateListeners.add(listener)

    fun translateLayout() {
        /* Ignore calls that occur during animation (prevents issues from wood-pecker'ing) */
        if (state !== ExpandableState.EXPANDING && state !== ExpandableState.COLLAPSING) {
            val pixels = 500.toDp
            /* translates layout 1px per millisecond */
            val millis : Long = pixels.toLong()
            val deltaY: Float
            when (state) {
                ExpandableState.EXPANDED -> {
                    updateState(ExpandableState.COLLAPSING)
                    /* pushes layout down 500 device pixels */
                    deltaY = pixels.toFloat()
                    animate().translationY(deltaY).setDuration(millis).withEndAction {
                        updateState(ExpandableState.COLLAPSED)
                        visibility = View.INVISIBLE
                    }.start()
                }
                ExpandableState.COLLAPSED -> {
                    updateState(ExpandableState.EXPANDING)
                    visibility = View.VISIBLE
                    /* pulls layout back to its original position */
                    deltaY = 0.0f
                    animate().translationY(deltaY).setDuration(millis).withEndAction {
                        updateState(ExpandableState.EXPANDED)
                    }.start()
                }
                else -> return
            }
        }
    }

    private fun updateState(nextState: ExpandableState) {
        state = nextState
        for (listener in stateListeners) {
            listener.onStateChange(nextState)
        }
    }

    abstract override fun configureSelf()
}
