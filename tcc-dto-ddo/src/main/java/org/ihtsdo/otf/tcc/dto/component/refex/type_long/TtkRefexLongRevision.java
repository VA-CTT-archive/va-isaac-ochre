package org.ihtsdo.otf.tcc.dto.component.refex.type_long;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.otf.tcc.api.refex.type_long.RefexLongVersionBI;
import org.ihtsdo.otf.tcc.dto.component.TtkRevision;

//~--- JDK imports ------------------------------------------------------------

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;

public class TtkRefexLongRevision extends TtkRevision {
   public static final long serialVersionUID = 1;

   //~--- fields --------------------------------------------------------------

   @XmlAttribute
   public long longValue;

   //~--- constructors --------------------------------------------------------

   public TtkRefexLongRevision() {
      super();
   }

   public TtkRefexLongRevision(RefexLongVersionBI another) throws IOException {
      super(another);
      this.longValue = another.getLong1();
   }

   public TtkRefexLongRevision(DataInput in, int dataVersion) throws IOException, ClassNotFoundException {
      super();
      readExternal(in, dataVersion);
   }


   //~--- methods -------------------------------------------------------------
   @Override
   protected final void addUuidReferencesForRevisionComponent(Collection<UUID> references) {
       // nothing to add
   }
   /**
    * Compares this object to the specified object. The result is {@code true}
    * if and only if the argument is not {@code null}, is a
    * {@code ERefsetLongVersion} object, and contains the same values, field by field,
    * as this {@code ERefsetLongVersion}.
    *
    * @param obj the object to compare with.
    * @return {@code true} if the objects are the same;
    *         {@code false} otherwise.
    */
   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }

      if (TtkRefexLongRevision.class.isAssignableFrom(obj.getClass())) {
         TtkRefexLongRevision another = (TtkRefexLongRevision) obj;

         // =========================================================
         // Compare properties of 'this' class to the 'another' class
         // =========================================================
         // Compare longValue
         if (this.longValue != another.longValue) {
            return false;
         }

         // Compare their parents
         return super.equals(obj);
      }

      return false;
   }


   @Override
   public void readExternal(DataInput in, int dataVersion) throws IOException, ClassNotFoundException {
      super.readExternal(in, dataVersion);
      longValue = in.readLong();
   }

   /**
    * Returns a string representation of the object.
    */
   @Override
   public String toString() {
      StringBuilder buff = new StringBuilder();

      buff.append(this.getClass().getSimpleName()).append(": ");
      buff.append(" long: ");
      buff.append(this.longValue);
      buff.append(" ");
      buff.append(super.toString());

      return buff.toString();
   }

   @Override
   public void writeExternal(DataOutput out) throws IOException {
      super.writeExternal(out);
      out.writeLong(longValue);
   }

   //~--- get methods ---------------------------------------------------------

   public long getLongValue() {
      return longValue;
   }

   //~--- set methods ---------------------------------------------------------

   public void setLongValue(long longValue) {
      this.longValue = longValue;
   }
}
