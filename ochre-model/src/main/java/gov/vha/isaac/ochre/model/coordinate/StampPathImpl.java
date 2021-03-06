/*
 * Copyright 2015 U.S. Department of Veterans Affairs.
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
package gov.vha.isaac.ochre.model.coordinate;

import gov.vha.isaac.ochre.api.Get;
import gov.vha.isaac.ochre.api.coordinate.StampPath;
import gov.vha.isaac.ochre.api.coordinate.StampPosition;
import java.util.Collection;

/**
 *
 * @author kec
 */
public class StampPathImpl implements StampPath {
    

    private final int pathConceptSequence;

    public StampPathImpl(int pathConceptSequence) {
        if (pathConceptSequence < 0) {
            pathConceptSequence = Get.identifierService().getConceptSequence(pathConceptSequence);
        }
        this.pathConceptSequence = pathConceptSequence;
    }

    @Override
    public int getPathConceptSequence() {
        return pathConceptSequence;
    }

    @Override
    public Collection<? extends StampPosition> getPathOrigins() {
        return Get.pathService().getOrigins(pathConceptSequence);
    }

    @Override
    public int compareTo(StampPath o) {
       return Integer.compare(pathConceptSequence, o.getPathConceptSequence());
    }
    
}
