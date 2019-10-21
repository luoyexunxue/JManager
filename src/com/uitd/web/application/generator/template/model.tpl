package {{package}}.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class {{name}} {
	// ID
	private String id;
	{{#repeat columns}}
	// {{columns.desc}}
	private {{columns.type}} {{columns.name}};
	{{#repeat}}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	{{#repeat columns}}

	public {{columns.type}} get{{columns.nameU}}() {
		return {{columns.name}};
	}

	public void set{{columns.nameU}}({{columns.type}} {{columns.name}}) {
		this.{{columns.name}} = {{columns.name}};
	}
	{{#repeat}}
}