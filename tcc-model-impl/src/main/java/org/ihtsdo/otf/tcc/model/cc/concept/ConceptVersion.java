package org.ihtsdo.otf.tcc.model.cc.concept;

import gov.vha.isaac.ochre.api.Get;
import gov.vha.isaac.ochre.api.State;
import gov.vha.isaac.ochre.api.chronicle.LatestVersion;
import gov.vha.isaac.ochre.api.chronicle.StampedVersion;
import gov.vha.isaac.ochre.api.commit.CommitStates;
import gov.vha.isaac.ochre.api.component.concept.ConceptChronology;
import gov.vha.isaac.ochre.api.component.concept.ConceptSnapshot;
import gov.vha.isaac.ochre.api.component.sememe.SememeChronology;
import gov.vha.isaac.ochre.api.component.sememe.version.DescriptionSememe;
import gov.vha.isaac.ochre.api.component.sememe.version.LogicGraphSememe;
import gov.vha.isaac.ochre.api.component.sememe.version.SememeVersion;
import gov.vha.isaac.ochre.api.coordinate.LanguageCoordinate;
import gov.vha.isaac.ochre.api.coordinate.LogicCoordinate;
import gov.vha.isaac.ochre.api.coordinate.PremiseType;
import gov.vha.isaac.ochre.api.coordinate.StampCoordinate;
import gov.vha.isaac.ochre.api.relationship.RelationshipVersionAdaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.mahout.math.map.OpenIntIntHashMap;
import org.ihtsdo.otf.tcc.api.blueprint.ConceptCB;
import org.ihtsdo.otf.tcc.api.blueprint.IdDirective;
import org.ihtsdo.otf.tcc.api.blueprint.InvalidCAB;
import org.ihtsdo.otf.tcc.api.blueprint.RefexDirective;
import org.ihtsdo.otf.tcc.api.changeset.ChangeSetGenerationPolicy;
import org.ihtsdo.otf.tcc.api.changeset.ChangeSetGenerationThreadingPolicy;
import org.ihtsdo.otf.tcc.api.chronicle.ComponentChronicleBI;
import org.ihtsdo.otf.tcc.api.chronicle.ProcessComponentChronicleBI;
import org.ihtsdo.otf.tcc.api.conattr.ConceptAttributeVersionBI;
import org.ihtsdo.otf.tcc.api.concept.ConceptChronicleBI;
import org.ihtsdo.otf.tcc.api.concept.ConceptVersionBI;
import org.ihtsdo.otf.tcc.api.constraint.ConstraintBI;
import org.ihtsdo.otf.tcc.api.constraint.ConstraintCheckType;
import org.ihtsdo.otf.tcc.api.constraint.DescriptionConstraint;
import org.ihtsdo.otf.tcc.api.constraint.RelConstraint;
import org.ihtsdo.otf.tcc.api.constraint.RelConstraintIncoming;
import org.ihtsdo.otf.tcc.api.constraint.RelConstraintOutgoing;
import org.ihtsdo.otf.tcc.api.contradiction.ContradictionException;
import org.ihtsdo.otf.tcc.api.coordinate.EditCoordinate;
import org.ihtsdo.otf.tcc.api.coordinate.Position;
import org.ihtsdo.otf.tcc.api.coordinate.Status;
import org.ihtsdo.otf.tcc.api.coordinate.ViewCoordinate;
import org.ihtsdo.otf.tcc.api.cs.ChangeSetPolicy;
import org.ihtsdo.otf.tcc.api.cs.ChangeSetWriterThreading;
import org.ihtsdo.otf.tcc.api.description.DescriptionChronicleBI;
import org.ihtsdo.otf.tcc.api.description.DescriptionVersionBI;
import org.ihtsdo.otf.tcc.api.id.IdBI;
import org.ihtsdo.otf.tcc.api.media.MediaChronicleBI;
import org.ihtsdo.otf.tcc.api.media.MediaVersionBI;
import org.ihtsdo.otf.tcc.api.metadata.binding.HistoricalRelType;
import org.ihtsdo.otf.tcc.api.metadata.binding.SnomedMetadataRf2;
import org.ihtsdo.otf.tcc.api.nid.NidList;
import org.ihtsdo.otf.tcc.api.nid.NidListBI;
import org.ihtsdo.otf.tcc.api.nid.NidSet;
import org.ihtsdo.otf.tcc.api.nid.NidSetBI;
import org.ihtsdo.otf.tcc.api.refex.RefexChronicleBI;
import org.ihtsdo.otf.tcc.api.refex.RefexVersionBI;
import org.ihtsdo.otf.tcc.api.relationship.RelAssertionType;
import org.ihtsdo.otf.tcc.api.relationship.RelationshipChronicleBI;
import org.ihtsdo.otf.tcc.api.relationship.RelationshipVersionBI;
import org.ihtsdo.otf.tcc.api.relationship.group.RelGroupChronicleBI;
import org.ihtsdo.otf.tcc.api.relationship.group.RelGroupVersionBI;
import org.ihtsdo.otf.tcc.api.spec.ConceptSpec;
import org.ihtsdo.otf.tcc.api.spec.ValidationException;
import org.ihtsdo.otf.tcc.api.store.TerminologySnapshotDI;
import org.ihtsdo.otf.tcc.api.store.Ts;
import org.ihtsdo.otf.tcc.model.cc.LanguageSortPrefs.LANGUAGE_SORT_PREF;
import org.ihtsdo.otf.tcc.model.cc.PersistentStore;
import org.ihtsdo.otf.tcc.model.cc.ReferenceConcepts;
import org.ihtsdo.otf.tcc.model.cc.attributes.ConceptAttributesVersion;
import org.ihtsdo.otf.tcc.model.cc.relationship.group.RelGroupVersion;

