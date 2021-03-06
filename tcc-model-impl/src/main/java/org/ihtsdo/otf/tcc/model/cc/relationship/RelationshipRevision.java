package org.ihtsdo.otf.tcc.model.cc.relationship;

import gov.vha.isaac.ochre.api.chronicle.LatestVersion;
import gov.vha.isaac.ochre.api.coordinate.StampCoordinate;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.ihtsdo.otf.tcc.api.blueprint.IdDirective;
import org.ihtsdo.otf.tcc.api.blueprint.InvalidCAB;
import org.ihtsdo.otf.tcc.api.blueprint.RefexDirective;
import org.ihtsdo.otf.tcc.api.blueprint.RelationshipCAB;
import org.ihtsdo.otf.tcc.api.contradiction.ContradictionException;
import org.ihtsdo.otf.tcc.api.coordinate.Status;
import org.ihtsdo.otf.tcc.api.coordinate.ViewCoordinate;
import org.ihtsdo.otf.tcc.api.metadata.binding.SnomedMetadataRf2;
import org.ihtsdo.otf.tcc.api.relationship.RelationshipAnalogBI;
import org.ihtsdo.otf.tcc.api.relationship.RelationshipType;
import org.ihtsdo.otf.tcc.api.relationship.RelationshipVersionBI;
import org.ihtsdo.otf.tcc.dto.component.relationship.TtkRelationshipRevision;
import org.ihtsdo.otf.tcc.model.cc.PersistentStore;
import org.ihtsdo.otf.tcc.model.cc.component.ConceptComponent;
import org.ihtsdo.otf.tcc.model.cc.component.Revision;

