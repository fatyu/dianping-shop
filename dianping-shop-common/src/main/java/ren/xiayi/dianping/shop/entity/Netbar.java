package ren.xiayi.dianping.shop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "netbar")
public class Netbar extends BaseEntity {
	private static final long serialVersionUID = 2932931217637097061L;
	private String name;
	private String subName;
	private String address;
	private String phone;
	private Double score;
	private Integer commentCount;
	private Integer avgCost;
	private Double lat;
	private Double lon;
	private String dpUrl;
	private String streetName;
	private Long cid;
	private Double qqLon;
	private Double qqLat;

	@Column(name = "qq_lon")
	public Double getQqLon() {
		return qqLon;
	}

	public void setQqLon(Double qqLon) {
		this.qqLon = qqLon;
	}

	@Column(name = "qq_lat")
	public Double getQqLat() {
		return qqLat;
	}

	public void setQqLat(Double qqLat) {
		this.qqLat = qqLat;
	}

	private Long aid;

	@Column(name = "dp_url")
	public String getDpUrl() {
		return dpUrl;
	}

	public void setDpUrl(String dpUrl) {
		this.dpUrl = dpUrl;
	}

	@Column(name = "street_name")
	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "sub_name")
	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	@Column(name = "address")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "phone")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "score")
	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	@Column(name = "comment_count")
	public Integer getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	@Column(name = "avg_cost")
	public Integer getAvgCost() {
		return avgCost;
	}

	public void setAvgCost(Integer avgCost) {
		this.avgCost = avgCost;
	}

	@Column(name = "lat")
	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	@Column(name = "lon")
	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	@Column(name = "cid")
	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	@Column(name = "aid")
	public Long getAid() {
		return aid;
	}

	public void setAid(Long aid) {
		this.aid = aid;
	}

}
