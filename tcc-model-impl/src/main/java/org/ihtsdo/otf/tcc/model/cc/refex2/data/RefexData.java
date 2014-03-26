/**
 * Copyright Notice
 *
 * This is a work of the U.S. Government and is not subject to copyright 
 * protection in the United States. Foreign copyrights may apply.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ihtsdo.otf.tcc.model.cc.refex2.data;

import org.ihtsdo.otf.tcc.api.refex2.data.RefexDataBI;
import org.ihtsdo.otf.tcc.api.refex2.data.RefexDataType;

/**
 * {@link RefexData}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a> 
 */
public abstract class RefexData implements RefexDataBI
{
	protected byte[] data_;
	protected RefexDataType type_;
	
	protected RefexData(RefexDataType type)
	{
		type_ = type;
	}
	
	/**
	 * @see org.ihtsdo.otf.tcc.api.refex2.data.RefexDataBI#getRefexDataType()
	 */
	@Override
	public RefexDataType getRefexDataType()
	{
		return type_;
	}
	
	/**
	 * @see org.ihtsdo.otf.tcc.api.refex2.data.RefexDataBI#getData()
	 */
	@Override
	public byte[] getData()
	{
		return data_;
	}
}
