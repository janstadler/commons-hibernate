/*
 * Copyright (c) 2012 Jakub Jirutka <jakub@jirutka.cz>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the  GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.jirutka.commons.hibernate.criteria;

import cz.jirutka.commons.persistence.dao.OrderBy;
import cz.jirutka.commons.persistence.dao.PagingOrdering;
import java.util.Iterator;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.sql.JoinType;

/**
 * This is concrete {@link Criteria} decorator that implements the Visitor 
 * pattern for easy extensibility. It also adds methods for setting paging and 
 * ordering through {@link PagingOrdering}.
 * 
 * @author Jakub Jirutka <jakub@jirutka.cz>
 * @version 2012-06-16
 * @since 1.1
 */
public class ExtendedCriteria extends AbstractCriteriaDecorator<ExtendedCriteria> {   

    /**
     * Decorate the given criteria.
     * 
     * @param criteria the criteria being decorated
     */
    public ExtendedCriteria(Criteria criteria) {
        super(criteria);
    }

    @Override
    protected ExtendedCriteria decorate(Criteria criteria) {
        this.criteria = criteria;
        return this;
    }
    
    
    /**
     * Accept the given visitor, i.e. apply its modifications to the root 
     * criteria.
     * 
     * @param visitor a visitor instance that will be called
     * @return this (for method chaining)
     */
    public ExtendedCriteria apply(CriteriaVisitor visitor) {
        if (visitor != null) visitor.visit(getRootCriteriaImpl());
        return this;
    }
    
    
    /**
     * Set paging and ordering of the result set from the given 
     * <tt>PagingOrdering</tt> holder, with the default association alias 
     * {@link #ROOT_ALIAS ROOT_ALIAS}.
     * 
     * @param pao a paging & ordering
     * @return this (for method chaining)
     */
    public ExtendedCriteria setPagingOrdering(PagingOrdering pao) {
        return setPagingOrdering(pao, ROOT_ALIAS, false);
    }
    
    /**
     * Set paging and ordering of the result set from the given 
     * <tt>PagingOrdering</tt> holder, with the default association alias 
     * {@link #ROOT_ALIAS ROOT_ALIAS}.
     * 
     * @param pao a paging & ordering
     * @param cleanOrderings remove all existing orderings before adding new ones?
     *                       (only when <tt>paging</tt> has some ordering)
     * @return this (for method chaining)
     */
    public ExtendedCriteria setPagingOrdering(PagingOrdering pao, boolean cleanOrderings) {
        return setPagingOrdering(pao, ROOT_ALIAS, cleanOrderings);
    }
    
    /**
     * Set paging and ordering of the result set from the given 
     * <tt>PagingOrdering</tt> holder.
     * 
     * @param pao a paging & ordering
     * @param orderAlias an association alias for ordering property/ies
     * @param cleanOrderings remove all existing orderings before adding new ones?
     *                       (only when <tt>paging</tt> has some ordering)
     * @return this (for method chaining)
     */
    public ExtendedCriteria setPagingOrdering(PagingOrdering pao, String orderAlias, boolean cleanOrderings) {
        if (pao == null) return this;
        Criteria rootCriteria = getRootCriteriaImpl();
        
        if (cleanOrderings) cleanOrderings();
        
        if (pao.getOffset() > 0) {
            rootCriteria.setFirstResult(pao.getOffset());
        }
        if (pao.getLimit() > 0) {
            rootCriteria.setMaxResults(pao.getLimit());
        }
        for (OrderBy orderBy : pao.getOrdering()) {
            String name = orderAlias + '.' + orderBy.getPropertyName();
            Order order = orderBy.isAscending() ? Order.asc(name) : Order.desc(name);
            rootCriteria.addOrder(order);
        }
        return this;
    }
    
    /**
     * Remove all orderings from the root Criteria.
     */
    public void cleanOrderings() {
        Iterator<Order> it = getRootCriteriaImpl().iterateOrderings();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    /**
     * Join an association using the specified join-type, assigning an alias
     * to the joined association.
     * <p/>
     * The joinType is expected to be one of {@link org.hibernate.sql.JoinType#INNER_JOIN} (the default),
     * {@link org.hibernate.sql.JoinType#FULL_JOIN}, or {@link org.hibernate.sql.JoinType#LEFT_OUTER_JOIN}.
     *
     * @param associationPath A dot-seperated property path
     * @param alias           The alias to assign to the joined association (for later reference).
     * @param joinType        The type of join to use.
     * @return this (for method chaining)
     * @throws org.hibernate.HibernateException
     *          Indicates a problem creating the sub criteria
     */
    @Override
    public Criteria createAlias(String associationPath, String alias, JoinType joinType) throws HibernateException {
        return criteria.createAlias(associationPath, alias, joinType);
    }

    /**
     * Join an association using the specified join-type, assigning an alias
     * to the joined association.
     * <p/>
     * The joinType is expected to be one of {@link org.hibernate.sql.JoinType#INNER_JOIN} (the default),
     * {@link org.hibernate.sql.JoinType#FULL_JOIN}, or {@link org.hibernate.sql.JoinType#LEFT_OUTER_JOIN}.
     *
     * @param associationPath A dot-seperated property path
     * @param alias           The alias to assign to the joined association (for later reference).
     * @param joinType        The type of join to use.
     * @param withClause      The criteria to be added to the join condition (<tt>ON</tt> clause)
     * @return this (for method chaining)
     * @throws org.hibernate.HibernateException
     *          Indicates a problem creating the sub criteria
     */
    @Override
    public Criteria createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException {
        return criteria.createAlias(associationPath, alias, joinType, withClause);
    }

    /**
     * Create a new <tt>Criteria</tt>, "rooted" at the associated entity, using the
     * specified join type.
     *
     * @param associationPath A dot-seperated property path
     * @param joinType        The type of join to use.
     * @return the created "sub criteria"
     * @throws org.hibernate.HibernateException
     *          Indicates a problem creating the sub criteria
     */
    @Override
    public Criteria createCriteria(String associationPath, JoinType joinType) throws HibernateException {
        return criteria.createCriteria(associationPath, joinType);
    }

    /**
     * Create a new <tt>Criteria</tt>, "rooted" at the associated entity,
     * assigning the given alias and using the specified join type.
     *
     * @param associationPath A dot-seperated property path
     * @param alias           The alias to assign to the joined association (for later reference).
     * @param joinType        The type of join to use.
     * @return the created "sub criteria"
     * @throws org.hibernate.HibernateException
     *          Indicates a problem creating the sub criteria
     */
    @Override
    public Criteria createCriteria(String associationPath, String alias, JoinType joinType) throws HibernateException {
        return criteria.createCriteria(associationPath, alias, joinType);
    }

    /**
     * Create a new <tt>Criteria</tt>, "rooted" at the associated entity,
     * assigning the given alias and using the specified join type.
     *
     * @param associationPath A dot-seperated property path
     * @param alias           The alias to assign to the joined association (for later reference).
     * @param joinType        The type of join to use.
     * @param withClause      The criteria to be added to the join condition (<tt>ON</tt> clause)
     * @return the created "sub criteria"
     * @throws org.hibernate.HibernateException
     *          Indicates a problem creating the sub criteria
     */
    @Override
    public Criteria createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException {
        return criteria.createCriteria(associationPath, alias, joinType, withClause);
    }
}
