/*
 * Copyright 2015 kec.
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import gov.vha.isaac.ochre.api.Get;
import gov.vha.isaac.ochre.api.coordinate.StampPath;
import gov.vha.isaac.ochre.api.coordinate.StampPosition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;

/**
 *
 * @author kec
 */
@XmlRootElement(name = "stampPosition")
@XmlAccessorType(XmlAccessType.FIELD)
public class StampPositionImpl implements StampPosition, Comparable<StampPosition> {
    
    long time;
    int stampPathSequence;
    
    private StampPositionImpl() {
        //for jaxb
    }

    public StampPositionImpl(long time, int stampPathSequence) {
        this.time = time;
        this.stampPathSequence = Get.identifierService().getConceptSequence(stampPathSequence);
    }
    public ChangeListener<Number> setStampPathSequenceProperty(IntegerProperty stampPathSequenceProperty) {
        ChangeListener<Number> listener = (ObservableValue<? extends Number> observable,
                                           Number oldValue,
                                           Number newValue) -> {
            stampPathSequence = newValue.intValue();
        };
        stampPathSequenceProperty.addListener(new WeakChangeListener<>(listener));
        return listener;
    }

    public ChangeListener<Number> setTimeProperty(LongProperty timeProperty) {
        ChangeListener<Number> listener = (ObservableValue<? extends Number> observable,
                                           Number oldValue,
                                           Number newValue) -> {
            time = newValue.longValue();
        };
        timeProperty.addListener(new WeakChangeListener<>(listener));
        return listener;
    }


    @Override
    public StampPath getStampPath() {
        return new StampPathImpl(stampPathSequence);
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public int getStampPathSequence() {
        return stampPathSequence;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.time ^ (this.time >>> 32));
        hash = 83 * hash + this.stampPathSequence;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StampPositionImpl other = (StampPositionImpl) obj;
        if (this.time != other.time) {
            return false;
        }
        return this.stampPathSequence == other.stampPathSequence;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StampPosition:{");
        if (time == Long.MAX_VALUE) {
            sb.append("latest");
        } else if (time == Long.MIN_VALUE) {
            sb.append("CANCELED");
        } else {
            sb.append(getTimeAsInstant());
        }
        sb.append(" on '").append(Get.conceptDescriptionText(stampPathSequence)).append("' path}");
        return sb.toString();
    }

    @Override
    public int compareTo(StampPosition o) {
        if (this.stampPathSequence != o.getStampPathSequence()) {
            return Integer.compare(stampPathSequence, o.getStampPathSequence());
        }
        return Long.compare(time, o.getTime());
    }

}