public class RelationshipRevision extends Revision<RelationshipRevision, Relationship>
        implements RelationshipAnalogBI<RelationshipRevision> {

    protected int characteristicNid;
    protected int group;
    protected int refinabilityNid;
    protected int typeNid;

    //~--- constructors --------------------------------------------------------
    public RelationshipRevision() {
        super();
    }

    public RelationshipRevision(Relationship primordialRel) {
        super(primordialRel.primordialStamp, primordialRel);
        this.characteristicNid = primordialRel.getCharacteristicNid();
        this.group = primordialRel.getGroup();
        this.refinabilityNid = primordialRel.getRefinabilityNid();
        this.typeNid = primordialRel.getTypeNid();
    }

    public RelationshipRevision(int statusAtPositionNid, Relationship primordialRel) {
        super(statusAtPositionNid, primordialRel);
    }

    public RelationshipRevision(RelationshipRevision another, Relationship primordialRel) {
        super(another.stamp, primordialRel);
        this.characteristicNid = another.characteristicNid;
        this.group = another.group;
        this.refinabilityNid = another.refinabilityNid;
        this.typeNid = another.typeNid;
    }

    public RelationshipRevision(TtkRelationshipRevision erv, Relationship primordialRel) throws IOException {
        super(erv.getStatus(), erv.getTime(), PersistentStore.get().getNidForUuids(erv.getAuthorUuid()),
                PersistentStore.get().getNidForUuids(erv.getModuleUuid()), PersistentStore.get().getNidForUuids(erv.getPathUuid()), primordialRel);
        this.characteristicNid = PersistentStore.get().getNidForUuids(erv.getCharacteristicUuid());
        this.group = erv.getGroup();
        this.refinabilityNid = PersistentStore.get().getNidForUuids(erv.getRefinabilityUuid());
        this.typeNid = PersistentStore.get().getNidForUuids(erv.getTypeUuid());
        this.stamp = PersistentStore.get().getStamp(erv);
    }

    public RelationshipRevision(RelationshipAnalogBI another, Status status, long time, int authorNid,
            int moduleNid, int pathNid, Relationship primordialRel) {
        super(status, time, authorNid, moduleNid, pathNid, primordialRel);
        this.characteristicNid = another.getCharacteristicNid();
        this.group = another.getGroup();
        this.refinabilityNid = another.getRefinabilityNid();
        this.typeNid = another.getTypeNid();
    }
    
    public RelationshipRevision(RelationshipAnalogBI another, Relationship primordialMember) {
        super(another.getStatus(), another.getTime(), another.getAuthorNid(), another.getModuleNid(),
              another.getPathNid(), primordialMember);
        this.characteristicNid = another.getCharacteristicNid();
        this.group = another.getGroup();
        this.refinabilityNid = another.getRefinabilityNid();
        this.typeNid = another.getTypeNid();
    }

    //~--- methods -------------------------------------------------------------
    @Override
    protected void addComponentNids(Set<Integer> allNids) {
        allNids.add(primordialComponent.getDestinationNid());
        allNids.add(characteristicNid);
        allNids.add(refinabilityNid);
        allNids.add(typeNid);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (RelationshipRevision.class.isAssignableFrom(obj.getClass())) {
            RelationshipRevision another = (RelationshipRevision) obj;

            return this.stamp == another.stamp;
        }

        return false;
    }

    @Override
    public RelationshipRevision makeAnalog(org.ihtsdo.otf.tcc.api.coordinate.Status status, long time, int authorNid, int moduleNid, int pathNid) {
        if ((this.getTime() == time) && (this.getPathNid() == pathNid)) {
            this.setStatus(status);
            this.setAuthorNid(authorNid);
            this.setModuleNid(moduleNid);

            return this;
        }

        RelationshipRevision newR = new RelationshipRevision(this, status, time, authorNid, moduleNid,
                pathNid, this.primordialComponent);

        this.primordialComponent.addRevision(newR);

        return newR;
    }

    @Override
    public RelationshipCAB makeBlueprint(ViewCoordinate vc,
            IdDirective idDirective, RefexDirective refexDirective) throws IOException, ContradictionException, InvalidCAB {
        RelationshipType relType = null;

        if (getCharacteristicNid()  == SnomedMetadataRf2.INFERRED_RELATIONSHIP_RF2.getLenient().getNid()) {
            throw new InvalidCAB("Inferred relationships can not be used to make blueprints");
        } else if (getCharacteristicNid() == SnomedMetadataRf2.STATED_RELATIONSHIP_RF2.getLenient().getNid()) {
            relType = RelationshipType.STATED_HIERARCHY;
        } else if (getCharacteristicNid() == SnomedMetadataRf2.QUALIFYING_RELATIONSSHIP_RF2.getLenient().getNid()) {
        	relType = RelationshipType.QUALIFIER;
        } else if (getCharacteristicNid() == SnomedMetadataRf2.HISTORICAL_RELATIONSSHIP_RF2.getLenient().getNid()) {
        	relType = RelationshipType.HISTORIC;
        }

        RelationshipCAB relBp = new RelationshipCAB(getOriginNid(), getTypeNid(), getDestinationNid(), getGroup(), relType,
                getVersion(vc), Optional.of(vc),
                idDirective, refexDirective);

        return relBp;
    }

    @Override
    public boolean readyToWriteRevision() {
        assert characteristicNid != Integer.MAX_VALUE : assertionString();
        assert group != Integer.MAX_VALUE : assertionString();
        assert refinabilityNid != Integer.MAX_VALUE : assertionString();
        assert typeNid != Integer.MAX_VALUE : assertionString();

        return true;
    }

    /*
     *  (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append(this.getClass().getSimpleName()).append(":{");
        buf.append("src:");
        ConceptComponent.addNidToBuffer(buf, this.primordialComponent.enclosingConceptNid);
        buf.append(" t:");
        ConceptComponent.addNidToBuffer(buf, typeNid);
        buf.append(" dest:");
        ConceptComponent.addNidToBuffer(buf, this.primordialComponent.getDestinationNid());
        buf.append(" c:");
        ConceptComponent.addNidToBuffer(buf, this.characteristicNid);
        buf.append(" g:").append(this.group);
        buf.append(" r:");
        ConceptComponent.addNidToBuffer(buf, this.refinabilityNid);
        buf.append(super.toString());

        return buf.toString();
    }

    @Override
    public String toUserString() {
        StringBuffer buf = new StringBuffer();

        ConceptComponent.addTextToBuffer(buf, typeNid);
        buf.append(": ");
        ConceptComponent.addTextToBuffer(buf, primordialComponent.getDestinationNid());

        return buf.toString();
    }

    //~--- get methods ---------------------------------------------------------
    @Override
    public int getCharacteristicNid() {
        return characteristicNid;
    }

    @Override
    public int getDestinationNid() {
        return primordialComponent.getDestinationNid(); 
    }

    @Override
    public int getGroup() {
        return group;
    }

    @Override
    public int getOriginNid() {
        return primordialComponent.getOriginNid();
    }

    @Override
    public Relationship getPrimordialVersion() {
        return primordialComponent;
    }

    @Override
    public int getRefinabilityNid() {
        return refinabilityNid;
    }

    @Override
    public int getTypeNid() {
        return typeNid;
    }

    @Override
    public Optional<RelationshipVersion> getVersion(ViewCoordinate c) throws ContradictionException {
        return primordialComponent.getVersion(c);
    }

    @Override
    public List<? extends RelationshipVersion> getVersions() {
        return ((Relationship) primordialComponent).getVersions();
    }

    @Override
    public List<? extends RelationshipVersion> getVersionList() {
        return ((Relationship) primordialComponent).getVersions();
    }

    @Override
    public Collection<RelationshipVersion> getVersions(ViewCoordinate c) {
        return primordialComponent.getVersions(c);
    }

    @Override
    public boolean isInferred()  {
        if (Relationship.inferredNid == Integer.MAX_VALUE) {
            Relationship.inferredNid  = PersistentStore.get().getNidForUuids(SnomedMetadataRf2.INFERRED_RELATIONSHIP_RF2.getUuids());
        }
        return characteristicNid == Relationship.inferredNid ;
    }

    @Override
    public boolean isStated() throws IOException {
        if (Relationship.statedNid == Integer.MAX_VALUE) {
            Relationship.statedNid = PersistentStore.get().getNidForUuids(SnomedMetadataRf2.STATED_RELATIONSHIP_RF2.getUuids());
        }
        return characteristicNid == Relationship.statedNid;
    }
    //~--- set methods ---------------------------------------------------------
    @Override
    public void setCharacteristicNid(int characteristicNid) {
        this.characteristicNid = characteristicNid;
    }

    @Override
    public void setDestinationNid(int nid) throws PropertyVetoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGroup(int group) {
        this.group = group;
        modified();
    }

    @Override
    public void setRefinabilityNid(int refinabilityNid) {
        this.refinabilityNid = refinabilityNid;
    }

    @Override
    public void setTypeNid(int typeNid) {
        this.typeNid = typeNid;
        modified();
    }

    @Override
    public Optional<LatestVersion<RelationshipVersionBI<?>>> getLatestVersion(Class<RelationshipVersionBI<?>> type, StampCoordinate coordinate) {
        return this.primordialComponent.getLatestVersion(type, coordinate);
    }
}
