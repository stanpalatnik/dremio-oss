/*
 * Copyright (C) 2017-2019 Dremio Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dremio.sabot.op.join.vhash.spill.pool;

import static org.junit.Assert.assertEquals;

import org.apache.arrow.memory.ArrowBuf;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.dremio.common.AutoCloseables;

/**
 * Test the Page system.
 */
public class TestPage {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private BufferAllocator allocator;
  private PagePool pool;

  @Before
  public void before() {
    this.allocator = new RootAllocator(Long.MAX_VALUE);
    this.pool = new PagePool(allocator, 1024, 0);
    pool.start();
  }

  @After
  public void after() throws Exception {
    AutoCloseables.close(pool, allocator);
  }

  @Test
  public void deallocNeg() {
    Page p = new Page(1, allocator.buffer(0), buf -> {});
    p.initialRetain();
    // can't release when open.
    thrown.expect(IllegalStateException.class);
    p.deallocate();
  }

  @Test
  public void deallocPos() {
    Page p = new Page(1, allocator.buffer(0), buf -> {});
    p.initialRetain();
    p.close();
    p.deallocate();
  }

  @Test
  public void newPageNeg() {
    Page p = new Page(1, allocator.buffer(0), buf -> {});
    p.initialRetain();
    thrown.expect(IllegalStateException.class);
    p.toNewPage();
  }

  @Test
  public void newPagePos() {
    Page p = new Page(1, allocator.buffer(0), buf -> {});
    p.initialRetain();
    p.close();
    p.toNewPage();
  }

  @Test
  public void deadSliceNeg() {
    Page p = new Page(1, allocator.buffer(0), buf -> {});
    p.initialRetain();
    p.close();

    thrown.expect(IllegalStateException.class);
    p.deadSlice(1);
  }

  @Test
  public void sliceNeg() {
    Page p = new Page(1, allocator.buffer(0), buf -> {});
    p.initialRetain();
    p.close();

    thrown.expect(IllegalStateException.class);
    p.slice(1);
  }

  @Test
  public void addrNeg() {
    Page p = new Page(1, allocator.buffer(0), buf -> {});
    p.initialRetain();
    p.close();

    thrown.expect(IllegalStateException.class);
    p.getAddress();
  }

  @Test
  public void remainNeg() {
    Page p = new Page(1, allocator.buffer(0), buf -> {});
    p.initialRetain();
    p.close();

    thrown.expect(IllegalStateException.class);
    p.getRemainingBytes();
  }

  @Test
  public void slice() {
    Page p = new Page(1, allocator.buffer(1), buf -> {});
    p.initialRetain();
    ArrowBuf b = p.slice(1);
    b.release();
    p.close();
    p.deallocate();
  }

  @Test
  public void deadSlice() {
    Page p = new Page(1, allocator.buffer(1), buf -> {});
    p.initialRetain();
    p.deadSlice(1);
    p.close();
    p.deallocate();
  }

  @Test
  public void props() {
    ArrowBuf buf = allocator.buffer(2);
    Page p = new Page(2, buf, b -> {});
    p.initialRetain();
    try {
      assertEquals(buf.memoryAddress(), p.getAddress());
      assertEquals(2, p.getPageSize());
      assertEquals(2, p.getRemainingBytes());
      p.deadSlice(1);
      assertEquals(1, p.getRemainingBytes());
      p.deadSlice(1);
      assertEquals(0, p.getRemainingBytes());
      thrown.expect(IllegalArgumentException.class);
      p.deadSlice(1);
    } finally {
      p.close();
      p.deallocate();
    }
  }
}
