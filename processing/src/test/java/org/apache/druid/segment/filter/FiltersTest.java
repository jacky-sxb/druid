/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.druid.segment.filter;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntIterators;
import org.apache.druid.collections.bitmap.BitmapFactory;
import org.apache.druid.collections.bitmap.ConciseBitmapFactory;
import org.apache.druid.collections.bitmap.ImmutableBitmap;
import org.apache.druid.collections.bitmap.MutableBitmap;
import org.apache.druid.segment.IntIteratorUtils;
import org.apache.druid.segment.column.BitmapIndex;
import org.apache.druid.testing.InitializedNullHandlingTest;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class FiltersTest extends InitializedNullHandlingTest
{
  @Test
  public void testEstimateSelectivityOfBitmapList()
  {
    final int bitmapNum = 100;
    final List<ImmutableBitmap> bitmaps = Lists.newArrayListWithCapacity(bitmapNum);
    final BitmapIndex bitmapIndex = makeNonOverlappedBitmapIndexes(bitmapNum, bitmaps);

    final double estimated = Filters.estimateSelectivity(
        bitmapIndex,
        IntIteratorUtils.toIntList(IntIterators.fromTo(0, bitmapNum)),
        10000
    );
    final double expected = 0.1;
    Assert.assertEquals(expected, estimated, 0.00001);
  }

  private static BitmapIndex getBitmapIndex(final List<ImmutableBitmap> bitmapList)
  {
    return new BitmapIndex()
    {
      @Override
      public int getCardinality()
      {
        return 10;
      }

      @Override
      public String getValue(int index)
      {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean hasNulls()
      {
        return false;
      }

      @Override
      public BitmapFactory getBitmapFactory()
      {
        return new ConciseBitmapFactory();
      }

      @Override
      public int getIndex(String value)
      {
        throw new UnsupportedOperationException();
      }

      @Override
      public ImmutableBitmap getBitmapForValue(@Nullable String value)
      {
        throw new UnsupportedOperationException();
      }

      @Override
      public Iterable<ImmutableBitmap> getBitmapsInRange(
          @Nullable String startValue,
          boolean startStrict,
          @Nullable String endValue,
          boolean endStrict
      )
      {
        throw new UnsupportedOperationException();
      }

      @Override
      public Iterable<ImmutableBitmap> getBitmapsInRange(
          @Nullable String startValue,
          boolean startStrict,
          @Nullable String endValue,
          boolean endStrict,
          Predicate<String> matcher
      )
      {
        throw new UnsupportedOperationException();
      }

      @Override
      public Iterable<ImmutableBitmap> getBitmapsForValues(Set<String> values)
      {
        throw new UnsupportedOperationException();
      }

      @Override
      public ImmutableBitmap getBitmap(int idx)
      {
        return bitmapList.get(idx);
      }
    };
  }

  private static BitmapIndex makeNonOverlappedBitmapIndexes(final int bitmapNum, final List<ImmutableBitmap> bitmaps)
  {
    final BitmapIndex bitmapIndex = getBitmapIndex(bitmaps);
    final BitmapFactory factory = bitmapIndex.getBitmapFactory();
    for (int i = 0; i < bitmapNum; i++) {
      final MutableBitmap mutableBitmap = factory.makeEmptyMutableBitmap();
      for (int j = 0; j < 10; j++) {
        mutableBitmap.add(i * 10 + j);
      }
      bitmaps.add(factory.makeImmutableBitmap(mutableBitmap));
    }
    return bitmapIndex;
  }
}
