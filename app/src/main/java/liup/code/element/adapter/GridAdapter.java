/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package liup.code.element.adapter;


import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import java.util.concurrent.atomic.AtomicBoolean;

import liup.code.element.MainActivity;
import liup.code.element.R;
import liup.code.element.fragment.ImagePagerFragment;

import static liup.code.element.adapter.ImageData.IMAGE_DRAWABLES;

/**
 * A fragment for displaying a grid of images.
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ImageViewHolder> {

  /**
   * A listener that is attached to all ViewHolders to handle image loading events and clicks.
   *
   * 一个连接到所有ViewHolders的侦听器，用于处理图像加载事件和点击。
   */
  private interface ViewHolderListener {

    void onLoadCompleted(ImageView view, int adapterPosition);

    void onItemClicked(View view, int adapterPosition);
  }

  private final RequestManager requestManager;
  private final ViewHolderListener viewHolderListener;

  /**
   * Constructs a new grid adapter for the given {@link Fragment}.
   */
  public GridAdapter(Fragment fragment) {
    this.requestManager = Glide.with(fragment);
    this.viewHolderListener = new ViewHolderListenerImpl(fragment);
  }

  @Override
  public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.image_card, parent, false);
    return new ImageViewHolder(view, requestManager, viewHolderListener);
  }

  @Override
  public void onBindViewHolder(ImageViewHolder holder, int position) {
    holder.onBind();
  }

  @Override
  public int getItemCount() {
    return IMAGE_DRAWABLES.length;
  }


  /**
   * Default {@link ViewHolderListener} implementation.
   */
  private static class ViewHolderListenerImpl implements ViewHolderListener {

    private Fragment fragment;
    private AtomicBoolean enterTransitionStarted;

    ViewHolderListenerImpl(Fragment fragment) {
      this.fragment = fragment;
      this.enterTransitionStarted = new AtomicBoolean();
    }

    @Override
    public void onLoadCompleted(ImageView view, int position) {
      // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
      if (MainActivity.currentPosition != position) {
        return;
      }
      if (enterTransitionStarted.getAndSet(true)) {
        return;
      }
      fragment.startPostponedEnterTransition();
    }

    /**
     * Handles a view click by setting the current position to the given {@code position} and
     * starting a {@link  ImagePagerFragment} which displays the image at the position.
     *
     * @param view the clicked {@link ImageView} (the shared element view will be re-mapped at the
     * GridFragment's SharedElementCallback)
     * @param position the selected view position   选择view的position
     */
    @Override
    public void onItemClicked(View view, int position) {
      // Update the position.
      // 更新位置
      MainActivity.currentPosition = position;

      // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
      // instead of fading out with the rest to prevent an overlapping animation of fade and move).
      // 从退出转换中排除已点击的卡片（例如，卡片将立即消失，而不会与其余部分消失，以防止淡入淡出和移动的重叠动画）。
      ((TransitionSet) fragment.getExitTransition()).excludeTarget(view, true);

      ImageView transitioningView = view.findViewById(R.id.card_image);
      fragment.getFragmentManager()
          .beginTransaction()
          .setReorderingAllowed(true) // Optimize for shared element transition
          .addSharedElement(transitioningView, transitioningView.getTransitionName())
          .replace(R.id.fragment_container, new ImagePagerFragment(), ImagePagerFragment.class
              .getSimpleName())
          .addToBackStack(null)
          .commit();
    }
  }

  /**
   * ViewHolder for the grid's images.
   */
  static class ImageViewHolder extends RecyclerView.ViewHolder implements
      View.OnClickListener {

    private final ImageView image;
    private final RequestManager requestManager;
    private final ViewHolderListener viewHolderListener;

    ImageViewHolder(View itemView, RequestManager requestManager,
        ViewHolderListener viewHolderListener) {
      super(itemView);
      this.image = itemView.findViewById(R.id.card_image);
      this.requestManager = requestManager;
      this.viewHolderListener = viewHolderListener;
      itemView.findViewById(R.id.card_view).setOnClickListener(this);
    }

    /**
     * Binds this view holder to the given adapter position.
     *
     * The binding will load the image into the image view, as well as set its transition name for
     * later.
     * 该绑定会将图像加载到图像视图中，并为其稍后设置其转换名称。
     */
    void onBind() {
      int adapterPosition = getAdapterPosition();
      setImage(adapterPosition);
      // Set the string value of the image resource as the unique transition name for the view.
      // 将图像资源的字符串值设置为视图的唯一过渡名称。
      image.setTransitionName(String.valueOf(IMAGE_DRAWABLES[adapterPosition]));
    }

    void setImage(final int adapterPosition) {
      // Load the image with Glide to prevent OOM error when the image drawables are very large.
      // 使用Glide加载图像以防止图像可绘制非常大时的OOM错误。
      requestManager
          .load(IMAGE_DRAWABLES[adapterPosition])
          .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                Target<Drawable> target, boolean isFirstResource) {
              viewHolderListener.onLoadCompleted(image, adapterPosition);
              return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                target, DataSource dataSource, boolean isFirstResource) {
              viewHolderListener.onLoadCompleted(image, adapterPosition);
              return false;
            }
          })
          .into(image);
    }

    @Override
    public void onClick(View view) {
      // Let the listener start the ImagePagerFragment.
      // 让侦听器启动ImagePagerFragment
      viewHolderListener.onItemClicked(view, getAdapterPosition());
    }
  }

}