public class ConceptVersion implements ConceptVersionBI, 
        Comparable<ConceptVersion>, ConceptSnapshot {

    private static NidSetBI classifierCharacteristics_;

    //~--- fields --------------------------------------------------------------
    private ConceptChronicle concept;

    NidListBI fsnOrder;
    NidListBI preferredOrder;
    NidListBI synonymOrder;
    private ViewCoordinate vc;

   //~--- constructors --------------------------------------------------------
    public ConceptVersion(ConceptChronicle concept, ViewCoordinate coordinate) {
        super();

        if (concept == null) {
            throw new IllegalArgumentException();
        }

        this.concept = concept;
        this.vc = new ViewCoordinate(UUID.randomUUID(), coordinate.getName() + " clone", coordinate);
    }

    @Override
    public ConceptVersionBI createMutableVersion(State state, gov.vha.isaac.ochre.api.coordinate.EditCoordinate ec) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //~--- methods -------------------------------------------------------------
    @Override
    public ConceptVersionBI createMutableVersion(int stampSequence) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IntStream getVersionStampSequences() {
        return concept.getVersionStampSequences();
    }

    @Override
    public int getConceptSequence() {
        return concept.getConceptSequence();
    }

    @Override
    public String getConceptDescriptionText() {
        return concept.getConceptDescriptionText();
    }
    
    @Override
    public boolean addAnnotation(RefexChronicleBI<?> annotation) throws IOException {
        return concept.addAnnotation(annotation);
    }

    @Override
    public void cancel() throws IOException {
        concept.cancel();
    }

    // TODO handle null return by getConceptVersion(vc, cNid)
    private boolean checkConceptVersionConstraint(int cNid, ConceptSpec constraint,
            ConstraintCheckType checkType)
            throws IOException, ContradictionException {
        switch (checkType) {
            case EQUALS:
                return PersistentStore.get().getConceptVersion(vc, cNid).getNid() == constraint.getStrict(vc).getNid();

            case IGNORE:
                return true;

            case KIND_OF:
                return PersistentStore.get().getConceptVersion(vc, cNid).isKindOf(constraint.getStrict(vc));

            default:
                throw new UnsupportedOperationException("Illegal ConstraintCheckType: " + checkType);
        }
    }

    private boolean checkTextConstraint(String text, String constraint, ConstraintCheckType checkType) {
        switch (checkType) {
            case EQUALS:
                return text.equals(constraint);

            case IGNORE:
                return true;

            case REGEX:
                Pattern pattern = Pattern.compile(constraint);
                Matcher matcher = pattern.matcher(text);

                return matcher.find();

            default:
                throw new UnsupportedOperationException("Illegal ConstraintCheckType: " + checkType);
        }
    }

    @Override
    public boolean commit(ChangeSetGenerationPolicy changeSetPolicy,
            ChangeSetGenerationThreadingPolicy changeSetWriterThreading)
            throws IOException {
        return concept.commit(changeSetPolicy, changeSetWriterThreading);
    }

    public boolean isLatestVersionActive(StampCoordinate coordinate) {
        return concept.isLatestVersionActive(coordinate);
    }

    public void commit(ChangeSetPolicy changeSetPolicy, ChangeSetWriterThreading changeSetWriterThreading)
            throws IOException {
        concept.commit(changeSetPolicy, changeSetWriterThreading);
    }

    @Override
    public int compareTo(ConceptVersion o) {
        return getNid() - o.getNid();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConceptVersion) {
            ConceptVersion another = (ConceptVersion) obj;

            if (concept.nid != another.concept.nid) {
                return false;
            }

            if (vc == another.vc) {
                return true;
            }

            return vc.equals(another.vc);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return concept.hashCode;
    }

    @Override
    public boolean makeAdjudicationAnalogs(EditCoordinate ec, ViewCoordinate vc) throws Exception {
        return concept.makeAdjudicationAnalogs(ec, vc);
    }

    @Override
    public ConceptCB makeBlueprint(ViewCoordinate vc,
            IdDirective idDirective, RefexDirective refexDirective) throws IOException, ContradictionException, InvalidCAB {
        return concept.makeBlueprint(vc, idDirective, refexDirective);
    }

    @Override
    public void processComponentChronicles(ProcessComponentChronicleBI processor) throws Exception {
        concept.processComponentChronicles(processor);
    }

    @Override
    public boolean satisfies(ConstraintBI constraint, ConstraintCheckType subjectCheck,
            ConstraintCheckType propertyCheck, ConstraintCheckType valueCheck)
            throws IOException, ContradictionException {
        if (RelConstraintOutgoing.class.isAssignableFrom(constraint.getClass())) {
            return testRels(constraint, subjectCheck, propertyCheck, valueCheck, getRelationshipsOutgoingActive());
        } else if (RelConstraintIncoming.class.isAssignableFrom(constraint.getClass())) {
            return testRels(constraint, subjectCheck, propertyCheck, valueCheck, getRelationshipsIncomingActive());
        } else if (DescriptionConstraint.class.isAssignableFrom(constraint.getClass())) {
            DescriptionConstraint dc = (DescriptionConstraint) constraint;

            for (DescriptionVersionBI<?> desc : getDescriptionsActive()) {
                if (checkConceptVersionConstraint(desc.getConceptNid(), dc.getConceptSpec(), subjectCheck)
                        && checkConceptVersionConstraint(desc.getTypeNid(), dc.getDescTypeSpec(), propertyCheck)
                        && checkTextConstraint(desc.getText(), dc.getText(), valueCheck)) {
                    return true;
                }
            }

            return false;
        }

        throw new UnsupportedOperationException("Can't handle constraint of type: " + constraint);
    }

    private static NidSetBI getClassifierCharacteristics() {
        if (classifierCharacteristics_ == null)
        {
            NidSetBI temp = new NidSet();

            try {
                temp.add(SnomedMetadataRf2.INFERRED_RELATIONSHIP_RF2.getLenient().getNid());
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }

            classifierCharacteristics_ = temp;
        }
        return classifierCharacteristics_;
    }

    private NidListBI getFsnOrder() {
        if (fsnOrder == null) {
            NidListBI newList = new NidList();
            newList.add(ReferenceConcepts.FULLY_SPECIFIED_RF2.getNid());
            fsnOrder = newList;
        }
        
        return fsnOrder;
    }

    private NidListBI getPreferredOrder() {
        if (preferredOrder == null) {
            NidListBI newList = new NidList();

            newList.add(ReferenceConcepts.PREFERRED_ACCEPTABILITY_RF2.getNid());
            newList.add(ReferenceConcepts.SYNONYM_RF2.getNid());
            preferredOrder = newList;
        }
        
        return preferredOrder;
    }

    @Override
    public boolean stampIsInRange(int min, int max) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private boolean testRels(ConstraintBI constraint, ConstraintCheckType subjectCheck,
            ConstraintCheckType propertyCheck, ConstraintCheckType valueCheck,
            Collection<? extends RelationshipVersionBI<?>> rels)
            throws IOException, ContradictionException {
        RelConstraint rc = (RelConstraint) constraint;

        for (RelationshipVersionBI<?> rel : rels) {
            if (checkConceptVersionConstraint(rel.getOriginNid(), rc.getOriginSpec(), subjectCheck)
                    && checkConceptVersionConstraint(rel.getTypeNid(), rc.getRelTypeSpec(), propertyCheck)
                    && checkConceptVersionConstraint(rel.getDestinationNid(), rc.getDestinationSpec(),
                            valueCheck)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toLongString() {
        return concept.toLongString();
    }

    @Override
    public String toString() {
        return concept.toString() + "\n\nviewCoordinate:\n" + vc;
    }

    @Override
    public String toUserString() {
        return concept.toString();
    }

    @Override
    public String toUserString(TerminologySnapshotDI snapshot) throws IOException, ContradictionException {
        if (getPreferredDescription() != null) {
            return getPreferredDescription().getText();
        }

        return concept.getText();
    }

    @Override
    public boolean versionsEqual(ViewCoordinate vc1, ViewCoordinate vc2, Boolean compareAuthoring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   //~--- get methods ---------------------------------------------------------
    @Override
    public Collection<? extends IdBI> getAdditionalIds() throws IOException {
        return concept.getAdditionalIds();
    }

    @Override
    public Collection<? extends IdBI> getAllIds() throws IOException {
        return concept.getAllIds();
    }

    @Override
    public Set<Integer> getAllNidsForVersion() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Integer> getAllStamps() throws IOException {
        return concept.getAllStamps();
    }

    @Override
    public Collection<? extends RefexChronicleBI<?>> getAnnotations() throws IOException {
        return concept.getAnnotations();
    }

    @Override
    public int getAuthorNid() {
        try {
            return getConceptAttributes().getAuthorNid();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ConceptChronicleBI getChronicle() {
        return concept;
    }

    @Override
    public ComponentChronicleBI<?> getComponent(int nid) throws IOException {
        return (ComponentChronicleBI<?>) concept.getComponent(nid);
    }

    @Override
    public ConceptAttributeVersionBI getConceptAttributes() throws IOException {
        try {
            return concept.getConceptAttributes().getVersion(vc.getVcWithAllStatusValues()).get();
        } catch (ContradictionException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public Optional<? extends ConceptAttributeVersionBI> getConceptAttributesActive() throws IOException, ContradictionException {
        Optional<ConceptAttributesVersion> version = concept.getConceptAttributes().getVersion(vc);
        if (version.isPresent() && version.get().getStatus() == Status.ACTIVE) {
            return version;
        }
        return Optional.empty();
    }

    public Collection<Integer> getConceptNidsAffectedByCommit() throws IOException {
        return concept.getConceptNidsAffectedByCommit();
    }

    @Override
    public Collection<? extends RefexVersionBI<?>> getAnnotationsActive(ViewCoordinate xyz)
            throws IOException {
        return concept.getAnnotationsActive(xyz);
    }

    @Override
    public <T extends RefexVersionBI<?>> Collection<T> getAnnotationsActive(ViewCoordinate xyz,
            Class<T> cls)
            throws IOException {
        return concept.getAnnotationsActive(xyz, cls);
    }

    @Override
    public Collection<? extends RefexVersionBI<?>> getAnnotationsActive(ViewCoordinate xyz,
            int refexNid)
            throws IOException {
        return concept.getAnnotationsActive(xyz, refexNid);
    }

    @Override
    public <T extends RefexVersionBI<?>> Collection<T> getAnnotationsActive(ViewCoordinate xyz,
            int refexNid, Class<T> cls)
            throws IOException {
        return concept.getAnnotationsActive(xyz, refexNid, cls);
    }

    @Override
    public Collection<? extends RefexVersionBI<?>> getCurrentRefexMembers(int refsetNid) throws IOException {
        return concept.getRefexMembersActive(vc, refsetNid);
    }

    @Override
    public Collection<? extends RefexVersionBI<?>> getRefexMembersActive(ViewCoordinate xyz, int refsetNid)
            throws IOException {
        return concept.getRefexMembersActive(xyz, refsetNid);
    }

    @Override
    public Collection<? extends RefexVersionBI<?>> getRefexMembersActive(ViewCoordinate xyz) throws IOException {
        return concept.getRefexMembersActive(xyz);
    }

    @Override
    public RefexChronicleBI<?> getCurrentRefsetMemberForComponent(int componentNid) throws IOException {
        return concept.getCurrentRefsetMemberForComponent(vc, componentNid);
    }

    @Override
    public RefexVersionBI<?> getCurrentRefsetMemberForComponent(ViewCoordinate vc, int componentNid)
            throws IOException {
        return concept.getCurrentRefsetMemberForComponent(vc, componentNid);
    }

    @Override
    public Collection<? extends RefexVersionBI<?>> getCurrentRefsetMembers(ViewCoordinate vc)
            throws IOException {
        return concept.getCurrentRefsetMembers(vc);
    }

    @Override
    public Collection<? extends RefexVersionBI<?>> getCurrentRefsetMembers(ViewCoordinate vc, Long cutoffTime)
            throws IOException {
        return concept.getCurrentRefsetMembers(vc, cutoffTime);
    }

    @Override
    public Collection<? extends DescriptionVersionBI> getDescriptions() throws IOException {
        List<DescriptionVersionBI<?>> versions = new ArrayList<>();
        for (DescriptionChronicleBI descriptionChronicleBI : concept.getDescriptions()) {
            try {
                Optional<? extends DescriptionVersionBI> dv = descriptionChronicleBI.getVersion(vc.getVcWithAllStatusValues());
                if (dv.isPresent()) {
                    versions.add(dv.get());
                }
            } catch (ContradictionException ex) {
                throw new IOException(ex);
            }
        }
        return versions;
    }

    @Override
    public Collection<? extends DescriptionVersionBI> getDescriptionsActive() throws IOException {
        Collection<DescriptionVersionBI<?>> returnValues = new ArrayList<>();

        for (DescriptionChronicleBI desc : getDescriptions()) {
            for (DescriptionVersionBI<?> dv : desc.getVersions(vc)) {
                returnValues.add(dv);
            }
        }

        return returnValues;
    }

    @Override
    public Collection<? extends DescriptionVersionBI> getDescriptionsActive(int typeNid) throws IOException {
        return getDescriptionsFullySpecifiedActive(new NidSet(new int[]{typeNid}));
    }

    @Override
    public Collection<? extends DescriptionVersionBI> getDescriptionsFullySpecifiedActive(NidSetBI typeNids) throws IOException {
        Collection<DescriptionVersionBI<?>> results = new ArrayList<>();

        for (DescriptionVersionBI<?> d : getDescriptionsActive()) {
            if (d != null && typeNids.contains(d.getTypeNid())) {
                results.add(d);
            }
        }

        return results;
    }

    @Override
    public Collection<? extends DescriptionVersionBI> getDescriptionsFullySpecifiedActive() throws IOException {
        return getDescriptionsFullySpecifiedActive(new NidSet(getFsnOrder().getListArray()));
    }

    @Override
    public DescriptionVersionBI getFullySpecifiedDescription() throws IOException, ContradictionException {
        return concept.getDesc(getFsnOrder(), vc.getLangPrefList(), vc.getAllowedStatus(), vc.getViewPosition(),
                LANGUAGE_SORT_PREF.getPref(vc.getLanguageSort()), vc.getPrecedence(),
                vc.getContradictionManager());
    }

    @Override
    public Collection<? extends RefexVersionBI<?>> getRefexMembersInactive(ViewCoordinate xyz) throws IOException {
        return concept.getRefexMembersInactive(xyz);
    }

    @Override
    public Collection<? extends MediaChronicleBI> getMedia() throws IOException {
        List<MediaVersionBI<?>> versions = new ArrayList<>();
        for (MediaChronicleBI media : concept.getImages()) {
            try {
                Optional<? extends MediaVersionBI> mv = media.getVersion(vc.getVcWithAllStatusValues());
                if (mv.isPresent()) {
                    versions.add(mv.get());
                }
            } catch (ContradictionException ex) {
                throw new IOException(ex);
            }
        }
        return versions;
    }

    @Override
    public Collection<? extends MediaVersionBI> getMediaActive() throws IOException, ContradictionException {
        Collection<MediaVersionBI<?>> returnValues = new ArrayList<>();

        for (MediaChronicleBI media : getMedia()) {
            for (MediaVersionBI<?> mv : media.getVersions(vc)) {
                returnValues.add(mv);
            }
        }

        return returnValues;
    }

    @Override
    public int getModuleNid() {
        try {
            return getConceptAttributes().getModuleNid();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getNid() {
        return concept.getNid();
    }

    @Override
    public Collection<List<Integer>> getNidPathsToRoot() throws IOException {
        return getNidPathsToRootNoAdd(new ArrayList<Integer>());
    }

    private Collection<List<Integer>> getNidPathsToRoot(List<Integer> nidPath) throws IOException {
        nidPath.add(this.getNid());

        return getNidPathsToRootNoAdd(nidPath);
    }

    private Collection<List<Integer>> getNidPathsToRootNoAdd(List<Integer> nidPath) throws IOException {
        TreeSet<List<Integer>> pathList = new TreeSet<>(new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                if (o1.size() != o2.size()) {
                    return o1.size() - o2.size();
                }

                int size = o1.size();

                for (int i = 0; i < size; i++) {
                    if (o1.get(i) != o2.get(i)) {
                        return o1.get(i) - o2.get(i);
                    }
                }

                return 0;
            }
        });

        try {
            Collection<? extends ConceptVersionBI> parents = getRelationshipsOutgoingDestinationsActiveIsa();

            if (parents.isEmpty()) {
                pathList.add(nidPath);
            } else {
                for (ConceptVersionBI parent : parents) {
                    pathList.addAll(((ConceptVersion) parent).getNidPathsToRoot(new ArrayList<>(nidPath)));
                }
            }
        } catch (ContradictionException ex) {
            ConceptChronicle.logger.log(Level.SEVERE, "Contradiction exception.", ex);
        }

        return pathList;
    }

    @Override
    public int getPathNid() {
        try {
            return getConceptAttributes().getPathNid();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getStampSequence() {
        return getStamp();
    }

    @Override
    public State getState() {
        return getStatus().getState();
    }

    @Override
    public int getAuthorSequence() {
        return Get.identifierService().getConceptSequence(getAuthorNid());
    }

    @Override
    public int getModuleSequence() {
        return Get.identifierService().getConceptSequence(getModuleNid());
   }

    @Override
    public int getPathSequence() {
        return Get.identifierService().getConceptSequence(getPathNid());
   }

    @Override
    public Position getPosition() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Position> getPositions() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<? extends DescriptionVersionBI> getDescriptionsPreferredActive() throws IOException {
        return getDescriptionsFullySpecifiedActive(new NidSet(getPreferredOrder().getListArray()));
    }

    @Override
    public DescriptionVersionBI getPreferredDescription() throws IOException, ContradictionException {
        return concept.getDesc(getPreferredOrder(), vc.getLangPrefList(), vc.getAllowedStatus(),
                vc.getViewPosition(), LANGUAGE_SORT_PREF.getPref(vc.getLanguageSort()),
                vc.getPrecedence(), vc.getContradictionManager());
    }

    @Override
    public UUID getPrimordialUuid() {
        return concept.getPrimordialUuid();
    }

    @Override
    public ConceptVersionBI getPrimordialVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<? extends RefexChronicleBI<?>> getRefexMembers(int refsetNid) throws IOException {
        return concept.getRefexMembers(refsetNid);
    }

    @Override
    public Collection<? extends RefexChronicleBI<?>> getRefexes() throws IOException {
        return concept.getRefexes();
    }

    @Override
    public RefexChronicleBI<?> getRefsetMemberForComponent(int componentNid) throws IOException {
        return concept.getRefsetMemberForComponent(componentNid);
    }

    @Override
    public Collection<? extends RefexChronicleBI<?>> getRefsetMembers() throws IOException {
        return concept.getRefsetMembers();
    }

    @Override
    public Collection<? extends RefexVersionBI<?>> getRefsetMembersActive() throws IOException {
        return concept.getCurrentRefsetMembers(vc);
    }

    @Override
    public Collection<? extends RelGroupVersionBI> getRelationshipGroupsActive() throws IOException, ContradictionException {
        ArrayList<RelGroupVersionBI> results = new ArrayList<>();

        for (RelGroupChronicleBI rgc : concept.getRelationshipGroupsActive(vc)) {
            RelGroupVersionBI rgv = new RelGroupVersion(rgc, vc);

            if (rgv != null && rgv.getRels().size() > 0) {
                results.add(rgv);
            }
        }

        return results;
    }

    @Override
    public Collection<? extends RelGroupVersionBI> getRelationshipGroupsActive(ViewCoordinate vc)
            throws IOException, ContradictionException {
        return concept.getRelationshipGroupsActive(vc);
    }

    @Override
    public Collection<? extends RelationshipVersionBI> getRelationshipsIncoming() throws IOException {
        ArrayList<RelationshipVersionBI<?>> results = new ArrayList<>();
        
        for (RelationshipChronicleBI rc : concept.getRelationshipsIncoming()) {
            for (RelationshipVersionBI<?> rv : rc.getVersions()) {
                if (((vc.getRelationshipAssertionType() == RelAssertionType.INFERRED || vc.getRelationshipAssertionType() == RelAssertionType.INFERRED_THEN_STATED)
                            && (getClassifierCharacteristics().contains(rv.getCharacteristicNid())))
                        || (vc.getRelationshipAssertionType() == RelAssertionType.STATED && !getClassifierCharacteristics().contains(rv.getCharacteristicNid()))) {
                    try {
                        Optional<? extends RelationshipVersionBI<?>> rvForVc = rc.getVersion(vc.getVcWithAllStatusValues());
                        if (rvForVc.isPresent()) {
                            results.add(rvForVc.get());
                            break;
                        }
                    } catch (ContradictionException ex) {
                        throw new IOException(ex);
                    }
                }
            }
        }
        return results;
    }

    @Override
    public Collection<? extends RelationshipVersionBI<?>> getRelationshipsIncomingActive()
            throws IOException, ContradictionException {
        Collection<RelationshipVersionBI<?>> returnValues = new ArrayList<>();

        for (RelationshipChronicleBI rel : getRelationshipsIncoming()) {
            returnValues.addAll(rel.getVersions(vc));
        }

        return returnValues;
    }

    @Override
    public Collection<? extends RelationshipVersionBI> getRelationshipsIncomingActiveIsa()
            throws IOException, ContradictionException {
        Collection<RelationshipVersionBI<?>> returnValues = new ArrayList<>();

        for (RelationshipChronicleBI rel : getRelationshipsIncoming()) {
            for (RelationshipVersionBI<?> rv : rel.getVersions(vc)) {
                if (rv != null && vc.getIsaNid() == rv.getTypeNid()) {
                    returnValues.add(rv);
                }
            }
        }

        return returnValues;
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsIncomingOrigins() throws IOException {
        HashSet<ConceptVersionBI> conceptSet = new HashSet<>();

        for (RelationshipChronicleBI rel : getRelationshipsIncoming()) {
            for (RelationshipVersionBI<?> relv : rel.getVersions()) {
                ConceptVersionBI cv = PersistentStore.get().getConceptVersion(vc, relv.getOriginNid());

                if (cv != null) {
                    conceptSet.add(cv);
                }
            }
        }

        return conceptSet;
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsIncomingOrigins(int typeNid) throws IOException {
        return getRelationshipsIncomingOrigins(new NidSet(new int[]{typeNid}));
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsIncomingOrigins(NidSetBI typeNids)
            throws IOException {
        HashSet<ConceptVersionBI> conceptSet = new HashSet<>();

        for (RelationshipChronicleBI rel : getRelationshipsIncoming()) {
            for (RelationshipVersionBI<?> relv : rel.getVersions()) {
                if (typeNids.contains(relv.getTypeNid())) {
                    ConceptVersionBI cv = PersistentStore.get().getConceptVersion(vc, relv.getOriginNid());

                    if (cv != null) {
                        conceptSet.add(cv);
                    }
                }
            }
        }

        return conceptSet;
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsIncomingOriginsActive()
            throws IOException, ContradictionException {
        HashSet<ConceptVersionBI> conceptSet = new HashSet<>();

        for (RelationshipChronicleBI rel : getRelationshipsIncoming()) {
            for (RelationshipVersionBI<?> relv : rel.getVersions(vc)) {
                ConceptVersionBI cv = PersistentStore.get().getConceptVersion(vc, relv.getOriginNid());

                if (cv != null) {
                    conceptSet.add(cv);
                }
            }
        }

        return conceptSet;
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsIncomingOriginsActive(int typeNid)
            throws IOException, ContradictionException {
        return getRelationshipsIncomingOriginsActive(new NidSet(new int[]{typeNid}));
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsIncomingOriginsActive(NidSetBI typeNids)
            throws IOException, ContradictionException {
        HashSet<ConceptVersionBI> conceptSet = new HashSet<>();

        for (RelationshipChronicleBI rel : getRelationshipsIncoming()) {
            for (RelationshipVersionBI<?> relv : rel.getVersions(vc)) {
                if (typeNids.contains(relv.getTypeNid())) {
                    ConceptVersionBI cv = PersistentStore.get().getConceptVersion(vc, relv.getOriginNid());

                    if (cv != null) {
                       conceptSet.add(cv);
                    }
                }
            }
        }

        return conceptSet;
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsIncomingOriginsActiveIsa()
            throws IOException, ContradictionException {
        HashSet<ConceptVersionBI> conceptSet = new HashSet<>();

        for (RelationshipChronicleBI rel : getRelationshipsIncoming()) {
            for (RelationshipVersionBI<?> relv : rel.getVersions(vc)) {
                if (vc.getIsaNid() == relv.getTypeNid()) {
                    ConceptVersionBI cv = PersistentStore.get().getConceptVersion(vc, relv.getOriginNid());

                    if (cv != null) {
                        conceptSet.add(cv);
                    }
                }
            }
        }

        return conceptSet;
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsIncomingOriginsIsa() throws IOException {
        HashSet<ConceptVersionBI> conceptSet = new HashSet<>();

        for (RelationshipChronicleBI rel : getRelationshipsIncoming()) {
            for (RelationshipVersionBI<?> relv : rel.getVersions()) {
                if (vc.getIsaNid() == relv.getTypeNid()) {
                    ConceptVersionBI cv = PersistentStore.get().getConceptVersion(vc, relv.getOriginNid());

                    if (cv != null) {
                        conceptSet.add(cv);
                    }
                }
            }
        }

        return conceptSet;
    }

    @Override
    public Collection<? extends RelationshipVersionBI> getRelationshipsOutgoing() throws IOException {
        Collection<RelationshipVersionBI<?>> results = new ArrayList<>();
        
        for (RelationshipChronicleBI rc : concept.getRelationshipsOutgoing()) {
            for (RelationshipVersionBI<?> rv : rc.getVersions()) {
                if (((vc.getRelationshipAssertionType() == RelAssertionType.INFERRED || vc.getRelationshipAssertionType() == RelAssertionType.INFERRED_THEN_STATED)
                            && (getClassifierCharacteristics().contains(rv.getCharacteristicNid())))
                        || (vc.getRelationshipAssertionType() == RelAssertionType.STATED && !getClassifierCharacteristics().contains(rv.getCharacteristicNid()))) {
                    try {
                        Optional<? extends RelationshipVersionBI<?>> rvForVc = rc.getVersion(vc.getVcWithAllStatusValues());
                        if (rvForVc.isPresent()) {
                            results.add(rvForVc.get());
                            break;
                        }
                    } catch (ContradictionException ex) {
                        throw new IOException(ex);
                    }
                }
            }
        }
        return results;
    }

    @Override
    public Collection<? extends RelationshipVersionBI<?>> getRelationshipsOutgoingActive()
            throws IOException, ContradictionException {
        Collection<RelationshipVersionBI<?>> returnValues = new ArrayList<>();

        for (RelationshipChronicleBI rel : concept.getRelationshipsOutgoing()) {
            returnValues.addAll(rel.getVersions(vc));
        }

        return returnValues;
    }

    @Override
    public Collection<? extends RelationshipVersionBI<?>> getRelationshipsOutgoingActiveIsa()
            throws IOException, ContradictionException {
        Collection<RelationshipVersionBI<?>> returnValues = new ArrayList<>();

        for (RelationshipChronicleBI rel :concept.getRelationshipsOutgoing()) {
            for (RelationshipVersionBI<?> rv : rel.getVersions(vc)) {
                if (vc.getIsaNid() == rv.getTypeNid()) {
                    returnValues.add(rv);
                }
            }
        }

        return returnValues;
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsOutgoingDestinations() throws IOException {
        HashSet<ConceptVersionBI> conceptSet = new HashSet<>();

        for (RelationshipChronicleBI rel :concept.getRelationshipsOutgoing()) {
            for (RelationshipVersionBI<?> relv : rel.getVersions()) {
                ConceptVersionBI cv = PersistentStore.get().getConceptVersion(vc, relv.getDestinationNid());

                if (cv != null) {
                    conceptSet.add(cv);
                }
            }
        }

        return conceptSet;
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsOutgoingDestinations(int typeNid) throws IOException {
        return getRelationshipsOutgoingDestinations(new NidSet(new int[]{typeNid}));
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsOutgoingDestinations(NidSetBI typeNids)
            throws IOException {
        HashSet<ConceptVersionBI> conceptSet = new HashSet<>();

        for (RelationshipChronicleBI rel :concept.getRelationshipsOutgoing()) {
            for (RelationshipVersionBI<?> relv : rel.getVersions()) {
                if (typeNids.contains(relv.getTypeNid())) {
                    ConceptVersionBI cv = PersistentStore.get().getConceptVersion(vc, relv.getDestinationNid());

                    if (cv != null) {
                        conceptSet.add(cv);
                    }
                }
            }
        }

        return conceptSet;
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsOutgoingDestinationsActive()
            throws IOException, ContradictionException {
        HashSet<ConceptVersionBI> conceptSet = new HashSet<>();

        for (RelationshipChronicleBI rel :concept.getRelationshipsOutgoing()) {
            for (RelationshipVersionBI<?> relv : rel.getVersions(vc)) {
                ConceptVersionBI cv = PersistentStore.get().getConceptVersion(vc, relv.getDestinationNid());

                if (cv != null) {
                    conceptSet.add(cv);
                }
            }
        }

        return conceptSet;
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsOutgoingDestinationsActive(int typeNid)
            throws IOException, ContradictionException {
        return getRelationshipsOutgoingDestinationsActive(new NidSet(new int[]{typeNid}));
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsOutgoingDestinationsActive(NidSetBI typeNids)
            throws IOException, ContradictionException {
        HashSet<ConceptVersionBI> conceptSet = new HashSet<>();

        for (RelationshipChronicleBI rel :concept.getRelationshipsOutgoing()) {
            for (RelationshipVersionBI<?> relv : rel.getVersions(vc)) {
                if (typeNids.contains(relv.getTypeNid())) {
                    ConceptVersionBI cv = PersistentStore.get().getConceptVersion(vc, relv.getDestinationNid());

                    if (cv != null) {
                        conceptSet.add(cv);
                    }
                }
            }
        }

        return conceptSet;
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsOutgoingDestinationsActiveIsa()
            throws IOException, ContradictionException {
        HashSet<ConceptVersionBI> conceptSet = new HashSet<>();

        for (RelationshipChronicleBI rel :concept.getRelationshipsOutgoing()) {
            for (RelationshipVersionBI<?> relv : rel.getVersions(vc)) {
                if (vc.getIsaNid() == relv.getTypeNid()) {
                    ConceptVersionBI cv = PersistentStore.get().getConceptVersion(vc, relv.getDestinationNid());

                    if (cv != null) {
                        conceptSet.add(cv);
                    }
                }
            }
        }

        return conceptSet;
    }

    @Override
    public Collection<? extends ConceptVersionBI> getRelationshipsOutgoingDestinationsIsa() throws IOException {
        HashSet<ConceptVersionBI> conceptSet = new HashSet<>();

        for (RelationshipChronicleBI rel :concept.getRelationshipsOutgoing()) {
            for (RelationshipVersionBI<?> relv : rel.getVersions()) {
                if (vc.getIsaNid() == relv.getTypeNid()) {
                    ConceptVersionBI cv = PersistentStore.get().getConceptVersion(vc, relv.getDestinationNid());

                    if (cv != null) {
                        conceptSet.add(cv);
                    }
                }
            }
        }

        return conceptSet;
    }

    @Override
    public int[] getRelationshipsOutgoingDestinationsNidsActiveIsa() throws IOException {
        OpenIntIntHashMap nidList = new OpenIntIntHashMap(10);

        for (RelationshipChronicleBI rel :concept.getRelationshipsOutgoing()) {
            for (RelationshipVersionBI<?> relv : rel.getVersions(vc)) {
                if (vc.getIsaNid() == relv.getTypeNid()) {
                    nidList.put(relv.getDestinationNid(), relv.getDestinationNid());
                }
            }
        }

        return nidList.keys().elements();
    }

    @Override
    public int getStamp() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Status getStatus() {
        try {
            return getConceptAttributes().getStatus();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<? extends DescriptionVersionBI> getSynonyms() throws IOException {
        if (synonymOrder == null) {
            synonymOrder = new NidList();
            synonymOrder.add(ReferenceConcepts.SYNONYM_RF2.getNid());
        }

        throw new UnsupportedOperationException();
        
        //return synonymOrder;
    }

    @Override
    public long getTime() {
        try {
            return getConceptAttributes().getTime();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UUID> getUuidList() {
        return concept.getUuidList();
    }

    @Override
    public Optional<ConceptVersionBI> getVersion(ViewCoordinate c) {
        return concept.getVersion(c);
    }

    @Override
    public List<? extends ConceptVersionBI> getVersions() {
        return concept.getVersionList();
    }

    @Override
    public List<? extends ConceptVersionBI> getVersionList() {
        return concept.getVersionList();
    }

    // TODO this method calls ConceptChronicle.getVersions() which always returns UnsupportedOperationException.  If a different implementation getting called, it's probably wrong anyway
    @Override
    public Collection<? extends ConceptVersionBI> getVersions(ViewCoordinate c) {
        return concept.getVersions();
    }

    @Override
    public ViewCoordinate getViewCoordinate() {
        return vc;
    }

    @Override
    public boolean hasAnnotationMemberActive(int refsetNid) throws IOException {
        return concept.hasCurrentAnnotationMember(vc, refsetNid);
    }

    @Override
    public boolean hasChildren() throws IOException, ContradictionException {
        Collection<? extends RelationshipVersionBI<?>> children = this.getRelationshipsIncomingActive();

        if (children.isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean hasCurrentAnnotationMember(ViewCoordinate xyz, int refsetNid) throws IOException {
        return concept.hasCurrentAnnotationMember(xyz, refsetNid);
    }

    @Override
    public boolean hasCurrentRefexMember(ViewCoordinate xyz, int refsetNid) throws IOException {
        return concept.hasCurrentRefexMember(xyz, refsetNid);
    }

    @Override
    public boolean hasCurrentRefsetMemberForComponent(ViewCoordinate vc, int componentNid) throws IOException {
        return concept.hasCurrentRefsetMemberForComponent(vc, componentNid);
    }

    @Override
    public boolean hasHistoricalRels() throws IOException, ContradictionException {
        boolean history = false;
        Collection<? extends RelationshipChronicleBI> outRels =concept.getRelationshipsOutgoing();

        if (outRels != null) {
            NidSet historicalTypeNids = new NidSet();

            for (ConceptSpec spec : HistoricalRelType.getHistoricalTypes()) {
                historicalTypeNids.add(spec.getStrict(vc).getNid());
            }

            for (RelationshipChronicleBI outRel : outRels) {
                Optional<? extends RelationshipVersionBI<?>> vOutRel = outRel.getVersion(vc);

                if (vOutRel.isPresent()) {
                    if (historicalTypeNids.contains(vOutRel.get().getTypeNid())) {
                        history = true;

                        break;
                    }
                }
            }
        }

        return history;
    }

    @Override
    public boolean hasRefexMemberActive(int refsetNid) throws IOException {
        return concept.hasCurrentRefexMember(vc, refsetNid);
    }

    @Override
    public boolean hasRefsetMemberForComponentActive(int componentNid) throws IOException {
        return concept.hasCurrentRefsetMemberForComponent(vc, componentNid);
    }

    @Override
    public boolean isActive() throws IOException {
        try {
            if (!getConceptAttributesActive().isPresent()) {
                return false;
            }

            return true;
        } catch (ContradictionException ex) {
            for (ConceptAttributeVersionBI<?> version : concept.getConceptAttributes().getVersions(vc)) {
                if (version.getStatus() == Status.ACTIVE) {
                    return true;
                }
            }
        }

        return false;
    }
    
    

    @Override
    public boolean isCanceled() throws IOException
    {
        return concept.isCanceled();
    }

    @Override
    public boolean isAnnotationStyleRefex() throws IOException {
        return concept.isAnnotationStyleRefex();
    }

    @Override
    public boolean isChildOf(ConceptSnapshot possibleParent) throws IOException {
        for (int nid : getRelationshipsOutgoingDestinationsNidsActiveIsa()) {
            if (nid == possibleParent.getNid()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isKindOf(ConceptSnapshot possibleKind) throws IOException, ContradictionException {
        return Ts.get().isKindOf(getNid(), possibleKind.getNid(), vc);
    }

    @Override
    public boolean isLeaf() throws IOException {
        return PersistentStore.get().getPossibleChildren(concept.nid, vc).length == 0;
    }

    // TODO
    @Override
    public boolean isMember(int collectionNid) throws IOException {
        boolean isMember = false;

        try {
            Collection<? extends RefexChronicleBI<?>> refexes
                    = concept.getConceptAttributes().getRefexMembersActive(vc);

            if (refexes != null) {
                for (RefexChronicleBI<?> refex : refexes) {
                    if (refex.getAssemblageNid() == collectionNid) {
                        return true;
                    }
                }
            }

            return isMember;
        } catch (Exception e) {
            throw new IOException(e);    // AceLog.getAppLog().alertAndLogException(e);
        }
    }

    @Override
    public boolean isUncommitted() {
        return concept.isUncommitted();
    }
    @Override
    public CommitStates getCommitState() {
        if (isUncommitted()) {
            return CommitStates.UNCOMMITTED;
        }
        return CommitStates.COMMITTED;
    }

   //~--- set methods ---------------------------------------------------------
    @Override
    public void setAnnotationStyleRefex(boolean annotationStyleRefset) {
        concept.setAnnotationStyleRefex(annotationStyleRefset);
    }

    public boolean isIndexed() {
        return concept.isIndexed();
    }

    public void setIndexed() {
        concept.setIndexed();
    }

    @Override
    public Stream<SememeChronology<? extends SememeVersion<?>>> getSememeChronicles() {
        return concept.getSememeChronicles();
    }

    @Override
    public List<SememeChronology<? extends SememeVersion<?>>> getSememeList() {
        return concept.getSememeList();
    }

    @Override
    public List<SememeChronology<? extends SememeVersion<?>>> getSememeListFromAssemblage(int assemblageSequence) {
        return concept.getSememeListFromAssemblage(assemblageSequence);
    }

    @Override
    public <SV extends SememeVersion> List<SememeChronology<SV>> getSememeListFromAssemblageOfType(int assemblageSequence, Class<SV> type) {
        return concept.getSememeListFromAssemblageOfType(assemblageSequence, type);
    }

    @Override
    public List<SememeChronology<? extends DescriptionSememe<?>>> getConceptDescriptionList() {
       return concept.getConceptDescriptionList();
    }

    @Override
    public boolean containsDescription(String descriptionText) {
        return concept.containsDescription(descriptionText);
    }

    @Override
    public boolean containsDescription(String descriptionText, StampCoordinate stampCoordinate) {
        return concept.containsDescription(descriptionText, stampCoordinate);
    }

    @Override
    public ConceptChronology<ConceptVersionBI> getChronology() {
        throw new UnsupportedOperationException("Only for OCHRE implementation");
    }


    @Override
    public StampCoordinate getStampCoordinate() {
        return vc;
    }

    @Override
    public boolean containsActiveDescription(String descriptionText) {
       return concept.containsDescription(descriptionText, vc);
    }

    @Override
    public Optional<? extends Set<? extends StampedVersion>> getContradictions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public int getEnclosingConceptNid() {
       return getNid();
    }
    @Override
    public int getAssociatedConceptNid() {
        return getEnclosingConceptNid();
    }

    @Override
    public Optional<LatestVersion<ConceptVersionBI>> getLatestVersion(Class<ConceptVersionBI> type, StampCoordinate coordinate) {
        return concept.getLatestVersion(type, coordinate);
    }
    

    @Override
    public Optional<LatestVersion<DescriptionSememe<?>>> getFullySpecifiedDescription(LanguageCoordinate languageCoordinate, StampCoordinate stampCoordinate) {
       return languageCoordinate.getFullySpecifiedDescription(getConceptDescriptionList(), stampCoordinate);
    }

    @Override
    public Optional<LatestVersion<DescriptionSememe<?>>> getPreferredDescription(LanguageCoordinate languageCoordinate, StampCoordinate stampCoordinate) {
       return languageCoordinate.getPreferredDescription(getConceptDescriptionList(), stampCoordinate);
    }

    @Override
    public List<? extends SememeChronology<? extends RelationshipVersionAdaptor<?>>> getRelationshipListOriginatingFromConcept(LogicCoordinate logicCoordinate) {
        return concept.getRelationshipListOriginatingFromConcept(logicCoordinate);
    }

    @Override
    public List<? extends SememeChronology<? extends RelationshipVersionAdaptor<?>>> getRelationshipListOriginatingFromConcept() {
        return concept.getRelationshipListOriginatingFromConcept();
    }

    @Override
    public List<? extends SememeChronology<? extends RelationshipVersionAdaptor<?>>> getRelationshipListWithConceptAsDestination() {
        return concept.getRelationshipListWithConceptAsDestination();
    }

    @Override
    public List<? extends SememeChronology<? extends RelationshipVersionAdaptor<?>>> getRelationshipListWithConceptAsDestination(LogicCoordinate logicCoordinate) {
        return concept.getRelationshipListWithConceptAsDestination(logicCoordinate);
    }

    @Override
    public Optional<LatestVersion<LogicGraphSememe<?>>> getLogicalDefinition(StampCoordinate stampCoordinate, 
            PremiseType premiseType, LogicCoordinate logicCoordinate) {
        return concept.getLogicalDefinition(stampCoordinate, premiseType, logicCoordinate);
    }

    @Override
    public LanguageCoordinate getLanguageCoordinate() {
        return Get.configurationService().getDefaultLanguageCoordinate();
    }
    
    @Override
    public String getLogicalDefinitionChronologyReport(StampCoordinate stampCoordinate, PremiseType premiseType, 
            LogicCoordinate logicCoordinate) {
       return "Not supported in OTF"; 
    }

}
