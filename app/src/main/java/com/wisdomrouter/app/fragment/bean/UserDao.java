package com.wisdomrouter.app.fragment.bean;

import com.wisdomrouter.app.utils.TimeUtil;

import java.io.Serializable;
import java.util.List;

public class UserDao {

	private String key;
	private int score;
	private String state;
	private String message;
	private Errors errors;
	private Userinfo userinfo;

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public class Errors {
		private String [] likename;
		private String [] mobile;
		private String [] email;
		private String [] password;
		private String [] sex;
		public String[] getLikename() {
			return likename;
		}
		public void setLikename(String[] likename) {
			this.likename = likename;
		}
		public String[] getMobile() {
			return mobile;
		}
		public void setMobile(String[] mobile) {
			this.mobile = mobile;
		}
		public String[] getEmail() {
			return email;
		}
		public void setEmail(String[] email) {
			this.email = email;
		}
		public String[] getPassword() {
			return password;
		}
		public void setPassword(String[] password) {
			this.password = password;
		}
		public String[] getSex() {
			return sex;
		}
		public void setSex(String[] sex) {
			this.sex = sex;
		}
	}

	public class Userinfo {
		private String openid;
		private String likename;
		private String email;
		private String mobile;
		private String password;
		private String createtime;
		private String facepic;
		private String key;
		private List<UserGroup> usergroup;
		private String promoter_complete;
		private int is_sign;//0未签到

		public List<UserGroup> getUsergroup() {
			return usergroup;
		}

		public void setUsergroup(List<UserGroup> usergroup) {
			this.usergroup = usergroup;
		}


		public int getIs_sign() {
			return is_sign;
		}

		public void setIs_sign(int is_sign) {
			this.is_sign = is_sign;
		}

		public class UserGroup implements Serializable{
			private String identification;
			private String ischeckd ;
			private String key ;
			private String name;

			public String getIdentification() {
				return identification;
			}

			public void setIdentification(String identification) {
				this.identification = identification;
			}

			public String getIscheckd() {
				return ischeckd;
			}

			public void setIscheckd(String ischeckd) {
				this.ischeckd = ischeckd;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getKey() {
				return key;
			}

			public void setKey(String key) {
				this.key = key;
			}
		}

		public String getPromoter_complete() {
			return promoter_complete;
		}

		public void setPromoter_complete(String promoter_complete) {
			this.promoter_complete = promoter_complete;
		}



		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getOpenid() {
			return openid;
		}

		public void setOpenid(String openid) {
			this.openid = openid;
		}

		public String getLikename() {
			return likename;
		}

		public void setLikename(String likename) {
			this.likename = likename;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getCreatetime() {
			return createtime;
		}

		public void setCreatetime(String createtime) {
			this.createtime = TimeUtil.getContentTime(Integer
					.parseInt(createtime));
		}

		public String getFacepic() {
			return facepic;
		}

		public void setFacepic(String facepic) {
			this.facepic = facepic;
		}
	}

	

	public Userinfo getUserinfo() {
		return userinfo;
	}

	public void setUserinfo(Userinfo userinfo) {
		this.userinfo = userinfo;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	
}
