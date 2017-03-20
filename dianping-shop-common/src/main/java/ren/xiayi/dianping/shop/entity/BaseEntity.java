package ren.xiayi.dianping.shop.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 统一定义id的entity基类,基类统一定义主键的名称、类型、列名映射及生成策略.
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = -6177434407872557134L;
	protected Long id;

	@Id
	@Column(name = "id", nullable = false, unique = true)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
