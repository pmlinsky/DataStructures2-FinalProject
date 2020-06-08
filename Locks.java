package finalProject;


public class Locks {

	private Object url_LOCK;
	private Object pn_LOCK;
	private Object email_LOCK;
	private Object tl_LOCK;
	private Object el_LOCK;
	private Object date_LOCK;
	private Object time_LOCK;
	private Object html_LOCK;
		
	public Locks() {
		this.url_LOCK  = new  Object();
		this.pn_LOCK = new  Object();
		this.email_LOCK = new  Object();
		this.tl_LOCK = new  Object();
		this.el_LOCK = new  Object();
		this.date_LOCK = new  Object();
		this.time_LOCK = new  Object();
		this.html_LOCK = new Object();
	}
	
	public Object getUrl_LOCK() {
		return url_LOCK;
	}
	public Object getPn_LOCK() {
		return pn_LOCK;
	}
	public Object getEmail_LOCK() {
		return email_LOCK;
	}
	public Object getTl_LOCK() {
		return tl_LOCK;
	}
	public Object getEl_LOCK() {
		return el_LOCK;
	}
	public Object getDate_LOCK() {
		return date_LOCK;
	}
	public Object getTime_LOCK() {
		return time_LOCK;
	}
	public Object getHTML_LOCK() {
		return html_LOCK;
	}
}
