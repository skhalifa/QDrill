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
package org.apache.drill.exec.store.schedule;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.cursors.ObjectLongCursor;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import org.apache.drill.common.exceptions.DrillRuntimeException;
import org.apache.drill.exec.ExecConstants;
import org.apache.drill.exec.proto.CoordinationProtos.DrillbitEndpoint;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import org.apache.drill.exec.server.DrillbitContext;

/**
 * The AssignmentCreator is responsible for assigning a set of work units to the available slices.
 */
public class AssignmentCreator<T extends CompleteWork> {
  static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AssignmentCreator.class);


  /**
   * Comparator used to sort in order of decreasing affinity
   */
  private static Comparator<Entry<DrillbitEndpoint,Long>> comparator = new Comparator<Entry<DrillbitEndpoint,Long>>() {
    @Override
    public int compare(Entry<DrillbitEndpoint, Long> o1, Entry<DrillbitEndpoint,Long> o2) {
      return (int) (o1.getValue() - o2.getValue());
    }
  };

  /**
   * the maximum number of work units to assign to any minor fragment
   */
  private int maxWork;

  /**
   * The units of work to be assigned
   */
  private List<T> units;

  /**
   * Mappings
   */
  private ArrayListMultimap<Integer, T> mappings = ArrayListMultimap.create();

  /**
   * A list of DrillbitEndpoints, where the index in the list corresponds to the minor fragment id
   */
  private List<DrillbitEndpoint> incomingEndpoints;

  private AssignmentCreator(List<DrillbitEndpoint> incomingEndpoints, List<T> units) {
    this.incomingEndpoints = incomingEndpoints;
    this.units = units;
  }

  /**
   * Assign each unit of work to a minor fragment, given that a list of DrillbitEndpoints, whose index in the list correspond
   * to the minor fragment id for each fragment. A given DrillbitEndpoint can appear multiple times in this list. This method
   * will try to assign work based on the affinity of each work unit, but will also evenly distribute the work units among
   * all of the minor fragments
   *
   * @param incomingEndpoints The list of incomingEndpoints, indexed by minor fragment id
   * @param units the list of work units to be assigned
   * @return A multimap that maps each minor fragment id to a list of work units
   */
  public static <T extends CompleteWork> ListMultimap<Integer,T> getMappings(List<DrillbitEndpoint> incomingEndpoints, List<T> units, DrillbitContext context) {
    boolean useOldAssignmentCode = context == null ? false : context.getOptionManager().getOption(ExecConstants.USE_OLD_ASSIGNMENT_CREATOR).bool_val;
    if (useOldAssignmentCode) {
      return OldAssignmentCreator.getMappings(incomingEndpoints, units);
    } else {
      AssignmentCreator<T> creator = new AssignmentCreator(incomingEndpoints, units);
      return creator.getMappings();
    }
  }

  /**
   * Does the work of creating the mappings for this AssignmentCreator
   * @return the minor fragment id to work units mapping
   */
  private ListMultimap<Integer, T> getMappings() {
    Stopwatch watch = new Stopwatch();
    watch.start();
    maxWork = (int) Math.ceil(units.size() / ((float) incomingEndpoints.size()));
    LinkedList<WorkEndpointListPair<T>> workList = getWorkList();
    LinkedList<WorkEndpointListPair<T>> unassignedWorkList;
    Map<DrillbitEndpoint,FragIteratorWrapper> endpointIterators = getEndpointIterators();

    unassignedWorkList = assign(workList, endpointIterators, true);

    assignLeftovers(unassignedWorkList, endpointIterators, true);
    assignLeftovers(unassignedWorkList, endpointIterators, false);

    if (unassignedWorkList.size() != 0) {
      throw new DrillRuntimeException("There are still unassigned work units");
    }

    logger.debug("Took {} ms to assign {} work units to {} fragments", watch.elapsed(TimeUnit.MILLISECONDS), units.size(), incomingEndpoints.size());
    return mappings;
  }

  /**
   *
   * @param workList the list of work units to assign
   * @param endpointIterators the endpointIterators to assign to
   * @param assignMinimum whether to assign only up to the minimum required
   * @return a list of unassigned work units
   */
  private LinkedList<WorkEndpointListPair<T>> assign(List<WorkEndpointListPair<T>> workList, Map<DrillbitEndpoint,FragIteratorWrapper> endpointIterators, boolean assignMinimum) {
    LinkedList<WorkEndpointListPair<T>> currentUnassignedList = Lists.newLinkedList();
    outer: for (WorkEndpointListPair<T> workPair : workList) {
      List<DrillbitEndpoint> endpoints = workPair.sortedEndpoints;
      for (DrillbitEndpoint endpoint : endpoints) {
        FragIteratorWrapper iteratorWrapper = endpointIterators.get(endpoint);
        if (iteratorWrapper == null) {
          continue;
        }
        if (iteratorWrapper.count < (assignMinimum ? iteratorWrapper.minCount : iteratorWrapper.maxCount)) {
          Integer assignment = iteratorWrapper.iter.next();
          iteratorWrapper.count++;
          mappings.put(assignment, workPair.work);
          continue outer;
        }
      }
      currentUnassignedList.add(workPair);
    }
    return currentUnassignedList;
  }

  /**
   *
   * @param unassignedWorkList the work units to assign
   * @param endpointIterators the endpointIterators to assign to
   * @param assignMinimum wheterh to assign the minimum amount
   */
  private void assignLeftovers(LinkedList<WorkEndpointListPair<T>> unassignedWorkList, Map<DrillbitEndpoint,FragIteratorWrapper> endpointIterators, boolean assignMinimum) {
    outer: for (FragIteratorWrapper iteratorWrapper : endpointIterators.values()) {
      while (iteratorWrapper.count < (assignMinimum ? iteratorWrapper.minCount : iteratorWrapper.maxCount)) {
        WorkEndpointListPair<T> workPair = unassignedWorkList.poll();
        if (workPair == null) {
          break outer;
        }
        Integer assignment = iteratorWrapper.iter.next();
        iteratorWrapper.count++;
        mappings.put(assignment, workPair.work);
      }
    }
  }

  /**
   * Builds the list of WorkEndpointListPairs, which pair a work unit with a list of endpoints sorted by affinity
   * @return the list of WorkEndpointListPairs
   */
  private LinkedList<WorkEndpointListPair<T>> getWorkList() {
    Stopwatch watch = new Stopwatch();
    watch.start();
    LinkedList<WorkEndpointListPair<T>> workList = Lists.newLinkedList();
    for (T work : units) {
      List<Map.Entry<DrillbitEndpoint,Long>> entries = Lists.newArrayList();
      for (ObjectLongCursor<DrillbitEndpoint> cursor : work.getByteMap()) {
        final DrillbitEndpoint ep = cursor.key;
        final Long val = cursor.value;
        Map.Entry<DrillbitEndpoint,Long> entry = new Entry() {

          @Override
          public Object getKey() {
            return ep;
          }

          @Override
          public Object getValue() {
            return val;
          }

          @Override
          public Object setValue(Object value) {
            throw new UnsupportedOperationException();
          }
        };
        entries.add(entry);
      }
      Collections.sort(entries, comparator);
      List<DrillbitEndpoint> sortedEndpoints = Lists.newArrayList();
      for (Entry<DrillbitEndpoint,Long> entry : entries) {
        sortedEndpoints.add(entry.getKey());
      }
      workList.add(new WorkEndpointListPair<T>(work, sortedEndpoints));
    }
    return workList;
  }

  /**
   *  A wrapper class around a work unit and its associated sort list of Endpoints (sorted by affinity in decreasing order)
   */
  private static class WorkEndpointListPair<T> {
    T work;
    List<DrillbitEndpoint> sortedEndpoints;

    WorkEndpointListPair(T work, List<DrillbitEndpoint> sortedEndpoints) {
      this.work = work;
      this.sortedEndpoints = sortedEndpoints;
    }
  }

  /**
   * Groups minor fragments together by corresponding endpoint, and creates an iterator that can be used to evenly
   * distribute work assigned to a given endpoint to all corresponding minor fragments evenly
   *
   * @return
   */
  private Map<DrillbitEndpoint,FragIteratorWrapper> getEndpointIterators() {
    Stopwatch watch = new Stopwatch();
    watch.start();
    Map<DrillbitEndpoint,FragIteratorWrapper> map = Maps.newLinkedHashMap();
    Map<DrillbitEndpoint,List<Integer>> mmap = Maps.newLinkedHashMap();
    for (int i = 0; i < incomingEndpoints.size(); i++) {
      DrillbitEndpoint endpoint = incomingEndpoints.get(i);
      List<Integer> intList = mmap.get(incomingEndpoints.get(i));
      if (intList == null) {
        intList = Lists.newArrayList();
      }
      intList.add(Integer.valueOf(i));
      mmap.put(endpoint, intList);
    }

    for (DrillbitEndpoint endpoint : mmap.keySet()) {
      FragIteratorWrapper wrapper = new FragIteratorWrapper();
      wrapper.iter = Iterators.cycle(mmap.get(endpoint));
      wrapper.maxCount = maxWork * mmap.get(endpoint).size();
      wrapper.minCount = Math.max(maxWork - 1, 1) * mmap.get(endpoint).size();
      map.put(endpoint, wrapper);
    }
    return map;
  }

  /**
   * A struct that holds a fragment iterator and keeps track of how many units have been assigned, as well as the maximum
   * number of assignment it will accept
   */
  private static class FragIteratorWrapper {
    int count = 0;
    int maxCount;
    int minCount;
    Iterator<Integer> iter;
  }

}
