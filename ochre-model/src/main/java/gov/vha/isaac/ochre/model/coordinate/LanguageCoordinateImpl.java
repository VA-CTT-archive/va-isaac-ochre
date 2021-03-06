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

import gov.vha.isaac.ochre.api.Get;
import gov.vha.isaac.ochre.api.chronicle.LatestVersion;
import gov.vha.isaac.ochre.api.component.sememe.SememeChronology;
import gov.vha.isaac.ochre.api.component.sememe.version.DescriptionSememe;
import gov.vha.isaac.ochre.api.coordinate.LanguageCoordinate;
import gov.vha.isaac.ochre.api.coordinate.StampCoordinate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ArrayChangeListener;
import javafx.collections.ObservableIntegerArray;

/**
 *
 * @author kec
 */
@XmlRootElement(name = "languageCoordinate")
@XmlAccessorType(XmlAccessType.FIELD)
public class LanguageCoordinateImpl implements LanguageCoordinate {

	int languageConceptSequence;
	int[] dialectAssemblagePreferenceList;
	int[] descriptionTypePreferenceList;

	private LanguageCoordinateImpl() {
		//for jaxb
	}

	public LanguageCoordinateImpl(int languageConceptId, int[] dialectAssemblagePreferenceList, int[] descriptionTypePreferenceList) {
		this.languageConceptSequence = Get.identifierService().getConceptSequence(languageConceptId);
		this.dialectAssemblagePreferenceList = dialectAssemblagePreferenceList;
		for (int i = 0; i < this.dialectAssemblagePreferenceList.length; i++) {
			this.dialectAssemblagePreferenceList [i] = Get.identifierService().getConceptSequence(this.dialectAssemblagePreferenceList [i]);
		}
		this.descriptionTypePreferenceList = descriptionTypePreferenceList;
		for (int i = 0; i < this.descriptionTypePreferenceList.length; i++) {
			this.descriptionTypePreferenceList [i] = Get.identifierService().getConceptSequence(this.descriptionTypePreferenceList [i]);
		}
	}

	@Override
	public int getLanguageConceptSequence() {
		return languageConceptSequence;
	}

	@Override
	public int[] getDialectAssemblagePreferenceList() {
		return dialectAssemblagePreferenceList;
	}

	@Override
	public int[] getDescriptionTypePreferenceList() {
		return descriptionTypePreferenceList;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + this.languageConceptSequence;
		hash = 79 * hash + Arrays.hashCode(this.dialectAssemblagePreferenceList);
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
		final LanguageCoordinateImpl other = (LanguageCoordinateImpl) obj;
		if (this.languageConceptSequence != other.languageConceptSequence) {
			return false;
		}
		if (!Arrays.equals(this.dialectAssemblagePreferenceList, other.dialectAssemblagePreferenceList)) {
			return false;
		}
		return Arrays.equals(this.descriptionTypePreferenceList, other.descriptionTypePreferenceList);
	}

	@Override
	public Optional<LatestVersion<DescriptionSememe<?>>> getFullySpecifiedDescription(
			  List<SememeChronology<? extends DescriptionSememe<?>>> descriptionList, StampCoordinate stampCoordinate) {
		return Get.languageCoordinateService().getSpecifiedDescription(stampCoordinate, descriptionList,
				  Get.languageCoordinateService().getFullySpecifiedConceptSequence(), this);
	}

	@Override
	public Optional<LatestVersion<DescriptionSememe<?>>> getPreferredDescription(List<SememeChronology<? extends DescriptionSememe<?>>> descriptionList, StampCoordinate stampCoordinate) {
		return Get.languageCoordinateService().getSpecifiedDescription(stampCoordinate, descriptionList,
				  Get.languageCoordinateService().getSynonymConceptSequence(), this);
	}

	@Override
	public Optional<LatestVersion<DescriptionSememe<?>>> getDescription(List<SememeChronology<? extends DescriptionSememe<?>>> descriptionList, StampCoordinate stampCoordinate) {
		return Get.languageCoordinateService().getSpecifiedDescription(stampCoordinate, descriptionList, this);
	}

	@Override
	public String toString() {
		return "Language Coordinate{" + Get.conceptDescriptionText(languageConceptSequence)
				  + ", dialect preference: " + Get.conceptDescriptionTextList(dialectAssemblagePreferenceList)
				  + ", type preference: " + Get.conceptDescriptionTextList(descriptionTypePreferenceList) + '}';
	}

	public ArrayChangeListener<ObservableIntegerArray> setDescriptionTypePreferenceListProperty(ObjectProperty<ObservableIntegerArray> descriptionTypePreferenceListProperty) {
		ArrayChangeListener<ObservableIntegerArray> listener = (ObservableIntegerArray observableArray, boolean sizeChanged, int from, int to) -> {
			descriptionTypePreferenceList = observableArray.toArray(descriptionTypePreferenceList);
		};
		descriptionTypePreferenceListProperty.getValue().addListener(new WeakArrayChangeListener(listener));
		return listener;
	}

	public ArrayChangeListener<ObservableIntegerArray> setDialectAssemblagePreferenceListProperty(ObjectProperty<ObservableIntegerArray> dialectAssemblagePreferenceListProperty) {
		ArrayChangeListener<ObservableIntegerArray> listener = (ObservableIntegerArray observableArray, boolean sizeChanged, int from, int to) -> {
			dialectAssemblagePreferenceList = observableArray.toArray(dialectAssemblagePreferenceList);
		};
		dialectAssemblagePreferenceListProperty.getValue().addListener(new WeakArrayChangeListener(listener));
		return listener;
	}

	public ChangeListener<Number> setLanguageConceptSequenceProperty(IntegerProperty languageConceptSequenceProperty) {
		ChangeListener<Number> listener = (ObservableValue<? extends Number> observable,
				  Number oldValue,
				  Number newValue) -> {
			languageConceptSequence = newValue.intValue();
		};
		languageConceptSequenceProperty.addListener(new WeakChangeListener<>(listener));
		return listener;
	}
}
