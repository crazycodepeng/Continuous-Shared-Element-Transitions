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

import android.support.annotation.DrawableRes;

import liup.code.element.R;

/**
 * Holds the image resource references used by the grid and the pager fragments.
 *
 * 保存grid和fragments使用的图像资源引用。
 */
abstract class ImageData {

  // Image assets (free for commercial use, no attribution required, from pixabay.com)
  @DrawableRes
  static final int[] IMAGE_DRAWABLES = {
          R.drawable.cfdbc2efef87499a9cba304452c64a27,
          R.drawable.image1,
          R.drawable.image2,
          R.drawable.image3,
          R.drawable.wallhaven281671,
          R.drawable.spoon2426623,
          R.drawable.wallhaven62159,
          R.drawable.wallhaven75060,
          R.drawable.wallhaven398613,
          R.drawable.wallhaven433199,
          R.drawable.wallhaven140200,
          R.drawable.wallhaven497378,
          R.drawable.wallhaven178923,
          R.drawable.wallhaven185927
  };

}
