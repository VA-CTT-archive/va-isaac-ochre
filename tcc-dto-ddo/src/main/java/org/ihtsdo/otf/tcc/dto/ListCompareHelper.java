/*
 * Copyright 2014 Informatics, Inc..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ihtsdo.otf.tcc.dto;

import gov.vha.isaac.ochre.api.chronicle.StampedVersion;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.ihtsdo.otf.tcc.dto.component.TtkComponentChronicle;
import org.ihtsdo.otf.tcc.dto.component.TtkRevision;

/**
 *
 * @author kec
 */
public class ListCompareHelper {
    private static final TtkRevisionComparator comparator = new TtkRevisionComparator();
    
    private static class TtkRevisionComparator implements Comparator<StampedVersion> {

        @Override
        public int compare(StampedVersion o1, StampedVersion o2) {
            if (o1 instanceof TtkComponentChronicle) {
                TtkComponentChronicle cc1 = (TtkComponentChronicle) o1;
                TtkComponentChronicle cc2 = (TtkComponentChronicle) o2;
                int compare = cc1.getPrimordialComponentUuid().compareTo(cc2.getPrimordialComponentUuid());
                if (compare != 0) {
                    return compare;
                }
            }
            
            if (o1.getTime() > o2.getTime()) {
                return 1;
            }
            if (o1.getTime() < o2.getTime()) {
                return -1;
            }
            int compare = Integer.compare(o1.getAuthorSequence(), o2.getAuthorSequence());
            if (compare != 0) {
                return compare;
            }
            compare = Integer.compare(o1.getPathSequence(), o2.getPathSequence());
            if (compare != 0) {
                return compare;
            }
            compare = Integer.compare(o1.getModuleSequence(), o2.getModuleSequence());
            if (compare != 0) {
                return compare;
            }
            return o1.getState().compareTo(o2.getState());
        }
    
    }
    public static boolean equals(List<? extends TtkRevision> a, List<? extends TtkRevision> b) {
        if (a == null) a = new ArrayList<>();
        if (b == null) b = new ArrayList<>();
        a.sort(comparator);
        b.sort(comparator);
        return a.equals(b); 
    }
}
