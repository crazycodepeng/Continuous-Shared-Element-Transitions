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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.Map;

import liup.code.element.MainActivity;
import liup.code.element.R;
import liup.code.element.adapter.ImagePagerAdapter;

/**
 * A fragment for displaying a pager of images.
 * 显示image的fragment
 */
public class ImagePagerFragment extends Fragment {

  private ViewPager viewPager;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    viewPager = (ViewPager) inflater.inflate(R.layout.fragment_pager, container, false);
    viewPager.setAdapter(new ImagePagerAdapter(this));
    // Set the current position and add a listener that will update the selection coordinator when
    // paging the images.
    // 设置当前位置并添加一个侦听器，在分页图像时将更新选择协调器。
    viewPager.setCurrentItem(MainActivity.currentPosition);
    viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        MainActivity.currentPosition = position;
      }
    });

    prepareSharedElementTransition();

    // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
    // 在方向更改上避免使用postponeEnterTransition，首次创建时推迟。
    if (savedInstanceState == null) {
      postponeEnterTransition();
    }

    return viewPager;
  }

  /**
   * Prepares the shared element transition from and back to the grid fragment.
   *
   * 准备共享元素转换并返回到grid的fragment。
   */
  private void prepareSharedElementTransition() {
    Transition transition =
        TransitionInflater.from(getContext())
            .inflateTransition(R.transition.image_shared_element_transition);
    setSharedElementEnterTransition(transition);

    // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
    // 使用setExitSharedElementCallback在GridFragment中设置一个类似的映射。
    setEnterSharedElementCallback(
        new SharedElementCallback() {
          @Override
          public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            // Locate the image view at the primary fragment (the ImageFragment that is currently
            // visible). To locate the fragment, call instantiateItem with the selection position.
            // At this stage, the method will simply return the fragment at the position and will
            // not create a new one.

            // 在主要fragment（当前可见的ImageFragment）上找到图像视图。
            // 要查找fragment，请使用选择位置调用instantiateItem。
            // 在这个fragment，该方法将简单地在该位置返回fragment，并且不会创建新fragment。
            Fragment currentFragment = (Fragment) viewPager.getAdapter()
                .instantiateItem(viewPager, MainActivity.currentPosition);
            View view = currentFragment.getView();
            if (view == null) {
              return;
            }

            // Map the first shared element name to the child ImageView.
            // 将第一个共享元素名称映射到子ImageView。
            sharedElements.put(names.get(0), view.findViewById(R.id.image));
          }
        });
  }
}
