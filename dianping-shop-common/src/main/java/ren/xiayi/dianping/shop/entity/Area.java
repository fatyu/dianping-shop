package ren.xiayi.dianping.shop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "area")
public class Area extends BaseEntity {
	private static final long serialVersionUID = -3177866074569115100L;
	private String enname;
	private String name;
	private Long cid;

	public Area() {
		super();
	}

	public Area(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Area(Long id, String name, String enname) {
		this(id, name);
		this.enname = enname;
	}

	@Column(name = "enname")
	public String getEnname() {
		return enname;
	}

	public void setEnname(String enname) {
		this.enname = enname;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "cid")
	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

}
