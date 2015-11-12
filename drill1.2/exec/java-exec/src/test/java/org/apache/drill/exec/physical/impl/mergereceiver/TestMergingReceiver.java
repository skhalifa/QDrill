/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.drill.exec.physical.impl.mergereceiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.drill.common.util.FileUtils;
import org.apache.drill.exec.client.DrillClient;
import org.apache.drill.exec.pop.PopUnitTestBase;
import org.apache.drill.exec.record.RecordBatchLoader;
import org.apache.drill.exec.record.VectorWrapper;
import org.apache.drill.exec.rpc.user.QueryDataBatch;
import org.apache.drill.exec.server.Drillbit;
import org.apache.drill.exec.server.RemoteServiceSet;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class TestMergingReceiver extends PopUnitTestBase {
  // private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestMergingReceiver.class);

  @Test
  public void twoBitTwoExchange() throws Exception {
    final RemoteServiceSet serviceSet = RemoteServiceSet.getLocalServiceSet();

    try (final Drillbit bit1 = new Drillbit(CONFIG, serviceSet);
        final Drillbit bit2 = new Drillbit(CONFIG, serviceSet);
        final DrillClient client = new DrillClient(CONFIG, serviceSet.getCoordinator());) {
      bit1.run();
      bit2.run();
      client.connect();
      final List<QueryDataBatch> results = client.runQuery(org.apache.drill.exec.proto.UserBitShared.QueryType.PHYSICAL,
        Files.toString(FileUtils.getResourceAsFile("/mergerecv/merging_receiver.json"),
          Charsets.UTF_8));
      int count = 0;
      final RecordBatchLoader batchLoader = new RecordBatchLoader(client.getAllocator());
      // print the results
      for (final QueryDataBatch b : results) {
        count += b.getHeader().getRowCount();
        for (int valueIdx = 0; valueIdx < b.getHeader().getRowCount(); valueIdx++) {
          final List<Object> row = Lists.newArrayList();
          batchLoader.load(b.getHeader().getDef(), b.getData());
          for (final VectorWrapper<?> vw : batchLoader) {
            row.add(vw.getValueVector().getField().toExpr() + ":" + vw.getValueVector().getAccessor().getObject(valueIdx));
          }
          for (final Object cell : row) {
            if (cell == null) {
//              System.out.print("<null>    ");
              continue;
            }
            final int len = cell.toString().length();
//            System.out.print(cell + " ");
            for (int i = 0; i < (30 - len); ++i) {
//              System.out.print(" ");
            }
          }
//          System.out.println();
        }
        b.release();
        batchLoader.clear();
      }
      assertEquals(200000, count);
    }
  }

  @Test
  public void testMultipleProvidersMixedSizes() throws Exception {
    final RemoteServiceSet serviceSet = RemoteServiceSet.getLocalServiceSet();

    try (final Drillbit bit1 = new Drillbit(CONFIG, serviceSet);
        final Drillbit bit2 = new Drillbit(CONFIG, serviceSet);
        final DrillClient client = new DrillClient(CONFIG, serviceSet.getCoordinator());) {

      bit1.run();
      bit2.run();
      client.connect();
      final List<QueryDataBatch> results =
          client.runQuery(org.apache.drill.exec.proto.UserBitShared.QueryType.PHYSICAL,
              Files.toString(FileUtils.getResourceAsFile("/mergerecv/multiple_providers.json"),
                  Charsets.UTF_8));
      int count = 0;
      final RecordBatchLoader batchLoader = new RecordBatchLoader(client.getAllocator());
      // print the results
      Long lastBlueValue = null;
      for (final QueryDataBatch b : results) {
        count += b.getHeader().getRowCount();
        for (int valueIdx = 0; valueIdx < b.getHeader().getRowCount(); valueIdx++) {
          final List<Object> row = Lists.newArrayList();
          batchLoader.load(b.getHeader().getDef(), b.getData());
          for (final VectorWrapper vw : batchLoader) {
            row.add(vw.getValueVector().getField().toExpr() + ":" + vw.getValueVector().getAccessor().getObject(valueIdx));
            if (vw.getValueVector().getField().getAsSchemaPath().getRootSegment().getPath().equals("blue")) {
              // check that order is ascending
              if (((Long)vw.getValueVector().getAccessor().getObject(valueIdx)).longValue() == 0) {
                continue; // ignore initial 0's from sort
              }
              if (lastBlueValue != null) {
                assertTrue(((Long)vw.getValueVector().getAccessor().getObject(valueIdx)).longValue() >= ((Long)lastBlueValue).longValue());
              }
              lastBlueValue = (Long)vw.getValueVector().getAccessor().getObject(valueIdx);
            }
          }
          for (final Object cell : row) {
            int len = cell.toString().length();
//            System.out.print(cell + " ");
            for (int i = 0; i < (30 - len); ++i) {
//              System.out.print(" ");
            }
          }
//          System.out.println();
        }
        b.release();
        batchLoader.clear();
      }
      assertEquals(400000, count);
    }
  }

  @Test
  public void handleEmptyBatch() throws Exception {
    final RemoteServiceSet serviceSet = RemoteServiceSet.getLocalServiceSet();

    try (final Drillbit bit1 = new Drillbit(CONFIG, serviceSet);
        final Drillbit bit2 = new Drillbit(CONFIG, serviceSet);
        final DrillClient client = new DrillClient(CONFIG, serviceSet.getCoordinator());) {

      bit1.run();
      bit2.run();
      client.connect();
      final List<QueryDataBatch> results =
          client.runQuery(org.apache.drill.exec.proto.UserBitShared.QueryType.PHYSICAL,
              Files.toString(FileUtils.getResourceAsFile("/mergerecv/empty_batch.json"),
                  Charsets.UTF_8));
      int count = 0;
      final RecordBatchLoader batchLoader = new RecordBatchLoader(client.getAllocator());
      // print the results
      for (final QueryDataBatch b : results) {
        count += b.getHeader().getRowCount();
        for (int valueIdx = 0; valueIdx < b.getHeader().getRowCount(); valueIdx++) {
          final List<Object> row = Lists.newArrayList();
          batchLoader.load(b.getHeader().getDef(), b.getData());
          for (final VectorWrapper vw : batchLoader) {
            row.add(vw.getValueVector().getField().toExpr() + ":" + vw.getValueVector().getAccessor().getObject(valueIdx));
          }
          for (final Object cell : row) {
            if (cell == null) {
//              System.out.print("<null>    ");
              continue;
            }
            int len = cell.toString().length();
//            System.out.print(cell + " ");
            for (int i = 0; i < (30 - len); ++i) {
//              System.out.print(" ");
            }
          }
//          System.out.println();
        }
        b.release();
        batchLoader.clear();
      }
      assertEquals(100000, count);
    }
  }
}
