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

package liup.code.element.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import liup.code.element.R;

/**
 * A fragment for displaying an image.
 * 用于显示图像的fragment。
 */
public class ImageFragment extends Fragment {

  private static final String KEY_IMAGE_RES = "com.google.samples.gridtopager.key.imageRes";

  public static ImageFragment newInstance(@DrawableRes int drawableRes) {
    ImageFragment fragment = new ImageFragment();
    Bundle argument = new Bundle();
    argument.putInt(KEY_IMAGE_RES, drawableRes);
    fragment.setArguments(argument);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_image, container, false);

    Bundle arguments = getArguments();
    @DrawableRes int imageRes = arguments.getInt(KEY_IMAGE_RES);

    // Just like we do when binding views at the grid, we set the transition name to be the string
    // value of the image res.
    // 就像我们在网格中绑定视图时一样，我们将转换名称设置为图像res的字符串值。
    view.findViewById(R.id.image).setTransitionName(String.valueOf(imageRes));

    // Load the image with Glide to prevent OOM error when the image drawables are very large.
    // 使用Glide加载图像以防止图像可绘制非常大时的OOM错误。
    Glide.with(this)
        .load(imageRes)
        .listener(new RequestListener<Drawable>() {
          @Override
          public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable>
              target, boolean isFirstResource) {
            // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
            // startPostponedEnterTransition() should also be called on it to get the transition
            // going in case of a failure.
            // 在父ImagePagerFragment上调用postponeEnterTransition，因此还应该调用startPostponedEnterTransition（）以在发生故障时进行转换。
            getParentFragment().startPostponedEnterTransition();
            return false;
          }

          @Override
          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
              target, DataSource dataSource, boolean isFirstResource) {
            // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
            // startPostponedEnterTransition() should also be called on it to get the transition
            // going when the image is ready.
            getParentFragment().startPostponedEnterTransition();
            return false;
          }
        })
        .into((ImageView) view.findViewById(R.id.image));
    return view;
  }
}
