package de.mhus.lib.adb.relation;

import de.mhus.lib.adb.IRelationObject;
import de.mhus.lib.adb.model.Field;
import de.mhus.lib.adb.model.FieldRelation;
import de.mhus.lib.core.parser.AttributeMap;
import de.mhus.lib.sql.DbConnection;

/**
 * <p>RelMultible class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class RelMultible <T> implements IRelationObject {

	private FieldRelation field;
	private Object obj;
	private RelList<T> relations;

	/**
	 * <p>Getter for the field <code>relations</code>.</p>
	 *
	 * @return a {@link de.mhus.lib.adb.relation.RelList} object.
	 * @throws java.lang.Exception if any.
	 */
	public RelList<T> getRelations() throws Exception {
		synchronized (this) {
			if (relations == null) {

				Class<?> target = field.getConfig().target();
				String src = field.getConfig().sourceAttribute();
				if ("".equals(src)) src = "id";
				src = src.toLowerCase();

				String tar = field.getConfig().targetAttribute();
				if ("".equals(tar)) tar = field.getName() + "id";
				//tar = tar.toLowerCase();

				String order = "";
				if (!"".equals(field.getConfig().orderBy())) {
					order = " ORDER BY $db." + field.getManager().getMappingName(target) + "." + field.getConfig().orderBy() + "$";
				}

				Field idField = field.getTable().getField(src);
				if (idField == null) return null;
				Object id = idField.getFromTarget(obj);
				if (id == null) return null;
				relations = new RelList<T>( field.getManager().getByQualification(target,
						"$db." + field.getManager().getMappingName(field.getConfig().target()) + "." + tar + "$ = $id$" + order ,
						new AttributeMap("id", id) ).toCacheAndClose(), field.getConfig());

			}
		}
		return relations;
	}

	/**
	 * <p>reset.</p>
	 */
	public void reset() {
		synchronized (this) {
			relations = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void prepareCreate() {

	}

	/** {@inheritDoc} */
	@Override
	public void created(DbConnection con) throws Exception {

		if (!field.getConfig().managed() || !isChanged()) return;

		String src = field.getConfig().sourceAttribute();
		if ("".equals(src)) src = "Id";
		src = src.toLowerCase();
		Field idField = field.getTable().getField(src);
		if (idField == null) return;
		Object id = idField.getFromTarget(obj);
		if (id == null) return;

		String tar = field.getConfig().targetAttribute();
		if ("".equals(tar)) tar = field.getName() + "Id";
		tar = tar.toLowerCase();

		relations.save(field.getManager(), con, tar, id);
	}

	/** {@inheritDoc} */
	@Override
	public void saved(DbConnection con) throws Exception {
		created(con);
	}

	/** {@inheritDoc} */
	@Override
	public void setManager(FieldRelation field, Object obj) {
		this.field = field;
		this.obj = obj;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isChanged() {
		return relations != null && relations.changed();
	}

	/** {@inheritDoc} */
	@Override
	public void loaded(DbConnection con) {
		synchronized (this) {
			relations = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void prepareSave(DbConnection con) {
		// TODO Auto-generated method stub

	}

}
