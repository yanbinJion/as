package com.wisdomrouter.app.fragment.bean;

public class EventDao {
	private String state;
	private String message;
	private Error error;

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

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public class Error {
		private String info_key;

		public String getInfo_key() {
			return info_key;
		}

		public void setInfo_key(String info_key) {
			this.info_key = info_key;
		}
	}
}
