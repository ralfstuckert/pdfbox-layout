package rst.pdfbox.layout.elements;


public class ControlElement implements Element {

	public final static ControlElement NEWPAGE = new ControlElement("NEWPAGE");
	
	private String name;
	
	public ControlElement(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ControlElement [NEWPAGE=" + NEWPAGE + ", name=" + name + "]";
	}

	
}
