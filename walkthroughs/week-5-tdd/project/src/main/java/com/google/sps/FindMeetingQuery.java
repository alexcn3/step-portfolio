// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.*;
import java.util.*;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> optionalConsidered = getTimeRanges(events, request, true);
    if (optionalConsidered.isEmpty() && !request.getAttendees().isEmpty()) {
      return getTimeRanges(events, request, false);
    } else {
      return optionalConsidered;
    }
  }


  private List<TimeRange> getTimeRanges(Collection<Event> events, MeetingRequest request, boolean optional) {
    Set<TimeRange> busySet = new HashSet<>();
    for (Event block : events){
      if (optional) {
        if (oneOf(block.getAttendees(), request.getAttendees()) || oneOf(block.getAttendees(), request.getOptionalAttendees())) {
          busySet.add(block.getWhen());
        }
      } else {
        if (oneOf(block.getAttendees(), request.getAttendees())) {
          busySet.add(block.getWhen());
        }
      } 
    }
    List<TimeRange> busyList = mergeTimeRanges(busySet);
    return enoughTimeForMeeting(getFreeTime(busyList), request.getDuration());
  }

  private List<TimeRange> getFreeTime(List<TimeRange> blockedTime) {
    List<TimeRange> freeTime = new ArrayList<>();
    int start = TimeRange.START_OF_DAY;
    for (TimeRange time : blockedTime) {
      if (start != time.start()) {
        TimeRange freeBlock = TimeRange.fromStartEnd(start, time.start(), false);
        freeTime.add(freeBlock);
        start = time.end();
      } else {
        start = time.end();
      }
    }

    if (start == TimeRange.END_OF_DAY + 1) {
        return freeTime;
    }

    freeTime.add(TimeRange.fromStartEnd(start, TimeRange.END_OF_DAY, true));
    return freeTime;
  }

  private boolean oneOf(Set<String> firstList, Collection<String> SecondList) {
    for (String item : firstList) {
      if (SecondList.contains(item)) {
        return true;
      }
    }
    return false;
  }

  private List<TimeRange> enoughTimeForMeeting(List<TimeRange> freeTime, long meetingDuration) {
    List<TimeRange> enoughFreeTime = new ArrayList<TimeRange>();
    for (TimeRange block : freeTime) {
      if (meetingDuration <= block.duration()) {
        enoughFreeTime.add(block);
      }
    }
    return enoughFreeTime;
  }

  private List<TimeRange> mergeTimeRanges(Set<TimeRange> blockedTime) {
    List<TimeRange> cleanList = new ArrayList<TimeRange>();
    List<TimeRange> sortedBlockedTime = blockedTime.stream().sorted(Comparator.comparing(TimeRange::start)).collect(Collectors.toList());
    for (int i = 0; i < sortedBlockedTime.size(); i++) {
      try
      {
        if (sortedBlockedTime.get(i).contains(sortedBlockedTime.get(i+1))) {
          cleanList.add(sortedBlockedTime.get(i));
          i = i + 2;
        } else if (sortedBlockedTime.get(i).overlaps(sortedBlockedTime.get(i+1))) {
          cleanList.add(TimeRange.fromStartEnd(sortedBlockedTime.get(i).start(), sortedBlockedTime.get(i+1).end(), false));
          i = i + 2;
        } else {
          cleanList.add(sortedBlockedTime.get(i));
        }
      }
      catch (IndexOutOfBoundsException e)
      {
        cleanList.add(sortedBlockedTime.get(i));
      }
    }
    return cleanList;
  }
}
