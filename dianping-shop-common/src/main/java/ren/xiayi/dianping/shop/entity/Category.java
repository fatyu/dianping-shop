package ren.xiayi.dianping.shop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "category")
public class Category extends BaseEntity {

	public Category(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Category(Long id, String name, Long pid) {
		this(id, name);
		this.pid = pid;
	}

	private static final long serialVersionUID = -5455641880847958825L;
	private String name;
	private Long pid;

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "pid")
	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

}
