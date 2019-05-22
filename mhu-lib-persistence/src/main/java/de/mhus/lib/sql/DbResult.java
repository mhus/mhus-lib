/**
 * Copyright 2018 Mike Hummel
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
package de.mhus.lib.sql;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import de.mhus.lib.basics.MCloseable;
import de.mhus.lib.core.MDate;


/**
 * <p>Abstract DbResult class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public abstract class DbResult implements MCloseable {

	/**
	 * <p>getString.</p>
	 *
	 * @param columnLabel a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 * @throws java.lang.Exception if any.
	 */
	public abstract String getString(String columnLabel) throws Exception;

	/**
	 * <p>next.</p>
	 *
	 * @return a boolean.
	 * @throws java.lang.Exception if any.
	 */
	public abstract boolean next() throws Exception;

	/**
	 * <p>getBinaryStream.</p>
	 *
	 * @param columnLabel a {@link java.lang.String} object.
	 * @return a {@link java.io.InputStream} object.
	 * @throws java.lang.Exception if any.
	 */
	public abstract InputStream getBinaryStream(String columnLabel) throws Exception;

	/**
	 * <p>getBoolean.</p>
	 *
	 * @param columnLabel a {@link java.lang.String} object.
	 * @return a boolean.
	 * @throws java.lang.Exception if any.
	 */
	public abstract boolean getBoolean(String columnLabel) throws Exception;

	/**
	 * <p>getInt.</p>
	 *
	 * @param columnLabel a {@link java.lang.String} object.
	 * @return a int.
	 * @throws java.lang.Exception if any.
	 */
	public abstract int getInt(String columnLabel) throws Exception;

	/**
	 * <p>getLong.</p>
	 *
	 * @param columnLabel a {@link java.lang.String} object.
	 * @return a long.
	 * @throws java.lang.Exception if any.
	 */
	public abstract long getLong(String columnLabel) throws Exception;

	/**
	 * <p>getFloat.</p>
	 *
	 * @param columnLabel a {@link java.lang.String} object.
	 * @return a float.
	 * @throws java.lang.Exception if any.
	 */
	public abstract float getFloat(String columnLabel) throws Exception;

	/**
	 * <p>getDouble.</p>
	 *
	 * @param columnLabel a {@link java.lang.String} object.
	 * @return a double.
	 * @throws java.lang.Exception if any.
	 */
	public abstract double getDouble(String columnLabel) throws Exception;

	/**
	 * <p>getDate.</p>
	 *
	 * @param columnLabel a {@link java.lang.String} object.
	 * @return a {@link java.sql.Date} object.
	 * @throws java.lang.Exception if any.
	 */
	public abstract Date getDate(String columnLabel) throws Exception;

	/**
	 * <p>getMDate.</p>
	 *
	 * @param columnLabel a {@link java.lang.String} object.
	 * @return a {@link de.mhus.lib.core.MDate} object.
	 * @throws java.lang.Exception if any.
	 */
	public abstract MDate getMDate(String columnLabel) throws Exception;

	/**
	 * <p>getTime.</p>
	 *
	 * @param columnLabel a {@link java.lang.String} object.
	 * @return a {@link java.sql.Time} object.
	 * @throws java.lang.Exception if any.
	 */
	public abstract Time getTime(String columnLabel) throws Exception;
	
	/**
	 * <p>getTimestamp.</p>
	 *
	 * @param columnLabel a {@link java.lang.String} object.
	 * @return a {@link java.sql.Timestamp} object.
	 * @throws java.lang.Exception if any.
	 */
	public abstract Timestamp getTimestamp(String columnLabel) throws Exception;

	/**
	 * <p>getColumnNames.</p>
	 *
	 * @return a {@link java.util.List} object.
	 * @throws java.lang.Exception if any.
	 */
	public abstract List<String> getColumnNames() throws Exception;

	/**
	 * <p>getObject.</p>
	 *
	 * @param columnLabel a {@link java.lang.String} object.
	 * @return a {@link java.lang.Object} object.
	 * @throws java.lang.Exception if any.
	 */
	public abstract Object getObject(String columnLabel) throws Exception;

	/**
	 * <p>get BigDecimal value
	 * @param columnLabel 
	 * @return The value
	 * @throws Exception 
	 */
	public abstract BigDecimal getBigDecimal(String columnLabel) throws Exception;
	
}
