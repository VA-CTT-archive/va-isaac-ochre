/*
 * Copyright 2010 International Health Terminology Standards Development Organisation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ihtsdo.otf.tcc.model.cc.refex2.data.dataTypes;

import java.beans.PropertyVetoException;

import org.ihtsdo.otf.tcc.api.refex2.data.RefexDataType;
import org.ihtsdo.otf.tcc.model.cc.refex2.data.RefexData;

/**
 * 
 * {@link RefexByteArray}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class RefexByteArray extends RefexData {
    public RefexByteArray(byte[] bytes) throws PropertyVetoException {
        super(RefexDataType.BYTEARRAY);
        setDataByteArray(bytes);
    }

    public void setDataByteArray(byte[] bytes) throws PropertyVetoException {
        data_ = bytes;
    }

    public byte[] getDataByteArray() {
        return data_;
    }

    /**
     * @see org.ihtsdo.otf.tcc.api.refex2.data.RefexDataBI#getDataObject()
     */
    @Override
    public Object getDataObject() {
        return getDataByteArray();
    }
}
