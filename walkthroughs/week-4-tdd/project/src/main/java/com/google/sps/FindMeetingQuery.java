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
import java.util.Iterator;
import java.util.ArrayList;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // check if valid request
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
        return Collections.emptyList();
    }

    // get all attendees
    Collection<String> attendees = request.getAttendees();
     
    // get all occupied time ranges by attendees
    ArrayList<TimeRange> busy = getOccupiedTimes(events, attendees);
     
    // combine overlapping times
    busy = combineOverlap(busy);

    // get available times
    ArrayList<TimeRange> available = getAvailable(busy);
    return available;
    
    //throw new UnsupportedOperationException("TODO: Implement this method.");
  }
  
  /**
   * Get all available time slots.
   */
  public ArrayList<TimeRange> getAvailable(ArrayList<TimeRange> busy) {
    ArrayList<TimeRange> available = new ArrayList<TimeRange>();
    
    // no occupied times
    if (busy.size() == 0) {
        available.add(TimeRange.WHOLE_DAY);
        return available;
    }

    // find free times
    int start = 0;
    for (int i = 0; i < busy.size(); i++) {
        int end = busy.get(i).start();
        if (start != end) {
            available.add(TimeRange.fromStartEnd(start, end, false));
        }
        start = busy.get(i).end();
    }
    if (start < TimeRange.END_OF_DAY) {
        available.add(TimeRange.fromStartEnd(start, TimeRange.END_OF_DAY, true));
    }
    return available;
  }

  /**
   * Combine all overlapping time slots.
   */
  public ArrayList<TimeRange> combineOverlap(ArrayList<TimeRange> busy) {
    int index = 0;
    while (index + 1 < busy.size()) {
        TimeRange curr = busy.get(index);
        if (curr.overlaps(busy.get(index + 1))) {
            // calculate maximum combined occupied time
            int end = Math.max(curr.end(), busy.get(index + 1).end());
            int duration = end - curr.start();
            TimeRange overlap = TimeRange.fromStartDuration(curr.start(), duration);

            // set values in array
            busy.set(index, overlap);
            busy.remove(index + 1);
        } else {
            index = index + 1;
        }
    }
    return busy;
  }

  /**
   * Get all time slots where attendees are busy.
   */
  public ArrayList<TimeRange> getOccupiedTimes(Collection<Event> events, Collection<String> attendees) {
    Iterator<Event> eventsIterator = events.iterator();
    ArrayList<TimeRange> busy = new ArrayList<TimeRange>();
    while (eventsIterator.hasNext()) {
        Event curr_event = eventsIterator.next();
        Collection<String> curr_attendees = curr_event.getAttendees();
        if (hasIntersection(attendees, curr_attendees)) {
            busy.add(curr_event.getWhen());
        } 
    }

    Collections.sort(busy, TimeRange.ORDER_BY_START);
    return busy;
  }

  /**
   * Check if an attendee in `event_attendees` is also in `attendees`.
   */
  public Boolean hasIntersection(Collection<String> attendees, Collection<String> event_attendees) {
      Iterator<String> iterator = attendees.iterator();
      while (iterator.hasNext()) {
          String attendee = iterator.next();
          if (event_attendees.contains(attendee)) {
              return true;
          }
      }
      return false;
  }
}
