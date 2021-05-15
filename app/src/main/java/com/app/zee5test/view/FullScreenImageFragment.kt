package com.app.zee5test.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.app.zee5test.R
import com.app.zee5test.databinding.FragmentFullScreenImageBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

/**
 * A simple [Fragment] subclass.
 * Use the [FullScreenImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FullScreenImageFragment : Fragment() {

    private val args: FullScreenImageFragmentArgs by navArgs()
    private var mBinding: FragmentFullScreenImageBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSharedElementTransitionOnEnter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentFullScreenImageBinding.inflate(inflater, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.fullScreenImageView?.apply {
            transitionName = args.url
            postponeEnterTransition()
            Glide.with(context)
                .load(args.url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .listener(object : RequestListener<Drawable> {
                    /**
                     * Called when an exception occurs during a load, immediately before [ ][Target.onLoadFailed]. Will only be called if we currently want to display an image
                     * for the given model in the given target. It is recommended to create a single instance per
                     * activity/fragment rather than instantiate a new object for each call to `Glide.with(fragment/activity).load()` to avoid object churn.
                     *
                     *
                     * It is not safe to reload this or a different model in this callback. If you need to do so
                     * use [com.bumptech.glide.RequestBuilder.error] instead.
                     *
                     *
                     * Although you can't start an entirely new load, it is safe to change what is displayed in the
                     * [Target] at this point, as long as you return `true` from the method to prevent
                     * [Target.onLoadFailed] from being called.
                     *
                     *
                     * For threading guarantees, see the class comment.
                     *
                     *
                     * For example:
                     *
                     * <pre>`public boolean onLoadFailed(Exception e, T model, Target target, boolean isFirstResource) {
                     * target.setPlaceholder(R.drawable.a_specific_error_for_my_exception);
                     * return true; // Prevent onLoadFailed from being called on the Target.
                     * }
                    `</pre> *
                     *
                     * @param e The maybe `null` exception containing information about why the request failed.
                     * @param model The model we were trying to load when the exception occurred.
                     * @param target The [Target] we were trying to load the image into.
                     * @param isFirstResource `true` if this exception is for the first resource to load.
                     * @return `true` to prevent [Target.onLoadFailed] from being called on
                     * `target`, typically because the listener wants to update the `target` or the
                     * object the `target` wraps itself or `false` to allow [     ][Target.onLoadFailed] to be called on `target`.
                     */
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        startPostponedEnterTransition()
                        return false
                    }

                    /**
                     * Called when a load completes successfully, immediately before [ ][Target.onResourceReady].
                     *
                     *
                     * For threading guarantees, see the class comment.
                     *
                     * @param resource The resource that was loaded for the target.
                     * @param model The specific model that was used to load the image.
                     * @param target The target the model was loaded into.
                     * @param dataSource The [DataSource] the resource was loaded from.
                     * @param isFirstResource `true` if this is the first resource to in this load to be loaded
                     * into the target. For example when loading a thumbnail and a full-sized image, this will be
                     * `true` for the first image to load and `false` for the second.
                     * @return `true` to prevent [Target.onResourceReady] from being called on
                     * `target`, typically because the listener wants to update the `target` or the
                     * object the `target` wraps itself or `false` to allow [     ][Target.onResourceReady] to be called on `target`.
                     */
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        startPostponedEnterTransition()
                        return false
                    }
                })
                .into(this)
        }
        mBinding?.backButton?.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun setSharedElementTransitionOnEnter() {
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.shared_element_transition)
    }

    companion object {

        private const val ARGUMENT_IMAGE_URL = "image_url"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fullScreenImageFragment.
         */
        fun newInstance(imageUrl: String) =
            FullScreenImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARGUMENT_IMAGE_URL, imageUrl)
                }
            }
    }
}