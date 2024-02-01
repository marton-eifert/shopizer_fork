package com.salesmanager.core.model.customer.attribute;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.generic.SalesManagerEntity;

@Entity
@Table(name="CUSTOMER_OPTION_SET",
	uniqueConstraints={
		@UniqueConstraint(columnNames={
				"CUSTOMER_OPTION_ID",
				"CUSTOMER_OPTION_VALUE_ID"
			})
	}
)
public class CustomerOptionSet extends SalesManagerEntity<Long, CustomerOptionSet> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CUSTOMER_OPTIONSET_ID", unique=true, nullable=false)
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "CUST_OPTSET_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long id;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="CUSTOMER_OPTION_ID", nullable=false)
	private CustomerOption customerOption = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="CUSTOMER_OPTION_VALUE_ID", nullable=false)
	private CustomerOptionValue customerOptionValue = null;
	


	@Column(name="SORT_ORDER")




/**********************************
 * CAST-Finding START #1 (2024-02-01 20:44:46.089511):
 * TITLE: Avoid primitive type wrapper instantiation
 * DESCRIPTION: Literal values are built at compil time, and their value stored directly in the variable. Literal strings also benefit from an internal mechanism of string pool, to prevent useless duplication, according to the fact that literal string are immutable. On the contrary, values created through wrapper type instantiation need systematically the creation of a new object with many attributes and a life process to manage, and can lead to redondancies for identical values.
 * OUTLINE: The code line `private Integer sortOrder = new Integer(0);` is most likely affected.  - Reasoning: The line unnecessarily instantiates a new Integer object, leading to resource waste.  - Proposed solution: Replace `private Integer sortOrder = new Integer(0);` with `private int sortOrder = 0;` to avoid unnecessary instantiation of Integer object.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


	private Integer sortOrder = new Integer(0);
	


	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void setCustomerOptionValue(CustomerOptionValue customerOptionValue) {
		this.customerOptionValue = customerOptionValue;
	}

	public CustomerOptionValue getCustomerOptionValue() {
		return customerOptionValue;
	}

	public void setCustomerOption(CustomerOption customerOption) {
		this.customerOption = customerOption;
	}

	public CustomerOption getCustomerOption() {
		return customerOption;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}


}
