package org.ihtsdo.otf.tcc.api.chronicle;

import gov.vha.isaac.ochre.api.chronicle.ObjectChronology;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.ihtsdo.otf.tcc.api.contradiction.ContradictionException;
import org.ihtsdo.otf.tcc.api.coordinate.EditCoordinate;
import org.ihtsdo.otf.tcc.api.coordinate.Position;
import org.ihtsdo.otf.tcc.api.coordinate.ViewCoordinate;

public interface ComponentChronicleBI<T extends ComponentVersionBI>
        extends ComponentBI, ObjectChronology<T> {

    Optional<? extends T> getVersion(ViewCoordinate c) throws ContradictionException;

    Collection<? extends T> getVersions(ViewCoordinate c);


    @Override
    default List<? extends T> getVersionList() {
        return getVersions();
    }
    
    
    
    /**
     * 
     * @return the stamps for all versions of this chronicle. 
     * @throws IOException 
     */

    Set<Integer> getAllStamps() throws IOException;
    
    Set<Position> getPositions() throws IOException;
    
    T getPrimordialVersion();
    
    boolean makeAdjudicationAnalogs(EditCoordinate ec, ViewCoordinate vc) throws Exception;
    
    int getEnclosingConceptNid();

    default int getAssociatedConceptNid() {
        return getEnclosingConceptNid();
    }
 
}
