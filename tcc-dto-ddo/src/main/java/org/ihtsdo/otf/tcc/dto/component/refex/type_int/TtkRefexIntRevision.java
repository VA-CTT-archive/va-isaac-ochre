package org.ihtsdo.otf.tcc.dto.component.refex.type_int;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.otf.tcc.api.refex.type_int.RefexIntVersionBI;
import org.ihtsdo.otf.tcc.dto.component.TtkRevision;

//~--- JDK imports ------------------------------------------------------------

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import java.util.Collection;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAttribute;

public class TtkRefexIntRevision extends TtkRevision {
   public static final long serialVersionUID = 1;

   //~--- fields --------------------------------------------------------------

   @XmlAttribute
   public int intValue;

   //~--- constructors --------------------------------------------------------

   public TtkRefexIntRevision() {
      super();
   }

   public TtkRefexIntRevision(RefexIntVersionBI another) throws IOException {
      super(another);
      this.intValue = another.getInt1();
   }

   public TtkRefexIntRevision(DataInput in, int dataVersion) throws IOException, ClassNotFoundException {
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
    * {@code ERefsetIntVersion} object, and contains the same values, field by field,
    * as this {@code ERefsetIntVersion}.
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

      if (TtkRefexIntRevision.class.isAssignableFrom(obj.getClass())) {
         TtkRefexIntRevision another = (TtkRefexIntRevision) obj;

         // =========================================================
         // Compare properties of 'this' class to the 'another' class
         // =========================================================
         // Compare intValue
         if (this.intValue != another.intValue) {
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
      intValue = in.readInt();
   }

   /**
    * Returns a string representation of the object.
    */
   @Override
   public String toString() {
      StringBuilder buff = new StringBuilder();

      buff.append(this.getClass().getSimpleName()).append(": ");
      buff.append(" int: ");
      buff.append(this.intValue);
      buff.append(" ");
      buff.append(super.toString());

      return buff.toString();
   }

   @Override
   public void writeExternal(DataOutput out) throws IOException {
      super.writeExternal(out);
      out.writeInt(intValue);
   }

   //~--- get methods ---------------------------------------------------------

   public int getIntValue() {
      return intValue;
   }

   //~--- set methods ---------------------------------------------------------

   public void setIntValue(int intValue) {
      this.intValue = intValue;
   }
}
