package ren.xiayi.dianping.shop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "street")
public class Street extends BaseEntity {
	private static final long serialVersionUID = -1323396130115135716L;
	private String enname;
	private String name;
	private Long aid;

	public Street(long id, String name, Long aid, String enname) {
		this(id, name, aid);
		this.enname = enname;
	}

	public Street(long id, String name, Long aid) {
		this.id = id;
		this.name = name;
		this.aid = aid;
	}

	public Street() {
		super();
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

	@Column(name = "aid")
	public Long getAid() {
		return aid;
	}

	public void setAid(Long aid) {
		this.aid = aid;
	}

}
