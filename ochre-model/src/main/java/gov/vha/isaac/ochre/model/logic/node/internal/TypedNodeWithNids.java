package gov.vha.isaac.ochre.model.logic.node.internal;


import gov.vha.isaac.ochre.api.DataTarget;
import gov.vha.isaac.ochre.api.Get;
import gov.vha.isaac.ochre.api.component.concept.ConceptChronology;
import gov.vha.isaac.ochre.api.component.concept.ConceptVersion;
import gov.vha.isaac.ochre.model.logic.LogicalExpressionOchreImpl;
import gov.vha.isaac.ochre.api.logic.Node;
import gov.vha.isaac.ochre.model.logic.node.AbstractNode;
import gov.vha.isaac.ochre.model.logic.node.ConnectorNode;
import gov.vha.isaac.ochre.model.logic.node.external.TypedNodeWithUuids;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by kec on 12/9/14.
 */
public abstract class TypedNodeWithNids extends ConnectorNode {

    int typeConceptNid;

    public TypedNodeWithNids(LogicalExpressionOchreImpl logicGraphVersion, DataInputStream dataInputStream) throws IOException {
        super(logicGraphVersion, dataInputStream);
        this.typeConceptNid = dataInputStream.readInt();
    }

    public TypedNodeWithNids(LogicalExpressionOchreImpl logicGraphVersion, int typeConceptNid, AbstractNode child) {
        super(logicGraphVersion, child);
        this.typeConceptNid = typeConceptNid;
    }

    public TypedNodeWithNids(TypedNodeWithUuids externalForm) {
        super(externalForm);
        this.typeConceptNid = Get.identifierService().getNidForUuids(externalForm.getTypeConceptUuid());
    }

    public int getTypeConceptNid() {
        return typeConceptNid;
    }

    @Override
    public String toString() {
        Optional<? extends ConceptChronology<? extends ConceptVersion>> typeConcept 
                = Get.conceptService().getOptionalConcept(typeConceptNid);
        if (typeConcept.isPresent()) {
        return " type: " + typeConcept.get().toUserString() +"<"
                + typeConcept.get().getConceptSequence()
                + ">"+ super.toString();
        }
        return " type: " + typeConceptNid + super.toString();
    }

        @Override
    protected void writeData(DataOutput dataOutput, DataTarget dataTarget) throws IOException {
        super.writeData(dataOutput, dataTarget);
        dataOutput.writeInt(typeConceptNid);
    }
        
    public Node getOnlyChild() {
        Node[] children = getChildren();
        if (children.length == 1) {
            return children[0];
        }
        throw new IllegalStateException("Typed nodes can have only one child. Found: " + Arrays.toString(children));
    }
}